package com.hello;

import java.util.concurrent.atomic.AtomicReference;

public class CLHSpinLock {

    private final ThreadLocal<Node> prev;
    private final ThreadLocal<Node> current;
    private final AtomicReference<Node> tail = new AtomicReference<Node>(new Node());

    public CLHSpinLock() {
        current = new ThreadLocal<Node>() {
            protected Node initialValue() {
                return new Node();
            }
        };

        prev = new ThreadLocal<Node>();
    }

    public void lock() {
        final Node node = current.get();
        node.locked = true;
        // 一个CAS操作即可将当前线程对应的节点加入到队列中，
        // 并且同时获得了前继节点的引用，然后就是等待前继释放锁
        Node pred = tail.getAndSet(node);
        prev.set(pred); // 记录前继节点，在unlock时回收为当前节点，再利用
        while (pred.locked) {// 进入自旋
        }
    }

    public void unlock() {
        final Node node = current.get();
        node.locked = false;
        // 这里使用前继节点作为当前节点，因为当前节点可能正在被后续节点监控如果
        // 如果使用原来的节点作为当前节点，当本线程再次请求lock（locked=true）
        // 同时后续节点还在监控本节点的锁定状态，则后续节点不能得到锁，本节点也会卡在请求lock上，导致死锁
        current.set(prev.get());
        // current.set(new Node()); // 如果不使用回收的前继节点，则需要重新生成一个节点。老的当前节点不能立刻用
    }

    private class Node {
        private volatile boolean locked = false;
    }
}
