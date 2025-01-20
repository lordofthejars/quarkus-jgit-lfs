package com.lordofthejars.huggingface.downloader;

import java.nio.file.Path;

public class GitLfsInstallation {


    public boolean check(Path repo) {
        int statusCode = new ExecWrapper().execute("git lfs", repo);

         return statusCode == 0;
    }

    public boolean install(Path repo) {
        int statusCode = new ExecWrapper().execute("git lfs install", repo);
        return statusCode == 0;
    }

}
