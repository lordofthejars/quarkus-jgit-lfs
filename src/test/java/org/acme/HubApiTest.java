package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class HubApiTest {

    @RestClient
    HubApi hubApi;

    @Test
    public void testListFiles() {

        RepoInfo searched = hubApi.search("lordofthejars", "nuner-competitors");
        assertThat(searched.siblings()).hasSize(9);

    }

    @Test
    public void testDownloadFile(@TempDir Path tempDir) throws IOException {
        InputStream inputStream = hubApi.download("lordofthejars", "nuner-competitors", "config.json");
        Path outputFile = tempDir.resolve("config.json");
        Files.copy(inputStream, outputFile);

        assertThat(outputFile)
                .isRegularFile()
                .isNotEmptyFile();
    }

}
