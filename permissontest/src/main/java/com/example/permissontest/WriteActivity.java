package com.example.permissontest;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class WriteActivity extends Activity {

    public static String[] MY_PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.MOUNT_UNMOUNT_FILESYSTEMS"};

    public static final int REQUEST_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //检测是否有写的权限
        //Check if there is write permission
        int checkPermission = ContextCompat.checkSelfPermission(WriteActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
            // 没有写文件的权限，去申请读写文件的权限，系统会弹出权限许可对话框
            //Without the permission to Write, to apply for the permission to Read and Write, the system will pop up the permission dialog
            ActivityCompat.requestPermissions(WriteActivity.this, MY_PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
        else{
            setContentView(R.layout.activity_write);
        }

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
                Intent intent=new Intent(WriteActivity.this,WriteActivity.class);
                startActivity(intent);
            }else{
                Toast.makeText(WriteActivity.this,"no permission ,plz to request~",Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(WriteActivity.this, MY_PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        }
    }

    public void OnClickWrite(View view) {

        File file = null;

        String path = getStoragePath(getApplicationContext(), false) + "/liuhao.txt";

        file=new File(path);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            file = new File("/storage/emulated/0/liuhao.txt");
//        } else {
//            file = new File("/storage/sdcard0/liuhao.txt");
//        }

        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write("wo shi liuhao !".getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String result = "";

        try {

            FileInputStream fis = new FileInputStream(file);

            //获取文件长度
            int lenght = fis.available();

            byte[] buffer = new byte[lenght];

            fis.read(buffer);

            //将byte数组转换成指定格式的字符串
            result = new String(buffer, "UTF-8");

            Log.e("liuhao Result",result);

            Log.e("liuhao Path",file.getAbsolutePath());


        } catch (Exception e) {
            e.printStackTrace();
        }
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
