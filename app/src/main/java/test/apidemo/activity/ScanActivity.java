package test.apidemo.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;

public class ScanActivity extends Activity implements View.OnClickListener {

    private Button btnStopScan, btnEnableScan, btnSetScan, btnStartScan;

    private TextView tvMsg;

    private BroadcastReceiver mScanRecevier = null;

    public static final int ENCODE_MODE_UTF8 = 1;
    public static final int ENCODE_MODE_GBK = 2;
    public static final int ENCODE_MODE_NONE = 3;

    public static String PUBLIC_KEY = "-----BEGIN RSA PUBLIC KEY-----\n" +
            "MIGWAoGBAMqfGO9sPz+kxaRh/qVKsZQGul7NdG1gonSS3KPXTjtcHTFfexA4MkGA\n" +
            "mwKeu9XeTRFgMMxX99WmyaFvNzuxSlCFI/foCkx0TZCFZjpKFHLXryxWrkG1Bl9+\n" +
            "+gKTvTJ4rWk1RvnxYhm3n/Rxo2NoJM/822Oo7YBZ5rmk8NuJU4HLAhAYcJLaZFTO\n" +
            "sYU+aRX4RmoF\n" +
            "-----END RSA PUBLIC KEY-----";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_scan);

        btnStopScan = (Button) findViewById(R.id.btnStopScan);
        btnEnableScan = (Button) findViewById(R.id.btnEnableScan);
        btnSetScan = (Button) findViewById(R.id.btnSetScan);
        btnStartScan = (Button) findViewById(R.id.btnStartScan);

        tvMsg = (TextView) findViewById(R.id.tvMsg);
        tvMsg.setMovementMethod(ScrollingMovementMethod.getInstance());

        btnStopScan.setOnClickListener(ScanActivity.this);
        btnEnableScan.setOnClickListener(ScanActivity.this);
        btnSetScan.setOnClickListener(ScanActivity.this);
        btnStartScan.setOnClickListener(ScanActivity.this);

        mScanRecevier=new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                Log.e("Scan","scan receive.......");

                String  scanResult="";
                int length = intent.getIntExtra("EXTRA_SCAN_LENGTH",0);
                int encodeType= intent.getIntExtra("EXTRA_SCAN_ENCODE_MODE",1);

                if (encodeType  ==  ENCODE_MODE_NONE ){
                    byte[] data = intent.getByteArrayExtra("EXTRA_SCAN_DATA");
                    try {
                        scanResult=new String(data ,0,length ,"iso-8859-1");//Encode charSet
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }else {
                    scanResult=intent.getStringExtra("EXTRA_SCAN_DATA");
                }
//                final String  scanResultData=intent.getStringExtra("EXTRA_SCAN_DATA");
                tvMsg.setText("Scan Bar Code ："+scanResult);
            }
        };

//        mScanRecevier = new BroadcastReceiver() {
//            public void onReceive(Context context, Intent intent) {
//
//                synchronized (this) {
//
//                    String scanResultData = "";
//
//                    byte[] data = intent.getByteArrayExtra("EXTRA_SCAN_DATA");
//
//                    int length = intent.getIntExtra("EXTRA_SCAN_LENGTH", 0);
//                    Log.e("ScanAty", "data lenght - ---- >>" + data.length);
////                try {
////                    scanResultData=new String(data ,0,length ,"iso-8859-1");//Encode charSet
////                } catch (UnsupportedEncodingException e) {
////                    e.printStackTrace();
////                }
//
////                scanResultData=byteArrayToHexstring(data);
//
//                    try {
//                        String publicKey = "";
//                        // 生成公钥私钥
//                        Map<String, Object> map = RSAEncrypt.init();
//                        publicKey = RSAEncrypt.getPublicKey(map);
//                        byte tmp[] = RSAEncrypt.decryptByPublicKey("123456".getBytes(), PUBLIC_KEY);
//                        tvMsg.setText("Scan Bar Code ：" + new String(tmp, "iso-8859-1"));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//
//                        Log.e("ScanAty", "exc :" + e.getMessage());
//                    }
//                }
//
//            }
//        };

        IntentFilter filter = new IntentFilter("ACTION_BAR_SCAN");
        ScanActivity.this.registerReceiver(mScanRecevier, filter);

    }


    /**
     * @param bytes
     * @Functon byteArrayToHexstring
     * @Description byte数组转十六进制字串
     * @Return 转换结果String
     */
    public static String byteArrayToHexstring(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();

        if (bytes.length <= 0 || bytes == null) {
            return null;
        }

        String hv;
        int v = 0;

        for (int i = 0; i < bytes.length; i++) {
            v = bytes[i] & 0xFF;
            hv = Integer.toHexString(v);

            if (hv.length() < 2) {
                hexString.append(0);
            }

            hexString.append(hv);
        }

        return hexString.toString().toUpperCase();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStopScan:

                tvMsg.setText("Stop Scan...");

                Intent intentDisScan = new Intent();
                intentDisScan.setAction("ACTION_BAR_TRIGSTOP");
                ScanActivity.this.sendBroadcast(intentDisScan);

                break;
            case R.id.btnEnableScan:

                tvMsg.setText("Enable Scan...");

                Intent intentEnableScan = new Intent("ACTION_BAR_SCANCFG");
                intentEnableScan.putExtra("EXTRA_SCAN_POWER", 1);
                ScanActivity.this.sendBroadcast(intentEnableScan);

                break;
            case R.id.btnSetScan:

                tvMsg.setText("Set Scan...");

                Intent intentSetScan = new Intent("ACTION_BAR_SCANCFG");
                intentSetScan.putExtra("EXTRA_SCAN_MODE", 2);
                intentSetScan.putExtra("EXTRA_SCAN_AUTOENT", 1);
                ScanActivity.this.sendBroadcast(intentSetScan);

                break;
            case R.id.btnStartScan:

                tvMsg.setText("Start Scan...");

                Intent intentStartScan = new Intent("ACTION_BAR_TRIGSCAN");
                intentStartScan.putExtra("timeout", 4);// Units per second,and Maximum 9
                ScanActivity.this.sendBroadcast(intentStartScan);

                break;
            default:
                //Set Scan Key
//                PosApiHelper.getInstance().SetKeyScanByLetfVolume(this,1);
//                PosApiHelper.getInstance().SetKeyScanByRightVolume(this,1);
//
//                PosApiHelper.getInstance().SetKeyScanByLetfVolume(this,0);
//                PosApiHelper.getInstance().SetKeyScanByRightVolume(this,0);
                break;
        }
    }
}
