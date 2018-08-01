package com.clanconnect.api.model.enums;

public enum RoleName
{
    Member("Member"),
    Moderator("Moderator");

    private String value;

    RoleName(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

    public String toString()
    {
        return value;
    }
}
