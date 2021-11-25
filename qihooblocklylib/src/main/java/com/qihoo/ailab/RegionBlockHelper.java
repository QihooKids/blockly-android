package com.qihoo.ailab;

import com.google.blockly.model.Block;
import com.google.blockly.model.Connection;
import com.google.blockly.model.FieldNumber;
import com.google.blockly.model.Input;
import com.qihoo.ailab.util.NUIBlockHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RegionBlockHelper {

    private final RuleWorkspace mWorkspace;
    private static final String INPUT_NAME_REGION = "next_region";
    private static final String INPUT_NAME_XYS = "xy_arrays";

    private static final String FIELD_NAME_X = "x";
    private static final String FIELD_NAME_Y = "y";

    RegionBlockHelper(RuleWorkspace workspace){
        this.mWorkspace = workspace;
    }

    /**
     * Obtain the Region block arrays and connect to the region_set block.
     * @param blockId The region_set block id.
     * @param blockType The region block type.
     * @param json The xy arrays.
     * @return
     */
    public int onBlockConnectRegion(String blockId, String blockType, String json) {
        Block block = mWorkspace.getBlockById(blockId);//mBlockUIStack.peekUIBlockById(blockId, blockType);
        if(block == null || block.getOnlyValueInput() == null || block.getOnlyValueInput().getConnection()==null){
            return -1;
        }
        Connection connection = block.getOnlyValueInput().getConnection();
        try {
            JSONArray array = new JSONArray(json);
            String type = mWorkspace.getOnlyValueCheckType(block);
            if (type == null) {
                return -1;
            }
            Block region;
            for (int i = 0; i < array.length(); i++) {
                JSONArray xys = array.optJSONArray(i);
                if (xys != null) {
                    region = mWorkspace.obtainBlockByType(type);
                    if (connection != null) {
                        connection.disconnect();
                        connection.connect(region.getOutputConnection());
                        onRegionConnectXYs(region.getId(), xys);
                        connection = region.getInputByName(INPUT_NAME_REGION).getConnection();
                    }
                }
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Obtain the Region block arrays and connect to the region_set block.
     * @param blockId The region_set block id.
     * @param array The xy arrays.
     * @return
     */
    public int onRegionConnectXYs(String blockId, JSONArray array) {
        Block block = mWorkspace.getBlockById(blockId);
        Input input = block.getInputByName(INPUT_NAME_XYS);
        if(block == null || input == null || input.getConnection()==null){
            return -1;
        }
        Connection connection = input.getConnection();
        try {
            String type = mWorkspace.getInputCheckType(input);
            if (type == null) {
                return -1;
            }
            Block xy;
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.optJSONObject(i);
                if (object != null) {
                    xy = mWorkspace.obtainBlockByType(type);
                    double x = object.optDouble(FIELD_NAME_X);
                    double y = object.optDouble(FIELD_NAME_Y);
                    NUIBlockHelper.setNumber(xy, FIELD_NAME_X, x);
                    NUIBlockHelper.setNumber(xy, FIELD_NAME_Y, y);
                    if (connection != null) {
                        connection.disconnect();
                        connection.connect(xy.getOutputConnection());
                        connection = xy.getOnlyValueInput().getConnection();
                    }
                }
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void parse2Json(Block region, JSONArray array) throws JSONException {
        while (region != null) {
            JSONArray regionArray = new JSONArray();
            Block xy = region.getInputByName(INPUT_NAME_XYS).getConnectedBlock();
            while (xy != null) {
                regionArray.put(parseBlockXY(xy));
                xy = xy.getOnlyValueInput().getConnectedBlock();
            }
            array.put(regionArray);
            region = region.getInputByName(INPUT_NAME_REGION).getConnectedBlock();
        }
    }

    private JSONObject parseBlockXY(Block xy) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        FieldNumber xF = (FieldNumber) xy.getFieldByName(FIELD_NAME_X);
        jsonObject.put(FIELD_NAME_X, xF.getValue());
        xF = (FieldNumber) xy.getFieldByName(FIELD_NAME_Y);
        jsonObject.put(FIELD_NAME_Y, xF.getValue());
        return jsonObject;
    }
}
