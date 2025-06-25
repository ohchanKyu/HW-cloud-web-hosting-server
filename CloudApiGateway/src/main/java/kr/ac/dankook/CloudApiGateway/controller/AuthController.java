package kr.ac.dankook.CloudApiGateway.controller;

import jakarta.validation.Valid;
import kr.ac.dankook.CloudApiGateway.dto.request.LoginRequest;
import kr.ac.dankook.CloudApiGateway.dto.request.RegisterRequest;
import kr.ac.dankook.CloudApiGateway.dto.response.ApiMessageResponse;
import kr.ac.dankook.CloudApiGateway.dto.response.ApiResponse;
import kr.ac.dankook.CloudApiGateway.dto.response.TokenResponse;
import kr.ac.dankook.CloudApiGateway.exception.ApiErrorCode;
import kr.ac.dankook.CloudApiGateway.exception.ValidationException;
import kr.ac.dankook.CloudApiGateway.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiMessageResponse> register(
            @RequestBody @Valid RegisterRequest registerRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            validateBindingResult(bindingResult);
        }
        authService.registerProcess(registerRequest);
        return ResponseEntity.status(201).body(new ApiMessageResponse(true,201,"Registration was successful."));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(
            @RequestBody @Valid LoginRequest loginRequest, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            validateBindingResult(bindingResult);
        }
        return ResponseEntity.ok(new ApiResponse<>(200,authService.loginProcess(loginRequest)));
    }

    private void validateBindingResult(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(","));
            throw new ValidationException(ApiErrorCode.INVALID_REQUEST,errorMessages);
        }
    }
}
