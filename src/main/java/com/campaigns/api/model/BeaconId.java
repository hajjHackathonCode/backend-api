package com.campaigns.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeaconId implements Serializable
{
    private String guid;
    private String major;
    private String minor;
}
