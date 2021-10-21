package com.qihoo.ailab.repo.i;

import com.qihoo.ailab.model.AIRuleFileData;
import com.qihoo.ailab.proxy.QBlock;

/**
 * The repository for get the data of blocks.
 */
public interface IRuleDataRepo extends IBlockFileDataRepo{

    QBlock getRuleBlockData();

}
