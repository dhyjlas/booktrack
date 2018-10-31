package com.kaworu.booktrack.utils.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadPoolUtils {
    private static ExecutorService threadPool ;

    /**
     * 获取线程池
     * @return
     */
    public static ExecutorService getThreadPool(){
        if (threadPool == null) {
            synchronized (ThreadPoolUtils.class){
                if(threadPool == null) {
                    threadPool = Executors.newFixedThreadPool(5);
                    return threadPool;
                }else{
                    return threadPool;
                }
            }
        } else {
            return  threadPool;
        }
    }

    /**
     * 执行线程操作
     * @param callable
     * @param <T>
     * @return
     */
    public static <T> Future<T> submit(Callable<T> callable) {
        return getThreadPool().submit(callable);
    }

    /**
     * 关闭线程池
     */
    public static void shutdown(){
        getThreadPool().shutdown();
    }
}
