package solomon.com.pos;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TableRow;

public class keyPad extends View{
	public keyPad(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		keyPadInit();
	}

	static public final char NOKEY = 0XFF;
	
	static public final char KEY1 = '1';
	static public final char KEY2 = '2';
	static public final char KEY3 = '3';
	static public final char KEY4 = '4';
	static public final char KEY5 = '5';
	static public final char KEY6 = '6';
	static public final char KEY7 = '7';
	static public final char KEY8 = '8';
	static public final char KEY9 = '9';
	static public final char KEY0 = '0';
	
	static public final char KEYENTER = 0X0D;
	static public final char KEYCANCEL = 0X1B;
	static public final char KEYCLEAR = 0X1A;
	static public final char KEYBCKSPACE = 0X1C;
	static public final char KEYUP = 0X01;
	static public final char KEYDOWN = 0X02;
	static public final char KEYMENU = 0X03;
	static public final char KEYALPHA = 0X04;
	
	static public final char KEYF1 = 0X05;
	static public final char KEYF2 = 0X06;
	static public final char KEYF3 = 0X07;
	static public final char KEYF4 = 0X08;
	
    private Button key1;
	private Button key2;
	private Button key3;
	private Button key4;
	private Button key5;
	private Button key6;
	private Button key7;
	private Button key8;
	private Button key9;
	private Button key0;
	
	private Button keyF1;
	private Button keyF2;
	private Button keyF3;
	private Button keyF4;
	
	private Button keyEnter;
	private Button keyCancel;
	private Button keyClear;
	private Button keyBckspace;
	
	private Button keyUp;
	private Button keyDown;
	private Button keyMenu;
	private Button keyAlpha;
	
	private TableRow m_low1;
	
	static public final char MAXKEYBUFF = 16;
	
	private ArrayList m_keyBuff;
	private char m_keyIndex;
	private char m_keyHead;
	
	
	private void stdinKey(char keyvalue)
	{
		if(this.m_keyBuff.size() > MAXKEYBUFF)
			return ;
		
		this.m_keyBuff.add(this.m_keyBuff.size(), keyvalue);
	}
	
	private void keyPadInit()
	{
		m_low1 = new TableRow(this.getContext());
//		key1 = (Button)findViewById(R.id.button1);
//		key2 = (Button)findViewById(R.id.button2);
//		key3 = (Button)findViewById(R.id.button3);
//		key4 = (Button)findViewById(R.id.button4);
//		key5 = (Button)findViewById(R.id.button5);
//		key6 = (Button)findViewById(R.id.button6);
//		key7 = (Button)findViewById(R.id.button7);
//		key8 = (Button)findViewById(R.id.button8);
//		key9 = (Button)findViewById(R.id.button9);
//		key0 = (Button)findViewById(R.id.button10);
//		
//		key1.setOnClickListener(new OnClickListener() {
//			
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				stdinKey(KEY1);
//				key1.setText("click key1");
//				Log.i("keypad button1", "click");
//			}
//		});
		
		key1 = new Button(this.getContext());
		m_low1.addView(key1);
	}
	
}
