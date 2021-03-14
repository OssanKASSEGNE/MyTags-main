package com.example.mytags;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;


import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission_group.CAMERA;
//Class To get and manage All permissions
public class Util {
    public static final int PERMISSION_REQUEST_CODE = 200;

    public static boolean checkPermission(final Context context) {
        int location = ContextCompat.checkSelfPermission((Activity) context, ACCESS_FINE_LOCATION);
        int camera = ContextCompat.checkSelfPermission(context, CAMERA);
        int readstorage = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writeStorage = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return location == PackageManager.PERMISSION_GRANTED && camera == PackageManager.PERMISSION_GRANTED && readstorage == PackageManager.PERMISSION_GRANTED && writeStorage == PackageManager.PERMISSION_GRANTED;
    }
    public static void requestPermission(final Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        }

    }

    //Short message for successful task
    public static void showMessage (Context context ,String message){
        Toast toast = Toast.makeText(context ,message,Toast.LENGTH_SHORT);
        toast.show();
    }

}