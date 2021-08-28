package com.redhat.labs.lodestar.engagement.status.rest.client;

import com.redhat.labs.lodestar.engagement.status.model.Engagement;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import java.util.List;

@RegisterRestClient(configKey = "engagement.api")
@Produces("application/json")
@Consumes("application/json")
@Path("/api/v1/engagements")
public interface EngagementApiRestClient {

    @GET
    List<Engagement> getAllEngagements(@QueryParam("includeCommits") boolean includeCommits, @QueryParam("includeStatus") boolean includeStatus, @QueryParam("pagination") boolean pagination);


    @GET
    @Path("/uuid/{uuid}")
    Engagement getEngagement(@PathParam("uuid") String engagementUuid);
}
