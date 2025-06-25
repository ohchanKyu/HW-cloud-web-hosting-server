package kr.ac.dankook.CloudApiGateway.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ApiErrorCode implements ErrorCode{

    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "Invalid request parameters."),
    DUPLICATE_ID(HttpStatus.BAD_REQUEST, "This is duplicated Id."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "Not found member with your primary key or userId"),
    ALREADY_EXIST_VM(HttpStatus.CONFLICT, "Already exist your VM."),
    VM_NOT_FOUND(HttpStatus.NOT_FOUND, "Not found your VM Instance."),
    VM_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create VM instance."),
    VM_DELETION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete VM instance."),
    VM_SCRIPT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create VM script."),
    VM_SCRIPT_EXECUTE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to execute VM script.");

    private final HttpStatus httpStatus;
    private final String message;
}
