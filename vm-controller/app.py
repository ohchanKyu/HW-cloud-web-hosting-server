import grpc
from concurrent import futures
import vm_pb2
import vm_pb2_grpc
import threading
import subprocess
import time

def wait_until_vm_has_ip(vm_id, timeout=60):

    print(f"[WAIT] Waiting for VM '{vm_id}' to get an IP address via DHCP...")
    start_time = time.time()

    while time.time() - start_time < timeout:
        result = subprocess.run(
            ["sudo", "virsh", "domifaddr", vm_id],
            capture_output=True,
            text=True
        )
        output = result.stdout.strip()
        lines = output.splitlines()
        
        if len(lines) >= 3:
            for line in lines[2:]:
                if "ipv4" in line or "192." in line:
                    print(f"[WAIT] IP Address assigned:\n{line}")
                    return True
        time.sleep(3)
    print(f"[ERROR] Timeout: IP address was not assigned to VM '{vm_id}' after {timeout} seconds.")
    return False

class VmActionServiceServicer(vm_pb2_grpc.VmActionServiceServicer):

   
    def handle_create(self, vm_id, name):
        print(f"[CREATE] Starting creation of VM '{vm_id}'...")

        scripts = [
            ("../scripts/make_image.sh", [vm_id, name]),
            ("../scripts/create_vm.sh", [vm_id])
        ]

        for script, args in scripts:
            print(f"\n[+] Running: {script} {' '.join(args)}")
            result = subprocess.run(
                [script] + args,
                capture_output=True,
                text=True
            )
            if result.returncode != 0:
                print(f"[ERROR] {script} failed:\n{result.stderr.strip()}")
                return
            print(result.stdout.strip())

        if not wait_until_vm_has_ip(vm_id):
            return

        print(f"\n[+] Running: ./setup_vm.sh {vm_id}")
        result = subprocess.run(
            ["../scripts/setup_vm.sh", vm_id],
            capture_output=True,
            text=True
        )
        if result.returncode != 0:
            print(f"[ERROR] setup_vm.sh failed:\n{result.stderr.strip()}")
            return
        print(result.stdout.strip())
        print(f"\n[CREATE] VM '{vm_id}' created successfully.")


    def handle_delete(self, vm_id):
        print(f"[DELETE] Starting deletion of VM '{vm_id}'...")
        result = subprocess.run(["../scripts/delete_vm.sh", vm_id], capture_output=True, text=True)
        print(result.stdout.strip())
        time.sleep(3)
        print(f"[DELETE] VM '{vm_id}' deleted.")


    def HandleVmAction(self, request, context):
        vm_id = request.vm_id
        action = request.action
        name = request.name

        print(f"\n\n[Request] vm_id: {vm_id}, action: {vm_pb2.VmActionType.Name(action)}")

        if action == vm_pb2.CREATE:
            thread = threading.Thread(target=self.handle_create, args=(vm_id,name, ))
            thread.start()
            message = f"VM '{vm_id}' creation started asynchronously."
            success = True
        elif action == vm_pb2.DELETE:
            thread = threading.Thread(target=self.handle_delete, args=(vm_id,))
            thread.start()
            message = f"VM '{vm_id}' deletion started asynchronously."
            success = True
        else:
            message = f"Unknown action for VM '{vm_id}'."
            success = False

        return vm_pb2.VmActionResponse(
            success=success,
            message=message
        )

def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    vm_pb2_grpc.add_VmActionServiceServicer_to_server(VmActionServiceServicer(), server)
    server.add_insecure_port('[::]:50051')
    print("gRPC server started on port 50051")
    server.start()
    server.wait_for_termination()

if __name__ == "__main__":
    serve()
