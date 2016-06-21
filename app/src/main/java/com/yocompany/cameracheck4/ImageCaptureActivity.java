package com.yocompany.cameracheck4;

import android.app.Dialog;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import android.view.View.OnTouchListener;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.imgproc.Imgproc;


public  class ImageCaptureActivity extends AppCompatActivity implements CvCameraViewListener2,OnTouchListener {
   //     private Camera mCamera;
        private Mat mIntermediateMat;
        private CameraBridgeViewBase mOpenCvCameraView;
        private int seek1=0,seek2=255;

        private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                switch (status) {
                    case LoaderCallbackInterface.SUCCESS: {
                        mOpenCvCameraView.enableView();
                    }
                    break;
                    default: {
                        super.onManagerConnected(status);
                    }
                    break;
                }
            }
        };
        @Override
        public void onCreate (Bundle savedInstanceState){
            super.onCreate(savedInstanceState);

            setContentView(R.layout.content_image_capture);

            mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.image_manipulations_activity_surface_view);
            mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
            mOpenCvCameraView.setCvCameraViewListener(this);
        }

    public void startActivityForResult() {


    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d("", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d("", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mIntermediateMat = new Mat();
    }

    public void onCameraViewStopped() {
        // Explicitly deallocate Mats
        if (mIntermediateMat != null)
            mIntermediateMat.release();

        mIntermediateMat = null;
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat rgba = inputFrame.rgba();
        Size sizeRgba = rgba.size();

        Mat rgbaInnerWindow;

        int rows = (int) sizeRgba.height;
        int cols = (int) sizeRgba.width;
        int left = 0;
        int top = 0;
        int width = cols;
        int height = rows;
        rgbaInnerWindow = rgba.submat(top, top + height, left, left + width);
        Imgproc.Canny(rgbaInnerWindow, mIntermediateMat, 80, 90);
        Imgproc.cvtColor(mIntermediateMat, rgbaInnerWindow, Imgproc.COLOR_GRAY2BGRA, 4);
        Imgproc.threshold(rgbaInnerWindow, rgbaInnerWindow, seek1, seek2, 1);
        rgbaInnerWindow.release();
        return rgba;
    }

    public boolean onTouch(View v, MotionEvent event) {
        System.out.println("clicked");
      //  mCamera.setPreviewCallback(null);
        System.out.println("clicked++++++++++");
     //   mCamera.takePicture(null, null, this);
        System.out.println("clicked------------------");
        Toast.makeText(this, " saved", Toast.LENGTH_SHORT).show();
        finish();
        return false;
    }

    public  void showSettingDialog(View view) {
        final Dialog settingDialog = new Dialog(this);
        settingDialog.setTitle("Adjust Threshold");
        settingDialog.setContentView(R.layout.threshold_values);

        SeekBar highThreshold = (SeekBar) settingDialog.findViewById(R.id.high_threshold);
        SeekBar lowThreshold = (SeekBar) settingDialog.findViewById(R.id.low_threshold);
        lowThreshold.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                System.out.println(seek1);
                seek1 = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBar.setProgress(seek1);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setSecondaryProgress(seekBar.getProgress());

            }
        });
        highThreshold.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                System.out.println(seek2);
                seek2 = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBar.setProgress(seek2);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setSecondaryProgress(seekBar.getProgress());
            }
        });
        settingDialog.show();
    }
}
