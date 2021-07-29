package com.example.camera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    String [] permission_list = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    String dirPath;

    CameraAPI1 cameraAPI1;

    TextureView textureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textureView = (TextureView)findViewById(R.id.textureView);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(permission_list, 0);
        } else {
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for(int result : grantResults){
            if(result == PackageManager.PERMISSION_DENIED){
                return;
            }
        }

        init();
    }

    private void init(){
        try{
            String tempPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            dirPath = tempPath + "/Android/data/" + getPackageName();

            File file = new File(dirPath);
            if(file.exists() == false){
                file.mkdir();
            }

            cameraAPI1 = new CameraAPI1(this, textureView);
            cameraAPI1.init();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void imageCaptureBtn(View view){
        String filePath = dirPath + "/temp_" + System.currentTimeMillis() + ".jpg";
        cameraAPI1.imageCapture(filePath);
    }
}