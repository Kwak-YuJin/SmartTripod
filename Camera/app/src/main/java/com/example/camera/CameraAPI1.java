package com.example.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.Display;
import android.view.Surface;
import android.view.TextureView;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class CameraAPI1 {

    private MainActivity activity;
    private TextureView textureView;

    private Camera camera;

    public CameraAPI1(MainActivity activity, TextureView textureView){
        this.activity = activity;
        this.textureView = textureView;
    }

    public void init(){
        try{
            // 카메라를 연다.
            camera = Camera.open();
            //camera.setPreviewTexture(textureView.getSurfaceTexture());
            //camera.startPreview();

            // 카메라 회전을 조절
            WindowManager windowManager = activity.getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            int rotation = display.getRotation();

            int degree = 0;

            switch (rotation){
                case Surface.ROTATION_0 :
                    degree = 90;
                    break;
                case Surface.ROTATION_90 :
                    degree = 0;
                    break;
                case Surface.ROTATION_180 :
                    degree = 270;
                    break;
                case Surface.ROTATION_270 :
                    degree = 180;
                    break;
            }
            camera.setDisplayOrientation(degree);

            boolean chk = textureView.isAvailable();

            if(chk == true){
                startPreview();
            } else {
                TextureLinstener listener = new TextureLinstener();
                textureView.setSurfaceTextureListener(listener);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // TextureView의 상태가 변화하면 반응하는 리스너
    private class TextureLinstener implements TextureView.SurfaceTextureListener{
        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            startPreview();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    }

    // 미리보기 처리
    private void startPreview(){
        try{
            camera.setPreviewTexture(textureView.getSurfaceTexture());
            camera.startPreview();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // 사진 촬영
    public void imageCapture(String filePath){
        CaptureCallback callback = new CaptureCallback(filePath);
        camera.takePicture(null, null, callback);
    }

    // 사진 촬영이 성공하면 반응하는 콜백
    private class CaptureCallback implements Camera.PictureCallback{

        String filePath;

        public CaptureCallback(String filePath){
            this.filePath = filePath;
        }

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            try{
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                File file = new File(filePath);
                FileOutputStream fos = new FileOutputStream(file);

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();

                startPreview();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}