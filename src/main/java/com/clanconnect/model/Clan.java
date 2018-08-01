package com.clanconnect.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "clans")
public class Clan
{
    private List<TimedLocation> tentLocations;
    private Address mainOffice;
    private String name;
    private Contact mainContact;

}
