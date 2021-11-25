package com.qihoo.ailab;

import android.text.TextUtils;

import com.google.blockly.model.Block;
import com.google.blockly.model.Field;
import com.google.blockly.model.FieldCheckbox;
import com.google.blockly.model.FieldNumber;
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
    private final RuleWorkspace mWorkspace;

    public UIJsonParser(RuleWorkspace workspace) {
        this.mWorkspace = workspace;
    }

    public String parseBlockCandidates(Block block, List<Block> candidates) {
        JSONObject object = new JSONObject();
        try {
            object = parseBlock(block);
            object.put("activity_type", getUIType(block.getType()));
            JSONArray array = new JSONArray();
            for (Block cd : candidates) {
                JSONObject item = parseBlock(cd);
                array.put(item);
            }
            object.put("data", array);
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

    public JSONObject parseBlock(Block block) throws JSONException {

        //Start parse block.
        JSONObject object = new JSONObject();
        object.put("block", block.getType()); //Add type
        String title = NUIBlockHelper.getText(TextType.KEY_TITLE, block);
        object.put("title", title);
        StringBuilder viewType = new StringBuilder(); //Build the block view type to show as an list item.
        object.put("activity_type", getUIType(block.getType())); // Add the activity type for show the block edit datas in an activity.

        String des = NUIBlockHelper.getText(TextType.KEY_DES, block); //Add describe for item view.
        if (!TextUtils.isEmpty(des)) {    // Build the view type to add describe.
            object.put(TextType.KEY_DES, des);
            if (viewType.length() > 0) {
                viewType.append("_");
            }
            viewType.append(TextType.KEY_DES);
        }

        Field field = block.getFieldByName(TextType.KEY_CHECK); //To check if the block has checkbox.
        if (field != null) {
            object.put(TextType.KEY_CHECK, ((FieldCheckbox) field).isChecked());
            if (viewType.length() > 0)
                viewType.append("_");
            viewType.append(TextType.KEY_DES);
        }

        Input input = block.getOnlyValueInput();
        if (input != null) { // Add select view type and selected des for example:灵敏度设置   高 >
            title = NUIBlockHelper.getText(TextType.KEY_TITLE, input.getConnectedBlock());
            if (!TextUtils.isEmpty(title)) {
                object.put(TextType.KEY_SELECTED, title);
                if (viewType.length() > 0)
                    viewType.append("_");
                viewType.append(TextType.KEY_SELECTED);
            }
            if (viewType.length() > 0)
                viewType.append("_");
            viewType.append(TextType.KEY_SELECT);

            String[] checks = input.getConnection().getConnectionChecks();
            if (checks != null && checks.length > 0) {
                String check = checks[0];
                object.put(TextType.KEY_NEXT, getUIType(check));
            }

        }
        object.put("view_type", viewType);
        object.put("id", block.getId());

        JSONArray array = new JSONArray();
        if (UI_TYPE_REGION.equals(block.getType())) {
            Block region = block.getOnlyValueInput().getConnectedBlock();
            mWorkspace.getRegionHelper().parse2Json(region, array);
            if(array.length()>0) {
                object.put("data", array);
            }
        } else {
            array = parseStatementInputs(block, object);
            if(array != null){
                object.put("data", array);
            }
        }

        return object;
    }

    private JSONArray parseStatementInputs(Block block, JSONObject object) {
        List<Input> inputs = block.getInputs();
        try {
            if (inputs != null && inputs.size() > 0) {
                JSONArray array = new JSONArray();
                for (Input input : inputs) {
                    if(input instanceof Input.InputStatement) {
                        array.put(parseInput(input));
                    }
                }
                if(array.length()>0) {
                    object.put("data", array);
                    return array;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getUIType(String type) {
        if (type.equals(BLOCK_TYPE_ROOT)) {
            return UI_TYPE_RULE_EDIT;
        }
        if (isSpeciaType(type)) {
            return type;
        }
        return UI_TYPE_DATA_LIST;
    }

    public boolean isSpeciaType(String type) {
        return UI_SPECIAL_TYPES.contains(type);
    }

}
