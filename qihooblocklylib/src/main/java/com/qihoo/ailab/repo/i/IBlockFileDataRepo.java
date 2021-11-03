package com.qihoo.ailab.repo.i;

import com.qihoo.ailab.model.AIRuleFileUrl;

public interface IBlockFileDataRepo {

    /**
     * Get the rule files from the cache.
     * @return
     */
    AIRuleFileUrl getRuleFileData();

}
