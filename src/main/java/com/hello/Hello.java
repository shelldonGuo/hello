package com.hello;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by guoxuedong on 2015/6/25.
 */
public class Hello {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("start");

        ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10000; ++i) {
            pool.submit(new Runnable() {
                @Override
                public void run() {
                    add(1);
                }
            });
        }
        pool.awaitTermination(1, TimeUnit.SECONDS);
        pool.shutdown();

        System.out.println("count = " + count);
        System.out.println("done");
    }

    public static int count = 0;
    static  CLHLock lock = new CLHLock();

    public static void add(int n) {
        CLHLock.CLHNode node = new CLHLock.CLHNode();
        lock.lock(node);
        count += n;
        lock.unlock(node);
    }
}
