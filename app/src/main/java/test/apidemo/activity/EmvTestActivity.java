package test.apidemo.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import vpos.apipackage.ByteUtil;
import vpos.apipackage.PosApiHelper;
import vpos.keypad.EMVCOHelper;

/**
 * Created by Administrator on 2017/12/12.
 */

public class EmvTestActivity extends Activity {

    public static final String TAG = EmvTestActivity.class.getSimpleName();

    public static String[] MY_PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.MOUNT_UNMOUNT_FILESYSTEMS"};

    public static final int REQUEST_EXTERNAL_STORAGE = 1;

    public static final int TYPE_TEST_EMV = 1;
    public static final int TYPE_PIN_BLOCK = 2;
    public static final int TYPE_SHOW_PAD = 3;

    private EMVCOHelper emvcoHelper = EMVCOHelper.getInstance();

    TextView txtTitle, tvEmvMsg;

    boolean isOpen = false;

    byte[] TermParabuf = {
            (byte) 0xDF, 0x18, 0x07, (byte) 0xF4, (byte) 0xE0, (byte) 0xF8, (byte) 0xE4, (byte) 0xEF, (byte) 0xF2, (byte) 0xA0, (byte) 0x9F, 0x35, 0x01, 0x22, (byte) 0x9F, 0x33, 0x03, (byte) 0xE0, 0x40, 0x00, (byte) 0x9F, 0x40, 0x05, 0x60,
            0x00, (byte) 0xF0, (byte) 0xF0, 0x01, (byte) 0xDF, 0x19, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xDF, 0x26, 0x0F, (byte) 0x9F, 0x02, 0x06, 0x5F, 0x2A, 0x02,
            (byte) 0x9A, 0x03, (byte) 0x9C, 0x01, (byte) 0x95, 0x05, (byte) 0x9F, 0x37, 0x04, (byte) 0xDF, 0x40, 0x01, (byte) 0xFF, (byte) 0x9F, 0x39, 0x01, 0x05, (byte) 0x9F, 0x1A, 0x02, 0x01, 0x56, (byte) 0x9F, 0x1E,
            0x08, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, (byte) 0x88, (byte) 0xDF, 0x42, 0x01, 0x00, (byte) 0xDF, 0x43, 0x01, 0x00, (byte) 0xDF, 0x44, 0x01, 0x00, (byte) 0xDF, 0x45, 0x01,
            0x00, (byte) 0xDF, 0x46, 0x01, 0x01, (byte) 0x9F, 0x66, 0x04, 0x74, 0x00, 0x00, (byte) 0x80, 0x00, (byte) 0xDF, 0x47, 0x05, (byte) 0xAF, 0x61, (byte) 0xFF, 0x0C, 0x07
    };
    byte[] aid1buf = {
            (byte) 0x9F, 0x06, 0x07, (byte) 0xA0, 0x00, 0x00, 0x00, 0x03, 0x10, 0x10, (byte) 0x9F, 0x01, 0x06, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, (byte) 0x9F, 0x09, 0x02, 0x00,
            0x20, (byte) 0x9F, 0x15, 0x02, 0x00, 0x01, (byte) 0x9F, 0x16, 0x08, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, (byte) 0x88, (byte) 0x9F, 0x4E, 0x12, (byte) 0xD2, (byte) 0xF8, (byte) 0xC1,
            (byte) 0xAA, (byte) 0xC9, (byte) 0xCC, (byte) 0xCE, (byte) 0xF1, (byte) 0xC9, (byte) 0xEE, (byte) 0xDB, (byte) 0xDA, (byte) 0xB7, (byte) 0xD6, (byte) 0xB9, (byte) 0xAB, (byte) 0xCB, (byte) 0xBE, (byte) 0xDF, 0x11, 0x05, (byte) 0xCC, 0x00, 0x00, 0x00,
            0x00, (byte) 0xDF, 0x13, 0x05, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xDF, 0x12, 0x05, (byte) 0xCC, 0x00, 0x00, 0x00, 0x00, (byte) 0xDF, 0x14, 0x03, (byte) 0x9f, 0x37, 0x04,
            (byte) 0xDF, 0x15, 0x04, 0x00, 0x00, (byte) 0x9C, 0x40, (byte) 0xDF, 0x16, 0x01, 0x32, (byte) 0xDF, 0x17, 0x01, 0x14, (byte) 0xDF, 0x18, 0x01, 0x01, (byte) 0x9F, 0x1B, 0x04, 0x00,
            0x01, (byte) 0x86, (byte) 0xA0, (byte) 0x9F, 0x1C, 0x08, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, (byte) 0x88, 0x5F, 0x2A, 0x02, 0x01, 0x56, 0x5F, 0x36, 0x01, 0x02,
            (byte) 0x9F, 0x3C, 0x02, 0x01, 0x56, (byte) 0x9F, 0x3D, 0x01, 0x02, (byte) 0x9F, 0x1D, 0x01, 0x01, (byte) 0xDF, 0x01, 0x01, 0x00, (byte) 0xDF, 0x19, 0x06, 0x00, 0x00, 0x00,
            0x05, 0x00, 0x00, (byte) 0xDF, 0x20, 0x06, 0x00, 0x00, 0x00, 0x10, 0x00, 0x00, (byte) 0xDF, 0x21, 0x06, 0x00, 0x00, 0x00, 0x10, 0x00, 0x00, (byte) 0x9F, 0x7B,
            0x06, 0x00, 0x00, 0x00, 0x10, 0x00, 0x00
    };
    byte[] capk1buf = {
            (byte) 0x9f, 0x22, 0x01, (byte) 0x80, (byte) 0x9f, 0x06, 0x05, (byte) 0xa0,
            0x00, 0x00, 0x03, 0x33, (byte) 0xdf, 0x05, 0x08, 0x32,
            0x30, 0x31, 0x38, 0x30, 0x35, 0x30, 0x31, (byte) 0xdf,
            0x06, 0x01, 0x01, (byte) 0xdf, 0x07, 0x01, 0x01, (byte) 0xdf,
            0x04, 0x03, 0x01, 0x00, 0x01, (byte) 0xdf, 0x03, 0x14,
            (byte) 0xa5, (byte) 0xe4, 0x4b, (byte) 0xb0, (byte) 0xe1, (byte) 0xfa, 0x4f, (byte) 0x96,
            (byte) 0xa1, 0x17, 0x09, 0x18, 0x66, 0x70, (byte) 0xd0, (byte) 0x83,
            0x50, 0x57, (byte) 0xd3, 0x5e, (byte) 0xdf, 0x02, (byte) 0x81, (byte) 0x80,
            (byte) 0xcc, (byte) 0xdb, (byte) 0xa6, (byte) 0x86, (byte) 0xe2, (byte) 0xef, (byte) 0xb8, 0x4c,
            (byte) 0xe2, (byte) 0xea, 0x01, 0x20, (byte) 0x9e, (byte) 0xeb, 0x53, (byte) 0xbe,
            (byte) 0xf2, 0x1a, (byte) 0xb6, (byte) 0xd3, 0x53, 0x27, 0x4f, (byte) 0xf8,
            0x39, 0x1d, 0x70, 0x35, (byte) 0xd7, 0x6e, 0x21, 0x56,
            (byte) 0xca, (byte) 0xed, (byte) 0xd0, 0x75, 0x10, (byte) 0xe0, 0x7d, (byte) 0xaf,
            (byte) 0xca, (byte) 0xca, (byte) 0xbb, 0x7c, (byte) 0xcb, 0x09, 0x50, (byte) 0xba,
            0x2f, 0x0a, 0x3c, (byte) 0xec, 0x31, 0x3c, 0x52, (byte) 0xee,
            0x6c, (byte) 0xd0, (byte) 0x9e, (byte) 0xf0, 0x04, 0x01, (byte) 0xa3, (byte) 0xd6,
            (byte) 0xcc, 0x5f, 0x68, (byte) 0xca, 0x5f, (byte) 0xcd, 0x0a, (byte) 0xc6,
            0x13, 0x21, 0x41, (byte) 0xfa, (byte) 0xfd, 0x1c, (byte) 0xfa, 0x36,
            (byte) 0xa2, 0x69, 0x2d, 0x02, (byte) 0xdd, (byte) 0xc2, 0x7e, (byte) 0xda,
            0x4c, (byte) 0xd5, (byte) 0xbe, (byte) 0xa6, (byte) 0xff, 0x21, (byte) 0x91, 0x3b,
            0x51, 0x3c, (byte) 0xe7, (byte) 0x8b, (byte) 0xf3, 0x3e, 0x68, 0x77,
            (byte) 0xaa, 0x5b, 0x60, 0x5b, (byte) 0xc6, (byte) 0x9a, 0x53, 0x4f,
            0x37, 0x77, (byte) 0xcb, (byte) 0xed, 0x63, 0x76, (byte) 0xba, 0x64,
            (byte) 0x9c, 0x72, 0x51, 0x6a, 0x7e, 0x16, (byte) 0xaf, (byte) 0x85
    };


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_emv_lay);

        txtTitle = (TextView) findViewById(R.id.txtTitle);

        tvEmvMsg = (TextView) findViewById(R.id.tvEmvMsg);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //disable the power key
        disableFunctionLaunch(true);

        isOpen = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        //enable the power key
        disableFunctionLaunch(false);

        if(emvThread!=null){
            emvThread.interrupt();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(emvThread!=null){
            emvThread.interrupt();
        }

    }

    public void onClickTestEmv(View view) {
        //Determine if the current Android version is >=23
        // 判断Android版本是否大于23
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermission();
        } else {
            testEmv();
        }
    }

    public void onClickPinBlock(View view) {

        tvEmvMsg.setText(getResources().getText(R.string.emvTips));

        if (emvThread != null && !emvThread.isThreadFinished()) {
            Log.e(TAG, "Thread is still running...");
            return;
        }
        emvThread = new EmvThread(TYPE_PIN_BLOCK);
        emvThread.start();
    }

    public void onClickShowPad(View view) {

        Log.e(TAG, "onClickShowPad");

        tvEmvMsg.setText("");

        if (emvThread != null && !emvThread.isThreadFinished()) {
            Log.e(TAG, "Thread is still running...");
            return;
        }
        emvThread = new EmvThread(TYPE_SHOW_PAD);
        emvThread.start();

    }


    interface IBackFinish {
        void isBack();
    }

    IBackFinish mIBackFinish;
    public void setIBackFinish(IBackFinish mIBackFinish){
        this.mIBackFinish=mIBackFinish;
    }

    private boolean bIsBack=false;

    private EmvThread emvThread=null;
    private boolean m_bThreadFinished = true;

    class EmvThread extends Thread {

        int type = 0;

        EmvThread(int type) {
            this.type = type;
        }

        public boolean isThreadFinished() {
            return m_bThreadFinished;
        }

        public void run() {
            synchronized (this) {

                m_bThreadFinished = false;
                int ret = 0;

                switch (type) {
                    case TYPE_TEST_EMV:

                        int mCardType=-1;

                        byte picc_mode = 'M';
                        byte cardtype[] = new byte[3];
                        byte serialNo[] = new byte[50];

                        final long time = System.currentTimeMillis();
                        while (System.currentTimeMillis() < time + 30*1000) {

                            if(bIsBack){
                                break;
                            }

                            setIBackFinish(new IBackFinish() {
                                public void isBack() {
                                    emvcoHelper.EmvFinal();
                                }
                            });

                            ret = PosApiHelper.getInstance().IccCheck((byte)0);
                            if (ret == 0) {
                                mCardType=1;
                                break;
                            }

                            ret = PosApiHelper.getInstance().PiccOpen();
                            if (0 == ret) {
                                ret = PosApiHelper.getInstance().PiccCheck(picc_mode, cardtype, serialNo);
                                if(ret==0){
                                    mCardType=2;
                                    break;
                                }
                            }
                        }

                        if(mCardType==-1){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(bIsBack){
                                        tvEmvMsg.setText(getResources().getText(R.string.emvTips));
                                    }else {
                                        tvEmvMsg.setText("timeOut~");
                                        Toast.makeText(EmvTestActivity.this,"Card timeOut~",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                            return;
                        }

                        Tag5A_data = "";
                        ret = emvcoHelper.EmvKeyPadInit(EmvTestActivity.this);
                        if (ret != 0) {
                            m_bThreadFinished = true;
                            return;
                        }
                        short TagCardNo = 0x5A;
                        int TagCardNo_len;
                        byte CardNoData[] = new byte[56];

                        short TagPIN = 0xBD;
                        int PinData_len;
                        byte PinData[] = new byte[56];

                        emvcoHelper.EmvEnvParaInit();
                        emvcoHelper.EmvSaveTermParas(TermParabuf, TermParabuf.length);
                        emvcoHelper.EmvAddOneAIDS(aid1buf, aid1buf.length);
                        emvcoHelper.EmvKernelInit(0, 3);
                        emvcoHelper.EmvAddOneCAPK(capk1buf, capk1buf.length);

                        emvcoHelper.EmvSetTransType(2);
                        emvcoHelper.EmvSetTransAmount(1);
                        emvcoHelper.EmvSetCardType(mCardType); //1--CONTACT 2--CONTACTLESS
                        Log.e("liuhaoEMV", "EMV TEST");

                        ret = emvcoHelper.EmvProcess(mCardType, 0); ////1--CONTACT 2--CONTACTLESS

                        Log.e("liuhaoEMV", "ret = " + ret);

                        if (ret < 0) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    strEmvStatus = "EMV Termination";
                                    tvEmvMsg.setText("EMV Termination");
                                }
                            });

                            m_bThreadFinished = true;
                            return;

                        } else if (ret == 3) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    strEmvStatus = "EMV  GOONLINE";
                                }
                            });
                        }

                        byte [] data55=new byte[256];
                        int len55=emvcoHelper.EmvPrePare55Field(data55, data55.length);
                        Log.e("EMV_55LOG","len55 :" +len55);
                        for (int i = 0; i < data55.length; i++) {
                            Log.e("EMV_55LOG", "i = "+i+"  " + ByteUtil.byteToHexString(data55[i] /*& 0xFF*/));
                        }

                        TagCardNo_len = emvcoHelper.EmvGetTagData(CardNoData, 56, TagCardNo);
                        for (int i = 0; i < TagCardNo_len; i++) {
                            Log.e("CardNoData", "i = "+i+"  " + CardNoData[i]);
                            Tag5A_data += ByteUtil.byteToHexString(CardNoData[i] /*& 0xFF*/);
                        }

                        if(TagCardNo_len/2!=0){
                            Tag5A_data=Tag5A_data.substring(0,TagCardNo_len*2-1);
                        }

                        Log.e("Tag55", "-Tag5A_data=----" + Tag5A_data);
