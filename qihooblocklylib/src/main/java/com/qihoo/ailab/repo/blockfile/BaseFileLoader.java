package com.qihoo.ailab.repo.blockfile;

import android.support.annotation.NonNull;

import com.qihoo.ailab.ThreadPoolFactory;
import com.qihoo.ailab.model.BlockUrlFileData;
import com.qihoo.ailab.repo.blockfile.i.IFileLoader;

public abstract class BaseFileLoader implements IFileLoader, Runnable {

    private final BlockUrlFileData mData;

    private int mState;
    private int mErrorCode;

    public BaseFileLoader(@NonNull BlockUrlFileData fileData){
        this.mData = fileData;
    }

    @Override
    public boolean load() {
        run();
        return mErrorCode == IFileLoader.ERROR_CODE_NONE;
    }

    @Override
    public int getState() {
        return mState;
    }

    @Override
    public int getErrorCode() {
        return mErrorCode;
    }

    @Override
    public void clearCache() {

    }

    @Override
    public void enqueue() {
        ThreadPoolFactory.executorService().execute(this);
    }

    @Override
    public void run() {

    }
}
