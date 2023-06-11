package com.crawlab.model.metadata;

import lombok.Data;

@Data
public abstract class MetaDataAbstractModel {
    private PageInfoModel pageInfo;
    private QueryComplexity queryComplexity;
}
