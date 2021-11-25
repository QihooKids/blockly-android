package com.qihoo.ailab;

import android.text.TextUtils;
import android.util.Pair;

import com.google.blockly.model.Block;

import java.util.List;
import java.util.Stack;

public class BlockUIStack {

    private Stack<Pair<Block, List<Block>>> mStack = new Stack<>();

    public void put(Block mRoot, List<Block> allBlocksThisUI) {
        mStack.push(new Pair<>(mRoot, allBlocksThisUI));
    }

    public List<Block> peekUIBlocks(){
        return mStack.peek().second;
    }

    public Block peekBlock(){
        return mStack.peek().first;
    }

    public int size() {
        return mStack.size();
    }

    public Block peekUIBlockById(String id, String type) {
        List<Block> list = peekUIBlocks();
        if(list != null){
            for (Block block : list) {
                if(!TextUtils.isEmpty(id)) {
                    if (block.getId().equals(id)) {
                        return block;
                    }
                } else if(!TextUtils.isEmpty(type)){
                    if(type.equals(block.getType())){
                        return block;
                    }
                }
            }
        }
        return null;
    }

    public void pop() {
        mStack.pop();
    }
}
