package com.redhat.labs.lodestar.engagement.status.mock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class ExternalApiWireMock implements QuarkusTestResourceLifecycleManager {

    private WireMockServer wireMockServer; 
    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        
        String body = ResourceLoader.load("seed-engagement.json");
        
        stubFor(get(urlEqualTo("/api/v1/engagements?includeCommits=false&includeStatus=false&pagination=false")).willReturn(aResponse()
                .withHeader("Content-Type",  "application/json")
                .withBody(body)
                ));

        stubFor(getEngagement("uuid"));
        stubFor(getEngagement("no-status-file"));
        stubFor(getEngagement("bad-thing"));

        body = ResourceLoader.loadStatusFile("gitlab-status-uuid.json");
        
        stubFor(get(urlEqualTo("/api/v4/projects/13065/repository/files/status.json?ref=master")).willReturn(aResponse()
                .withHeader("Content-Type",  "application/json")
                .withBody(body)
                ));

        stubFor(get(urlEqualTo("/api/v4/projects/20962/repository/files/status.json?ref=master")).willReturn(aResponse()
                .withHeader("Content-Type",  "application/json")
                .withStatus(404)
                .withBody("{\"msg\": \" 404 No file found \"}")
                ));
        
        stubFor(get(urlEqualTo("/api/v4/projects/666/repository/files/status.json?ref=master")).willReturn(aResponse()
                .withStatus(500)
                .withHeader("Content-Type",  "application/json")
                .withBody("{\"msg\": \" 500 Something bad happened\"}")
                ));
        
        Map<String, String> config = new HashMap<>();
        config.put("gitlab.api/mp-rest/url", wireMockServer.baseUrl());
        config.put("engagement.api/mp-rest/url", wireMockServer.baseUrl());
        
        return config;
    }

    private MappingBuilder getEngagement(String uuid) {
        String body = ResourceLoader.load("engagement-" + uuid + ".json");

        return get(urlEqualTo("/api/v1/engagements/uuid/" + uuid)).willReturn(aResponse()
                .withHeader("Content-Type",  "application/json")
                .withBody(body));
    }

    @Override
    public void stop() {
        if(null != wireMockServer) {
           wireMockServer.stop();
        }
        
    }


}
