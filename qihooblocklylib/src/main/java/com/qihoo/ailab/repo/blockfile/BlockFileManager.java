package com.qihoo.ailab.repo.blockfile;

import android.content.Context;

import com.qihoo.ailab.model.AIRuleFileData;

public class BlockFileManager {

    private final Context mContext;

    public BlockFileManager(Context context){
        this.mContext = context.getApplicationContext();
    }

    /**
     * Get the id of this rule.
     * @param id The rule id.
     * @return The valid paths of block files.
     */
    public AIRuleFileData getValidFileData(String id){
        AIRuleFileData data = new AIRuleFileData();
        return data;
    }
}
