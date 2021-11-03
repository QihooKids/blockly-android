package com.qihoo.ailab.repo;

import android.content.Context;

import com.google.blockly.utils.BlockFileUtil;
import com.qihoo.ailab.model.AIRule;
import com.qihoo.ailab.model.AIRuleFileUrl;
import com.qihoo.ailab.repo.blockfile.BlockFileManager;
import com.qihoo.ailab.repo.i.IBlockFileDataRepo;
import com.qihoo.ailab.test.Test;
import com.qihoo.ailab.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class RuleDataHttp implements IBlockFileDataRepo {

    private final AIRule mRule;
    private final BlockFileManager mFileManager;
    private final Context mContext;

    public RuleDataHttp(Context context, AIRule mRule, BlockFileManager manager) {
        this.mRule = mRule;
        this.mFileManager = manager;
        this.mContext = context;
    }

    @Override
    public AIRuleFileUrl getRuleFileData() {
        if(Test.TEST){

            String toolbox = mFileManager.getToolboxPath();
            String workspace = mFileManager.getWorkspacePath();
            String blocks = mFileManager.getBlocksFilePath();
            String code = mFileManager.getCodeFilePath();
            InputStream inputStream = null;

            try {
                inputStream = mContext.getAssets().open("human_detect_toolbox.xml");
                FileUtils.copyToFileOrThrow(inputStream, new File(toolbox));

                inputStream = mContext.getAssets().open("human_detect_rule.xml");
                FileUtils.copyToFileOrThrow(inputStream, new File(workspace));

                inputStream = mContext.getAssets().open("human_detect_blocks.json");
                FileUtils.copyToFileOrThrow(inputStream, new File(blocks));

                inputStream = mContext.getAssets().open("human_detect_blocks_gen.js");
                FileUtils.copyToFileOrThrow(inputStream, new File(code));

                return new AIRuleFileUrl(mRule, BlockFileUtil.urlStorage(toolbox), BlockFileUtil.urlStorage(blocks),
                        BlockFileUtil.urlStorage(code), BlockFileUtil.urlStorage(workspace));
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(inputStream != null){
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        return null;
    }

}
