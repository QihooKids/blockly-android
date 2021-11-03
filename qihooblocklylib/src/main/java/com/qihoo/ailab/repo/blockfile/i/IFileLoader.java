package com.qihoo.ailab.repo.blockfile.i;

import android.support.annotation.IntDef;

public interface IFileLoader<D, T> {

    int STATE_IDLE = 0;
    int STATE_LOADING = 1;
    int STATE_FAILED = 2;
    int STATE_SUCCESS = 3;

    @IntDef({STATE_IDLE, STATE_LOADING, STATE_FAILED, STATE_SUCCESS})
    @interface LoadState{}

    int ERROR_CODE_NONE = 0;
    int ERROR_CODE_NET = -1;
    int ERROR_CODE_FILE_SYSTEM = -2;
    int ERROR_CODE_HTTP_SERVER = -3;
    int ERROR_CODE_CHECK_SUM = -4;
    int ERROR_CODE_UNKNOW = -5;

    @IntDef({ERROR_CODE_NONE, ERROR_CODE_NET, ERROR_CODE_FILE_SYSTEM, ERROR_CODE_HTTP_SERVER, ERROR_CODE_CHECK_SUM, ERROR_CODE_UNKNOW})
    @interface LoadEorrorCode{}

    /**
     * Load the block file.
     * @return true success.
     */
    T load();

    /**
     * Get the state
     * @return
     */
    @LoadState int getState();

    @LoadEorrorCode int getErrorCode();

    void clearCache();

    void enqueue(LoadCallback<T> callback);

}
