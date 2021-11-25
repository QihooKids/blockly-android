package com.qihoo.ailab.repo.blockfile;

import android.content.Context;

import com.qihoo.ailab.model.AIRule;
import com.qihoo.ailab.model.AIRuleFileUrl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class BlockFileManager {

    private static final String TOOL_BOX_FILE = "toolbox.xml";
    private static final String WORKSPACE_FILE = "workspace.xml";
    private static final String BLOCKS_FILE = "blocks.json";
    private static final String CODE_FILE = "code.js";

    private static final String BLOCKS_DIR = "blocks";
    private static final String CODE_DIR = "code";
    private static final String LUA_FILE_NAME = "script.lua";

    private final Context mContext;
    private final BlockFilePathGenerator mPath;

    public BlockFileManager(Context context, BlockFilePathGenerator pathGenerator){
        this.mContext = context.getApplicationContext();
        this.mPath = pathGenerator;
    }

    /**
     * Get the id of this rule.
     * @param id The rule id.
     * @return The valid paths of block files.
     */
    public AIRuleFileUrl getValidFileData(String id){
        AIRule rule = null;
        AIRuleFileUrl data = null;
        return data;
    }

    public String getToolboxPath() {
        return  mPath.generateFilePath(TOOL_BOX_FILE).getAbsolutePath();
    }

    public String getWorkspacePath() {
        return mPath.generateFilePath(WORKSPACE_FILE).getAbsolutePath();
    }

    public String getBlocksFilePath() {
        return mPath.generateFilePath(BLOCKS_FILE).getAbsolutePath();
    }

    public String getCodeFilePath() {
        return mPath.generateFilePath(CODE_FILE).getAbsolutePath();
    }

    public File saveLua(String generatedCode) {
        FileWriter writer = null;
        File file = mPath.generateFilePath(LUA_FILE_NAME);
        try {
            writer = new FileWriter(file);
            writer.write(generatedCode);
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(writer != null){
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
