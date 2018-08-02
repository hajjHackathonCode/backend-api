package com.campaigns.api.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.bson.types.ObjectId;

import java.io.IOException;

public class ObjectIdSerializer extends JsonSerializer<ObjectId>
{

    @Override
    public void serialize(ObjectId objid, JsonGenerator jsongen, SerializerProvider provider) throws IOException
    {
        if (objid == null)
        {
            jsongen.writeNull();
        }
        else
        {
            jsongen.writeString(objid.toHexString());
        }
    }


}

