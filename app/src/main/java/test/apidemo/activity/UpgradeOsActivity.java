package test.apidemo.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import vpos.apipackage.PosApiHelper;

/**
 * Created by Administrator on 2018/1/10.
 */

public class UpgradeOsActivity extends Activity {

    public static final String TAG = UpgradeOsActivity.class.getSimpleName();

    private ImageView ivHome;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_upgrade_os);

        ivHome= (ImageView) findViewById(R.id.ivHome);

    }

    public void OnClickBackHome(View view){
        UpgradeOsActivity.this.finish();
    }

    public void OnClickUpgradeOs(View view) {

        synchronized (this){

            ivHome.setEnabled(false);

            //        String path = "/storage/emulated/0/Download/update.zip";
            String path = getStoragePath(getApplicationContext(), false) + "/upgrade.zip";
//        Log.e(TAG, "OnClickUpgradeOs File : " + path);

            File mOsFile = new File(path);

            if (!mOsFile.exists()) {
                Toast.makeText(UpgradeOsActivity.this, getString(R.string.file_not_found_update_os), Toast.LENGTH_SHORT).show();
                ivHome.setEnabled(true);
                return;
            }

            boolean flag = PosApiHelper.getInstance().installRomPackage(UpgradeOsActivity.this, path);

            if (!flag) {

            }

            ivHome.setEnabled(true);


        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    /**
     * @param mContext
     * @param is_removale
     * @return
     * @Description : 获取内置存储设备 或 外置SD卡的路径
     * Get path : the built-in storage device or external SD card path.
     */
    private static String getStoragePath(Context mContext, boolean is_removale) {

        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (is_removale == removable) {
                    return path;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}
