package com.qihoo.ailab.ui;

import com.qihoo.ailab.R;

public class BlockRuleActivity extends BaseActivity{

    @Override
    protected void setData() {

    }

    @Override
    protected void setHeader() {
        getTitleView().setText(getString(R.string.rule_edit));
    }

    @Override
    protected void initView() {

    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_rule_edit;
    }
}
