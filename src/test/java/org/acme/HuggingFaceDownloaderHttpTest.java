package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class HuggingFaceDownloaderHttpTest {

    @Inject
    HuggingFaceDownloaderHttp huggingFaceDownloaderHttp;

    @Test
    public void testDownload(@TempDir Path tempDir) throws IOException {

        Path modelDir = huggingFaceDownloaderHttp.download(tempDir,
                new Model("lordofthejars", "nuner-competitors"));

        assertThat(modelDir)
                .isDirectory()
                .isDirectoryContaining(path -> path.endsWith("model.onnx"))
                .isDirectoryContaining(path -> path.endsWith("config.json"));

    }


}
