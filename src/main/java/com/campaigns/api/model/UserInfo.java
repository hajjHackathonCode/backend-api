package com.campaigns.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo
{
    private String firstName;
    private String lastName;
    private String username;
}
