package com.qihoo.ailab.proxy;

import com.google.blockly.model.Block;
import com.google.blockly.model.BlocklyCategory;

public class QBlock{
    private Block rootBlock;
    private BlocklyCategory toolbox;

    public Block getBlock() {
        return rootBlock;
    }

    public void setBlock(Block block) {
        this.rootBlock = block;
    }
}
