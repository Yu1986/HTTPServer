package com.yj;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;

public class Main {


    private static final String WEB_ROOTDIR_DEFAULT = "yoga/";
    private static final String SERVER_PORT_DEFAULT = "1234";
    private static final String CONFIG_NAME_ROOT_DIR = "rootdir";
    private static final String CONFIG_NAME_PORT = "port";

    private static ServerConfig mServerConf;

    public static void main(String[] args) {
        mServerConf = new ServerConfig("http.conf");
        String rootdir = mServerConf.getConf(CONFIG_NAME_ROOT_DIR);
        if (rootdir == null) {
            rootdir = WEB_ROOTDIR_DEFAULT;
            mServerConf.setConf(CONFIG_NAME_ROOT_DIR, rootdir);
        }

        String portStr = mServerConf.getConf(CONFIG_NAME_PORT);
        if (portStr == null) {
            portStr = SERVER_PORT_DEFAULT;
            mServerConf.setConf(CONFIG_NAME_PORT, portStr);
        }

        MyHttpServer server = new MyHttpServer(new InetSocketAddress(Integer.parseInt(portStr)), 0, defaultHandler);
        server.addHandler("/version", versionHandler);
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static MyHttpHandler defaultHandler = new MyHttpHandler() {
        @Override
        public void handle(MyHttpExchange exchange) {
            if (exchange.getRequestMethod().equals("GET")) {
                handleGet(exchange);
            } else if (exchange.getRequestMethod().equals("POST")) {
                handlePost(exchange);
            }

        }

        private void handleResourceNotFound(MyHttpExchange exchange, String httpStatus) {
            OutputStream os = null;
            try {
                exchange.sendResponseHeaders(httpStatus, 0);
                os = exchange.getResponseBody();
                os.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void handleGet(MyHttpExchange exchange) {
            String request = exchange.getRequestString();
            if (request.equals("/")) {
                request =  "/index.html";
            }
            String fname = mServerConf.getConf(CONFIG_NAME_ROOT_DIR) + request;
            if (new File(fname).exists()) {
                InputStream fileIn = null;
                OutputStream os = null;
                try {
                    fileIn = new FileInputStream(fname);
                    byte[] buf = new byte[fileIn.available()];
                    exchange.sendResponseHeaders(MyHttpHeader.HTTP_STATUS_200, fileIn.available());
                    os = exchange.getResponseBody();
                    fileIn.read(buf);
                    os.write(buf);
                    os.close();
                    fileIn.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                handleResourceNotFound(exchange, MyHttpHeader.HTTP_STATUS_404);
            }
        }

        private void handlePost(MyHttpExchange exchange) {
            ArrayList<String> contentLenStr = exchange.getRequestHeaders().getProp("Content-Length");
            if (contentLenStr.size() != 1) {
                System.out.println("Can not get correct content length for POST request");
                return;
            }
            int contentLen = Integer.parseInt(contentLenStr.get(0).trim());
            InputStream in = exchange.getRequestBody();
            byte[] buf = new byte[contentLen];
            String contentEncode = null;
            try {
                in.read(buf);
                contentEncode = new String(buf);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (contentEncode != null) {
                String content = URLDecoder.decode(contentEncode);
                HashMap<String, String> smacrosMap = new HashMap<>();
                String[] strs = content.split("&");
                for (String s : strs) {
                    String[] params = s.split("=");
                    if (params.length == 2) {
                        smacrosMap.put(params[0], params[1]);
                    } else {
                        smacrosMap.put(s, "");
                    }
                }
                showHtmlWithSmacros(exchange, smacrosMap);
            } else {
                handleResourceNotFound(exchange, MyHttpHeader.HTTP_STATUS_400);
            }
        }

        private void showHtmlWithSmacros(MyHttpExchange exchange, HashMap<String, String> smacrosMap) {
            String request = exchange.getRequestString();
            if (request.equals("/")) {
                request =  "/index.html";
            }
            String fname = mServerConf.getConf(CONFIG_NAME_ROOT_DIR) + request;
            if (new File(fname).exists()) {
                InputStream fileIn = null;
                OutputStream os = null;
                try {
                    fileIn = new FileInputStream(fname);
                    byte[] buf = new byte[fileIn.available()];
                    fileIn.read(buf);
                    String pageStr = smacrosReplace(buf, smacrosMap);
                    exchange.sendResponseHeaders(MyHttpHeader.HTTP_STATUS_200, pageStr.length());
                    os = exchange.getResponseBody();
                    os.write(pageStr.getBytes());
                    os.close();
                    fileIn.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                handleResourceNotFound(exchange, MyHttpHeader.HTTP_STATUS_404);
            }
        }

        private String smacrosReplace(byte[] buf, HashMap<String, String> smacrosMap) {
            boolean inMacro = false;
            StringBuilder sbPage = new StringBuilder();
            StringBuilder sbMacro = new StringBuilder();
            for (int i=0; i<buf.length;i++) {
                if (inMacro) {
                    if (buf[i] == ')') {
                        String key = sbMacro.toString();
                        if (smacrosMap.containsKey(key)) {
                            sbPage.append(smacrosMap.get(key));
                        }
                        sbMacro.setLength(0);
                        inMacro = false;
                    } else {
                        sbMacro.append((char)buf[i]);
                    }
                } else {
                    if (buf[i] == '$' && i<(buf.length-1) && buf[i+1] == '(') {
                        i++;
                        inMacro = true;
                    } else {
                        sbPage.append((char)buf[i]);
                    }
                }
            }
            return sbPage.toString();
        }
    };

    private static MyHttpHandler versionHandler = new MyHttpHandler() {
        @Override
        public void handle(MyHttpExchange exchange) {
            String response = String.format("Version: %s\r\n", "1.01");
            exchange.sendResponseHeaders(MyHttpHeader.HTTP_STATUS_200, response.length());
            OutputStream os = exchange.getResponseBody();
            try {
                os.write(response.getBytes());
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };
}