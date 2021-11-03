package com.qihoo.ailab;

import android.support.annotation.NonNull;

import com.qihoo.ailab.util.L;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class ThreadPoolFactory {
    private static final String TAG = ThreadPoolFactory.class.getSimpleName();

    private static final int CORE_THREAD_SIZE = 10;
    private static final int MAX_THREAD_SIZE = 20;
    private static final int MAX_RUNNABLE_WAIT = 50;
    private static final ArrayBlockingQueue<Runnable> mQueue = new ArrayBlockingQueue<>(MAX_THREAD_SIZE);
    private static final RejectedExecutionHandler mRejectHandler = new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            L.e(TAG, "Rejected onUIThread:" + r + "; current cache size:" + mQueue.size() + "; too many onUIThread to be running!");
        }
    };

    private static Executor executorService;

    public static void setExecutor(@NonNull Executor executor){
        executorService = executor;
    }

    public static synchronized Executor executorService() {
        if (executorService == null) {
            executorService = new ThreadPoolExecutor(CORE_THREAD_SIZE, MAX_THREAD_SIZE, 0, TimeUnit.SECONDS,
                    mQueue, Executors.defaultThreadFactory(), mRejectHandler);
        }
        return executorService;
    }
}
