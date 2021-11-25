package com.qihoo.ailab.util;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.blockly.android.codegen.CodeGenerationRequest;
import com.google.blockly.android.codegen.CodeGeneratorManager;
import com.google.blockly.android.codegen.LanguageDefinition;
import com.google.blockly.android.control.BlocklyController;
import com.google.blockly.model.Block;
import com.google.blockly.model.BlockFactory;
import com.google.blockly.model.BlockTemplate;
import com.google.blockly.model.BlocklyCategory;
import com.google.blockly.model.BlocklyEvent;
import com.google.blockly.model.BlocklySerializerException;
import com.google.blockly.model.Field;
import com.google.blockly.model.FieldInput;
import com.google.blockly.model.FieldLabel;
import com.google.blockly.model.FieldNumber;
import com.google.blockly.model.IOOptions;
import com.google.blockly.model.Input;
import com.google.blockly.utils.BlockFileUtil;
import com.google.blockly.utils.BlockLoadingException;
import com.google.blockly.utils.BlocklyXmlHelper;
import com.google.blockly.utils.StringOutputStream;
import com.qihoo.ailab.TextType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NUIBlockHelper {

    private static final String TAG = NUIBlockHelper.class.getSimpleName();

    private final BlockFactory mBlockFactory;

    private static final String FILE_BASE = "file://";
    private static final String ASSET_BASE = FILE_BASE+"/android_asset/";
    private final Context mContext;
    private static final String SNAPSHOT_BUNDLE_KEY = "com.google.blockly.snapshot";
    private static final String SERIALIZED_WORKSPACE_KEY = "SERIALIZED_WORKSPACE";
    private final BlocklyController mController;
    private final CodeGeneratorManager mCodeGeneratorManager;
    private final LanguageDefinition mCodeGeneratorLanguage;
    private final List<String> mJsonDefinitionsPaths;
    private final List<String> mGeneratorsJsPaths;

    /**
     *
     * @param context
     * @param jsonDefinitions  The list of file uris, example {file:///data/data/com.test.demo/file/block.json, file:///android_asset/block.json}
     *                         File uri start with file://
     *                         Asset File uri start with file:///android_asset/
     */
    public NUIBlockHelper(Context context, List<String> jsonDefinitions, List<String> generatorsJsPaths){
        mContext = context;
        mController = new BlocklyController.Builder(context)
                .build();
        mBlockFactory = mController.getBlockFactory();
        mCodeGeneratorManager = new CodeGeneratorManager(context);
        initBlockDefinitions(jsonDefinitions);
        mJsonDefinitionsPaths = jsonDefinitions;
        mCodeGeneratorLanguage = LanguageDefinition.LUA_LANGUAGE_DEFINITION;
        mGeneratorsJsPaths = new ArrayList<>(generatorsJsPaths);
    }

    public void onResume(){
        mCodeGeneratorManager.onResume();
    }

    public void onPause(){
        mCodeGeneratorManager.onPause();
    }

    /**
     * Generate the lua code.
     * @param codeGenerationCallback
     */
    public void requestCodeGeneration(Block root,
            CodeGenerationRequest.CodeGeneratorCallback codeGenerationCallback) {

        final StringOutputStream serialized = new StringOutputStream();
        try {
//            mController.getWorkspace().serializeToXml(serialized);
            BlocklyXmlHelper.writeToXml(Arrays.asList(root), serialized, IOOptions.WRITE_ALL_DATA);
        } catch (BlocklySerializerException e) {
            // Not using a string resource because no non-developer should see this.
            String msg = "Failed to serialize workspace during code generation.";
            Log.wtf(TAG, msg, e);
            throw new IllegalStateException(msg, e);
        }

        mCodeGeneratorManager.requestCodeGeneration(
                new CodeGenerationRequest(
                        serialized.toString(),
                        codeGenerationCallback,
                        mCodeGeneratorLanguage,
                        mJsonDefinitionsPaths,
                        mGeneratorsJsPaths));
        try {
            serialized.close();
        } catch (IOException e) {
            // Ignore error on close().
        }
    }

    /**
     * Obtain a new block from the
     * @param type
     * @return
     */
    public Block obtainBlockByType(String type){
        try {
            return mBlockFactory.obtainBlockFrom(new BlockTemplate(type));
        } catch (BlockLoadingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Save the workspace to xml.
     * @param root The root block.
     * @param path The file path to save.
     */
    public void saveWorkspace(Block root, String path){
        ArrayList<Block> list = new ArrayList<>();
        list.add(root);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(path);
            BlocklyXmlHelper.writeToXml(list, outputStream, IOOptions.WRITE_ALL_DATA);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(outputStream != null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public BlocklyCategory loadToolbox(String path){
        InputStream inputStream = null;
        try {
            inputStream = BlockFileUtil.openFile(mContext, path);
            return BlocklyXmlHelper.loadToolboxFromXml(inputStream, mBlockFactory, BlocklyEvent.WORKSPACE_ID_TOOLBOX);
        } catch (BlockLoadingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Load the root block for rules config.Only one root block for the business.
     * @param path The workspace xml file path.
     * @return The root block.
     */
    public Block loadWorkspace(String path){
        InputStream inputStream = null;
        try {
            inputStream = BlockFileUtil.openFile(mContext, path);
            List<Block> newBlocks = BlocklyXmlHelper.loadFromXml(inputStream, mBlockFactory);
            if(newBlocks.size() > 0) {
                return newBlocks.get(0);
            }
        } catch (FileNotFoundException | BlockLoadingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    /**
     * Saves a snapshot of current workspace contents to a temporary cache file, and saves the
     * filename to the instance state bundle.
     * @param mSavedInstanceState The output Bundle to write the state to.
     * @return True if all values were written successfully to the bundle. Otherwise, false with
     *         errors written to log.
     */
    public boolean onSaveSnapshot(Bundle mSavedInstanceState, Block root) {
        boolean success;
        Bundle blocklyState = new Bundle();

        // First attempt to save the workspace to a file.
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ArrayList<Block> blocks = new ArrayList<>();
            blocks.add(root);
            BlocklyXmlHelper.writeToXml(blocks, out, IOOptions.WRITE_ALL_DATA);
            blocklyState.putByteArray(SERIALIZED_WORKSPACE_KEY, out.toByteArray());
            success = true;
        } catch (BlocklySerializerException e) {
            Log.e(TAG, "Error serializing workspace.", e);
            success = false;
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                // Ignore.
            }
        }

        // TODO(#58): Save the rest of the state.

        mSavedInstanceState.putBundle(SNAPSHOT_BUNDLE_KEY, blocklyState);
        return success;
    }

    /**
     * Loads a Workspace state from an Android {@link Bundle}, previous saved in
     *
     * @param savedInstanceState
     * @return RootBlock if a Blockly state was found and successfully loaded into the Controller.
     *         Otherwise, null.
     */
    public Block onRestoreSnapshot(@Nullable Bundle savedInstanceState) {

        Bundle blocklyState = (savedInstanceState == null) ? null :
                savedInstanceState.getBundle(SNAPSHOT_BUNDLE_KEY);
        if (blocklyState == null) {
            return  null;
        }
        byte[] bytes = blocklyState.getByteArray(SERIALIZED_WORKSPACE_KEY);
        if (bytes == null) {
            // Ignore all other workspace variables.
            return null;
        }

        boolean success = true;
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        try {
            List<Block> newBlocks = BlocklyXmlHelper.loadFromXml(in, mBlockFactory);
            return newBlocks.get(0);
        } catch (BlockLoadingException e) {
            Log.e(TAG, "Failed to load snapshot from Bundle.", e);
            success = false;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                // Ignore.
            }
        }
        return null;
    }



    private void initBlockDefinitions(List<String> jsonDefinitions) {
        mBlockFactory.clear();
        String assetPath = null;
        InputStream inputStream = null;
        try {
            if (jsonDefinitions != null) {
                for (String path : jsonDefinitions) {
                    assetPath = path;
                    inputStream = BlockFileUtil.openFile(mContext, path);
                    if(inputStream != null){
                        mBlockFactory.addJsonDefinitions(inputStream);
                    }
                }
            }
        } catch (IOException | BlockLoadingException e) {
            throw new IllegalStateException(
                    "Failed to load block definition asset file: " + assetPath, e);
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void setNumber(Block block, String key, double num) {
        if(block != null) {
            Field field = block.getFieldByName(key);
            if(field instanceof FieldNumber){
                FieldNumber number = (FieldNumber) field;
                number.setValue(num);
                L.d(TAG, "FieldNumber value:"+number.getValue());
            }
        }
    }

    public static String getText(@TextType String key, Block block) {
        if (block != null) {
            Field field = block.getFieldByName(key);
            if (field != null) {
                if (field instanceof FieldInput) {
                    return ((FieldInput) field).getText();
                } else if (field instanceof FieldLabel) {
                    return ((FieldLabel) field).getText();
                }
            }
        }
        return "";
    }

    public static String getText(@TextType String key, Input input) {
        if (input != null) {
            List<Field> fields = input.getFields();
            if (fields != null) {
                for (Field field : fields) {
                    if (key.equals(field.getName())) {
                        if (field instanceof FieldInput) {
                            return ((FieldInput) field).getText();
                        } else if (field instanceof FieldLabel) {
                            return ((FieldLabel) field).getText();
                        }
                    }
                }
            }
        }
        return "";
    }

}
