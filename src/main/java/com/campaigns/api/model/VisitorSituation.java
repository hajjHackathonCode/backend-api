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

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "visitor")
@Document(collection = "visitorSituations")
public class VisitorSituation
{
    @Id
    @JsonDeserialize(using = ObjectIdDeserializer.class)
    @JsonSerialize(using = ObjectIdSerializer.class)

    private ObjectId id;
    private Visitor visitor;
    private GeoPoint location;
    private SituationStatus situationStatus;
    private InformationHistory informationHistory;
    private Date lostStart;
}
