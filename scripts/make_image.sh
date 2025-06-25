#!/bin/bash
# make_image.sh <vm_id>

vm_id=$1
name=$2

if [ -z "$vm_id" ]; then
  echo "Usage: $0 <vm_id>"
  exit 1
fi

WORKDIR="../scripts/cloud-init/$vm_id"
mkdir -p "$WORKDIR"
cd "$WORKDIR" || exit 1

cat <<EOF > user-data
#cloud-config
apt:
  disable_ipv6: true

hostname: $vm_id
users:
  - name: ubuntu
    groups: sudo
    shell: /bin/bash
    sudo: ['ALL=(ALL) NOPASSWD:ALL']
    lock_passwd: false

ssh_pwauth: true

chpasswd:
  list: |
    ubuntu:ubuntu
  expire: false

package_update: true
package_upgrade: false
packages:
  - nginx
  - openssh-server

write_files:
  - path: /var/www/html/$vm_id/index.html
    content: |
      <!DOCTYPE html>
      <html lang="en">
      <head>
          <meta charset="UTF-8">
          <meta name="viewport" content="width=device-width, initial-scale=1.0">
          <title>Welcome to $vm_id</title>
          <style>
              body {
                  font-family: Arial, sans-serif;
                  background-color: #f4f4f4;
                  margin: 0;
                  padding: 0;
              }
              .container {
                  max-width: 600px;
                  margin: 100px auto;
                  padding: 40px;
                  background-color: #fff;
                  border-radius: 8px;
                  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
                  text-align: center;
              }
              h1 {
                  color: #333;
              }
              p {
                  color: #666;
              }
          </style>
      </head>
      <body>
          <div class="container">
              <h1>Welcome to VM</h1>
              <p>This virtual machine is provisioned for <strong>$name</strong>.</p>
              <p>Enjoy your server powered by Nginx!</p>
          </div>
      </body>
      </html>

  - path: /etc/nginx/sites-available/default
    content: |
      server {
          listen 80 default_server;
          listen [::]:80 default_server;

          root /var/www/html;

          index index.html index.htm;

          location / {
              return 404;
          }

          location /$vm_id/ {
              alias /var/www/html/$vm_id/;
              index index.html index.htm;
              try_files \$uri \$uri/ =404;
          }
      }

runcmd:
  - systemctl enable nginx
  - systemctl restart nginx
EOF

cat <<EOF > meta-data
instance-id: $vm_id
local-hostname: $vm_id
EOF

genisoimage -output seed.iso -volid cidata -joliet -rock user-data meta-data

echo "[+] seed.iso 생성 완료"