package com.crawlab.model;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class ProjectRepoModel {
    private String id;
    private String name;
    private String fullPath;
    private String visibility;
    private RepositoryModel repository;

    public List<NodeModel> getNodes() {
        if (repository == null || repository.getTree() == null)
            return Collections.emptyList();
        return repository.getTree().getBlobs().getNodes();
    }
}
