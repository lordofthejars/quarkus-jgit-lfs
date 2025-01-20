package com.lordofthejars.huggingface.downloader;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

@ApplicationScoped
public class HuggingFaceDownloaderHttp {

    @RestClient
    HubApi hubApi;

    public Path download(Path baseDirectory, Model model) throws IOException {

        Path modelDir = baseDirectory.resolve(model.name());
        Files.createDirectories(modelDir);

        RepoInfo searched = hubApi.search(model.org(), model.name());

        for (Sibling sibling : searched.siblings()) {

            File output = modelDir.resolve(sibling.rfilename()).toFile();
            InputStream inputStream = hubApi.download(model.org(), model.name(), sibling.rfilename());

            try (BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                 BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(output))) {

                bufferedInputStream.transferTo(bufferedOutputStream);
            }
        }

        return modelDir;
    }

}
