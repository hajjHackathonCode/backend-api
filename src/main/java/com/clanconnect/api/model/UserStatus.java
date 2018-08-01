/**
 *
 */
package com.clanconnect.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatus
{
    private String guid;
    private String username;
    private String status;
    private Date activationDate;
}
