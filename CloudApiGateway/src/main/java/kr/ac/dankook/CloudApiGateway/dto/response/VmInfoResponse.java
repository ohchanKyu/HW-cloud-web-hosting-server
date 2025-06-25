package kr.ac.dankook.CloudApiGateway.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VmInfoResponse {
    private String vmId;
    private String status;
    private String webUrl;
    private SshInfo ssh;
}
