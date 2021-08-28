package com.redhat.labs.lodestar.engagement.status.model;

import com.redhat.labs.lodestar.engagement.status.exception.EngagementStatusException;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
class GitlabFileTest {

    @Test
    void testNullFilePathEncoding() {
        final GitlabFile file = GitlabFile.builder().build();

        assertThrows(EngagementStatusException.class, () -> {
            file.encodeFileAttributes();
        });


        assertThrows(EngagementStatusException.class, () -> {
            file.decodeFileAttributes();
        });
    }

    @Test
    void testNoContentNoError() {
        final GitlabFile file = GitlabFile.builder().filePath("path").build();

        assertDoesNotThrow(() -> {
            file.encodeFileAttributes();
        });

        assertDoesNotThrow(() -> {
            file.decodeFileAttributes();
        });
    }
}
