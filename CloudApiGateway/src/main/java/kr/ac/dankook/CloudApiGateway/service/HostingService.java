package kr.ac.dankook.CloudApiGateway.service;

import kr.ac.dankook.CloudApiGateway.dto.response.SshInfo;
import kr.ac.dankook.CloudApiGateway.dto.response.VmInfoResponse;
import kr.ac.dankook.CloudApiGateway.entity.Member;
import kr.ac.dankook.CloudApiGateway.entity.Vm;
import kr.ac.dankook.CloudApiGateway.exception.ApiErrorCode;
import kr.ac.dankook.CloudApiGateway.exception.ApiException;
import kr.ac.dankook.CloudApiGateway.repository.VmRepository;
import kr.ac.dankook.VmActionProto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class HostingService {

    @Value("${vm.check.path}")
    private String scriptPath;
    private final VmRepository vmRepository;
    private final VmGrpcService vmGrpcService;

    @Transactional
    public void createVmProcess(Member member){

        Optional<Vm> checkVm = vmRepository.findByMember(member);
        if (checkVm.isPresent()) throw new ApiException(ApiErrorCode.ALREADY_EXIST_VM);

        String randomVmId = generateUrlSafeUUID();
        Vm vm = Vm.builder()
                .id(randomVmId)
                .member(member).build();

        VmActionProto.VmActionResponse vmActionResponse = vmGrpcService
                .sendVmActionInfo(vm,VmActionProto.VmActionType.CREATE);

        if (vmActionResponse.getSuccess()){
            log.info("Success creating vm process. USER_ID - {}", member.getUserId());
            vmRepository.save(vm);
        }else{
            log.error("Error creating vm process. USER_ID - {}. Error Message - {}",
                    member.getUserId(), vmActionResponse.getMessage());
            throw new ApiException(ApiErrorCode.VM_CREATION_FAILED);
        }
    }

    @Transactional(readOnly = true)
    public VmInfoResponse getVmInfoProcess(Member member) {

        Optional<Vm> checkVm = vmRepository.findByMember(member);
        if (checkVm.isEmpty()) {
            throw new ApiException(ApiErrorCode.VM_NOT_FOUND);
        }

        String vmId = checkVm.get().getId();
        List<String> command = List.of(scriptPath, vmId);

        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new ApiException(ApiErrorCode.VM_SCRIPT_FAILED);
            }
            return parseVmInfoOutput(output.toString());

        } catch (Exception e) {
            throw new ApiException(ApiErrorCode.VM_SCRIPT_EXECUTE_FAILED);
        }
    }

    private VmInfoResponse parseVmInfoOutput(String output) {

        String[] lines = output.split("\n");

        String vmId = null;
        String status = null;
        String webUrl = "[wait] VM을 세팅중입니다. 잠시만 기다려주세요.";
        String sshCmd = "";

        for (String line : lines) {
            if (line.contains("VM 상태 확인:")) {
                vmId = line.split(":")[1].trim();
            } else if (line.trim().matches("^\\d+\\s+.*\\s+(running|shut off)$")) {
                String[] parts = line.trim().split("\\s+");
                status = parts[parts.length - 1];
            } else if (line.contains("- 웹 접속 주소:")) {
                webUrl = line.split(": ", 2)[1].trim();
            } else if (line.contains("- SSH 접속:")) {
                sshCmd = line.split(": ", 2)[1].trim();
            }
        }

        String sshUser = "", sshHost = "";
        int sshPort = 22;
        if (sshCmd.startsWith("ssh")) {
            String[] sshParts = sshCmd.split(" ");
            String[] userHost = sshParts[1].split("@");
            sshUser = userHost[0];
            sshHost = userHost[1];
            sshPort = Integer.parseInt(sshParts[3]);
        }
        SshInfo sshInfo = new SshInfo(sshUser,sshHost,sshPort,sshCmd);
        return new VmInfoResponse(vmId,status,webUrl,sshInfo);
    }

    @Transactional
    public void deleteVmProcess(Member member){

        Optional<Vm> checkVm = vmRepository.findByMember(member);
        if (checkVm.isEmpty()) throw new ApiException(ApiErrorCode.VM_NOT_FOUND);

        VmActionProto.VmActionResponse vmActionResponse = vmGrpcService
                .sendVmActionInfo(checkVm.get(),VmActionProto.VmActionType.DELETE);

        if (vmActionResponse.getSuccess()){
            log.info("Success deleting vm process. USER_ID - {}", member.getUserId());
            vmRepository.delete(checkVm.get());
        }else{
            log.error("Error deleting vm process. USER_ID - {}. Error Message - {}",
                    member.getUserId(), vmActionResponse.getMessage());
            throw new ApiException(ApiErrorCode.VM_DELETION_FAILED);
        }
    }

    private String generateUrlSafeUUID() {

        UUID uuid = UUID.randomUUID();

        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());
        byte[] uuidBytes = byteBuffer.array();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(uuidBytes);
    }
}
