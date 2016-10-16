package com.yj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyHttpHeader {

    public static final String HTTP_STATUS_200 = "200 OK";
    public static final String HTTP_STATUS_204 = "204 No Content";
    public static final String HTTP_STATUS_301 = "301 Moved Permanently";
    public static final String HTTP_STATUS_302 = "302 Found";
    public static final String HTTP_STATUS_303 = "303 See Other";
    public static final String HTTP_STATUS_304 = "304 Not Modified";
    public static final String HTTP_STATUS_400 = "400 Bad Format";
    public static final String HTTP_STATUS_401 = "401 Unauthorized";
    public static final String HTTP_STATUS_403 = "403 Forbidden";
    public static final String HTTP_STATUS_404 = "404 Not Found";
    public static final String HTTP_STATUS_405 = "405 Method Not Allowed";
    public static final String HTTP_STATUS_406 = "406 Not Acceptable";
    public static final String HTTP_STATUS_411 = "411 Length Required";
    public static final String HTTP_STATUS_413 = "413 Request Entity Too Large";
    public static final String HTTP_STATUS_414 = "414 Request-URI Too Long";
    public static final String HTTP_STATUS_415 = "415 Unsupported Media Type";
    public static final String HTTP_STATUS_500 = "500 Internal Server Error";
    public static final String HTTP_STATUS_501 = "501 Not Implemented";
    public static final String HTTP_STATUS_503 = "503 Service Unavailable";

    public MyHttpHeader(InputStream headerIn) {
        mHeaderInputStream = headerIn;
        mPropMap = new HashMap<String, ArrayList<String>>();

        String firstLine = readLine(headerIn);
        if (firstLine == null) {
            return;
        }
        parseHeaderFirstLine(firstLine);
        String line = null;
        while (true) {
            line = readLine(headerIn);
            if (line == null) {
                break;
            }
            String[] props = line.split(":");
            if (props.length != 2) {
                continue;
            }
            ArrayList<String> propValues = null;
            if (mPropMap.containsKey(props[0])) {
                propValues = mPropMap.get(props[0]);
            } else {
                propValues = new ArrayList<String>();
                mPropMap.put(props[0], propValues);
            }
            String[] values = props[1].split(",");
            for (String v : values) {
                propValues.add(v);
            }
        }
    }

    public String getRequestString() {

        return mRequestString;
    }

    public String getRequestMethod() {

        return mRequestMethod;
    }

    public boolean containsKey(String key) {

        return mPropMap.containsKey(key);
    }

    public HashMap<String, String> getRequestParamMap() {
        return mRequestParamMap;
    }

    public ArrayList<String> getProp(String key) {
        if (containsKey(key)) {
            return mPropMap.get(key);
        } else {
            return null;
        }
    }

    private InputStream mHeaderInputStream;
    private String mRequestMethod;
    private String mRequestString;
    private HashMap<String, String> mRequestParamMap;
    private String mRequest;
    private String mRequestProtocol;
    private HashMap<String, ArrayList<String>> mPropMap;

    private String parseRequest(String s) {
        int idx = s.indexOf('/', 1);
        if (idx < 0) {
            return s;
        } else {
            return s.substring(0, idx);
        }
    }

    private void parseHeaderFirstLine(String firstLine) {
        String[] requests = firstLine.split(" ");
        mRequestMethod = requests[0];

        int idx = requests[1].indexOf('?');
        if (idx > 0) {
            mRequestString = requests[1].substring(0, idx);
            mRequestParamMap = new HashMap<String, String>();
            String[] params = requests[1].substring(idx+1).split("&");
            for (String p : params) {
                String[] a = p.split("=");
                if (a.length == 2) {
                    mRequestParamMap.put(a[0], a[1]);
                } else {
                    mRequestParamMap.put(p, "");
                }
            }
        } else {
            mRequestString = requests[1];
        }

        mRequest = parseRequest(mRequestString);
        mRequestProtocol = requests[2];
    }

    private String readLine(InputStream in) {
        StringBuilder sb = new StringBuilder();
        int c;
        try {
            while ((c = in.read()) > 0) {
                if (c != '\n') {
                    if (c != '\r') {
                        sb.append((char)c);
                    }
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (sb.length() == 0) {
            return null;
        } else {
            return sb.toString();
        }
    }
}
