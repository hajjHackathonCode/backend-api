/*
 * Copyright (c) 2017.  Alneqat Ltd (https://alneqat.com). All Rights Reserved
 */

package com.clanconnect.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.*;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Document(collection = "visitors")
public class Visitor
{
    @Id
    private ObjectId id;
    private String firstName;
    private String lastName;
    private Contact contact;
    private EmergencyContact emergencyContact;
    private MedicalInfo medicalInfo;
    private Byte[] image;
    private VisitorType type;
    @Indexed(unique = true)
    private String beaconId;
    private String relativeBeaconsIDs;
    private String country;
    private String city;
    private Clan clan;


    @Version
    private long version;
    @CreatedBy
    private String createdBy;
    @CreatedDate
    private Date createdDate;
    @LastModifiedBy
    private String lastModifiedBy;
    @LastModifiedDate
    private Date lastModifiedDate;

    public enum VisitorType
    {
        Human, Device
    }
}
