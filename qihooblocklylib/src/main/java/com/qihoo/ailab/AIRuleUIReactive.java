package com.qihoo.ailab;

import android.content.Context;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import android.util.Pair;

import com.google.blockly.android.codegen.CodeGenerationRequest;
import com.google.blockly.model.Block;
import com.qihoo.ailab.block.QBlock;
import com.qihoo.ailab.model.AIRule;
import com.qihoo.ailab.model.AIRuleFileUrl;
import com.qihoo.ailab.repo.RuleDataRepo;
import com.qihoo.ailab.util.L;
import com.qihoo.ailab.util.NUIBlockHelper;

import java.util.List;

/**
 * The manager for the api of blocks.
 */
public class AIRuleUIReactive {

    private static final String TAG = "RCT";

    private final Context mContext;
    private final WorkspaceFactory mWM;
    private final UIJsonParser mUIJsonParser;
    private final RuleDataRepo mRepo;
    private final AIRule mRule;
    private final String mRuleId;
    private RuleWorkspace mWorkspace;
    private NUIBlockHelper mHelper;
    private AIRuleFileUrl mFileData;

    public AIRuleUIReactive(Context context, AIRule aiRule){
        this.mContext = context;
        mWM = new WorkspaceFactory(context);
        mUIJsonParser = new UIJsonParser();
        mRepo = new RuleDataRepo(context, aiRule);
        this.mRule = aiRule;
        this.mRuleId = aiRule.getId();
    }


    @WorkerThread
    public void load(){
        QBlock block = mRepo.getRuleBlockData();
        L.d(TAG, "qblock="+(block != null ? block.toString():"null"));
        mWorkspace = mWM.getWorkspace(mRuleId);
        mWorkspace.addRootBlock(block);
        mHelper = block.getHelper();
        mFileData = block.getFiles();
        onResume();
    }

    /**
     * Get the json data of rule block data for the UI layout.
     * @see<a href="http://wiki.360iot.qihoo.net/pages/viewpage.action?pageId=44049689">Flutter UI 和 native数据交互</>
     * @return The json data for ui layout.
     */
    public String getRuleSettingsJson(){
        try {
            return mUIJsonParser.parseTree(mWorkspace.getRootBlock());
        }catch (Exception e){
            e.printStackTrace();
            reload();
        }
        return null;
    }

    /**
     * This is the core method for flutter ui reactive.
     * The next ui message will be returned when the block clicked.
     * @param blockId The block id when selected.
     * @return The next ui message for show.
     */
    public String onBlockNext(String blockId, String blockType){
        try {
            Pair<Block, List<Block>> pair = mWorkspace.onSelectNext(blockId, blockType);
            if(pair != null) {
                mUIJsonParser.parseBlockCandidates(pair.first, pair.second);
            }
        } catch (BlockConnectException e) {
            e.printStackTrace();
            reload();
        }
        return "";
    }

    private void reload() {
        mWM.clearWorkspace(mRuleId);
        mWorkspace.clear();
        load();
    }

    /**
     * When the check state of this changed,need call this method to update the block data.
     * @param blockId The block id.
     * @param check The check state.
     */
    public void onClickCheck(String blockId, String blockType,boolean check){
        mWorkspace.onBlockChecked(blockId, blockType, check);
    }

    /**
     * This method need to be called when the activity back pressed,associated with the onBlockNext.
     * This is important!
     */
    public void onBackPressed(){
        mWorkspace.onBackPressed();
    }



    /**
     * Generate the lua script file.
     * @return The file absolute path of the file.
     */
    public synchronized void getLua(final CodeGenerationRequest.CodeGeneratorCallback callback){
        try {
            mHelper.requestCodeGeneration(mWorkspace.getRootBlock(), new CodeGenerationRequest.CodeGeneratorCallback() {
                @Override
                public void onFinishCodeGeneration(String generatedCode) {
                    if (callback != null) {
                        L.d(TAG, generatedCode);
                        callback.onFinishCodeGeneration(generatedCode);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            reload();
        }
    }
    public void getLuaFilePath(final CodeGenerationRequest.CodeGeneratorCallback callback){
        getLua(new CodeGenerationRequest.CodeGeneratorCallback() {
            @Override
            public void onFinishCodeGeneration(String generatedCode) {
                String path;
                if(!TextUtils.isEmpty(generatedCode)) {
                    path = mRepo.saveLua(generatedCode);
                } else {
                    path = null;
                }
                if (callback != null) {
                    callback.onFinishCodeGeneration(path);
                }
            }
        });
    }

    /**
     * Save the workspace to the xml file.
     * @return The absolute file path.
     */
    public String saveWorkspaceXml(){
        if(mFileData != null){
            mHelper.saveWorkspace(mWorkspace.getRootBlock(), mRepo.fileManger().getWorkspacePath());
            return mRepo.fileManger().getWorkspacePath();
        }
        return "";
    }

    /**
     * Get all of the block types in the workspace.
     * @return The types string,  separator is ','  : type_a, type_b, type_c
     */
    public String getWorkBlockTypes(){
        String[] list = mWorkspace.getAllBlockTypes();
        StringBuilder builder = new StringBuilder();
        for (String str : list) {
            builder.append(str);
            builder.append(",");
        }
        if(builder.length()>0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }

    /**
     *
     * When the region selected will callback this method.
     * [{"x":0.11231,"y":0.1},{"x":0.1232131,"y":0.1},{"x":0.1123123,"y":0.1}],[{"x":0.1,"y":0.1},{"x":0.1,"y":0.1},{"x":0.1,"y":0.1}]
     * @param json
     * @return 0 success,otherwise failed.
     */
    public int onRegionData(String blockId, String blockType, String json){
        return mWorkspace.onBlockConnectRegion(blockId, blockType, json);
    }

    /**
     * When the available time selected will callback.
     * @param blockId
     * @param json   {"hour_s":5, "min_s":30, "hour_e":7, "min_e":30}
     * @return 0 success, otherwise failed.
     */
    public int onTimeDuration(String blockId, String blockType, String json){
        return mWorkspace.onTimeDuration(blockId, blockType, json);
    }

    public int onBlockSelectedTitle(String blockId, String blockType, String title){
        return mWorkspace.onBlockSelectedTitle(blockId, blockType, title);
    }

    public void onResume(){
        mHelper.onResume();
    }

    public void onPause(){
        mHelper.onPause();
    }

}
