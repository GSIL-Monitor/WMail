//package com.gongw.mailcore;
//
//import java.util.ArrayDeque;
//import java.util.Deque;
//import java.util.Iterator;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.LinkedBlockingDeque;
//import java.util.concurrent.RejectedExecutionHandler;
//import java.util.concurrent.SynchronousQueue;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
//import okhttp3.internal.Util;
//
///**
// * Created by gongw on 2018/9/4.
// */
//
//public class MailRequestDispatcher {
//
//    private int maxRequests = 32;
//    private int maxRequestsPerHost = 5;
//    /** Executes calls. Created lazily. */
//    private ExecutorService executorService;
//
//    /** Ready async calls in the order they'll be run. */
//    private final Deque<RealCall.AsyncCall> readyAsyncCalls = new ArrayDeque<>();
//
//    /** Running asynchronous calls. Includes canceled calls that haven't finished yet. */
//    private final Deque<RealCall.AsyncCall> runningAsyncCalls = new ArrayDeque<>();
//
//    /** Running synchronous calls. Includes canceled calls that haven't finished yet. */
//    private final Deque<RealCall> runningSyncCalls = new ArrayDeque<>();
//
//    public MailRequestDispatcher(){}
//
//    public MailRequestDispatcher(ExecutorService executorService){
//        this.executorService = executorService;
//    }
//
//    public synchronized ExecutorService executorService() {
//        if (executorService == null) {
//            executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
//                    new SynchronousQueue<Runnable>(), Util.threadFactory("Mail Dispatcher", false));
//        }
//        return executorService;
//    }
//
//    /**
//     * Set the maximum number of requests to execute concurrently. Above this requests queue in
//     * memory, waiting for the running calls to complete.
//     *
//     * <p>If more than {@code maxRequests} requests are in flight when this is invoked, those requests
//     * will remain in flight.
//     */
//    public synchronized void setMaxRequests(int maxRequests) {
//        if (maxRequests < 1) {
//            throw new IllegalArgumentException("max < 1: " + maxRequests);
//        }
//        this.maxRequests = maxRequests;
//        promoteCalls();
//    }
//
//    public synchronized int getMaxRequests() {
//        return maxRequests;
//    }
//
//    /**
//     * Set the maximum number of requests for each host to execute concurrently. This limits requests
//     * by the URL's host name. Note that concurrent requests to a single IP address may still exceed
//     * this limit: multiple hostnames may share an IP address or be routed through the same HTTP
//     * proxy.
//     *
//     * <p>If more than {@code maxRequestsPerHost} requests are in flight when this is invoked, those
//     * requests will remain in flight.
//     */
//    public synchronized void setMaxRequestsPerHost(int maxRequestsPerHost) {
//        if (maxRequestsPerHost < 1) {
//            throw new IllegalArgumentException("max < 1: " + maxRequestsPerHost);
//        }
//        this.maxRequestsPerHost = maxRequestsPerHost;
//        promoteCalls();
//    }
//
//    public synchronized int getMaxRequestsPerHost() {
//        return maxRequestsPerHost;
//    }
//
//    synchronized void enqueue(AsyncCall call) {
//        if (runningAsyncCalls.size() < maxRequests && runningCallsForHost(call) < maxRequestsPerHost) {
//            runningAsyncCalls.add(call);
//            executorService().execute(call);
//        } else {
//            readyAsyncCalls.add(call);
//        }
//    }
//
//    /**
//     * Cancel all calls currently enqueued or executing. Includes calls executed both {@linkplain
//     * Call#execute() synchronously} and {@linkplain Call#enqueue asynchronously}.
//     */
//    public synchronized void cancelAll() {
//        for (AsyncCall call : readyAsyncCalls) {
//            call.cancel();
//        }
//
//        for (AsyncCall call : runningAsyncCalls) {
//            call.cancel();
//        }
//
//        for (RealCall call : runningSyncCalls) {
//            call.cancel();
//        }
//    }
//
//    /** Used by {@code AsyncCall#run} to signal completion. */
//    synchronized void finished(AsyncCall call) {
//        if (!runningAsyncCalls.remove(call)) throw new AssertionError("AsyncCall wasn't running!");
//        promoteCalls();
//    }
//
//    private void promoteCalls() {
//        if (runningAsyncCalls.size() >= maxRequests) return; // Already running max capacity.
//        if (readyAsyncCalls.isEmpty()) return; // No ready calls to promote.
//
//        for (Iterator<AsyncCall> i = readyAsyncCalls.iterator(); i.hasNext(); ) {
//            AsyncCall call = i.next();
//
//            if (runningCallsForHost(call) < maxRequestsPerHost) {
//                i.remove();
//                runningAsyncCalls.add(call);
//                executorService().execute(call);
//            }
//
//            if (runningAsyncCalls.size() >= maxRequests) return; // Reached max capacity.
//        }
//    }
//
//    private void promoteCalls() {
//
//    }
//}
