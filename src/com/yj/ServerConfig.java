package com.yj;

// Ref: http://www.mkyong.com/java/java-properties-file-examples/

import java.io.*;
import java.util.Properties;

public class ServerConfig {
    public ServerConfig(String confFileName) {
        mProp = new Properties();
        InputStream in = null;
        mConfFileName = confFileName;
        try {
            in = new FileInputStream(confFileName);
            mProp.load(in);
        } catch (FileNotFoundException ex) {
            // conf not found, do nothing
            //ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getConf(String key) {
        return mProp.getProperty(key);
    }

    public void setConf(String key, String value) {
        mProp.put(key, value);
        OutputStream os = null;
        try {
            os = new FileOutputStream(mConfFileName);
            mProp.store(os, null);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Properties mProp;
    private String mConfFileName;
}
