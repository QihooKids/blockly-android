package com.qihoo.ailab.repo.blockfile.i;

public interface LoadCallback<T> {

    void onFaied(int code);

    void onSucces(T data);
}
