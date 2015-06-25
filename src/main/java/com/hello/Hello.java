package com.hello;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by guoxuedong on 2015/6/25.
 */
public class Hello {

    public static void main(String[] args) {
        System.out.println("start");
        Hello hello = new Hello();
        hello.test(100, false);
        System.out.println("done");
    }


    public static int count = 0;
    //public ReentrantLock lock = new ReentrantLock();
    CLHSpinLock lock = new CLHSpinLock();

    public void add(int n) {
        lock.lock();
        count += n;
        lock.unlock();
    }

    public void run_add(boolean debug) throws InterruptedException {
        count = 0;

        ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
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
        if (debug) {
            System.out.println("invoke all");
        }
        pool.invokeAll(c);
        if (debug) {
            System.out.println("invoke all done");
        }
        pool.shutdown();

        System.out.println("count = " + count);
        if (count != threadNum) {
            throw new RuntimeException("thread race condition");
        }
    }

    public void test(int n, boolean debug) {
        for (int i = 0; i < n; ++i) {
            try {
                run_add(debug);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
