package com.crawlab.model;

import com.crawlab.model.metadata.MetaDataAbstractModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProjectsModel extends MetaDataAbstractModel {
    private List<ProjectModel> projects;
}
