package com.redhat.labs.lodestar.engagement.status.utils;

import com.redhat.labs.lodestar.engagement.status.exception.EngagementStatusException;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

@QuarkusTest
class JsonMarshallerTest {

    @Inject
    JsonMarshaller json;

    @Test
    void testFromExceptions() {
        String badData = "";

        Assertions.assertThrows(EngagementStatusException.class, () -> {
            json.fromJson(badData);
        });

    }

}
