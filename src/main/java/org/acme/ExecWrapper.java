package org.acme;

import java.io.IOException;
import java.nio.file.Path;

public class ExecWrapper {

    public int execute(String command, Path repoPath) {

        ProcessBuilder processBuilder = new ProcessBuilder()
                .command(command.split(" "))
                .directory(repoPath.toAbsolutePath().toFile());

        try {
            Process process = processBuilder.start();
            process.waitFor();
            return process.exitValue();
        } catch (IOException | InterruptedException e) {
            return -1;
        }

    }

}
