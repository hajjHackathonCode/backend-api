package com.campaigns.api.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomException extends Exception
{
    private ErrorCode errorCode;
    private String customMessage;

    public CustomException(ErrorCode errorCode)
    {
        this.errorCode = errorCode;
        this.customMessage = null;
    }
}
