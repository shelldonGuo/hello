package com.clhlock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by guoxuedong on 2015/6/25.
 */
public class CLHLockTest {

    public static void main(String[] args) throws InterruptedException {
        CLHLockTest CLHLockTest = new CLHLockTest();
        CLHLockTest.test(100, false);
    }

    public static int count = 0;
    CLHLock clhLock = new CLHLock();
    CLHLock1 clhLock1 = new CLHLock1();

    public void lock(int n) {
        CLHLock.CLHNode node = new CLHLock.CLHNode();
        clhLock.lock(node);
        count += n;
        clhLock.unlock(node);
    }

    public void lock1(int n) {
        clhLock1.lock();
        count += n;
        clhLock1.unlock();
    }

    public void add(int n) {
        lock(n);
        //lock1(n);
    }

    public void run_add(boolean debug) throws InterruptedException {
        count = 0;

        ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(20);
        int threadNum = 10000;
        List<Callable<Integer>> c = new ArrayList<Callable<Integer>>();
        for (int i = 0; i < threadNum; ++i) {
            c.add(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    add(1);
                    return count;
                }
            });
        }
        pool.invokeAll(c);
        pool.shutdown();

        System.out.println("count = " + count);
        if (count != threadNum) {
            throw new IllegalStateException("thread race condition");
        }
    }

    public void test(int n, boolean debug) throws InterruptedException {
        for (int i = 0; i < n; ++i) {
            run_add(debug);
        }
    }
}
