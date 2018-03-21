package com.printertest_new;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

public class MainActivity extends Activity {

    public static final String tag = MainActivity.class.getSimpleName();

    byte[] bmpData;

    int IsWorking = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.example, o);
        bmpData = BitmapUtil.bitmapToByteArray(bitmap);
    }

    @Override
    protected void onResume() {
        disableFunctionLaunch(true);

        super.onResume();
    }

    protected void onPause() {
        // TODO Auto-generated method stub
        disableFunctionLaunch(false);
        super.onPause();
    }


    public void OnClickTestPrinter(View view) {

        synchronized (this){
            IsWorking=1;
            int ret = PrinterWrapper.open();//Printer Power On and initialize

            Log.d(tag, "open:" + ret);
            ret = PrinterWrapper.getStatus();
            Log.d(tag, "getStatus before:" + ret);

            for (int i = 1 ;i< 15 ;i++){
                Log.e("liuhao", "print..." );
                ret = PrinterWrapper.print(bmpData);
                Log.e("liuhao", "print:" + ret);
                ret = PrinterWrapper.getStatus();
                Log.e("liuhao", "getStatus after:" + ret); // this usually 5 - low voltage

                if(ret!=0){
                    //TODO
                    break;
                }
            }

            PrinterWrapper.close();//Printer power off

            IsWorking=0;
        }
    }

    private static final String DISABLE_FUNCTION_LAUNCH_ACTION = "android.intent.action.DISABLE_FUNCTION_LAUNCH";

    /*
    disable the power key when the device is boot from alarm but not ipo boot
     */
    private void disableFunctionLaunch(boolean state) {
        Intent disablePowerKeyIntent = new Intent(DISABLE_FUNCTION_LAUNCH_ACTION);
        if (state) {
            disablePowerKeyIntent.putExtra("state", true);
        } else {
            disablePowerKeyIntent.putExtra("state", false);
        }
        sendBroadcast(disablePowerKeyIntent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            if (IsWorking == 1)
                return true;
        }

        return super.onKeyDown(keyCode, event);    }
}


