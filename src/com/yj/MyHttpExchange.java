package com.yj;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MyHttpExchange {

    public MyHttpExchange(MyHttpHeader header, InputStream in, OutputStream os) {
        this.mHeader = header;
        this.mRespOutputStream = os;
        this.mReqInputStream = in;
    }

    public String getRequestString() {

        return mHeader.getRequestString();
    }

    public String getRequestMethod() {

        return mHeader.getRequestMethod();
    }

    public MyHttpHeader getRequestHeaders() {

        return mHeader;
    }

    public InputStream getRequestBody() {

        return mReqInputStream;
    }

    public MyHttpHeader getResponseHeaders() {
        return null;
    }

    public void sendResponseHeaders(String httpStatus, long responseLength) {

        String head = String.format("HTTP/1.1 %s\r\nContent-Length: %d\r\n\r\n", httpStatus, responseLength); // TODO,
        try {
            mRespOutputStream.write(head.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public OutputStream getResponseBody() {

        return mRespOutputStream;
    }

    private OutputStream mRespOutputStream;
    private InputStream mReqInputStream;
    private MyHttpHeader mHeader;
}
