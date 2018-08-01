package com.clanconnect.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSituation
{
    private Visitor visitor;
    private GeoLocation location;
    private SituationStatus situationStatus;
    private InformationHistory informationHistory;
}
