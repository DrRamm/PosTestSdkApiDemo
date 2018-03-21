package com.printertest_new;


import android.graphics.Bitmap;
import android.os.Bundle;

public abstract class AbstractPrinterWrapper {
    //COMMON PRINTER STATUSES
    public static final int PS_OK = 0;
    public static final int PS_NO_PAPER = 1;
    public static final int PS_CACHE_FULL = 2;
    public static final int PS_OVERHEAT = 3;
    public static final int PS_NOT_OPENED = 4;
    public static final int PS_LOW_VOLTAGE = 5;
    public static final int PS_HARDWARE_ERROR = 6;
    public static final int PS_PAPERJAM = 7;
    public static final int PS_BUSY = 8;
    public static final int PS_TIMEOUT = 9;
    public static final int PS_UNKNOWN = -1;
    protected int m_lineWidth = 384;
    //local vars
    private int timeout = 500;

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public abstract int open();

    public abstract int getStatus();

    public abstract int close();

    public abstract int print(byte[] image);

    public abstract int feedPaper(int value, int unit);

    public abstract int cutPaper();

    public int getLineWidth() {
        return m_lineWidth;
    }

    public int setLineWidth(int width) {
        return -1;
    }

}


