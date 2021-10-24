package com.project.iway.Util;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Base64;
import android.util.Rational;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;


import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.project.iway.R;
import com.project.iway.Activity.ShareActivity;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import id.zelory.compressor.Compressor;

import static com.project.iway.Util.ImageHelper.encodeImageToBase64;


public class CameraIntentFragment1 extends DialogFragment {

    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private int REQUEST_CODE_PERMISSIONS = 101;
    private static final int REQUEST_TAKE_PHOTO = 0;

    private TextureView cameraView, cameraViewOther;
    private FloatingActionButton cameraBtn2,  cameraShoot;
    private ConstraintLayout imageViewLayout, cameraLayoutOther;
    private TextView cameraMessage;
    private ImageView imageViewOther;

    Bundle bundle;
    Bitmap bm;
    private String imageType ;
    private CameraX.LensFacing lensFacing = CameraX.LensFacing.BACK;
    ImageView imageView;

    public CameraIntentFragment1() {
        // Required empty public constructor
    }

    public static CameraIntentFragment1 newInstance() {
      CameraIntentFragment1 fragment = new CameraIntentFragment1();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_camera_intent1, container, false);
        bundle = this.getArguments();
        cameraViewOther = view.findViewById(R.id.cameraViewOther);
        cameraLayoutOther = view.findViewById(R.id.cameraLayoutOther);
        cameraBtn2 = view.findViewById(R.id.cameraBtn2);
        imageView = view.findViewById(R.id.send);
        imageViewLayout = view.findViewById(R.id.imageViewLayout);
        imageViewOther = view.findViewById(R.id.imageViewOther);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ShareActivity) getActivity()).callAdd();

            }
        });
        try {
            prepareCameraPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return view;

    }

    private void prepareCameraPreview() throws IOException {

                cameraLayoutOther.setVisibility(View.VISIBLE);
                cameraBtn2.setVisibility(View.VISIBLE);
                cameraView = cameraViewOther;
                cameraShoot = cameraBtn2;
                lensFacing = CameraX.LensFacing.BACK;


        if(allPermissionsGranted()){
            startCamera();

        } else{
            ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

    }

    public Preview preview(){
        Rational aspectRatio = new Rational (cameraView.getWidth(), cameraView.getHeight());
        Size screen = new Size(cameraView.getWidth(), cameraView.getHeight()); //size of the screen

        PreviewConfig pConfig = new PreviewConfig.Builder()
                .setTargetAspectRatio(aspectRatio)
                .setTargetResolution(screen)
                .setLensFacing(lensFacing)
                .build();
        Preview preview = new Preview(pConfig);

        preview.setOnPreviewOutputUpdateListener(
                new Preview.OnPreviewOutputUpdateListener() {
                    @Override
                    public void onUpdated(Preview.PreviewOutput output){
                        ViewGroup parent = (ViewGroup) cameraView.getParent();
                        parent.removeView(cameraView);
                        parent.addView(cameraView, 0);

                        cameraView.setSurfaceTexture(output.getSurfaceTexture());
                        updateTransform();
                    }
                });
        return preview;
    }

    private void updateTransform(){
        Matrix mx = new Matrix();
        float w = cameraView.getMeasuredWidth();
        float h = cameraView.getMeasuredHeight();

        float cX = w / 2f;
        float cY = h / 2f;

        int rotationDgr;
        int rotation = (int)cameraView.getRotation();

        switch(rotation){
            case Surface.ROTATION_0:
                rotationDgr = 0;
                break;
            case Surface.ROTATION_90:
                rotationDgr = 90;
                break;
            case Surface.ROTATION_180:
                rotationDgr = 180;
                break;
            case Surface.ROTATION_270:
                rotationDgr = 270;
                break;
            default:
                return;
        }

        mx.postRotate((float)rotationDgr, cX, cY);
        cameraView.setTransform(mx);
    }

    private void startCamera() {
        CameraX.unbindAll();
        ImageCaptureConfig imageCaptureConfig = new ImageCaptureConfig.Builder()
                .setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
                .setLensFacing(lensFacing)
                .build();
        final ImageCapture imgCap = new ImageCapture(imageCaptureConfig);
        captureImage(imgCap);
        CameraX.bindToLifecycle((LifecycleOwner)this, preview(),  imgCap);
    }



    public void captureImage(final ImageCapture imgCap){
        cameraShoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + System.currentTimeMillis() + ".jpg");

                imgCap.takePicture(file, new ImageCapture.OnImageSavedListener() {
                    @Override
                    public void onImageSaved(@NonNull File file) {

                        saveImage(file);
                    }

                    @Override
                    public void onError(@NonNull ImageCapture.UseCaseError useCaseError, @NonNull String message, @Nullable Throwable cause) {
                        String msg = "Pic capture failed : " + message;
                        Toast.makeText(getActivity().getBaseContext(), msg,Toast.LENGTH_SHORT).show();
                        if(cause != null){
                            cause.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public void saveImage(File file){
        try {
            bm =   new Compressor(getActivity())
                    .setMaxWidth(315)
                    .setMaxHeight(480)
                    .setQuality(70)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                    .compressToBitmap(file);

        } catch (IOException e) {
            e.printStackTrace();
        }


                    String base64 = encodeImageToBase64(bm);
                    ((ShareActivity) getActivity()).value(base64, file);
                    alertDialog1();
                    byte[] imageByteArray = Base64.decode(base64, Base64.DEFAULT);
           Glide.with(CameraIntentFragment1.this).load(imageByteArray).into(imageViewOther);
           imageViewLayout.setVisibility(View.VISIBLE);
           cameraShoot.setVisibility(View.INVISIBLE);

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_PERMISSIONS){
            if(allPermissionsGranted()){
                startCamera();
            } else{
                Toast.makeText(getActivity(), "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        }
    }

    private boolean allPermissionsGranted(){

        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void alertDialog1(){
        View view = Objects.requireNonNull(this.getLayoutInflater().inflate(R.layout.loading, null));
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCustomTitle(view);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        new CountDownTimer(3000,1000){
            @Override
            public void onFinish() {


                dialog.dismiss();


            }

            @Override
            public void onTick(long millisUntilFinished) {

            }
        }.start();

    }


}
