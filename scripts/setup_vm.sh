#!/bin/bash
# setup_vm.sh <vm_id>

vm_id=$1

if [[ -z "$vm_id" ]]; then
  echo "[!] VM_ID를 인자로 주세요."
  exit 1
fi

VM_STATE=$(sudo virsh list --all | grep -w "$vm_id" | grep running)
if [[ -z "$VM_STATE" ]]; then
  echo "[!] '$vm_id' VM이 존재하지 않거나 실행 중이지 않습니다."
  exit 1
fi

MAC_ADDR=$(sudo virsh domiflist "$vm_id" | grep -i default | awk '{print $5}')
if [[ -z "$MAC_ADDR" ]]; then
  echo "[!] MAC 주소를 찾을 수 없습니다."
  exit 1
fi

DOMAIN_IP=$(sudo virsh net-dhcp-leases default | grep -i "$MAC_ADDR" | awk '{print $5}' | cut -d'/' -f1)
if [[ -z "$DOMAIN_IP" ]]; then
  echo "[!] IP를 찾을 수 없습니다. DHCP에서 아직 할당되지 않았을 수 있습니다."
  exit 1
fi

COMMON_CONF="/etc/nginx/sites-available/common"

if [ ! -f "$COMMON_CONF" ]; then
  sudo tee "$COMMON_CONF" > /dev/null <<EOF
server {
    listen 80;
    server_name _;

    location / {
        return 404;
    }
}
EOF
  sudo ln -sf "$COMMON_CONF" /etc/nginx/sites-enabled/
fi

if ! sudo grep -q "location /$vm_id/" "$COMMON_CONF"; then
  sudo sed -i "/location \/ {/i \\
    location /$vm_id/ {\\
        proxy_pass http://$DOMAIN_IP/$vm_id/;\\
        proxy_set_header Host \$host;\\
        proxy_set_header X-Real-IP \$remote_addr;\\
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;\\
    }\\
" "$COMMON_CONF"
fi

sudo nginx -t && sudo systemctl reload nginx

while :; do
  RANDOM_PORT=$((RANDOM % 10000 + 20000))
  ! sudo iptables -t nat -L PREROUTING | grep -q ":$PORT " && break
done
sudo iptables -t nat -A PREROUTING -p tcp --dport $RANDOM_PORT -j DNAT --to-destination $DOMAIN_IP:22
sudo iptables -I FORWARD 1 -p tcp -d $DOMAIN_IP --dport 22 -j ACCEPT

echo "$DOMAIN_IP" > ../scripts/vm_data/${vm_id}_ip.txt
echo "$RANDOM_PORT" > ../scripts/vm_data/${vm_id}_ssh_port.txt

HOST_IP=$(curl -s http://checkip.amazonaws.com)

echo "[+] 설정 완료"
echo " - 웹 접속:     http://$HOST_IP/$vm_id/"
echo " - SSH 접속:    ssh ubuntu@$HOST_IP -p $RANDOM_PORT"
echo " - SFTP 접속:   sftp -P $RANDOM_PORT ubuntu@$HOST_IP"