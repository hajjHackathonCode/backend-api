package com.campaigns.api.utils;

public enum ErrorCode
{
    INPUT_INVALID("INPUT_INVALID"),
    CLAN_MISS_CURRENT_GEO_FENCE("CLAN_MISS_CURRENT_GEO_FENCE"),
    OBJECT_ID_NOT_FOUND("OBJECT_ID_NOT_FOUND"),
    UPDATE_OPERATION_FAILED("UPDATE_OPERATION_FAILED");

    ErrorCode(String inputInvalid)
    {

    }
}