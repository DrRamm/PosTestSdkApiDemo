package solomon.com.pos;

import android.content.Context;
import android.nfc.tech.IsoDep;

public class NtPos {
	
	public PosLcd tm_lcd;
	public PosKeyboard tm_keypad;
	public IsoDep tm_isoDep;
	
	static{
		//System.loadLibrary("cardapi");
		//System.loadLibrary("AndroidEmvKernel");
		System.loadLibrary("NtPos");
	}

	public native int posMain(String packageName, Context ctx);
}
