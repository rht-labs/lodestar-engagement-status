package com.redhat.labs.lodestar.engagement.status.mock;

import com.fasterxml.jackson.core.type.TypeReference;
import com.redhat.labs.lodestar.engagement.status.model.GitlabFile;
import com.redhat.labs.lodestar.engagement.status.utils.JsonMarshaller;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class ResourceLoader {
    private static JsonMarshaller json = new JsonMarshaller();

    public static String load(String resourceName) {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName)) {
            return IOUtils.toString(is, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String loadStatusFile(String resourceName) {
        String content = load(resourceName);
        GitlabFile gitlabFile = GitlabFile.builder().filePath("status.json").content(content).build();
        gitlabFile.encodeFileAttributes();

        return json.toJson(gitlabFile);
    }
}
