package com.printertest_new;

import android.util.Log;

import vpos.apipackage.PosApiHelper;
import vpos.apipackage.Print;
import vpos.apipackage.PrintInitException;

/**
 * Created by swex on 10/24/17.
 */

public class CiontekPrinter extends AbstractPrinterWrapper {
    private static final String TAG = "CiontekPrinter";

    private static final int PRINTER_LINE_WIDTH = 384;
    PosApiHelper mPrinter = null;
    private static final int CIONTEK_PRINTER_ERROR_BUSY = -4001;
    private static final int CIONTEK_PRINTER_ERROR_NOPAPER = -4002;
    private static final int CIONTEK_PRINTER_ERROR_DATAERR = -4003;
    private static final int CIONTEK_PRINTER_ERROR_FAULT = -4004;
    private static final int CIONTEK_PRINTER_ERROR_TOOHEAT = -4005;
    private static final int CIONTEK_PRINTER_ERROR_UNFINISHED = -4006;
    private static final int CIONTEK_PRINTER_ERROR_NOFONTLIB = -4007;
    private static final int CIONTEK_PRINTER_ERROR_BUFFOVERFLOW = -4008;
    private static final int CIONTEK_PRINTER_ERROR_SETFONTERR = -4009;
    private static final int CIONTEK_PRINTER_ERROR_GETFONTERR = -4010;

    private static class SingletonHolder {
        public static final CiontekPrinter HOLDER_INSTANCE = new CiontekPrinter();
    }

    private static int convertToAbstractStatus(int error) {
        switch (error) {
            case CIONTEK_PRINTER_ERROR_BUSY:
                return PS_BUSY;
            case CIONTEK_PRINTER_ERROR_BUFFOVERFLOW:
                return PS_CACHE_FULL;
            case CIONTEK_PRINTER_ERROR_NOPAPER:
                return PS_NO_PAPER;
            case CIONTEK_PRINTER_ERROR_TOOHEAT:
                return PS_OVERHEAT;
            default:
                return PS_UNKNOWN;

        }

    }

    public static AbstractPrinterWrapper getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }

    private static final String tag = "ciontek_driver";

    public CiontekPrinter() {
        mPrinter = PosApiHelper.getInstance();
    }

    @Override
    public int open() {
        try {
//            int ret = mPrinter.PrintInit();
            //modify by liuhao 0112
            int ret=mPrinter.PrintOpen();
            Log.e(tag, "init code:" + ret);
            if (ret != 0) {
                return PS_UNKNOWN;
            }
        } catch (PrintInitException e) {
            e.printStackTrace();
            int exRet = e.getExceptionCode();
            return convertToAbstractStatus(exRet);
        }
        return PS_OK;
    }

    @Override
    public int getStatus() {
        int status = mPrinter.PrintCheckStatus();
        switch (status) {
            case -1:
                return PS_NO_PAPER;
            case -2:
                return PS_OVERHEAT;
            case -3:
                return PS_LOW_VOLTAGE;
            case 0:
                return PS_OK;
            default:
                return PS_UNKNOWN;
        }
    }

    @Override
    public int close() {

        //modify by liuhao 0120
        return mPrinter.PrintClose();
    }

    @Override
    public int print(byte[] image) {
        int width = getLineWidth();
        int height = image.length / (getLineWidth() / 8);
        int ret;
        if (height > 500) {
            //картинка слишком высокая. не больше 500 строк в высоту
            return -1;
        }
        {
            byte byLogoBuffer[] = new byte[image.length + 5];
            System.arraycopy(image, 0, byLogoBuffer, 5, image.length);
            byLogoBuffer[0] = (byte) (width / 256);
            byLogoBuffer[1] = (byte) (width % 256);
            byLogoBuffer[2] = (byte) (height / 256);
            byLogoBuffer[3] = (byte) (height % 256);
            Log.d("wbw", "print:11" );
            ret = Print.Lib_PrnLogo(byLogoBuffer);
            Log.d("wbw", "print:22" );
            if (ret != 0) {
                return convertToAbstractStatus(ret);
            }
        }
        ret  = mPrinter.PrintCtnStart();
        if (ret != 0) return convertToAbstractStatus(ret);
        return PS_OK;
    }

    @Override
    public int feedPaper(int value, int unit) {
        return 0;
    }

    @Override
    public int cutPaper() {
        return 0;
    }
}