package com.thigiacmaytinh.CameraOpenCV;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class MainActivity extends Activity implements CvCameraViewListener2, View.OnTouchListener{

    CameraView mOpenCvCameraView;
    private ImageButton btnFlash;
    private Mat mRgba;
    private boolean isFlashEnable = false;
    RelativeLayout mContainerView;
    LayoutInflater inflater;
    String log = ">>>>>>>>";

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mOpenCvCameraView = (CameraView) findViewById(R.id.surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setFocusable(true);

        btnFlash = (ImageButton) findViewById(R.id.btnFlash);

        //set size of camera view
//        int width = 800;
//        int height = 450;
//        mOpenCvCameraView.setMinimumHeight(height);
//        mOpenCvCameraView.setMinimumWidth(width);
//        mOpenCvCameraView.setMaxFrameSize(width, height);


        //mOpenCvCameraView.enableFpsMeter();
        CheckFlashlight();

        mContainerView = (RelativeLayout)findViewById(R.id.mainview);
        inflater =(LayoutInflater)getSystemService(this.LAYOUT_INFLATER_SERVICE);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(MainActivity.this);
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(log, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(log, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        inputFrame.rgba().copyTo(mRgba);
        return mRgba;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void onCameraViewStopped()
    {
        mRgba.release();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean onTouch(View v, MotionEvent event)
    {
        return true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void onCameraViewStarted(int width, int height)
    {
        //Log.d(Common.d + "onCameraViewStarted", String.valueOf(width) + " " + String.valueOf(height) );
        mRgba = new Mat(height, width, CvType.CV_8UC4);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void btnFlash_onClick(View v)
    {
        if(isFlashEnable)
        {
            btnFlash.setImageResource(R.drawable.noflash256);
        }
        else
        {
            btnFlash.setImageResource(R.drawable.flash256);
        }
        mOpenCvCameraView.setupCameraFlashLight();
        isFlashEnable = !isFlashEnable;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onBackPressed()
    {
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void CheckFlashlight()
    {
        if(!this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH))
            btnFlash.setVisibility(View.GONE);
    }
}
