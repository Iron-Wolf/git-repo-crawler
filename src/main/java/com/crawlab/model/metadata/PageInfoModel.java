package com.crawlab.model.metadata;

import lombok.Data;

@Data
public class PageInfoModel {
    private String endCursor;
    private Boolean hasNextPage;
}
