package com.redhat.labs.lodestar.engagement.status.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subsystem {

    private String name;
    private String status;
    private String state;
    private String info;
    private String updated;
    private List<Message> messages;
    private List<Map<String, Object>> accessUrls;

}
