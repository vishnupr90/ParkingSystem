package com.infy.parkingSystem.exception;

public record CustomErrorResponse(int errorCode, String errorMessage) {
}