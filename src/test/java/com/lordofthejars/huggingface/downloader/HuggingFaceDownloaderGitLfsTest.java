package com.lordofthejars.huggingface.downloader;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class HuggingFaceDownloaderGitLfsTest {

    @Inject
    HuggingFaceDownloaderGitLfs huggingFaceDownloaderGitLfs;

    @Test
    public void testDownload(@TempDir Path tempDir) throws GitAPIException {

        Path modelDir = huggingFaceDownloaderGitLfs.download(tempDir,
                new Model("lordofthejars", "nuner-competitors"));

        assertThat(modelDir)
                .isDirectory()
                .isDirectoryContaining(path -> path.endsWith("model.onnx"))
                .isDirectoryContaining(path -> path.endsWith("config.json"));

    }

}
