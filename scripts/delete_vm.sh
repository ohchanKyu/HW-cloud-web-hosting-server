#!/bin/bash
# delete_vm.sh <vm_id>

vm_id=$1
VM_DISK="/var/lib/libvirt/images/${vm_id}.qcow2"
NGINX_CONF="/etc/nginx/sites-available/$vm_id"

sudo virsh destroy "$vm_id" 2>/dev/null
sudo virsh undefine "$vm_id" --remove-all-storage

COMMON_CONF="/etc/nginx/sites-available/common"
sudo sed -i "/location \/$vm_id\//,/\}/d" "$COMMON_CONF"

sudo nginx -t && sudo systemctl reload nginx

if [[ -f vm_data/${vm_id}_ip.txt && -f vm_data/${vm_id}_ssh_port.txt ]]; then
    DOMAIN_IP=$(cat ../scripts/vm_data/${vm_id}_ip.txt)
    RANDOM_PORT=$(cat ../scripts/vm_data/${vm_id}_ssh_port.txt)

    sudo iptables -t nat -D PREROUTING -p tcp --dport $RANDOM_PORT -j DNAT --to-destination $DOMAIN_IP:22
    sudo iptables -D FORWARD -p tcp -d $DOMAIN_IP --dport 22 -j ACCEPT
fi

rm -f ../scripts/vm_data/${vm_id}_ip.txt
rm -f ../scripts/vm_data/${vm_id}_ssh_port.txt
rm -rf ../scripts/cloud-init/$vm_id

echo "[*] $vm_id VM 및 관련 설정이 삭제되었습니다."