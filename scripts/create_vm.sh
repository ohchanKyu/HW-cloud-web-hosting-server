#!/bin/bash
# create_vm.sh <vm_id>

vm_id=$1
RAM=1024
DISK_GB=5
BASE_IMG="/var/lib/libvirt/images/ubuntu-base.qcow2"
VM_DISK="/var/lib/libvirt/images/${vm_id}.qcow2"
SEED_DIR="/var/lib/libvirt/seedisos/$vm_id"
SEED_ISO="$SEED_DIR/seed.iso"

sudo mkdir -p "$SEED_DIR"
sudo cp "../scripts/cloud-init/$vm_id/seed.iso" "$SEED_ISO"
sudo cp "$BASE_IMG" "$VM_DISK"

sudo virt-install \
  --name "$vm_id" \
  --ram "$RAM" \
  --vcpus=1 \
  --os-variant=ubuntu20.04 \
  --disk path="$VM_DISK",format=qcow2 \
  --disk path="$SEED_ISO",device=cdrom \
  --network network=default \
  --graphics none \
  --noautoconsole \
  --boot cdrom,hd

echo "[+] VM 생성 요청 완료: $vm_id"