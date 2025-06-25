#!/bin/bash

sudo apt update
sudo apt install -y qemu-kvm libvirt-daemon-system libvirt-clients bridge-utils virt-manager \
                    genisoimage nginx iptables curl net-tools jq
wget https://cloud-images.ubuntu.com/focal/current/focal-server-cloudimg-amd64.img -O ubuntu-base.qcow2
sudo mv ubuntu-base.qcow2 /var/lib/libvirt/images/
mkdir cloud-init vm_data

set -e

COMMON_FILE="/etc/nginx/sites-available/common"
ENABLED_LINK="/etc/nginx/sites-enabled/common"

echo "[+] Creating common Nginx config..."

sudo rm /etc/nginx/sites-enabled/default

sudo tee "$COMMON_FILE" > /dev/null <<EOF
server {
    listen 80 default_server;
    server_name _;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
    }
}
EOF

echo "[+] Symlinking common config to sites-enabled..."
sudo ln -sf "$COMMON_FILE" "$ENABLED_LINK"

echo "[+] Testing Nginx configuration..."
sudo nginx -t

echo "[+] Reloading Nginx service..."
sudo systemctl reload nginx

echo "Nginx common configuration applied successfully."

echo "[+] Downloading JDK 21..."
wget -q https://download.oracle.com/java/21/archive/jdk-21_linux-x64_bin.tar.gz

echo "[+] Extracting JDK..."
tar -xzf jdk-21_linux-x64_bin.tar.gz
sudo mv jdk-21 /usr/local/

echo "[+] Setting JAVA_HOME..."
if ! grep -q "JAVA_HOME" ~/.bashrc; then
  echo 'export JAVA_HOME=/usr/local/jdk-21' >> ~/.bashrc
  echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc
fi
echo "[+] JAVA_HOME is set to $JAVA_HOME"
rm *.gz

cd ../vm-controller
echo "[+] Installing python3-venv..."
sudo apt install -y python3-venv

echo "[+] Creating Python virtual environment..."
python3 -m venv venv

echo "[+] Activating virtual environment..."
source venv/bin/activate

echo "[+] Installing required Python packages..."
pip install --upgrade pip 
pip install grpcio grpcio-tools requests

echo "[+] Generating gRPC Python files from vm.proto..."
python3 -m grpc_tools.protoc -I. --python_out=. --grpc_python_out=. ./vm.proto