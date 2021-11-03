package com.qihoo.ailab.repo.i;

import com.qihoo.ailab.block.QBlock;

/**
 * The repository for get the data of blocks.
 */
public interface IRuleDataRepo extends IBlockFileDataRepo{


    /**
     * Get the block data of the rule.
     * @param id The rule id.
     * @return
     */
    QBlock getRuleBlockData();

}
