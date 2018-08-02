package com.campaigns.api.model;

import com.campaigns.api.repository.VisitorRepository;
import com.campaigns.api.utils.ObjectIdDeserializer;
import com.campaigns.api.utils.ObjectIdSerializer;
import com.campaigns.api.utils.Utils;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

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
    private GeoPoint lastLocation;
    private SituationStatus situationStatus;
    private List<InformationHistory> informationHistory;
    private Date lostStart;

    @Transient
    private Long lostPeriod;

    public static void fillLostPeriodIfAny(List<VisitorSituation> visitorSituations, VisitorRepository repository)
    {
        for (VisitorSituation visitorSituation : visitorSituations)
        {
            if (visitorSituation.getLostStart() != null)
            {
                visitorSituation.setLostPeriod(Utils.getPeriod(visitorSituation.getLostStart()));
            }
            if (visitorSituation.getSituationStatus().equals(SituationStatus.Lost))
            {
                Visitor visitor = repository.findByBeaconId(visitorSituation.getVisitor().getBeaconId());
                visitorSituation.setVisitor(visitor);
            }
        }
    }

    public static void fillLostPeriodIfAny(VisitorSituation visitorSituation, VisitorRepository repository)
    {
        if (visitorSituation.getLostStart() != null)
            visitorSituation.setLostPeriod(Utils.getPeriod(visitorSituation.getLostStart()));

        if (visitorSituation.getSituationStatus().equals(SituationStatus.Lost))
        {
            Visitor visitor = repository.findByBeaconId(visitorSituation.getVisitor().getBeaconId());
            visitorSituation.setVisitor(visitor);
        }
    }
}
