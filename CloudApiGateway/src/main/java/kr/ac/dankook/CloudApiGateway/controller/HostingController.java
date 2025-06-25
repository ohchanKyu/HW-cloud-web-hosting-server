package kr.ac.dankook.CloudApiGateway.controller;

import kr.ac.dankook.CloudApiGateway.config.principal.PrincipalDetails;
import kr.ac.dankook.CloudApiGateway.dto.response.ApiMessageResponse;
import kr.ac.dankook.CloudApiGateway.dto.response.ApiResponse;
import kr.ac.dankook.CloudApiGateway.dto.response.VmInfoResponse;
import kr.ac.dankook.CloudApiGateway.entity.Member;
import kr.ac.dankook.CloudApiGateway.service.AuthService;
import kr.ac.dankook.CloudApiGateway.service.HostingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/host")
@RequiredArgsConstructor
public class HostingController {

    private final AuthService authService;
    private final HostingService hostingService;

    @PostMapping("")
    public ResponseEntity<ApiMessageResponse> createVm(@AuthenticationPrincipal PrincipalDetails userDetails) {
        Member member = authService.findMemberByUserIdProcess(
                userDetails.getUsername()
        );
        hostingService.createVmProcess(member);
        return ResponseEntity.status(201).body(new ApiMessageResponse(true, 201,
                "The VM has been successfully created." +
                        "Please check the VM status via a GET /host request and connect afterward."
        ));
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<VmInfoResponse>> getVm(@AuthenticationPrincipal PrincipalDetails userDetails) {

        Member member = authService.findMemberByUserIdProcess(
                userDetails.getUsername()
        );
        return ResponseEntity.status(200).body(new ApiResponse<>(200,
                hostingService.getVmInfoProcess(member)));
    }

    @DeleteMapping("")
    public ResponseEntity<ApiMessageResponse> deleteVm(@AuthenticationPrincipal PrincipalDetails userDetails) {
        Member member = authService.findMemberByUserIdProcess(
                userDetails.getUsername()
        );
        hostingService.deleteVmProcess(member);
        return ResponseEntity.status(200).body(new ApiMessageResponse(true, 200,
                "Your VM has been successfully deleted. To create a new VM, please send a POST request to /host."
        ));
    }
}
