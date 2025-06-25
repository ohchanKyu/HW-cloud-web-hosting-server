package kr.ac.dankook.CloudApiGateway.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SshInfo {
    private String user;
    private String host;
    private int port;
    private String command;
}
