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
    protected Block mRoot;
    private BlocklyCategory mToolbox;
    private BlockUIStack mBlockUIStack = new BlockUIStack();
    protected NUIBlockHelper mHelper;
    private RegionBlockHelper mRegionHelper;

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
        mRegionHelper = new RegionBlockHelper(this);
    }

    public Block getBlockById(String id){
        return getBlockById(mRoot, id);
    }

    protected Block getBlockById(Block rootBlock, String id){
        if(TextUtils.isEmpty(id)){
            return null;
        }

        if(id.equals(rootBlock.getId())){
            return rootBlock;
        }
        List<Input> inputList = rootBlock.getInputs();
        int inputCount = inputList.size();
        for (int i = 0; i < inputCount; ++i) {
            Input input = inputList.get(i);
            Block connectedBlock = input.getConnectedBlock();
            if (connectedBlock != null) {
                if(id.equals(connectedBlock.getId())){
                    return connectedBlock;
                } else {
                    Block next = getBlockById(connectedBlock, id);
                    if(next != null){
                        return next;
                    }
                }
            }
        }

        Connection nextConnection = rootBlock.getNextConnection();
        if (nextConnection != null) {
            Block next = nextConnection.getTargetBlock();
            if (next != null) {
                if(id.equals(next.getId())){
                    return next;
                } else {
                    next = getBlockById(next, id);
                    if(next != null){
                        return next;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Block only support for one block contains one value input or multi statementInput.
     * @param block
     * @return
     */
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
                return new Pair<>(block, getCandidateBlocks(block));
            }
        }
        connectCandidatesToRoot(id, type);
        return null;
    }

    protected List<Block> getCandidateBlocks(Block block){
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
                        return candidates;
                    }
                }
            }
        }
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



    public String getOnlyValueCheckType(Block block) {
        Input input = block.getOnlyValueInput();
        return getInputCheckType(input);
    }

    public String getInputCheckType(Input input) {
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
        Block block = getBlockById(blockId);//mBlockUIStack.peekUIBlockById(blockId, blockType);
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

    public Block getBlock(String id, String type) {
        if(id == null && type == null){
            return mRoot;
        }
        if (mBlockUIStack.size() > 0) {// Root block ui
            Block block = mBlockUIStack.peekUIBlockById(id, type);
            return block;
        }
        return null;
    }

    /**
     * Get the block input data to select.
     * @param id
     * @return The blocks for select.
     */
    public Pair<Block, List<Block>> getSelectBlocks(String id) {
        if(!TextUtils.isEmpty(id)) {
            Block block = getBlockById(id);
            if (block != null) {
                return new Pair<>(block, getCandidateBlocks(block));
            }
        }
        return null;
    }

    /**
     * Connect a new child to parent block.
     * @param blockId The parent block id.
     * @param newType The child block type.
     * @return
     */
    public Block onBlockSelected(String blockId, String newType) {
        Block block = getBlockById(blockId);
        if(block != null && !TextUtils.isEmpty(newType)){
            Block newBlock = mHelper.obtainBlockByType(newType);
            if(newBlock != null && newBlock.getOutputConnection()!= null) {
                newBlock.getOutputConnection().disconnect();
                Connection parentConnect = block.getOnlyValueInput().getConnection();
                if(parentConnect != null) {
                    parentConnect.disconnect();
                    parentConnect.connect(newBlock.getOutputConnection());
                    return newBlock;
                }
            }
        }
        return null;
    }

    public Block obtainBlockByType(String type) {
        return mHelper.obtainBlockByType(type);
    }

    public int onBlockConnectRegion(String blockId, String blockType, String json) {
        return mRegionHelper.onBlockConnectRegion(blockId, blockType, json);
    }

    public RegionBlockHelper getRegionHelper() {
        return mRegionHelper;
    }
}
