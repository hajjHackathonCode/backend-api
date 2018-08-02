package com.campaigns.api.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.bson.types.ObjectId;

import java.io.IOException;

public class ObjectIdDeserializer extends JsonDeserializer<ObjectId>
{

    @Override
    public ObjectId deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
    {

        String value = p.readValueAs(String.class);

        if(ObjectId.isValid(value))
            return new ObjectId(value);
        else
            return null;
    }
}
