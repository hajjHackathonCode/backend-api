/*
 * Copyright (c) 2017.  Alneqat Ltd (https://alneqat.com). All Rights Reserved
 */

package com.campaigns.api.model;

import com.campaigns.api.utils.ObjectIdDeserializer;
import com.campaigns.api.utils.ObjectIdSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "beaconId")
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
    @JsonDeserialize(using = ObjectIdDeserializer.class)
    @JsonSerialize(using = ObjectIdSerializer.class)
    private ObjectId groupId;
    private String image;
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
