#!/bin/bash
# check_vm.sh <vm_id>

vm_id=$1
IP_FILE="$HOME/CloudProject/scripts/vm_data/${vm_id}_ip.txt"
PORT_FILE="$HOME/CloudProject/scripts/vm_data/${vm_id}_ssh_port.txt"

echo ""
echo "[+] VM 상태 확인: $vm_id"
echo "---------------------------------------"
echo " Id    Name                    State"
echo "---------------------------------------"
sudo virsh list --all | grep "$vm_id"
echo ""

HOST_IP=$(curl -s http://checkip.amazonaws.com)

if [[ -f "$IP_FILE" && -f "$PORT_FILE" ]]; then
    VM_IP=$(cat "$IP_FILE")
    SSH_PORT=$(cat "$PORT_FILE")
    echo " - 웹 접속 주소: http://$HOST_IP/$vm_id/"
    echo " - SSH 접속: ssh ubuntu@$HOST_IP -p $SSH_PORT"
else
    echo "[wait] VM을 세팅중입니다. 잠시만 기다려주세요."
fi