package com.redhat.labs.lodestar.engagement.status.service;

import com.redhat.labs.lodestar.engagement.status.model.Engagement;
import com.redhat.labs.lodestar.engagement.status.model.EngagementStatus;
import com.redhat.labs.lodestar.engagement.status.model.GitlabFile;
import com.redhat.labs.lodestar.engagement.status.rest.client.EngagementApiRestClient;
import com.redhat.labs.lodestar.engagement.status.rest.client.GitlabRestClient;
import com.redhat.labs.lodestar.engagement.status.utils.JsonMarshaller;
import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheResult;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.vertx.ConsumeEvent;
import io.vertx.mutiny.core.eventbus.EventBus;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import java.util.List;

public class EngagementStatusService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EngagementStatusService.class);

    private static final String REFRESH_EVENT = "refresh.event";
    private static final String REFRESH_STATUS_EVENT = "refresh.status.event";
    private static final String STATUS_CACHE_NAME = "engagementStatusCache";

    @ConfigProperty(name = "git.branch")
    String branch;

    @ConfigProperty(name = "status.file")
    String statusFile;

    @Inject
    EventBus bus;

    @Inject
    JsonMarshaller json;

    @Inject
    @RestClient
    GitlabRestClient gitlabRestClient;

    @Inject
    @RestClient
    EngagementApiRestClient engagementApiRestClient;

    void onStart(@Observes StartupEvent ev) {
        LOGGER.debug("There are {} commits in the activity db.", "0");
        bus.publish(REFRESH_EVENT, REFRESH_EVENT);
    }

    public void refresh() {
        bus.publish(REFRESH_EVENT, "client invoked");
    }

    @ConsumeEvent(value = REFRESH_EVENT, blocking = true)
    @CacheInvalidateAll(cacheName = STATUS_CACHE_NAME)
    public void refresh(String message) {
        LOGGER.debug(message);
        List<Engagement> allEngagements = engagementApiRestClient.getAllEngagements(false, false, false);

        LOGGER.debug("Engagements {}", allEngagements.size());

        allEngagements.forEach(e -> {
            bus.publish(REFRESH_STATUS_EVENT, e.getUuid());
        });

        LOGGER.debug("End refresh");

    }

    @CacheInvalidate(cacheName = STATUS_CACHE_NAME)
    public void updateEngagementStatus(String engagementUuid) {
        getEngagementStatus(engagementUuid);
    }

    @ConsumeEvent(value = REFRESH_STATUS_EVENT)
    @CacheResult(cacheName = STATUS_CACHE_NAME)
    public EngagementStatus getEngagementStatus(String engagementUuid) {
        LOGGER.debug("Getting status for {}", engagementUuid);
        Engagement engagement = engagementApiRestClient.getEngagement(engagementUuid);
        return getStatusFromGitlab(engagement);
    }

    private EngagementStatus getStatusFromGitlab(Engagement e) {
        long projectIdOrPath = e.getProjectId();
        try {
            GitlabFile file = gitlabRestClient.getFile(projectIdOrPath, statusFile, branch);
            file.decodeFileAttributes();
            LOGGER.debug("status file json for project {}", projectIdOrPath);
            return json.fromJson(file.getContent());
        } catch (WebApplicationException ex) {
            if (ex.getResponse().getStatus() != 404) {
                throw ex;
            }
            LOGGER.error("No status file found for {} {}", projectIdOrPath, ex.getMessage());
            return null;
        }
    }
}
