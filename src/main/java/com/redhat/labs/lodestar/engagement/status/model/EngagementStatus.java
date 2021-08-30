package com.redhat.labs.lodestar.engagement.status.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EngagementStatus {
    String engagementUuid;
    @JsonProperty("overall_status")
    private String totallyBogus;
    private List<Message> messages;
    private List<Subsystem> subsystems;
}
