package com.lordofthejars.huggingface.downloader;

public record Model(String org, String name, String branch) {

    public Model(String org, String name) {
        this(org, name, HuggingFaceConstants.BRANCH);
    }

}
