package com.clanconnect.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimedGeoFence
{
    private Date from;
    private Date to;
    private List<GeoPoint> geoPoints;
}
