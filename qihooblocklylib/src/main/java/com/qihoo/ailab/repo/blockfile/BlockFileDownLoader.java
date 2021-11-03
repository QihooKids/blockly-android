package com.qihoo.ailab.repo.blockfile;

import android.support.annotation.NonNull;

import com.qihoo.ailab.model.FileData;


public class BlockFileDownLoader extends BaseBlockLoader<FileData, String> {


    public BlockFileDownLoader(@NonNull FileData data) {
        super(data);
    }

    @Override
    public void run() {

    }

    @Override
    public String parse() {
        return null;
    }

    @Override
    public void clearCache() {

    }
}
