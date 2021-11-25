package com.qihoo.ailab.repo.blockfile;

import android.support.annotation.NonNull;

import com.qihoo.ailab.ThreadPoolFactory;
import com.qihoo.ailab.repo.blockfile.i.IFileLoader;
import com.qihoo.ailab.repo.blockfile.i.LoadCallback;

public abstract class BaseBlockLoader<D, T> implements IFileLoader<D, T>, Runnable {

    protected final D mData;
    protected int mState;
    protected int mErrorCode;

    public BaseBlockLoader(@NonNull D data){
        this.mData = data;
    }

    @Override
    public T load() {
        mState = STATE_LOADING;
        try {
            run();
        }catch (Throwable throwable){
            throwable.printStackTrace();
            mErrorCode = ERROR_CODE_UNKNOW;
        }
        if(mErrorCode == 0){
            mState = STATE_SUCCESS;
        } else {
            mState = STATE_FAILED;
        }
        return parse();
    }

    @Override
    public int getState() {
        return mState;
    }

    @Override
    public int getErrorCode() {
        return mErrorCode;
    }

    public abstract T parse();

    @Override
    public void enqueue(final LoadCallback<T> callback) {
        ThreadPoolFactory.executorService().execute(new Runnable() {
            @Override
            public void run() {
                load();
                if(mErrorCode == 0){
                    callback.onSucces(parse());
                } else {
                    callback.onFaied(mErrorCode);
                }
            }
        });
    }
}
