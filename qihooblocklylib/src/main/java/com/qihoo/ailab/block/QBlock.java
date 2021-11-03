package com.qihoo.ailab.block;

import com.google.blockly.model.Block;
import com.google.blockly.model.BlocklyCategory;
import com.qihoo.ailab.model.AIModel;
import com.qihoo.ailab.model.AIRuleFileUrl;
import com.qihoo.ailab.model.LuaScriptData;
import com.qihoo.ailab.util.NUIBlockHelper;

import java.util.ArrayList;

public class QBlock{

    private final AIRuleFileUrl files;

    private Block rootBlock;

    private BlocklyCategory toolbox;

    private ArrayList<LuaScriptData> luaScripts;

    private ArrayList<AIModel> aiModels;

    private NUIBlockHelper mHelper;

    public QBlock(AIRuleFileUrl files, Block root, BlocklyCategory category, NUIBlockHelper helper) {
        this.rootBlock = root;
        this.toolbox = category;
        this.mHelper = helper;
        this.files = files;
    }

    public Block getBlock() {
        return rootBlock;
    }

    public void setBlock(Block block) {
        this.rootBlock = block;
    }

    public BlocklyCategory getToolbox() {
        return toolbox;
    }

    public void setToolbox(BlocklyCategory toolbox) {
        this.toolbox = toolbox;
    }

    public ArrayList<LuaScriptData> getLuaScripts() {
        return luaScripts;
    }

    public void setLuaScripts(ArrayList<LuaScriptData> luaScripts) {
        this.luaScripts = luaScripts;
    }

    public ArrayList<AIModel> getAiModels() {
        return aiModels;
    }

    public void setAiModels(ArrayList<AIModel> aiModels) {
        this.aiModels = aiModels;
    }

    public void setHelper(NUIBlockHelper helper) {
        this.mHelper = helper;
    }

    public NUIBlockHelper getHelper() {
        return mHelper;
    }


    public AIRuleFileUrl getFiles() {
        return files;
    }

    public String dump() {
        StringBuilder builder = new StringBuilder();
        builder.append("files:"+files);
        builder.append("rootBlock:"+rootBlock);
        builder.append("toolbox:"+toolbox);
        return builder.toString();
    }

    @Override
    public String toString() {
        return "QBlock{" +
                "files=" + files +
                ", rootBlock=" + rootBlock +
                ", toolbox=" + toolbox +
                ", luaScripts=" + luaScripts +
                ", aiModels=" + aiModels +
                ", mHelper=" + mHelper +
                '}';
    }
}
