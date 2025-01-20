package com.lordofthejars.huggingface.downloader;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.attributes.Attribute;
import org.eclipse.jgit.attributes.AttributesNode;
import org.eclipse.jgit.attributes.AttributesRule;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lfs.Lfs;
import org.eclipse.jgit.lfs.LfsPointer;
import org.eclipse.jgit.lfs.SmudgeFilter;
import org.eclipse.jgit.lib.Repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

@ApplicationScoped
public class HuffingFaceDownloaderJavaLfs {

    public Path download(Path baseDirectory, Model model) throws IOException, GitAPIException {

        final String modelName = model.name();
        Path modelDir = baseDirectory.resolve(modelName);

        if (Files.exists(modelDir)) {

            // Delete and clone again or pull?

        } else {

            Git master = Git.cloneRepository()
                    .setURI(HuggingFaceConstants.resolveHost(model.org(), model.name()))
                    .setDirectory(modelDir.toAbsolutePath().toFile())
                    .setBranch(model.branch())
                    .call();

            master.close();

            // I was not able to make jgit run smudge filter automatically, even though I registered them and I followed the jgit tests
            // So I execute manually.
            downloadLfs(modelDir);
        }

        return modelDir;

    }

    private void downloadLfs(Path modelDir) throws IOException {
        Repository repository = new FileRepository(new File(modelDir.toAbsolutePath().toFile(), ".git"));
        AttributesNode attributes = new AttributesNode();
        attributes.parse(new FileInputStream(
                new File(repository.getWorkTree(), ".gitattributes"))
        );

        List<AttributesRule> rules = attributes.getRules();

        List<String> lfsFiles = rules.stream()
                .filter(ar -> isLfsFilter(ar.getAttributes()))
                .map(AttributesRule::getPattern)
                .toList();

        final Lfs lfs = new Lfs(repository);

        final List<LfsPointer> lfsPointers = lfsFiles.stream()
                .map(modelDir::resolve)
                .map(pointerLoc -> {
                    try {
                        return LfsPointer.parseLfsPointer(Files.newInputStream(pointerLoc));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        List<Path> gitObjects = lfsPointers.stream()
                .map(p -> {
                    try {
                        return SmudgeFilter.downloadLfsResource(lfs, repository, p);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .flatMap(Collection::stream)
                .toList();


        for (int i = 0; i < lfsFiles.size(); i++) {
            final Path pointerLocation = modelDir.resolve(lfsFiles.get(i));
            Files.copy(gitObjects.get(i), new FileOutputStream(pointerLocation.toFile(), false));
        }
    }


    private boolean isLfsFilter(List<Attribute> attributes) {
        for (Attribute attribute : attributes) {
            if ("filter".equals(attribute.getKey()) &&
                    "lfs".equals(attribute.getValue())) {
                return true;
            }
        }

        return false;
    }

}
