package com.redhat.labs.lodestar.engagement.status.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.redhat.labs.lodestar.engagement.status.exception.EngagementStatusException;
import com.redhat.labs.lodestar.engagement.status.model.EngagementStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

/**
 * Used converting String to Objects (non-request, non-response)
 * 
 * @author mcanoy
 *
 */
@ApplicationScoped
public class JsonMarshaller {
    public static final Logger LOGGER = LoggerFactory.getLogger(JsonMarshaller.class);
    
    final ObjectMapper om;
    
    public JsonMarshaller() {
        om = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .enable(SerializationFeature.INDENT_OUTPUT);
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        om.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        om.registerModule(new JavaTimeModule());
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        LOGGER.debug("marshaller started");
    }


    public EngagementStatus fromJson(String json) {
        try {
            return om.readValue(json, new TypeReference<EngagementStatus>() {
            });
        } catch (JsonProcessingException e) {
            throw new EngagementStatusException("Error translating status json data", e);
        }
    }

    public String toJson(Object obj) {
        try {
            return om.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new EngagementStatusException("Error translating status data to json", e);
        }
    }

}
