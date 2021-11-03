package com.qihoo.ailab;

import android.content.Context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WorkspaceFactory {


    private final Context mContext;

    private Map<String, RuleWorkspace> mWorkspaces = new ConcurrentHashMap<>();

    public WorkspaceFactory(Context context) {
        this.mContext = context;
    }

    public RuleWorkspace getWorkspace(String ruleId) {
        RuleWorkspace workspace = mWorkspaces.get(ruleId);
        if(workspace == null){
            workspace = new RuleWorkspace(mContext);
            mWorkspaces.put(ruleId, workspace);
        }
        return workspace;
    }

    public void clearWorkspace(String ruleId){
        RuleWorkspace workspace = mWorkspaces.remove(ruleId);
        if(workspace != null){
            workspace.clear();
        }
    }

    public void clear(){
        for (String id : mWorkspaces.keySet()) {
            clearWorkspace(id);
        }
        mWorkspaces.clear();
    }
}
