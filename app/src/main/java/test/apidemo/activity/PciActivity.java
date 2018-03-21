package test.apidemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import vpos.apipackage.ByteUtil;
import vpos.apipackage.PosApiHelper;
import vpos.apipackage.StringUtil;

/**
 * Created by Administrator on 2017/8/17.
 */

public class PciActivity extends Activity {

    public static final int OPCODE_MAIN_KEY = 0;
    public static final int OPCODE_WORK_KEY = 1;
    private static final int OPCODE_GET_RND = 2;
    public static final int OPCODE_GET_DES = 3;
    public static final int OPCODE_GET_MAC = 4;
    public static final int OPCODE_GET_PIN = 5;
    private final String tag = "PciActivity";

    private ReadWriteRunnable _runnable;
    private int RESULT_CODE = 0;

    TextView textView = null;
    byte[] inData = null;
    byte[] desOut = null;
    byte[] macOut = null;

    byte keyNo = 9;
    byte mkeyNo = 9;
    byte keyLen = 16;
    byte mode = 0;
    short inLen = 8;

    PosApiHelper posApiHelper = PosApiHelper.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_pci);

        textView = (TextView) findViewById(R.id.textView_pci);
//		textView.setText("keyNo = " + keyNo);
        Spinner spinnerKeyNo = (Spinner) findViewById(R.id.spinner_key_no);
        ArrayAdapter<?> adapterKeyNo = ArrayAdapter.createFromResource(this,
                R.array.keyNo, R.layout.spinner_item);
        adapterKeyNo.setDropDownViewResource(R.layout.dropdown_stytle);
        spinnerKeyNo.setAdapter(adapterKeyNo);

        spinnerKeyNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view;
                tv.setGravity(android.view.Gravity.CENTER_HORIZONTAL);
                Log.e("onItenSelect position", "onItenSelect  " + Integer.toString(position));
                keyNo = (byte) position;
                mkeyNo = (byte) position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void OnClickMkey(View view) {
        if (_runnable != null && _runnable.IsThreadFinished() == false) {
            Log.e("", "Thread is still running, return...");
            return;
        }

        _runnable = new ReadWriteRunnable(OPCODE_MAIN_KEY);
        Thread requestThread = new Thread(_runnable);
        requestThread.start();
    }

    public void OnClickWkey(View view) {
        if (_runnable != null && _runnable.IsThreadFinished() == false) {
            Log.e("", "Thread is still running, return...");
            return;
        }
        _runnable = new ReadWriteRunnable(OPCODE_WORK_KEY);
        Thread requestThread = new Thread(_runnable);
        requestThread.start();
    }

    public void OnClickGetRnd(View view) {
        _runnable = new ReadWriteRunnable(OPCODE_GET_RND);
        Thread requestThread = new Thread(_runnable);
        requestThread.start();
    }

    public void OnClickGetDes(View view) {
        if (_runnable != null && _runnable.IsThreadFinished() == false) {
            Log.e("", "Thread is still running, return...");
            return;
        }
        _runnable = new ReadWriteRunnable(OPCODE_GET_DES);
        Thread requestThread = new Thread(_runnable);
        requestThread.start();
        Log.i(tag, "OnClickGetDes");
    }

    public void OnClickGetMac(View view) {
        if (_runnable != null && _runnable.IsThreadFinished() == false) {
            Log.e("", "Thread is still running, return...");
            return;
        }
        _runnable = new ReadWriteRunnable(OPCODE_GET_MAC);
        Thread requestThread = new Thread(_runnable);
        requestThread.start();
        Log.i(tag, "OnClickGetMac");
    }

    public void OnClickGetPin(View view) {
        if (_runnable != null && _runnable.IsThreadFinished() == false) {
            Log.e("", "Thread is still running, return...");
            return;
        }
        _runnable = new ReadWriteRunnable(OPCODE_GET_PIN);
        Thread requestThread = new Thread(_runnable);
        requestThread.start();
        Log.i(tag, "OnClickGetPin");
    }

    private class ReadWriteRunnable implements Runnable {
        private int mOpCode, ret;
        byte[] keyData = null;

        boolean isThreadFinished = false;

        public boolean IsThreadFinished() {
            return isThreadFinished;
        }

        public ReadWriteRunnable(int OpCode) {
            mOpCode = OpCode;
        }

        @Override
        public void run() {
            isThreadFinished = false;

            switch (mOpCode) {
                case OPCODE_MAIN_KEY: {
                    keyData = new byte[16];
                    for (int i = 0; i < 16; i++) {
                        keyData[i] = (byte) 0x31;
                    }

                    mode = 0;
                    ret = posApiHelper.PciWritePIN_MKey(keyNo, keyLen, keyData, mode);
                    if (ret == 0) {
                        RESULT_CODE = 0;
                        Log.d(tag, "Pci_WritePinMKey success");
                        SendMsg("Pci_WritePinMKey Succeed\n", 0);
                    } else {
                        RESULT_CODE = -1;
                        Log.e(tag, "Pci_WritePinMKey failed, ret = " + ret);
                        SendMsg("Pci_WritePinMKey Failed, ret = " + ret + "\n", 0);
                        break;
                    }

                    ret = posApiHelper.PciWriteMAC_MKey(keyNo, keyLen, keyData, mode);
                    if (ret == 0) {
                        RESULT_CODE = 0;
                        Log.d(tag, "Pci_WriteMacMKey success");
                        SendMsg("Pci_WriteMacMKey Succeed\n", 1);
                    } else {
                        RESULT_CODE = -1;
                        Log.e(tag, "Pci_WriteMacMKey failed, ret = " + ret);
                        SendMsg("Pci_WriteMacMKey Failed, ret = " + ret + "\n", 1);
                        break;
                    }

                    ret = posApiHelper.PciWriteDES_MKey(keyNo, keyLen, keyData, mode);
                    if (ret == 0) {
                        RESULT_CODE = 0;
                        Log.d(tag, "Pci_WriteDesMKey success");
                        SendMsg("Pci_WriteDesMKey Succeed\n", 1);
                    } else {
                        RESULT_CODE = -1;
                        Log.e(tag, "Pci_WriteDesMKey failed, ret = " + ret);
                        SendMsg("Pci_WriteDesMKey Failed, ret = " + ret + "\n", 1);
                        break;
                    }
                }
                break;
                case OPCODE_WORK_KEY: {
                    keyData = StringUtil.hexStringToBytes("3E1A500F23992F9349C498C96D41585D"); //31...  1234567812345678 ABCDEFABCDEF1234
//				keyData = StringUtil.hexStringToBytes("7A07F4951E14C867E5A5230C0FE2CBA2"); //32...  1234567812345678 ABCDEFABCDEF1234

                    mode = 1;
                    ret = posApiHelper.PciWritePinKey(keyNo, keyLen, keyData, mode, mkeyNo);
                    if (ret == 0) {
                        RESULT_CODE = 0;
                        Log.d(tag, "Pci_WritePinKey success");
                        SendMsg("Pci_WritePinKey Succeed\n", 0);
                    } else {
                        RESULT_CODE = -1;
                        Log.e(tag, "Pci_WritePinKey failed, ret = " + ret);
                        SendMsg("Pci_WritePinKey Failed, ret = " + ret + "\n", 0);
                        break;
                    }

                    ret = posApiHelper.PciWriteMacKey(keyNo, keyLen, keyData, mode, mkeyNo);
                    if (ret == 0) {
                        RESULT_CODE = 0;
                        Log.d(tag, "Pci_WriteMacKey success");
                        SendMsg("Pci_WriteMacKey Succeed\n", 1);
                    } else {
                        RESULT_CODE = -1;
                        Log.e(tag, "Pci_WriteMacKey failed, ret = " + ret);
                        SendMsg("Pci_WriteMacKey Failed, ret = " + ret + "\n", 1);
                        break;
                    }

                    ret = posApiHelper.PciWriteDesKey(keyNo, keyLen, keyData, mode, mkeyNo);
                    if (ret == 0) {
                        RESULT_CODE = 0;
                        Log.d(tag, "Pci_WriteDesKey success");
                        SendMsg("Pci_WriteDesKey Succeed\n", 1);
                    } else {
                        RESULT_CODE = -1;
                        Log.e(tag, "Pci_WriteDesKey failed, ret = " + ret);
                        SendMsg("Pci_WriteDesKey Failed, ret = " + ret + "\n", 1);
                        break;
                    }
                }
                break;
                case OPCODE_GET_DES:
                    Log.i(tag, "OPCODE_GET_DES");

                    inLen = 8;
                    inData = new byte[inLen];
                    desOut = new byte[inLen];
                    mode = 1;

                    ret = posApiHelper.PciGetDes(keyNo, inLen, inData, desOut, mode);// C0 02 4C A8 40 C9 A0 FB
                    if (ret == 0) {
                        RESULT_CODE = 0;
                        SendMsg("Pci_GetDes Succeed\ndesOut: " + ByteUtil.bytearrayToHexString(desOut, inLen), 0);
                    } else {
                        RESULT_CODE = -1;
                        Log.e(tag, "Pci_GetDes failed, ret = " + ret);
                        SendMsg("Pci_GetDes Failed, ret = " + ret + "\n", 0);
                    }
                    break;
                case OPCODE_GET_MAC:
                    Log.i(tag, "OPCODE_GET_MAC");

                    inLen = 64;
                    inData = new byte[inLen];
                    macOut = new byte[inLen];
                    mode = 0;

                    for (int i = 0; i < inData.length; i++) {
                        inData[i] = (byte) 0xff;
                    }

                    ret = posApiHelper.PciGetMac(keyNo, inLen, inData, macOut, mode);
                    if (ret == 0) {
                        RESULT_CODE = 0;
                        SendMsg("Pci_GetMac Succeed\nmacOut: " + ByteUtil.bytearrayToHexString(macOut, 8), 0);
                    } else {
                        RESULT_CODE = -1;
                        Log.e(tag, "Pci_GetMac failed, ret = " + ret);
                        SendMsg("Pci_GetMac Failed, ret = " + ret + "\n", 0);
                    }
                    break;
                case OPCODE_GET_PIN:
                    Log.i(tag, "OPCODE_GET_PIN");
                    SendMsg("Input Pin Please" + "\n", 0);

                    String pinValue="123456";

                    byte[] pinBlock = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                    byte[] pinPassword = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                    //	 int vec[] = new int[]{1, 5, 3};
                    byte[] cardNo = "0000010005114072".getBytes();
                    byte[] iAmount = "123456.00".getBytes();
                    byte minLen = 4;
                    byte maxLen = 12;
                    byte pinlen = 12;
                    byte waitTimeSec = 60;
                    byte mark = 1;
                    mode = 0;
                    for (int i = 0; i < 16; i++) {
                        pinBlock[i] = 0;
                    }

                    pinlen = (byte) pinValue.length();
                    pinPassword = pinValue.getBytes();

                    ret = posApiHelper.PciGetPin(keyNo, minLen, maxLen, mode, cardNo, pinBlock, pinPassword, pinlen, mark, iAmount, waitTimeSec, PciActivity.this);//42 9A B0 C2 06 60 D7 95
                    if (ret == 0) {
                        RESULT_CODE = 0;
                        SendMsg("Pci_GetPin Succeed\npinBlock: " + ByteUtil.bytearrayToHexString(pinBlock, 8), 0);
                    } else {
                        RESULT_CODE = -1;
                        Log.e(tag, "Pci_GetPin failed, ret = " + ret);
                        SendMsg("Pci_GetPin Failed, ret = " + ret + "\n", 0);
                    }
                    break;

                case OPCODE_GET_RND:
                    byte rnd[] = new byte[8];
                    ret = posApiHelper.PciGetRnd(rnd);

                    String tmpRnd="";

                    if (ret != 0) {
                        RESULT_CODE = -1;
                        SendMsg("Lib_PciGetRnd Failed, ret = " + ret + "\n", 0);
                    } else {
                        RESULT_CODE = 0;

                        tmpRnd+=ByteUtil.bytearrayToHexString(rnd,8);

                        ret = posApiHelper.PciGetRnd(rnd);
                        if (ret != 0) {
                            RESULT_CODE = -1;
                            SendMsg("Lib_PciGetRnd Failed, ret = " + ret + "\n", 0);
                        } else {
                            RESULT_CODE = 0;

                            tmpRnd+=ByteUtil.bytearrayToHexString(rnd,8);

                            ret = posApiHelper.PciGetRnd(rnd);
                            if (ret != 0) {
                                RESULT_CODE = -1;
                                SendMsg("Lib_PciGetRnd Failed, ret = " + ret + "\n", 0);
                            } else {
                                RESULT_CODE = 0;

                                tmpRnd+=ByteUtil.bytearrayToHexString(rnd,8);

                                SendMsg("Lib_PciGetRnd  Succeed\n" + tmpRnd + "\n", 0);
                            }
                        }
                    }
                    break;
                default:
                    break;
            }

            isThreadFinished = true;
        }
    }

    public void SendMsg(String strInfo, int what) {
        Message msg = new Message();
        msg.what = what;
        Bundle b = new Bundle();
        b.putString("MSG", strInfo);
        msg.setData(b);
        handler.sendMessage(msg);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle b = msg.getData();
            String strInfo = b.getString("MSG");
            if (msg.what == 0) {
                textView.setText(strInfo);
            } else {
                textView.setText(textView.getText() + "\n" + strInfo);
            }
            Log.i(tag, strInfo);
        }
    };

}
