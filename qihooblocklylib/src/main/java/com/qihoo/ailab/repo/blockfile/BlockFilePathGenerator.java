package com.qihoo.ailab.repo.blockfile;

import android.content.Context;

import com.qihoo.ailab.model.AIRule;
import com.qihoo.ailab.repo.blockfile.i.IPathGenerator;
import com.qihoo.ailab.util.MD5Utils;

import java.io.File;
import java.util.UUID;

public class BlockFilePathGenerator implements IPathGenerator {

    private final Context mContext;
    private final File mBase;
    private String PATH = "block_rules";

    public BlockFilePathGenerator(Context context, AIRule rule){
        this.mContext = context.getApplicationContext();
        this.PATH = PATH+"/"+rule.getId();
        this.mBase = mContext.getExternalCacheDir();
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
        File path = new File(mBase, builder.toString());
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
        return builder.toString();
    }

    @Override
    public File generateFilePath(String name) {
        return new File(pathDir(), fileName(name));
    }
}
