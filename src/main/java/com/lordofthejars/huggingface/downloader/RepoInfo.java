package com.lordofthejars.huggingface.downloader;

import java.util.List;

public record RepoInfo(String id, String modelId, String author, List<Sibling> siblings) {
}
