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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class EngagementStatusService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EngagementStatusService.class);

    private static final String REFRESH_EVENT = "refresh.event";
    private static final String REFRESH_STATUS_EVENT = "refresh.status.event";
    private static final String STATUS_CACHE_NAME = "engagementStatusCache";

    private final Set<String> statesWithNeedForStatus = Set.of("ACTIVE", "TERMINATING");

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
        bus.publish(REFRESH_EVENT, "Refresh at start up");
    }

    public void refresh() {
        bus.publish(REFRESH_EVENT, "client invoked");
    }

    @ConsumeEvent(value = REFRESH_EVENT, blocking = true)
    @CacheInvalidateAll(cacheName = STATUS_CACHE_NAME)
    public void refresh(String message) {
        LOGGER.debug(message);
        List<Engagement> allEngagements = engagementApiRestClient.getAllEngagements(Set.of("ACTIVE", "TERMINATING"));

        LOGGER.debug("Engagements {}", allEngagements.size());
        allEngagements.forEach(e -> {
            LOGGER.debug("Engagement {}", e);
            if( statesWithNeedForStatus.contains(e.getState())) {
                bus.publish(REFRESH_STATUS_EVENT, e.getUuid());
            }
        });

        LOGGER.debug("End refresh");

    }

    @CacheInvalidate(cacheName = STATUS_CACHE_NAME)
    public void updateEngagementStatus(String engagementUuid) {
        getEngagementStatus(engagementUuid);
    }

    /**
     * Handling outside of cache so we get muffle the 404 failures since we don't currently
     * have access to the engagement state (could add that to the call)
     * @param engagementUuid engagement to refresh
     */
    @ConsumeEvent(value = REFRESH_STATUS_EVENT)
    public void refreshEngagement(String engagementUuid) {
        try {
            getEngagementStatus(engagementUuid);
        } catch (WebApplicationException ex) {
            if (ex.getResponse().getStatus() != 404) {
                throw ex;
            }
            LOGGER.error("Refresh status not found for {}", engagementUuid);
        }
    }
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
            LOGGER.error("No status file found for {} {} {}", e.getUuid(), projectIdOrPath, ex.getMessage());
            throw ex;
        }
    }
}
