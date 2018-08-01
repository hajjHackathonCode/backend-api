package com.clanconnect.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InformationHistory
{
    private int seq;
    private Date date;
    private String information;
}
