package solomon.com.pos;


public class NtPosLcd {
	public PosLcd tm_lcd;
	
	static{
		System.loadLibrary("NtPosLcd");
	}

	public native int posMain();
	
}
