package com.campaigns.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserLocationRequest implements Serializable
{
    private String lat;
    private String lon;
    private String dir;
    private ArrayList<String> beaconsIds;
}
