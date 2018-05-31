/*
 * created by solomon
 *
 *
 */

package solomon.com.pos;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

import solomon.com.testcinvakejava.R;

public class PosLcd extends View {

    public static final char LINE1 = 1;
    public static final char LINE2 = 2;
    public static final char LINE3 = 3;
    public static final char LINE4 = 4;
    public static final char LINE5 = 5;
    public static final char LINE6 = 6;
    public static final char LINE7 = 7;
    public static final char LINE8 = 8;

    public static final char COLLUM0 = 0;
    public static final char COLLUM1 = 1;
    public static final char COLLUM2 = 2;
    public static final char COLLUM3 = 3;
    public static final char COLLUM4 = 4;
    public static final char COLLUM5 = 5;
    public static final char COLLUM6 = 6;
    public static final char COLLUM7 = 7;
    public static final char COLLUM8 = 8;
    public static final char COLLUM9 = 9;
    public static final char COLLUM10 = 10;
    public static final char COLLUM11 = 11;
    public static final char COLLUM12 = 12;
    public static final char COLLUM13 = 13;
    public static final char COLLUM14 = 14;
    public static final char COLLUM15 = 15;
    public static final char COLLUM16 = 16;
    public static final char COLLUM17 = 17;
    public static final char COLLUM18 = 18;
    public static final char COLLUM19 = 19;
    public static final char COLLUM20 = 20;
    public static final char COLLUM21 = 21;

    public static final char CFONT = 1;
    public static final char ASII = 0;

    public static final char CHN_CHARACTERSPERLINE = 10;
    public static final char EN_CHARACTERSPERLINE = 22;

    public static final int ALIGN_CENTER = 0x01;
    public static final int ALIGN_LEFT = 0x02;
    public static final int ALIGN_RIGHT = 0x03;

    public static final char linecap = 3;
    //public static final char linecap = 12;
    public static final int MAX_WIDTH = 1024;
    public static final int MAX_HEIGHT = 768;

    public Canvas m_canvas;
    private Bitmap m_bmp;
    private Paint m_paint;
    private int m_width;
    private int m_height;
    private int m_curLine;
    private int m_curCol;
    private Drawable m_drawableDrawable;
    private int m_margin;
    private int m_fontType;
    private int m_curTRow;
    private int m_curTCol;
    private int m_align;
    private Handler m_handler;
    private Handler m_handler_led;

    private Context mContext;

    @SuppressWarnings("deprecation")
    public PosLcd(Context context, int width, int height, Handler handler,Handler handler_led) {
        super(context);

        this.setBackground(null);

        mContext=context;

        this.m_paint = new Paint();
        this.m_margin = 8;
        this.m_width = width;
        this.m_height = height;
        this.m_curLine = this.getTop() + this.m_margin;
        this.m_curCol = this.getLeft() + this.m_margin;
        this.m_bmp = Bitmap.createBitmap(this.m_width, this.m_height,
                Config.ARGB_8888);
        this.m_bmp.eraseColor(Color.WHITE);
        this.setVisibility(VISIBLE);
        this.setDrawingCacheEnabled(true);
        this.m_canvas = new Canvas(this.m_bmp);

        LayoutParams tmp_layoutParams;
        tmp_layoutParams = new LayoutParams(this.m_width, this.m_height);
        this.setLayoutParams(tmp_layoutParams);
        this.m_drawableDrawable = new BitmapDrawable(this.m_bmp);
        this.setBackgroundDrawable(m_drawableDrawable);
        this.m_handler = handler;
        this.m_handler_led = handler_led;
//		InputMethodManager IM = (InputMethodManager)context.getSystemService(Context.INPUT_SERVICE);


    }

