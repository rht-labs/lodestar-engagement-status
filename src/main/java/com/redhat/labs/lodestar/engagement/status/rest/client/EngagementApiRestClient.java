package com.redhat.labs.lodestar.engagement.status.rest.client;

import com.redhat.labs.lodestar.engagement.status.model.Engagement;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import java.util.List;
import java.util.Set;

@RegisterRestClient(configKey = "engagement.api")
@Produces("application/json")
@Consumes("application/json")
@Path("/api/v2/engagements")
public interface EngagementApiRestClient {

    @GET
    @Path("inStates")
    List<Engagement> getAllEngagements(@QueryParam("inStates") Set<String> states);


    @GET
    @Path("/{uuid}")
    Engagement getEngagement(@PathParam("uuid") String engagementUuid);
}
