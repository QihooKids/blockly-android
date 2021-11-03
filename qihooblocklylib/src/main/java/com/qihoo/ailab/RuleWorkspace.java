package com.qihoo.ailab;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import com.google.blockly.model.Block;
import com.google.blockly.model.BlocklyCategory;
import com.google.blockly.model.Connection;
import com.google.blockly.model.Field;
import com.google.blockly.model.FieldCheckbox;
import com.google.blockly.model.FieldNumber;
import com.google.blockly.model.Input;
import com.qihoo.ailab.block.QBlock;
import com.qihoo.ailab.util.NUIBlockHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class RuleWorkspace {

    private final Context mContext;
    private Block mRoot;
    private BlocklyCategory mToolbox;
    private BlockUIStack mBlockUIStack = new BlockUIStack();
    private NUIBlockHelper mHelper;

    public RuleWorkspace(Context context) {
        this.mContext = context;
    }

    public void clear() {
        mRoot = null;
        if(mToolbox != null) {
            mToolbox.clear();
        }
        mToolbox = null;
    }

    public void addRootBlock(QBlock block) {
        mRoot = block.getBlock();
        mToolbox = block.getToolbox();
        mBlockUIStack.put(mRoot, getAllBlocksThisUI(mRoot));
        mHelper = block.getHelper();
    }

    public List<Block> getAllBlocksThisUI(Block block) {
        List<Block> blocks = new ArrayList<>();
        if(block != null) {
            //Connected blocks
            List<Input> inputs = block.getInputs();
            if (inputs != null) {
                for (Input input : inputs) {
                    Block next = input.getConnectedBlock();
                    while (next != null) {
                        blocks.add(next);
                        next = next.getNextBlock();
                    }
                }
            }
        }
        return blocks;
    }

    public Pair<Block, List<Block>> onSelectNext(String id, String type) throws BlockConnectException {
        if (mBlockUIStack.size() > 0) {// Root block ui
            Block block = mBlockUIStack.peekUIBlockById(id, type);
            if (block != null) {
                Input input = block.getOnlyValueInput();
                if (input != null) {
                    String[] checks = null;
                    if (input.getConnection() != null) {
                        checks = input.getConnection().getConnectionChecks();
                    }
                    if (checks != null && checks.length > 0) {
                        String check = checks[0];
                        List<Block> candidates = getCandidateBlocks(check);
                        if (candidates.size() > 0) {
                            return new Pair<>(block, candidates);
                        }
                    }
                }
            }
        }
        connectCandidatesToRoot(id, type);
        return null;
    }

    private void connectCandidatesToRoot(String id, String type) throws BlockConnectException {
        try {
            if (mBlockUIStack.size() <= 1) {
                return;
            }

            Block candidate = mBlockUIStack.peekUIBlockById(id, type);
            Block select = mBlockUIStack.peekBlock();
            select.getOnlyValueInput().getConnection().disconnect();
            candidate.getOutputConnection().disconnect();
            candidate = mHelper.obtainBlockByType(select.getType());
            select.getOnlyValueInput().getConnection().connect(candidate.getOutputConnection());
            mBlockUIStack.pop();
            connectCandidatesToRoot(select.getId(), select.getType());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BlockConnectException();
        }
    }

    private List<Block> getCandidateBlocks(String check) {
        List<Block> list = new ArrayList<>();
        List<Block> candidate = new ArrayList<>();
        mToolbox.getAllBlocksRecursive(list);
        for (Block b : list) {
            Connection out = b.getOutputConnection();
            if(out != null && out.getConnectionChecks()!=null && out.getConnectionChecks().length>0) {
                if (check.equals(out.getConnectionChecks()[0])) {
                    candidate.add(b);
                }
            }
        }
        return candidate;
    }

    private boolean checkMatch(String[] source, String[] target) {
        for (int i = 0; i < source.length; i++) {
            for (int j = 0; j < target.length; j++) {
                if (TextUtils.equals(source[i], target[j])) {
                    return true;
                }
            }
        }
        return false;
    }

    public Block getRootBlock() {
        return mRoot;
    }

    public void onBackPressed() {
        if (mBlockUIStack.size() > 1) {
            mBlockUIStack.pop();
        }
    }

    public void onBlockChecked(String blockId, String blockType, boolean check) {
        Block block = mBlockUIStack.peekUIBlockById(blockId, blockType);
        if (block != null) {
            Field c = block.getFieldByName("check");
            if (c != null && c instanceof FieldCheckbox) {
                FieldCheckbox checkbox = (FieldCheckbox) c;
                checkbox.setChecked(check);
            }
        }
    }

    public String[] getAllBlockTypes() {
        Set<String> types = new HashSet<>();
        addAllBlockTypes(mRoot, types);
        return types.toArray(new String[types.size()]);
    }

    public void addAllBlockTypes(Block block, Set<String> outList) {
        if(block == null){
            return;
        }
        outList.add(block.getType());
        List<Input> inputs = block.getInputs();
        int inputCount = inputs.size();
        for (int i = 0; i < inputCount; ++i) {
            Input input = inputs.get(i);
            Block connectedBlock = input.getConnectedBlock();
            if (connectedBlock != null) {
                addAllBlockTypes(connectedBlock, outList);
            }
        }

        Block next = block.getNextBlock();
        if (next != null) {
            addAllBlockTypes(next, outList);
        }
    }

    public int onBlockConnectRegion(String blockId, String blockType, String json) {
        Block block = mBlockUIStack.peekUIBlockById(blockId, blockType);
        if(block == null || block.getOnlyValueInput() == null || block.getOnlyValueInput().getConnection()==null){
            return -1;
        }
        Connection connection = block.getOnlyValueInput().getConnection();
        try {
            JSONArray array = new JSONArray(json);
            String type = getOnlyValueCheckType(block);
            if (type == null) {
                return -1;
            }
            Block root = null;
            Block xy = null;
            Block last = null;
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.optJSONObject(i);
                if (object != null) {
                    xy = mHelper.obtainBlockByType(type);
                    if(root == null){
                        root = xy;
                    }
                    double x = object.optDouble("x");
                    double y = object.optDouble("y");
                    NUIBlockHelper.setNumber(xy, "x", x);
                    NUIBlockHelper.setNumber(xy, "y", y);
                    if (last != null) {
                        Connection conn = last.getOnlyValueInput().getConnection();
                        if (conn != null) {
                            conn.connect(xy.getOutputConnection());
                        }
                    }
                    last = xy;
                }
            }
            if (root != null && connection != null) {
                connection.disconnect();
                connection.connect(root.getOutputConnection());
                return 0;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }


    private String getOnlyValueCheckType(Block block) {
        Input input = block.getOnlyValueInput();
        if (input != null) {
            Connection con = input.getConnection();
            if (con != null) {
                String[] checks = con.getConnectionChecks();
                if (checks != null && checks.length > 0) {
                    return checks[0];
                }
            }
        }
        return null;
    }

    public int onTimeDuration(String blockId, String blockType, String json) {
        Block block = mBlockUIStack.peekUIBlockById(blockId, blockType);
        try {
            JSONObject jsonObject = new JSONObject(json);
            Iterator<String> it = jsonObject.keys();
            if(it == null){
                return -1;
            }
            while (it.hasNext()){
                String key = it.next();
                NUIBlockHelper.setNumber(block, key, jsonObject.optDouble(key));
            }
            return 0;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int onBlockSelectedTitle(String blockId, String blockType, String title) {
        Block block = mBlockUIStack.peekUIBlockById(blockId, blockType);
        List<Block> list = getCandidateBlocks(getOnlyValueCheckType(block));
        for (Block b : list) {
            if(title.equals(NUIBlockHelper.getText(TextType.KEY_TITLE, b))){
                Input input = block.getOnlyValueInput();
                if(input != null && input.getConnection() != null){
                    Connection conn = input.getConnection();
                    conn.disconnect();
                    conn.connect(mHelper.obtainBlockByType(b.getType()).getOutputConnection());
                    return 0;
                }
            }
        }
        return -1;
    }
}
