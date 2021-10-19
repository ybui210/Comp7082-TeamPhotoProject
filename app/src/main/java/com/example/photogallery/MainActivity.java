package com.example.photogallery;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import static android.content.ContentValues.TAG;
import android.content.Intent;
import android.util.Log;
import android.view.View;


public class MainActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int SEARCH_ACTIVITY_REQUEST_CODE = 2;
    public static MainPresenter presenter;

//    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1252;
    public static boolean locationPermGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "-----------------------Oncreate");

        super.onCreate(savedInstanceState);
        TestingObj testingObj = new TestingObj();
        testingObj.help();
        setContentView(R.layout.activity_main);
        testingObj.help();

        PhotoFactory factory = new PhotoFactory();
        presenter = new MainPresenter(factory);
        presenter.bind(this);
        presenter.ready();
        testingObj.help();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                presenter.locPermGranted();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != Activity.RESULT_OK) {
            return;
        }
        if(requestCode == SEARCH_ACTIVITY_REQUEST_CODE) {
            presenter.searchResponse(data);
        }
        if(requestCode == REQUEST_IMAGE_CAPTURE) {
            presenter.takePhotoResponse();
        }
    }

    @Override
    protected void onDestroy() {
        presenter.unbind();
        super.onDestroy();
    }

    public void onShareClick(View view) {
        presenter.shareToSocialMedia();
    }

    public void onClickSearch(View view) {
        presenter.search();
    }

    public void onSnapClick(View view) { presenter.takePhoto();}

    public void onScrollClick(View view) { presenter.scrollPhotos(view);}

    public void onLocationClick(View view) { presenter.updateLocation(); }
}