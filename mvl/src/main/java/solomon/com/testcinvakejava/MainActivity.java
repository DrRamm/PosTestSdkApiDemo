package solomon.com.testcinvakejava;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import solomon.com.pos.NtPos;
import solomon.com.pos.PosKeyboardView;
import solomon.com.pos.PosLcd;

public class MainActivity extends Activity {
	
	static{
		System.setProperty("java.library.path", "/system/lib"); 
		//System.loadLibrary("vposapi");
		//System.loadLibrary("AndroidEmvKernel");
		System.loadLibrary("NtPos");
	}
	
	private static final String FILEPATH="/data/data/solomon.com.testcinvakejava";
	private Handler m_handler;
	private Handler m_handler_led;
	private PosLcd tm_lcd;
	private NtPos  NtPos;
	private PosKeyboardView kbView;
	private File destDir;
//	CustomApi cApiDemo;
	private ImageView[] mLedImages = new ImageView[4];
	private int[] LedImageIDs = new int[]{R.drawable.gray_led, R.drawable.blue_led,R.drawable.green_led, R.drawable.red_led,R.drawable.yellow_led};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		
		try {
			File file =new File("/storage/emulated/0/liuhao.txt");

			FileOutputStream fos=new FileOutputStream(file);
			fos.write("liuhao 123 ".getBytes());
			fos.flush();
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		init();
		
	}
	
	
	public void init(){
		
//		Log.i("onCreate", "onCreate");
//		this.findViewById(R.layout.activity_main);
		
		Log.i("onCreate", "findViewById");
		setContentView(R.layout.activity_main);
		
		Log.i("onCreate", "setContentView");
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		mLedImages[0] = (ImageView) findViewById(R.id.led2);
		mLedImages[1] = (ImageView) findViewById(R.id.led3);
		mLedImages[2] = (ImageView) findViewById(R.id.led1);
		mLedImages[3] = (ImageView) findViewById(R.id.led4);
		destDir = new File(this.FILEPATH);
		
		if (!destDir.exists()) {
			if (destDir.mkdirs()) {
				Log.i("make dir", "make dir OK");
				destDir.setExecutable(true);
				destDir.setReadable(true);
				destDir.setWritable(true);
			} else {
				Log.i("make dir", "make dir failed");
			}
		} else {
			Log.i("make dir", "make dir, dir exist");
			destDir.setExecutable(true);
			destDir.setReadable(true);
			destDir.setWritable(true);

			//for (File f : destDir.listFiles()) {
			//	Log.i("delete file" , f.toString());
			//	f.delete();
			//}
		}
		
		m_handler = new Handler(){
			public void handleMessage(Message msg) {   
	        	
//				Log.i("receive msg", "disp");
				 
	             switch (msg.what) {   
	                  case 1:   
	                	  //Log.d("case 1","case1------------");
	                	   tm_lcd.invalidate();
						  
	                       break; 
	                  case 2:
	                	  dialog();
	                	  break;
	             }   
	             super.handleMessage(msg);   
	        }   
		};
		
		m_handler_led = new Handler(){
			public void handleMessage(Message msg) {   
	        	
//				Log.i("receive msg", "disp");
				 
	             switch (msg.what) {   
	                  case 1:   
	                	  //Log.d("case 1","case1------------");
	                	   tm_lcd.invalidate();
	                       break; 
	                  case 2:
	                	  //dialog();
	                	  break;
	                	  // 0     1    2    3     4
	  				case 3://gray blue green red yellow 
						for (int i = 0; i < 4; i++){
							mLedImages[i].setImageResource(LedImageIDs[0]);
						}
						break;
					case 4:
						//for (int i = 0; i < 4; i++){
							//mLedImages[i].setImageResource(LedImageIDs[0]);
						//}
						mLedImages[0].setImageResource(LedImageIDs[1]);
							
						
						break;
					case 5:
						//for (int i = 0; i < 4; i++){
							//mLedImages[i].setImageResource(LedImageIDs[0]);
						//}
	
						mLedImages[1].setImageResource(LedImageIDs[4]);
						break;
					case 6:
						//for (int i = 0; i < 4; i++){
							//mLedImages[i].setImageResource(LedImageIDs[0]);
						//}
						mLedImages[2].setImageResource(LedImageIDs[2]);
						break;
					case 7:
						//for (int i = 0; i < 4; i++){
							//mLedImages[i].setImageResource(LedImageIDs[0]);
						//}
						mLedImages[3].setImageResource(LedImageIDs[3]);
						break;
	             }   
	             super.handleMessage(msg);   
	        }   
		};
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		tm_lcd = new PosLcd(this, dm.widthPixels, (int)(dm.heightPixels * 0.35), m_handler,m_handler_led);
//		tm_lcd.setBackground(null);

		Log.i("dm width:", Integer.valueOf(dm.widthPixels).toString());
		Log.i("dm height:", Integer.valueOf(dm.heightPixels).toString());
		
		LinearLayout tm_layout;
		tm_layout = (LinearLayout) findViewById(R.id.lcdView);
//		tm_layout.setBackground(null);
		tm_layout.addView(tm_lcd);

		kbView = new PosKeyboardView(this.getBaseContext(), m_handler,
				(KeyboardView) findViewById(R.id.keyboard_view),
				R.layout.poskeyboard, tm_lcd);
		
	//	tm_lcd.Lcd_PrintfXY(1, 1, this.getString(R.string.hello_world));
		NtPos = new NtPos();
		NtPos.tm_lcd = tm_lcd;
		NtPos.tm_keypad = kbView.m_PosKb;
		
	//tm_lcd.Lcd_DrawBox(0,0, dm.widthPixels-15, (int)(dm.heightPixels * 0.3));
		
		Runnable R = new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.i("th run", "pos main");
				tm_lcd.Lcd_SetFont(PosLcd.CFONT);
			
				NtPos.posMain(FILEPATH.toString(), MainActivity.this);
			}
			
		};
		
		Thread th = new Thread(R);
		th.start();
		
	}
	


	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	switch (keyCode) {
        case KeyEvent.KEYCODE_HOME:
//        	Toast.makeText(this, "KEYCODE_HOMEPAGE", Toast.LENGTH_SHORT).show();
            return true;
        case KeyEvent.KEYCODE_BACK:
//        	Toast.makeText(this, "KEYCODE_BACK", Toast.LENGTH_SHORT).show();
            return true;
        case KeyEvent.KEYCODE_MENU:
//        	Toast.makeText(this, "KEYCODE_MENU", Toast.LENGTH_SHORT).show();
            return true;
        case KeyEvent.KEYCODE_SEARCH:
//        	Toast.makeText(this, "KEYCODE_SEARCH", Toast.LENGTH_SHORT).show();
            return true;
    	}
        return super.onKeyDown(keyCode, event);
    }
    
	protected void dialog() {
		Builder builder = new Builder(MainActivity.this);
		builder.setMessage("Exit offline pin test?");
		builder.setTitle("Warning");
		
		builder.setNegativeButton("Canel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
//			dialog.dismiss();
				System.exit(0);
			}
		});
		
		builder.create().show();
	}
	public void LedShow(int mode){
		//mLedImages[0].setImageResource(LedImageIDs[1]);
	}
}