    private void restartApp() {
        Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
        PendingIntent restartIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent); // 1秒钟后重启应用
        System.exit(0);
    }

    public void Lcd_Cls() {
        // add your to do here
        Message msg = Message.obtain();
        this.m_bmp.eraseColor(Color.WHITE);


        msg.what = 1;

        if (this.m_handler.sendMessage(msg))
        {
//			Log.i("Lcd_Cls handler msg", "success");
        }
        else
        {
//			Log.i("Lcd_Cls handler msg", "fail");
        }
        //mainact.LedShow(1);
    }

    public void Lcd_ClearLine(int startLine, int endLine) {
        // add your to do here
        int temp ;
        if(startLine==0xffff){
            Message msg = Message.obtain();
            if(endLine<3)
                return;
            msg.what = endLine;
            this.m_handler_led.sendMessage(msg);
            return;
        }
//		Log.w("line between", ""+startLine+ "to" + endLine);

        if (endLine < startLine) {
            temp = startLine;
            startLine = endLine;
            endLine = temp;
        }

        Message msg = Message.obtain();
        FontMetrics fm = this.m_paint.getFontMetrics();
        float fontHeigth = (float)Math.floor(fm.descent - fm.ascent);
        int endDotLine = 0;

        endDotLine =  (endLine+1) * (int) (fontHeigth + linecap) + this.getTop() + this.m_margin - linecap;

        if (endDotLine > this.m_bmp.getHeight()) {
            endDotLine = this.m_bmp.getHeight() - 1;
        }

        int j = (int) ((startLine) * (fontHeigth + linecap));

//		Log.w("dotline between", ""+j+ "to" + endDotLine);

        int i = 0;
        Log.w("arraysize", ""+((endDotLine-j)*this.m_bmp.getWidth()));
        int[] pixels = new int[Math.abs((endDotLine-j)*this.m_bmp.getWidth())];

        for (int k = 0; k < pixels.length; k++) {
            pixels[k] = Color.WHITE;
        }

        if (j < 0) {
            j = 0;
        }
        this.m_bmp.setPixels(pixels, 0, this.m_bmp.getWidth(), i, j, this.m_bmp.getWidth(), Math.abs(endDotLine-j));

        msg.what = 1;
        this.m_handler.sendMessage(msg);
    }

    public void Lcd_Printf(String dispText) throws UnsupportedEncodingException {
        // Log.i("Lcd_Printf dispText", dispText);
        // int length;
        // int start = 0;
        // int worldPerLine;
        // String tmp_dispStr;

        // length = dispText.length();
        // worldPerLine = (int) ((this.m_width-this.m_margin*2) /
        // this.m_paint.getTextSize());

        // while((int)(((this.m_curCol-this.getLeft() -
        // this.m_margin)/this.m_paint.getTextSize() + length)/(worldPerLine)) >
        // 0)
        // {
        // tmp_dispStr = dispText.substring(start, (int)
        // (worldPerLine-(this.m_curCol-this.getLeft() -
        // this.m_margin)/this.m_paint.getTextSize()));
        // this.m_paint.setColor(Color.BLACK);
        // this.m_canvas.drawText(tmp_dispStr, this.m_curCol, this.m_curLine,
        // this.m_paint);
        // length = (int) (length + (this.m_curCol-this.getLeft() -
        // this.m_margin)/this.m_paint.getTextSize() - worldPerLine);
        //
        // if (this.m_fontType == CFONT)
        // {
        // this.m_curLine++;
        //
        // if(this.m_curLine > LINE4)
        // this.m_curLine = LINE1;
        // }
        // else
        // {
        // this.m_curLine++;
        //
        // if(this.m_curLine > LINE8)
        // this.m_curLine = LINE1;
        // }

        // this.m_curCol = this.getLeft() + this.m_margin;
        // start += worldPerLine-this.m_curCol;
        // }
        //
        // tmp_dispStr = dispText.substring(start, dispText.length());
        // this.m_paint.setColor(Color.BLACK);
        // this.m_canvas.drawText(dispText, this.m_curCol, this.m_curLine,
        // this.m_paint);
        // this.m_curCol = (this.m_curCol + dispText.length() - start) %
        // worldPerLine;
//		int i = 0;
//		Message msg = Message.obtain();
        this.m_paint.setColor(Color.BLACK);
//		this.m_paint.setFakeBoldText(true);
//		Log.i("string", dispText);
//		Log.i("len", Float.valueOf(this.m_paint.measureText(dispText) / this.m_width)
//				.toString());
//
//		while ((int)(this.m_paint.measureText(dispText) / this.m_width) > 0) {
//
        Log.i("heyp length over line", dispText);
//
//			this.m_canvas.drawText(
//					dispText.substring(
//							i
//									* (int) (this.m_width / this.m_paint
//											.measureText("A")),
//							(i + 1)
//									* (int) (this.m_width / this.m_paint
//											.measureText("A"))), this.m_curCol,
//					this.m_curLine, this.m_paint);
//					this.m_curLine +=  this.m_paint.getTextSize() + linecap;
//					dispText = dispText.substring((int) (this.m_width / this.m_paint.measureText("A")));
//		}
        this.m_paint.setTextSize(25);//���������С
        this.m_canvas.drawText(new String(dispText.getBytes(), "UTF-8"), this.m_curCol, this.m_curLine,
                this.m_paint);


        Message msg = new Message();
        msg.what = 1;
        this.m_handler.sendMessage(msg);


    }

    public void Lcd_GotoXY(int line, int col) {
        line += 1;
        this.m_curTRow = line;
        this.m_curTCol = col;
        this.m_curCol = (int) (col * this.m_paint.getTextSize() + 0.5)
                + this.getLeft() + this.m_margin;
        this.m_curLine = (int) (line * this.m_paint.getTextSize() + this
                .getTop()) + this.m_margin + (line - 1) * linecap;
        if (line == 0) {
            this.m_curLine += linecap;
        }
    }

    public void Lcd_PrintfXY(int line, int col,final String dispText) throws UnsupportedEncodingException {

//        this.Lcd_GotoXY(line, col);
//        this.Lcd_Printf(dispText);

        PosLcd.this.post(new Runnable() {
            @Override
            public void run() {
                showToast(mContext,dispText,5*1000,20);
            }
        });

    }

    public static void showToast(Context context, String msg,int showTime ,int intervalTime) {
        final Toast toast = new Toast(context);
        //设置Toast显示位置， X、Y轴偏移量
        toast.setGravity(Gravity.TOP, 0, 80);
        //获取自定义视图
        View view = LayoutInflater.from(context).inflate(R.layout.toast_view, null);
        TextView tvMessage = (TextView) view.findViewById(R.id.tv_message_toast);

        tvMessage.setText(msg);
        //设置视图
        toast.setView(view);
//        //设置显示时长
//        toast.setDuration(duration);
        //显示
//        toast.show();

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast.show();
            }
        }, 0, intervalTime);// intervalTime表示延迟多少毫秒时间显示

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                toast.cancel();
                timer.cancel();
            }
        }, showTime);// showTime表示Toast显示时间,单位为毫秒
    }

    public void Lcd_SetTime(String datetime){
        try {
            Process process = Runtime.getRuntime().exec("su");
            //	String datetime = "20131023.112800"; // ���Ե����õ�ʱ�䡾ʱ���ʽ
            // yyyyMMdd.HHmmss��
            datetime = BytesToHexString(datetime.getBytes(), 6);
//			Log.i("", "datetime = " + datetime);
            datetime = "20" + datetime.substring(0, 6) + "." + datetime.substring(6, 12);
            Log.i("", "datetime = " + datetime);

            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("setprop persist.sys.timezone GMT\n");
            os.writeBytes("/system/bin/date -s " + datetime + "\n");
            os.writeBytes("clock -w\n");
            os.writeBytes("exit\n");
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /// <summary>
    /// byte[]��תʮ�������ַ��� 0x34---->"34"
    /// </summary>
    /// <param name="bytes"></param>
    /// <returns></returns>
    public static String BytesToHexString(byte[] bytes, int len)
    {
        char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7','8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] chars = new char[len * 2];
        for (int i = 0; i < len; i++)
        {
            int b = bytes[i];//ʮ������ת��Ϊʮ���� 0x34->52
            chars[i * 2] = hexDigits[b >> 4];
            chars[i * 2 + 1] = hexDigits[b & 0xF];
        }
        return new String(chars);
    }

    public void Lcd_DrawBox(int left, int top, int right, int bottom) {
        Message msg = Message.obtain();
        this.m_paint.setColor(Color.DKGRAY);
        this.m_paint.setStyle(Paint.Style.STROKE);
        this.m_paint.setStrokeWidth(this.m_margin);
        this.m_canvas.drawRect(left + this.getLeft(), top + this.getTop(),
                right + this.getRight(), bottom + this.getBottom(),
                this.m_paint);
        Lcd_SetFont(this.m_fontType);
        msg.what = 1;

        if (this.m_handler.sendMessage(msg))
        {
            Log.i("Lcd_DrawBox send msg", "OK");
        }
        else
        {
            Log.i("Lcd_DrawBox send msg", "Fail");
        }

        this.m_paint.setStyle(Paint.Style.FILL);
    }

    public void Lcd_SetFont(int FontType) {
        switch (FontType) {
            case CFONT:
                this.m_paint
                        .setTextSize(18);

                this.m_fontType = CFONT;
                break;
            case ASII:
                this.m_paint
                        .setTextSize(12);
                this.m_fontType = ASII;
                break;
            default:
                this.m_paint
                        .setTextSize(12);
                this.m_fontType = ASII;
                break;
        }
    }

    public void Lcd_EraseOneWord() {
        int j = this.m_curLine;
        int i = 0;
        Message msg = Message.obtain();

        for (; j < this.m_curLine + this.m_paint.getTextSize(); j++) {
            for (i = (int) ((this.m_curTCol - 1) * this.m_paint.getTextSize()
                    + this.getLeft() + this.m_margin); i < (int) ((this.m_curTCol)
                    * this.m_paint.getTextSize() + this.getLeft() + this.m_margin); i++) {

                this.m_bmp.setPixel(i, j, Color.WHITE);
            }
        }

        msg.what = 1;
        this.m_handler.sendMessage(msg);
    }

    public void Lcd_EraseRect(int coord1X, int coord1Y, int coord2X, int coord2Y) {
        Message msg = Message.obtain();
        FontMetrics fm = this.m_paint.getFontMetrics();
        float fontHeigth = (float)Math.ceil(fm.descent - fm.ascent);
        System.out.println("this.getTop()=" + this.getTop());
        System.out.println("this.m_margin=" + this.m_margin);
        System.out.println("fontHeigth=" + fontHeigth);
        System.out.println("linecap=" + linecap);

        int j = (coord1X - 2) * (int) (fontHeigth + linecap)
                + this.getTop() + this.m_margin;
        int i = 0;

        for (; j < (coord2X-1) * (int) (fontHeigth + linecap)
                + 2*(this.getTop() + this.m_margin); j++) {
            for (i = coord1Y; i < coord2Y; i++) {
                this.m_bmp.setPixel(i, j, Color.WHITE);
            }
        }

        msg.what = 1;
        this.m_handler.sendMessage(msg);
    }


    public void Lcd_SetDispAlign(int alignMode)
    {
        switch (alignMode) {
            case ALIGN_LEFT:
                this.m_paint.setTextAlign(Align.LEFT);
                this.setM_align(ALIGN_LEFT);
                break;
            case ALIGN_CENTER:
                this.m_paint.setTextAlign(Align.CENTER);
                this.setM_align(ALIGN_CENTER);
                break;
            case ALIGN_RIGHT:
                this.m_paint.setTextAlign(Align.RIGHT);
                this.setM_align(ALIGN_RIGHT);
                break;
            default:
                this.m_paint.setTextAlign(Align.LEFT);
                this.setM_align(ALIGN_LEFT);
                break;
        }
    }


    public int getM_width() {
        return m_width;
    }

    public void setM_width(int m_width) {
        this.m_width = m_width;
    }

    public int getM_height() {
        return m_height;
    }

    public void setM_height(int m_height) {
        this.m_height = m_height;
    }

    public int getM_curLine() {
        return m_curLine;
    }

    public void setM_curLine(int m_curLine) {
        this.m_curLine = m_curLine;
    }

    public int getM_curCol() {
        return m_curCol;
    }

    public void setM_curCol(int m_curCol) {
        this.m_curCol = m_curCol;
    }

    public int getM_margin() {
        return m_margin;
    }

    public void setM_margin(int m_margin) {
        this.m_margin = m_margin;
    }

    public static char getLinecap() {
        return linecap;
    }

    public int getM_curTRow() {
        return m_curTRow;
    }

    public void setM_curTRow(int m_curTRow) {
        this.m_curTRow = m_curTRow;
    }

    public int getM_curTCol() {
        return m_curTCol;
    }

    public void setM_curTCol(int m_curTCol) {
        this.m_curTCol = m_curTCol;
    }

    public Paint getM_paint() {
        return m_paint;
    }

    public void setM_paint(Paint m_paint) {
        this.m_paint = m_paint;
    }

    public int getM_align() {
        return m_align;
    }

    public void setM_align(int m_align) {
        this.m_align = m_align;
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        // TODO Auto-generated method stub
        //return super.onCreateInputConnection(outAttrs);
        return new MyInputConnection(this, false);
    }



}
