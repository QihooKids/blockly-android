package com.google.blockly.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BlockFileUtil {
    private static final String FILE_BASE = "file://";
    private static final String ASSET_BASE = FILE_BASE+"/android_asset/";

    public static InputStream openFile(Context context, String path) throws IOException {
        AssetManager assets = context.getAssets();
        InputStream inputStream = null;
        if(!path.startsWith(FILE_BASE)){
            inputStream = assets.open(path);
        } else if(path.startsWith(ASSET_BASE)) {
            String absPath = path.substring(ASSET_BASE.length());
            inputStream = assets.open(absPath);
        } else if(path.startsWith(FILE_BASE)){
            String absPath = path.substring(FILE_BASE.length());
            inputStream = new FileInputStream(absPath);
        }
        return inputStream;
    }

    public static  String urlStorage(String file){
        return FILE_BASE + file;
    }

    public static  String urlAssetFile(String file){
        return ASSET_BASE + file;
    }
}
