package com.qihoo.ailab.repo.blockfile;

import android.support.annotation.NonNull;

import com.qihoo.ailab.model.AIRule;
import com.qihoo.ailab.model.AIRuleFileUrl;
import com.qihoo.ailab.test.Test;



public class AIRuleLoader extends BaseBlockLoader<AIRule , AIRuleFileUrl> {


    public AIRuleLoader(@NonNull AIRule data) {
        super(data);
    }

    @Override
    public AIRuleFileUrl parse() {
        if(Test.TEST){

            return new AIRuleFileUrl(mData, "human_detect_toolbox.xml", "human_detect_blocks.json"
            ,"human_detect_blocks_gen.js", "human_detect_rule.xml");
        }

        return null;
    }

    @Override
    public void clearCache() {

    }

    @Override
    public void run() {

    }
}
