package com.redhat.labs.lodestar.engagement.status.model;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.validation.constraints.NotBlank;

import com.redhat.labs.lodestar.engagement.status.exception.EngagementStatusException;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GitlabFile {

    @NotBlank private String filePath;
    @NotBlank private String fileName;
    @NotBlank private String ref;
    @NotBlank private String content;
    @NotBlank private String lastCommitId;
    @Builder.Default
    private String encoding = "base64";
    private Long size;
    private String branch;
    private String authorEmail;
    private String authorName;
    private String commitMessage;

    public void encodeFileAttributes() {
        this.filePath = urlEncode(this.filePath);

        // encode contents
        if (null != content) {
            byte[] encodedContents = Base64.getEncoder().encode(this.content.getBytes());
            this.content = new String(encodedContents, StandardCharsets.UTF_8);
        }
    }

    public void decodeFileAttributes() {

        this.filePath = urlDecode(this.filePath);

        // decode contents
        if (null != content) {
            byte[] decodedContents = Base64.getDecoder().decode(this.content);
            this.content = new String(decodedContents, StandardCharsets.UTF_8);

        }
    }

    private String urlEncode(String src) {
        if(src == null) {
            throw new EngagementStatusException("URL encoding error. Input is null");
        }

        try {
            return URLEncoder.encode(src, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new EngagementStatusException(String.format("failed to encode src %s", src));
        }
    }

    private String urlDecode(String src) {
        if(src == null) {
            throw new EngagementStatusException("URL decoding error. Input is null");
        }

        try {
            return URLDecoder.decode(src, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new EngagementStatusException(String.format("failed to decode src %s", src));
        }
    }
}
