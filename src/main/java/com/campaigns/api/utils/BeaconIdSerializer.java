package com.campaigns.api.utils;

import com.campaigns.api.model.BeaconId;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.bson.types.ObjectId;

import java.io.IOException;

public class BeaconIdSerializer extends JsonSerializer<BeaconId>
{

    @Override
    public void serialize(BeaconId objid, JsonGenerator jsongen, SerializerProvider provider) throws IOException
    {
        if (objid == null)
        {
            jsongen.writeNull();
        }
        else
        {
            jsongen.writeString(objid.getGuid() + "##" + objid.getMajor() + "##" + objid.getMinor());
        }
    }


}

