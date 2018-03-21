package com.printertest_new;

import java.io.UnsupportedEncodingException;

public class PrinterWrapper {
    public static final String TAG = "printerwrapper";
    public static final int PWT_AUTOSELECT = -1;
    public static final int PWT_BITMAP = 0;
    public static final int PWT_SZZT = 1;
    public static final int PWT_TELPO = 2;
    public static final int PWT_JOLIMARK = 3;
    public static final int PWT_SUNPHOR = 4;
    public static final int PWT_CIONTEK = 5;
    private static int timeout = 10000; //10 секунд по умолчанию
    private static AbstractPrinterWrapper mPrinter = null;
    private static int mDeviceType = PWT_AUTOSELECT;


    public static int getTimeout() {
        return nnPrinter().getTimeout();
    }

    private static AbstractPrinterWrapper nnPrinter() {
        if (mPrinter != null) return mPrinter;
        if (mDeviceType == PWT_AUTOSELECT) {
            mPrinter = CiontekPrinter.getInstance();
        } else {
            mPrinter = CiontekPrinter.getInstance();
        }
        return mPrinter;

    }

    public static void setTimeout(int timeout) {
        if (timeout < 500) timeout = 500;
        nnPrinter().setTimeout(timeout);
    }

    public static void setDeviceType(int deviceType) {
        if (mDeviceType != deviceType) {
            close();
            mPrinter = null;
            mDeviceType = deviceType;
        }
    }

    public static int getCurrentDeviceType() {
        return PWT_AUTOSELECT;
    }

    public static int getDeviceType() {
        return mDeviceType;
    }

    public static String cp1251StringfromBytes(byte[] bytes) {
        try {
            String enc = new String(bytes, "cp1251");
            return enc;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * open the device
     *
     * @return >=0 success <0 error code
     */
    public static int open() {
        return nnPrinter().open();
    }

    /**
     * get device status
     */
    public static int getStatus() {
        return nnPrinter().getStatus();
    }

    /**
     * close device
     *
     * @return close status
     */
    public static int close() {
        return nnPrinter().close();
    }

    /**
     * write the data to the device
     *
     * @param dataOrCmd data or control command
     * @return >=0 success <0 error code
     */
    public static int print(byte[] dataOrCmd) {
        return nnPrinter().print(dataOrCmd);
    }


    /**
     * go paper
     *
     * @param value
     * @param unit  0 line
     *              1 pixel
     * @return
     */
    public static int feedPaper(int value, int unit) {
        return nnPrinter().feedPaper(value, unit);
    }

    /**
     * cut paper
     *
     * @return >=0 success <0 error code
     */
    public static int cutPaper() {
        return nnPrinter().cutPaper();
    }

    public static int getLineWidth() {
        return nnPrinter().getLineWidth();
    }

    public static int setLineWidth(int width) {
        return nnPrinter().setLineWidth(width);
    }
}
