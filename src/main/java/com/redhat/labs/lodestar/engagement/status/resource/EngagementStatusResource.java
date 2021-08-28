package com.redhat.labs.lodestar.engagement.status.resource;

import com.redhat.labs.lodestar.engagement.status.model.EngagementStatus;
import com.redhat.labs.lodestar.engagement.status.model.Subsystem;
import com.redhat.labs.lodestar.engagement.status.service.EngagementStatusService;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;

@RequestScoped
@Path("/api/engagement/status")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Engagement Status")
public class EngagementStatusResource {

    @Inject
    EngagementStatusService engagementStatusService;

    @PUT
    @Path("{engagementUuid}")
    public Response updateStatus(@PathParam("engagementUuid") String engagementUuid) {
        engagementStatusService.updateEngagementStatus(engagementUuid);
        return Response.ok().build();
    }

    @GET
    @Path("{engagementUuid}")
    public Response getStatus(@PathParam("engagementUuid") String engagementUuid) {
        EngagementStatus status = engagementStatusService.getEngagementStatus(engagementUuid);
        return Response.ok(status).build();
    }

    @PUT
    @Path("refresh")
    public Response refresh() {
        engagementStatusService.refresh();
        return Response.accepted().build();
    }
}
