package com.crawlab.model;

import lombok.Data;

@Data
public class ProjectModel {
    private String id;
    private String name;
    private String fullPath;
    private GroupModel group;
    private String visibility;
}
