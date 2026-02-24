package com.ashraf.payment.dto;

import com.ashraf.payment.exceptions.ApiError;

import java.time.Instant;

public record ApiResult<T>(
        boolean success,
        T data,
        ApiError error,
        Instant timestamp
) {

    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(true, data, null, Instant.now());
    }

    public static <T> ApiResult<T> failure(ApiError error) {
        return new ApiResult<>(false, null, error, Instant.now());
    }
}