//
                        PinData_len = emvcoHelper.EmvGetTagData(PinData, 56, TagPIN);

                        String TagPin_data="";
                        for (int i = 0; i < PinData_len; i++) {
                            Log.e("EMV PinData", "i = "+i+"  " + PinData[i]);
                            TagPin_data += ByteUtil.byteToHexString(PinData[i] /*& 0xFF*/);
                        }
//                        final String TagPin_data = new String(PinData, 0, PinData_len);
                        final String finalTagPin_data =ByteUtil.hexStr2Str(TagPin_data);

                        runOnUiThread(new Runnable() {
                            public void run() {
                                tvEmvMsg.setText(strEmvStatus + "\n\nCardNO:" + Tag5A_data + "\n\n" + "PIN:" + finalTagPin_data);
                            }
                        });
                        Log.e("EMV PinData", "-TagPin_data=----" + TagPin_data);

                        emvcoHelper.EmvFinal();

                        break;
                    case TYPE_PIN_BLOCK:

                        int pinkey_n = 1;
                        byte[] card_no = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
                        byte[] mode = new byte[]{0};
                        byte[] pin_block = new byte[8];
                        //emvcoHelper.EmvKeyPadInit(EmvTestActivity.this);
                        ret = emvcoHelper.EmvGetPinBlock(EmvTestActivity.this, pinkey_n, card_no, mode, pin_block);
                        //String pin_block00 = new String(pin_block, 0, 8);
                        if (ret != 0) {
                            Log.e(TAG,"PinBlock ret :" +ret);
                            m_bThreadFinished = true;
                            return;
                        }

                        final String pin_block00 = ByteUtil.bytearrayToHexString(pin_block, pin_block.length);

                        Log.e("pin_block00", "heyp pin_block00=----" + pin_block00);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvEmvMsg.setText("PinBlock :" + pin_block00);
                            }
                        });

                        break;
                    case TYPE_SHOW_PAD:

                        final byte[] pwd = new byte[20];

                        short TagPIN2 = 0xBD;
                        int PinData_len2;
                        byte PinData2[] = new byte[56];

