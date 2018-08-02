package com.campaigns.api.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contact
{
    private String email;
    private String mobile;
    private String phone;
}
