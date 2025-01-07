package org.acme;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.nio.file.Path;

@ApplicationScoped
public class HuggingFaceDownloaderGitLfs {

    public Path download(Path baseDirectory, Model model) throws GitAPIException {

        final String modelName = model.name();
        Path modelDir = baseDirectory.resolve(modelName);

        GitLfsInstallation gitLfsInstallation = new GitLfsInstallation();

        boolean lfsInstalled = gitLfsInstallation.check(baseDirectory);

        if (lfsInstalled) {
            gitLfsInstallation.install(baseDirectory);

            Git master = Git.cloneRepository()
                    .setURI(HuggingFaceConstants.resolveHost(model.org(), model.name()))
                    .setDirectory(modelDir.toAbsolutePath().toFile())
                    .setBranch(model.branch())
                    .call();

            master.close();
        } else {
            // Fallback to pure java solution?¿
        }

        return modelDir;
    }
}
