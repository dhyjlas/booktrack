package com.kaworu.booktrack.utils.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

class TestCallable implements Callable<String> {

    private String message;

    public TestCallable(String message) {
        this.message = message;
    }

    @Override
    public String call() throws Exception {
        Thread.sleep(300);
        System.out.println(String.format("打印消息%s", message));
        return "OK";
    }

    /**
     * 测试类
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception{
        long start = System.currentTimeMillis();
        List<Future> futureList = new ArrayList();
        // 发送10次消息
        for (int i = 0; i < 10; i++) {
            try {
                Future<String> messageFuture = ThreadPoolUtils.submit(new TestCallable(String.format("这是第{%s}条消息", i)));
                futureList.add(messageFuture);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (Future<String> message : futureList) {
            String messageData = message.get();
        }
        System.out.println(String.format("共计耗时{%s}毫秒", System.currentTimeMillis() - start));
        ThreadPoolUtils.shutdown();
    }
}