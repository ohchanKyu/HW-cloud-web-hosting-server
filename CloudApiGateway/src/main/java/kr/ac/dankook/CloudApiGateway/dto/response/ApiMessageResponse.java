package kr.ac.dankook.CloudApiGateway.dto.response;

public record ApiMessageResponse(boolean success, int statusCode, String message) { }
