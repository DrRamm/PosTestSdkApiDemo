package test.apidemo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import vpos.apipackage.APDU_RESP;
import vpos.apipackage.APDU_SEND;
import vpos.apipackage.ByteUtil;
import vpos.apipackage.PosApiHelper;

import static vpos.apipackage.Sys.Lib_Beep;

/**
 * Created by Administrator on 2017/8/17.
 */

public class PiccActivity extends Activity implements View.OnClickListener {

    byte picc_mode = 'M';
    byte picc_type = 'a';
    byte blkNo = 60;
    byte blkValue[] = new byte[20];
    byte pwd[] = new byte[20];
    byte cardtype[] = new byte[3];
    byte serialNo[] = new byte[50];
    byte dataIn[] = new byte[530];

    TextView textViewMsg = null;
    Button btnStart, btnNfc;
//    private int RESULT_CODE = 0;

    PosApiHelper posApiHelper = PosApiHelper.getInstance();

    private boolean bIsFinish = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_picc);

        textViewMsg = (TextView) this.findViewById(R.id.textView_picc);
        btnStart = (Button) findViewById(R.id.btnPiccTest);
        btnNfc = (Button) findViewById(R.id.btnNfc);

        btnStart.setOnClickListener(this);
        btnNfc.setOnClickListener(this);

        //start.setEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        disableFunctionLaunch(true);
    }

    protected void onPause() {
        disableFunctionLaunch(false);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onPause();
//        isQuit = true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        bIsFinish = true;
    }

    public int readNfcCard() {

        Log.e("nfc", "heyp nfc Picc_Open start!");
        byte[] NfcData_Len = new byte[5];
        byte[] Technology = new byte[25];
        byte[] NFC_UID = new byte[56];
        byte[] NDEF_message = new byte[500];

        int ret = posApiHelper.PiccNfc(NfcData_Len, Technology, NFC_UID, NDEF_message);

        int TechnologyLength = NfcData_Len[0] & 0xFF;
        int NFC_UID_length = NfcData_Len[1] & 0xFF;
        int NDEF_message_length = (NfcData_Len[3] & 0xFF) + (NfcData_Len[4] & 0xFF);
        byte[] NDEF_message_data = new byte[NDEF_message_length];
        byte[] NFC_UID_data = new byte[NFC_UID_length];
        System.arraycopy(NFC_UID, 0, NFC_UID_data, 0, NFC_UID_length);
        System.arraycopy(NDEF_message, 0, NDEF_message_data, 0, NDEF_message_length);
        String NDEF_message_data_str = new String(NDEF_message_data);
        String NDEF_str = null;
        if (!TextUtils.isEmpty(NDEF_message_data_str)) {
            NDEF_str = NDEF_message_data_str.substring(NDEF_message_data_str.indexOf("en") + 2, NDEF_message_data_str.length());
        }

        if (ret == 0) {

            posApiHelper.SysBeep();
            //successCount ++;
            if (!TextUtils.isEmpty(NDEF_str)) {
                textViewMsg.setText("TYPE: " + new String(Technology).substring(0, TechnologyLength) + "\n"
                        + "UID: " + ByteUtil.bytearrayToHexString(NFC_UID_data, NFC_UID_data.length) + "\n"
                        + NDEF_str);
            } else {
                textViewMsg.setText("TYPE: " + new String(Technology).substring(0, TechnologyLength) + "\n"
                        + "UID: " + ByteUtil.bytearrayToHexString(NFC_UID_data, NFC_UID_data.length) + "\n"
                        + NDEF_str);
//                textViewMsg.setText("No data ~");
            }

            bIsFinish = true;
        }
        return ret;
    }


    public void startPiccTest() {

        int ret = posApiHelper.PiccOpen();
        if (0 != ret) {
            textViewMsg.setText("Picc_Open Error");
            Log.e("PICC_Thread[ run ]", "Picc_Open error!");
            return;
        }

        boolean bPICCCheck = false;

        ret = posApiHelper.PiccCheck(picc_mode, cardtype, serialNo);
        Log.e("liuhao picc", "000000000000 ret = " + ret);
        if (0 == ret) {
            Log.e("PICC_Thread[ run ]", "Picc_Check succeed!");
            bPICCCheck = true;
        }
        if (bPICCCheck) {
            if ('M' == picc_mode) {
                pwd[0] = (byte) 0xff;
                pwd[1] = (byte) 0xff;
                pwd[2] = (byte) 0xff;
                pwd[3] = (byte) 0xff;
                pwd[4] = (byte) 0xff;
                pwd[5] = (byte) 0xff;
                pwd[6] = (byte) 0x00;

                picc_type = 'A';
                ret = posApiHelper.PiccM1Authority(picc_type, blkNo, pwd, serialNo);
                if (0 == ret) {
                    textViewMsg.setText("Picc_M1Authority Succeed");

                    blkValue[0] = (byte) 0x22;
                    blkValue[1] = (byte) 0x00;
                    blkValue[2] = (byte) 0x00;
                    blkValue[3] = (byte) 0x00;
                    blkValue[4] = (byte) 0xbb;
                    blkValue[5] = (byte) 0xff;
                    blkValue[6] = (byte) 0xff;
                    blkValue[7] = (byte) 0xff;
                    blkValue[8] = (byte) 0x44;
                    blkValue[9] = (byte) 0x00;
                    blkValue[10] = (byte) 0x00;
                    blkValue[11] = (byte) 0x00;
                    blkValue[12] = (byte) blkNo;
                    blkValue[13] = (byte) ~blkNo;
                    blkValue[14] = (byte) blkNo;
                    blkValue[15] = (byte) ~blkNo;
                    ret = posApiHelper.PiccM1WriteBlock(blkNo, blkValue);
                    if (0 == ret) {
                        ret = posApiHelper.PiccM1ReadBlock(blkNo, blkValue);
                        Log.e("caihui", "ret = " + ret + ",  blkValue = " + blkValue.toString());
                        textViewMsg.setText("Picc_M1WriteBlock read blkValue :" + ByteUtil.bytearrayToHexString(blkValue, 20));
                        posApiHelper.SysBeep();
                    } else {
                        textViewMsg.setText("Picc_M1WriteBlock Error    return " + ret);
                    }
                } else {
                    textViewMsg.setText("Picc_M1Authority Error    return " + ret);
                }
            } else//
            {


                byte cmd[] = new byte[4];

                cmd[0] = 0x00;            //0-3 cmd
                cmd[1] = (byte) 0xa4;
                cmd[2] = 0x04;
                cmd[3] = 0x00;
                short lc = 0x0e;
                short le = 256;
                dataIn = "1PAY.SYS.DDF01".getBytes();
                APDU_SEND ApduSend = new APDU_SEND(cmd, lc, dataIn, le);
                APDU_RESP ApduResp = null;
                byte[] resp = new byte[516];

                ret = posApiHelper.PiccCommand(ApduSend.getBytes(), resp);
                if (0 == ret) {
                    Lib_Beep();
                    String strInfo = "";
                    ApduResp = new APDU_RESP(resp);
                    strInfo = ByteUtil.bytearrayToHexString(ApduResp.DataOut, ApduResp.LenOut) + "SWA:" + ByteUtil.byteToHexString(ApduResp.SWA) + " SWB:" + ByteUtil.byteToHexString(ApduResp.SWB);
                    textViewMsg.setText(strInfo);
                } else {
                    textViewMsg.setText("Picc_Command Error    return " + ret);
                    Log.e("PICC_Thread[ run ]", "Picc_Command failed! return " + ret);
                }
            }
        } else {
            textViewMsg.setText(" Looking for cards ");
            Log.e("PICC_Thread11[ run ]", "Time Out!");
        }
        posApiHelper.PiccClose();
        Log.e("PICC_Thread11[ run ]", "posApiHelperPiccClose()!");
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnNfc:

                Log.e("liuhao ","NFC");
                int ret = -1;
                long time = System.currentTimeMillis();
                while (System.currentTimeMillis() < time + 1500) {
                    textViewMsg.setText(getResources().getString(R.string.wait_time));

                    if (bIsFinish) {
                        break;
                    }

                    ret = readNfcCard();
                    if (ret == 0) {
                        break;
                    }
                }

                bIsFinish = false;

                break;

            case R.id.btnPiccTest:
                startPiccTest();
                break;
        }
    }


    // disable the power key when the device is boot from alarm but not ipo boot
    private static final String DISABLE_FUNCTION_LAUNCH_ACTION = "android.intent.action.DISABLE_FUNCTION_LAUNCH";

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
