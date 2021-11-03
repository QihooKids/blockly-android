package com.qihoo.ailab;

import android.text.TextUtils;

import com.google.blockly.model.Block;
import com.google.blockly.model.Field;
import com.google.blockly.model.FieldCheckbox;
import com.google.blockly.model.Input;
import com.qihoo.ailab.util.NUIBlockHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class UIJsonParser {

    private static final String UI_TYPE_DATA_LIST = "data_list";
    private static final String UI_TYPE_RULE_EDIT = "ailab_root";
    private static final String UI_TYPE_REGION = "region_set";
    private static final String UI_TYPE_TIME_DURATION = "time_duration_set";

    private static final List<String> UI_SPECIAL_TYPES = Arrays.asList(UI_TYPE_REGION,
            UI_TYPE_RULE_EDIT,
            UI_TYPE_TIME_DURATION);

    private static final String BLOCK_TYPE_ROOT = "ailab_root_block";


    public String parseTree(Block block) {
        JSONObject object = new JSONObject();
        try {
            object.put("id", block.getId());
            String type = getUIType(block);
            if (UI_TYPE_RULE_EDIT.equals(type)) {
                object.put("activity_type", getUIType(block));
                JSONArray array = new JSONArray();
                for (Input input : block.getInputs()) {
                    array.put(parseInput(input));
                }
                object.put("data", array);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    public String parseBlockCandidates(Block block, List<Block> candidates) {
        JSONObject object = new JSONObject();
        try {
            object.put("id", block.getId());
            String type = getUIType(block);
            if (BLOCK_TYPE_ROOT.equals(type)) {
                object.put("activity_type", getUIType(block));
                JSONArray array = new JSONArray();
                for (Block cd : candidates) {
                    JSONObject item = parseBlock(cd);
                    array.put(item);
                }
                object.put("data", array);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    private JSONObject parseInput(Input input) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("title", NUIBlockHelper.getText(TextType.KEY_TITLE, input));
        Block block = input.getConnectedBlock();
        if (block != null) {
            JSONArray array = new JSONArray();
            parseBlockNexts(block, array);
            object.put("list", array);
        }
        return object;
    }



    private void parseBlockNexts(Block block, JSONArray array) throws JSONException {
        if (block == null) {
            return;
        }

        array.put(parseBlock(block));
        parseBlockNexts(block.getNextBlock(), array);
    }

    private JSONObject parseBlock(Block block) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("block", block.getType());
        String title = NUIBlockHelper.getText(TextType.KEY_TITLE, block);
        StringBuilder viewType = new StringBuilder();
        if(!TextUtils.isEmpty(title)) {
            object.put(TextType.KEY_TITLE, title);
            viewType.append(TextType.KEY_TITLE);
        }

        String des = NUIBlockHelper.getText(TextType.KEY_DES, block);
        if(!TextUtils.isEmpty(des)){
            object.put(TextType.KEY_DES, des);
            viewType.append("_");
            viewType.append(TextType.KEY_DES);
        }

        Field field = block.getFieldByName(TextType.KEY_CHECK);
        if(field != null){
            object.put(TextType.KEY_CHECK, ((FieldCheckbox)field).isChecked());
            viewType.append("_");
            viewType.append(TextType.KEY_DES);
        }

        Input input = block.getOnlyValueInput();
        if(input != null){
            title = NUIBlockHelper.getText(TextType.KEY_TITLE, input.getConnectedBlock());
            if(!TextUtils.isEmpty(title)){
                object.put(TextType.KEY_SELECTED, title);
                viewType.append("_");
                viewType.append(TextType.KEY_SELECTED);
            }
            viewType.append("_");
            viewType.append(TextType.KEY_SELECT);

            String[] checks = input.getConnection().getConnectionChecks();
            if(checks != null && checks.length>0){
                String check = checks[0];
                object.put(TextType.KEY_NEXT, getNextType(check));
            }

        }
        object.put("view_type", viewType);
        object.put("id", block.getId());
        return object;
    }

    private String getNextType(String type) {
        if(UI_SPECIAL_TYPES.contains(type)){
            return type;
        }
        return UI_TYPE_DATA_LIST;
    }

    private String getUIType(Block block) {
        String type = block.getType();
        if(type.equals(BLOCK_TYPE_ROOT)){
            type = UI_TYPE_RULE_EDIT;
        }
        return getNextType(type);
    }
}
