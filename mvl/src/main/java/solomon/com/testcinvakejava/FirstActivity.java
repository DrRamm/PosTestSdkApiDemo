package solomon.com.testcinvakejava;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

public class FirstActivity extends Activity {

	public static String[] MY_PERMISSIONS_STORAGE = {
			"android.permission.READ_EXTERNAL_STORAGE",
			"android.permission.WRITE_EXTERNAL_STORAGE",
			"android.permission.MOUNT_UNMOUNT_FILESYSTEMS" };

	public static final int REQUEST_EXTERNAL_STORAGE = 1;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.first_lay);	
		
	}

	/**
	 */
	private void requestPermission() {
		// Check if there is write permission
		int checkCallPhonePermission = ContextCompat.checkSelfPermission(
				FirstActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

		if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
			// Without the permission to Write, to apply for the permission to
			// Read and Write, the system will pop up the permission dialog
			ActivityCompat.requestPermissions(FirstActivity.this,
					MY_PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
		} else {
			Intent intent = new Intent(FirstActivity.this, MainActivity.class);
			startActivity(intent);
		}
	}

	public void onRequestPermissionsResult(int requestCode,
			String[] permissions, int[] grantResults) {

		if (requestCode == REQUEST_EXTERNAL_STORAGE) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				Intent intent = new Intent(FirstActivity.this,
						MainActivity.class);
				startActivity(intent);
			} else {
				Toast.makeText(FirstActivity.this,
						"no permission ,plz to request~", Toast.LENGTH_SHORT)
						.show();
				requestPermission();
			}
		}
	}

	public void OnClickRequestPermission(View view) {

		if (Build.VERSION.SDK_INT > 21) {
	        //Check if there is write permission
	        int checkPermission = ContextCompat.checkSelfPermission(FirstActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

	        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
	            //Without the permission to Write, to apply for the permission to Read and Write, the system will pop up the permission dialog
	            ActivityCompat.requestPermissions(FirstActivity.this, MY_PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
	        }
	        else{
	        	Intent intent=new Intent(FirstActivity.this,MainActivity.class);
                startActivity(intent);
	        }
        } 
		else{
			Intent intent=new Intent(FirstActivity.this,MainActivity.class);
            startActivity(intent);
		}
	}

}
