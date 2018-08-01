package com.clanconnect.api.services;

import com.clanconnect.api.model.GeoPoint;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.Math.PI;

@Service
public class GeoPolygonServices
{

    public boolean coordinateIsInsidePolygon(GeoPoint point, List<GeoPoint> points)
    {
        int i;
        double angle = 0;
        double point1_lat;
        double point1_long;
        double point2_lat;
        double point2_long;
        int n = points.size();

        for (i = 0; i < n; i++)
        {
            point1_lat = points.get(i).getLat() - point.getLat();  //latArray.get(i) - latitude;
            point1_long = points.get(i).getLon() - point.getLon(); //longArray.get(i) - longitude;
            point2_lat = points.get((i + 1) % n).getLat() - point.getLat(); //latArray.get((i + 1) % n) - latitude;
            point2_long = points.get((i + 1) % n).getLon() - point.getLon(); //longArray.get((i + 1) % n) - longitude;
            angle += Angle2D(point1_lat, point1_long, point2_lat, point2_long);
        }

        return !(Math.abs(angle) < PI);
    }

    private double Angle2D(double y1, double x1, double y2, double x2)
    {
        double dTheta, theta1, theta2;
        double TwoPI = 2 * PI;

        theta1 = Math.atan2(y1, x1);
        theta2 = Math.atan2(y2, x2);
        dTheta = theta2 - theta1;
        while (dTheta > PI)
            dTheta -= TwoPI;
        while (dTheta < -PI)
            dTheta += TwoPI;

        return (dTheta);
    }
}
