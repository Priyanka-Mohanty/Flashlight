package com.priyankam.flashlight;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ToggleButton;

import com.google.android.gms.analytics.Tracker;


@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity {

    private CameraManager mCameraManager;
    private String mCameraId;
    private ImageButton mTorchOnOffButton;
    private Boolean isTorchOn;
    private MediaPlayer mp;
    private ToggleButton toggleButton;
    private Tracker mTracker;
    private Camera camera;
    private Parameters params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("FlashLightActivity", "onCreate()");
        setContentView(R.layout.activity_main);
        // [START shared_tracker]
        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        // [END shared_tracker]

        mTorchOnOffButton = (ImageButton) findViewById(R.id.button_on_off);
        toggleButton = (ToggleButton) findViewById(R.id.toggle_button);

        isTorchOn = false;

		/*
         * First check if device is supporting flashlight or not
		 */
        Boolean isFlashAvailable = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!isFlashAvailable) {

            AlertDialog alert = new AlertDialog.Builder(MainActivity.this)
                    .create();
            alert.setTitle("Error !!");
            alert.setMessage("Your device doesn't support flash light!");
            alert.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // closing the application
                    finish();
                    System.exit(0);
                }
            });
            alert.show();
            return;
        }

        checkAPIVersion();


        //setTorch mode added in API 23,so it is not support below API 23
/*        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            mCameraId = mCameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        mTorchOnOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                torchOnOFF();
            }
        });

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                torchOnOFF();
            }
        });
    }

    private void torchOnOFF() {
        try {
            if (isTorchOn) {
                turnOffFlashLight();
                isTorchOn = false;
            } else {
                turnOnFlashLight();
                isTorchOn = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("LongLogTag")
    private void checkAPIVersion() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //setTorch mode added in API 23,so it is not support below API 23
                mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                try {
                    mCameraId = mCameraManager.getCameraIdList()[0];
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {

                if (camera == null) {
                    try {
                        camera = Camera.open();
                        params = camera.getParameters();
                    } catch (RuntimeException e) {
                        Log.e("Camera Error. Failed to Open. Error: ", e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void turnOnFlashLight() {

        try {
            //setTorch mode added in API 23,so it is not support below API 23
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // changing button/switch image
                playOnOffSound();
                mTorchOnOffButton.setImageResource(R.drawable.on);

                mCameraManager.setTorchMode(mCameraId, true);

            } else {

                // changing button/switch image
                playOnOffSound();
                mTorchOnOffButton.setImageResource(R.drawable.on);

                if (camera == null || params == null) {
                    return;
                }
                params = camera.getParameters();
                params.setFlashMode(Parameters.FLASH_MODE_TORCH);
                camera.setParameters(params);
                camera.startPreview();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void turnOffFlashLight() {

        try {
            //setTorch mode added in API 23,so it is not support below API 23
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // changing button/switch image
                playOnOffSound();
                mTorchOnOffButton.setImageResource(R.drawable.off);

                mCameraManager.setTorchMode(mCameraId, false);


            } else {

                // changing button/switch image
                playOnOffSound();
                mTorchOnOffButton.setImageResource(R.drawable.off);

                if (camera == null || params == null) {
                    return;
                }

                params = camera.getParameters();
                params.setFlashMode(Parameters.FLASH_MODE_OFF);
                camera.setParameters(params);
                camera.stopPreview();


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playOnOffSound() {

        mp = MediaPlayer.create(MainActivity.this, R.raw.flash_sound);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mp.release();
            }
        });
        mp.start();
    }
/*
    @Override
    protected void onStop() {
        super.onStop();
        if (isTorchOn) {
            turnOffFlashLight();
        }
    }*/

    /*  @Override
      protected void onPause() {
          super.onPause();
          if (isTorchOn) {
              turnOffFlashLight();
          }
      }
  */
    @Override
    protected void onResume() {
        super.onResume();
        if (isTorchOn) {
            turnOnFlashLight();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isTorchOn) {
            turnOnFlashLight();
        }
    }
}