//                emvcoHelper.EmvKeyPadInit(EmvTestActivity.this);
                        ret = emvcoHelper.EmvShowKeyPad(EmvTestActivity.this, pwd);

                        PinData_len2 = emvcoHelper.EmvGetTagData(PinData2, 56, TagPIN2);

                        Log.e("liuhaoBDTag",PinData_len2+"");

                        String TagPin_data2="";
                        for (int i = 0; i < PinData_len2; i++) {
                            Log.e("EMV PinData", "i = "+i+"  " + PinData2[i]);
                            TagPin_data2 += ByteUtil.byteToHexString(PinData2[i] /*& 0xFF*/);
                        }
//                        final String TagPin_data = new String(PinData, 0, PinData_len);
                        final String finalTagPin_data2 =ByteUtil.hexStr2Str(TagPin_data2);

                        Log.e("liuhaoBDTag",finalTagPin_data2);

                        if (ret != 0) {
                            Log.e(TAG,"ShowPad ret :" +ret);
                            m_bThreadFinished = true;
                            return;
                        }

                        final String strPwd = ByteUtil.bytesToString(pwd);

                        if ((!TextUtils.isEmpty(strPwd))&&strPwd.trim().length()>0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvEmvMsg.setText("Password : " + strPwd);
                                }
                            });
                        }

                        Log.e(TAG, ByteUtil.bytesToString(pwd));

                        break;
                    default:
                        break;

                }

                m_bThreadFinished = true;

            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (emvThread != null && !emvThread.isThreadFinished()) {
            Log.e(TAG, "onBackPressed() , Thread is still running...");
            emvThread.interrupt();
//            return;
        }

        //add by liuhao 20180302
        if(mIBackFinish!=null){
            mIBackFinish.isBack();
            bIsBack=true;
        }

        //Close Emv Com
        emvcoHelper.EmvFinal();
