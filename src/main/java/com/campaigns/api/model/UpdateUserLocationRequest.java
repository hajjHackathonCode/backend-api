package com.campaigns.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserLocationRequest
{
    private String lat;
    private String lon;
    private ArrayList<String> beaconsIds;
}
