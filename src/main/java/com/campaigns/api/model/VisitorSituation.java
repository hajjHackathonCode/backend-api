package com.campaigns.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "visitor")
@Document(collection = "visitors")
public class VisitorSituation
{
    private Visitor visitor;
    private GeoPoint location;
    private SituationStatus situationStatus;
    private InformationHistory informationHistory;
    private Date lostStart;
}
