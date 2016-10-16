package com.yj;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyHttpServer {

    /*
     * backlog: maximum backlog
     * This is the maximum number of queued incoming connections to allow on the listening socket.
     * Queued TCP connections exceeding this limit may be rejected by the TCP implementation.
     */
    public MyHttpServer(InetSocketAddress addr, int backlog, MyHttpHandler handler) {

        mServerAddr = addr;
        mBlackLog = backlog;
        mHandlerMap = new HashMap<String, MyHttpHandler>();
        mDefaultHandler = handler;
        mThreadPool = Executors.newFixedThreadPool(THREAD_NUM);
    }

    public void addHandler(String path, MyHttpHandler handler) {

        mHandlerMap.put(path, handler);
    }

    public void removeHandler(String path) {

        mHandlerMap.remove(path);
    }

    public void start() throws IOException {

        System.out.println("HTTP Server is running...");
        mSvrSocket = new ServerSocket(mServerAddr.getPort(), mBlackLog, mServerAddr.getAddress());
        while(true){
            try {
                Socket socket = mSvrSocket.accept();
                mThreadPool.execute(new Client(socket, mDefaultHandler, mHandlerMap));
            } catch (SocketException ex) {
                break;
            }
        }
    }

    public void stop() {

        if (null != mSvrSocket) {
            try {
                mSvrSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static final int THREAD_NUM = 8;
    private MyHttpHandler mDefaultHandler;
    private HashMap<String, MyHttpHandler> mHandlerMap;
    private InetSocketAddress mServerAddr;
    private int mBlackLog;
    private ServerSocket mSvrSocket;
    private ExecutorService mThreadPool;

    private static class Client implements Runnable {

        public Client(Socket socket, MyHttpHandler defaultHandler, HashMap<String, MyHttpHandler> handlerMap) {
            this.socket = socket;
            this.mDefaultHandler = defaultHandler;
            this.mHandlerMap = handlerMap;
        }

        @Override
        public void run() {

            byte[] buf = new byte[1024*1024]; // TODO move buf to other place
            InputStream in = null;
            try {
                in = socket.getInputStream();
                MyHttpHeader header = new MyHttpHeader(in);
                MyHttpExchange exchange = new MyHttpExchange(header, in, socket.getOutputStream());
                MyHttpHandler handler = null;
                String req = header.getRequestString();
                if (mHandlerMap.containsKey(req)) {
                    handler = mHandlerMap.get(req);
                }
                if (handler == null) {
                    handler = mDefaultHandler;
                }
                handler.handle(exchange);
                in.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        private Socket socket;
        private MyHttpHandler mDefaultHandler;
        private HashMap<String, MyHttpHandler> mHandlerMap;
    }
}
