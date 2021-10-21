package com.qihoo.ailab.repo.blockfile;

import android.content.Context;

import com.qihoo.ailab.repo.blockfile.i.IPathGenerator;
import com.qihoo.ailab.util.MD5Utils;

import java.io.File;
import java.util.UUID;

public class BlockFilePathGenerator implements IPathGenerator {

    private final Context mContext;
    private String PATH = "block_rules";

    public BlockFilePathGenerator(Context context){
        this.mContext = context.getApplicationContext();
    }

    @Override
    public String pathDir(String ... args) {
        StringBuilder builder = new StringBuilder(PATH);
        if(args != null) {
            for (String arg : args) {
                builder.append(File.separator);
                builder.append(arg);
            }
        }
        File path = new File(mContext.getFilesDir(), builder.toString());
        if(!path.exists()){
            path.mkdirs();
        }
        return path.getAbsolutePath();
    }

    @Override
    public String fileName(String ... args) {
        if(args == null){
            return UUID.randomUUID().toString();
        }
        StringBuilder builder = new StringBuilder();
        for (String arg : args){
            builder.append(arg);
        }
        return MD5Utils.encode(builder.toString());
    }
}
