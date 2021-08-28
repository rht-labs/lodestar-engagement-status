package com.redhat.labs.lodestar.engagement.status.rest.client;

import com.redhat.labs.lodestar.engagement.status.model.GitlabFile;
import org.apache.http.NoHttpResponseException;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Retry(maxRetries = 5, delay = 1200, retryOn = NoHttpResponseException.class, abortOn = WebApplicationException.class)
@Path("/api/v4")
@RegisterRestClient(configKey = "gitlab.api")
@RegisterProvider(value = RestClientResponseMapper.class, priority = 50)
@RegisterClientHeaders(GitlabTokenFactory.class)
@Produces("application/json")
public interface GitlabRestClient {

    @GET
    @Path("/projects/{id}/repository/files/{file_path}")
    @Produces("application/json")
    GitlabFile getFile(@PathParam("id") long projectId, @PathParam("file_path") @Encoded String filePath,
                       @QueryParam("ref") @Encoded String ref);

}