//        emvcoHelper.EmvFinal();
//        emvcoHelper.EmvFinal();

        EmvTestActivity.this.finish();
    }

    String Tag5A_data = "";
    String strEmvStatus = "";

    /**
     * @Description :  Test Emv
     */
    private void testEmv() {

        tvEmvMsg.setText(getResources().getText(R.string.emvTips));

        strEmvStatus = "";


        if (emvThread != null && !emvThread.isThreadFinished()) {
            Log.e(TAG, "Thread is still running...");
            return;
        }

        emvThread = new EmvThread(TYPE_TEST_EMV);
        emvThread.start();
    }

    /**
     * a callback for request permission
     * 注册权限申请回调
     *
     * @param requestCode  申请码
     * @param permissions  申请的权限
     * @param grantResults 结果
     */
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                testEmv();
            }
        }
    }

    /**
     * @Description: Request permission
     * 申请权限
     */
    private void requestPermission() {
        //检测是否有写的权限
        //Check if there is write permission
        int checkCallPhonePermission = ContextCompat.checkSelfPermission(EmvTestActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
            // 没有写文件的权限，去申请读写文件的权限，系统会弹出权限许可对话框
            //Without the permission to Write, to apply for the permission to Read and Write, the system will pop up the permission dialog
            ActivityCompat.requestPermissions(EmvTestActivity.this, MY_PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        } else {
            testEmv();
        }
    }


    private static final String DISABLE_FUNCTION_LAUNCH_ACTION = "android.intent.action.DISABLE_FUNCTION_LAUNCH";
    // disable the power key when the device is boot from alarm but not ipo boot
    private void disableFunctionLaunch(boolean state) {
        Intent disablePowerKeyIntent = new Intent(DISABLE_FUNCTION_LAUNCH_ACTION);
        if (state) {
            disablePowerKeyIntent.putExtra("state", true);
        } else {
            disablePowerKeyIntent.putExtra("state", false);
        }
        sendBroadcast(disablePowerKeyIntent);
    }

}
