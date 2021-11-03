package com.qihoo.ailab.repo;

import android.content.Context;

import com.google.blockly.model.Block;
import com.google.blockly.model.BlocklyCategory;
import com.qihoo.ailab.model.AIRule;
import com.qihoo.ailab.model.AIRuleFileUrl;
import com.qihoo.ailab.block.QBlock;
import com.qihoo.ailab.repo.blockfile.BlockFileManager;
import com.qihoo.ailab.repo.blockfile.BlockFilePathGenerator;
import com.qihoo.ailab.repo.i.IRuleDataRepo;
import com.qihoo.ailab.util.NUIBlockHelper;

import java.io.File;
import java.util.Arrays;

public class RuleDataRepo implements IRuleDataRepo {

    private final Context mContext;
    private final AIRule mRule;
    private final RuleDataCache mRepoCache;
    private final RuleDataHttp mRepoHttp;
    private final BlockFileManager mFileManager;

    public RuleDataRepo(Context context, AIRule rule){
        this.mContext = context;
        this.mRule = rule;
        BlockFilePathGenerator pathGenerator = new BlockFilePathGenerator(context, mRule);
        mFileManager = new BlockFileManager(context, pathGenerator);
        mRepoCache = new RuleDataCache(context, mRule, mFileManager);
        mRepoHttp = new RuleDataHttp(context, mRule, mFileManager);
    }

    @Override
    public QBlock getRuleBlockData() {
        AIRuleFileUrl files = getRuleFileData();
        NUIBlockHelper helper = new NUIBlockHelper(mContext, Arrays.asList(files.getPathBlockJson()),
                Arrays.asList(files.getPathCodeJs()));
        Block root = helper.loadWorkspace(files.getPathRule());
        BlocklyCategory category = helper.loadToolbox(files.getPathToolbox());
        QBlock qblock = new QBlock(files, root, category, helper);
        return qblock;
    }

    @Override
    public AIRuleFileUrl getRuleFileData() {
        AIRuleFileUrl data = mRepoCache.getRuleFileData();
        if(data == null) {
            data = mRepoHttp.getRuleFileData();
        }
        return data;
    }

    public String saveLua(String generatedCode) {
        File file = mFileManager.saveLua(generatedCode);
        if(file != null){
            return file.getAbsolutePath();
        }
        return "";
    }

    public BlockFileManager fileManger() {
        return mFileManager;
    }
}
