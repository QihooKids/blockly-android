package com.qihoo.ailab.repo;

import android.content.Context;

import com.google.blockly.utils.BlockFileUtil;
import com.qihoo.ailab.model.AIRule;
import com.qihoo.ailab.model.AIRuleFileUrl;
import com.qihoo.ailab.repo.blockfile.BlockFileManager;
import com.qihoo.ailab.repo.i.IBlockFileDataRepo;

import java.io.File;

public class RuleDataCache implements IBlockFileDataRepo {

    private final AIRule mRule;
    private final BlockFileManager mFileManager;

    public RuleDataCache(Context context, AIRule mRule, BlockFileManager generator) {
        this.mRule = mRule;
        this.mFileManager = generator;
    }

    public AIRuleFileUrl getRuleFileData(String id) {

        return null;
    }

    @Override
    public AIRuleFileUrl getRuleFileData() {
        String toolbox = mFileManager.getToolboxPath();
        String workspace = mFileManager.getWorkspacePath();
        String blocks = mFileManager.getBlocksFilePath();
        String code = mFileManager.getCodeFilePath();

        if(isExists(toolbox) && isExists(workspace) && isExists(blocks) && isExists(code)){
            return new AIRuleFileUrl(mRule,
                    BlockFileUtil.urlStorage(toolbox),
                    BlockFileUtil.urlStorage(blocks),
                    BlockFileUtil.urlStorage(code),
                    BlockFileUtil.urlStorage(workspace));
        }
        return null;
    }

    private boolean isExists(String toolbox) {
        return new File(toolbox).exists();
    }
}
