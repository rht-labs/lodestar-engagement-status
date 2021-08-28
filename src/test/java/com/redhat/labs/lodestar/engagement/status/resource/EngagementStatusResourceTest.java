package com.redhat.labs.lodestar.engagement.status.resource;

import com.redhat.labs.lodestar.engagement.status.mock.ExternalApiWireMock;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
@TestHTTPEndpoint(EngagementStatusResource.class)
@QuarkusTestResource(ExternalApiWireMock.class)
class EngagementStatusResourceTest {

    @Test
    void testGetStatus() {
        given().pathParam("engagementUuid", "uuid").when().get("{engagementUuid}").then().statusCode(200)
                .body("overall_status", equalTo("green"))
                .body("messages.size()", equalTo(3))
                .body("subsystems.size()", equalTo(2))
                .body("messages[0].message", equalTo("We had sandwiches for lunch"))
                .body("subsystems[0].name", equalTo("openshift"));
    }

    @Test
    void testUpdateStatus() {
        given().pathParam("engagementUuid", "uuid").when().put("{engagementUuid}").then().statusCode(200);
    }

    @Test
    void testRefresh() {
        given().when().put("refresh").then().statusCode(202);
    }
}
