/*
 * Copyright (c) 2017.  Alneqat Ltd (https://alneqat.com). All Rights Reserved
 */

package com.campaigns.api.model;

import com.campaigns.api.utils.ObjectIdDeserializer;
import com.campaigns.api.utils.ObjectIdSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.*;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.annotation.Nonnegative;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
//@RequiredArgsConstructor
@Document(collection = "visitors")
public class Visitor
{
    @Id
    @JsonDeserialize(using = ObjectIdDeserializer.class)
    @JsonSerialize(using = ObjectIdSerializer.class)
    private ObjectId id;
    private String firstName;
    private String lastName;
    private String beaconId;
    private ObjectId groupId;
    private Byte[] image;
    private Contact contact;
    private EmergencyContact emergencyContact;
    private MedicalInfo medicalInfo;
    private String relativeBeaconsIDs;
    private String country;
    private String city;

    public enum VisitorType
    {
        Human, Device
    }
}
