/**
 *
 */
package com.clanconnect.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MobileError
{
    private String username;
    private String localtime;
    private String message;
    private String name;
    private String platform;
    private String stack;
    private String type;
    private String url;
}
