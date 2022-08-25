package com.baidu.location.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import android.content.Intent;
import android.view.Gravity;
import android.widget.Toast;

import static com.baidu.mapapi.BMapManager.getContext;

public class Splash extends AppCompatActivity {


    String[] permissions = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION, // GPS定位权限
            Manifest.permission.ACCESS_FINE_LOCATION,  // 访问wifi网络权限
            Manifest.permission.ACCESS_WIFI_STATE,    // 获取运营商权限
            Manifest.permission.ACCESS_NETWORK_STATE,    // 获取wifi定位权限
            Manifest.permission.CHANGE_WIFI_STATE,      // 存储权限
            Manifest.permission.WRITE_EXTERNAL_STORAGE,    // 访问网络权限
            Manifest.permission.INTERNET        // 网络权限
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        checkPermissions();
    }


    private void checkPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int q1 = ContextCompat.checkSelfPermission(getContext(), permissions[0]);
            int q2 = ContextCompat.checkSelfPermission(getContext(), permissions[1]);
            int q3 = ContextCompat.checkSelfPermission(getContext(), permissions[2]);
            int q4 = ContextCompat.checkSelfPermission(getContext(), permissions[3]);
            int q5 = ContextCompat.checkSelfPermission(getContext(), permissions[4]);
            int q6 = ContextCompat.checkSelfPermission(getContext(), permissions[5]);
            int q7 = ContextCompat.checkSelfPermission(getContext(), permissions[6]);
            // 权限是否已经授权 GRANTED---授权  DINIED---拒绝
            if (q1 != PackageManager.PERMISSION_GRANTED ||
                    q2 != PackageManager.PERMISSION_GRANTED ||
                    q3 != PackageManager.PERMISSION_GRANTED ||
                    q4 != PackageManager.PERMISSION_GRANTED ||
                    q5 != PackageManager.PERMISSION_GRANTED ||
                    q6 != PackageManager.PERMISSION_GRANTED ||
                    q7 != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                startRequestPermission();
            }
            else{
                //获取权限成功,跳转
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent mainIntent = new Intent(Splash.this,MainActivity.class);
                        Splash.this.startActivity(mainIntent);
                        Splash.this.finish();
                    }
                },2000);
            }
        }
    }

    private void startRequestPermission() {
        ActivityCompat.requestPermissions(this, permissions, 321);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 321) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    //如果没有获取权限，那么可以提示用户去设置界面--->应用权限开启权限
                    Toast toast = Toast.makeText(this, "请到设置界面授予权限再启动", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    //获取权限成功,跳转
                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent mainIntent = new Intent(Splash.this,MainActivity.class);
                            Splash.this.startActivity(mainIntent);
                            Splash.this.finish();
                        }
                    },2000);
                }
            }
        }
    }
}