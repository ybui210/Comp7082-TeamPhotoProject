package com.example.photogallery;

import static android.content.ContentValues.TAG;
import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainPresenter implements LocationListener {
    MainActivity view;

    private PhotoFactory factory;
    private List<Photo> photos;
    private int index = 0;
    private Photo currentPhoto = null;
    App properties = App.getInstance();
    LocationManager locationManager;
    public static boolean locationPermGranted = false;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int SEARCH_ACTIVITY_REQUEST_CODE = 2;
    public static DateFormat displayFormat;
    private static DateFormat storedFormat;

    public MainPresenter(PhotoFactory factory) {
        TestingObj testingObj = new TestingObj();
        testingObj.help();
        this.factory = factory;
    }

    public void bind(MainActivity view) {
        this.view = view;
    }

    public void unbind() {
        view = null;
    }

    public void ready() {
        displayFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        storedFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());

        photos = factory.findPhotos();
        if(photos.size() == 0) {
            displayPhoto(null);
        } else {
            displayPhoto(photos.get(0));
        }
        if (ContextCompat.checkSelfPermission(view, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(view, new String[]{
                   Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        }
        updateLocation();
    }

    public void locPermGranted() {
        locationPermGranted = true;
    }

    public void takePhoto() {
        Log.i(TAG, "does this work?---1");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Log.i(TAG, "does this work?---2");
        File photoFile = null;
        try {
            Log.i(TAG, "does this work?---3");
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            Log.i(TAG, "does this work?---5");
            Uri photoURI = FileProvider.getUriForFile(view, "com.example.photogallery", photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            view.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            Log.i(TAG, "does this work?---6");
        }

    }

    public void takePhotoResponse() {
        photos = factory.findPhotos(null, null, null, 0, 0);
        displayPhoto(currentPhoto);
    }

    private void displayPhoto(Photo photo) {
        ImageView iv = (ImageView) view.findViewById(R.id.ivGallery);
        TextView tv = (TextView) view.findViewById(R.id.tvTimestamp);
        EditText et = (EditText) view.findViewById(R.id.etCaption);
        TextView lo = (TextView)  view.findViewById(R.id.longTextView);
        TextView li = (TextView)  view.findViewById(R.id.latiTextView);
        if (photo == null || photo.getPath().length() == 0) {
            iv.setImageResource(R.mipmap.ic_launcher);
            et.setText("");
            tv.setText("");
            lo.setText("Longitude: "+0.0);
            li.setText("Latitude: "+0.0);
        } else {
            String path = photo.getPath();
            iv.setImageBitmap(BitmapFactory.decodeFile(path));
            String[] attr = path.split("_");
            et.setText(attr[1]);
            tv.setText(attr[2]);
            lo.setText("Longitude: " + attr[3]);
            li.setText("Latitude: " + attr[4]);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        DecimalFormat df = new DecimalFormat("#.###");
        String timeStamp = storedFormat.format(new Date());
        String longitude = df.format(properties.x);
        String latitude = df.format(properties.y);
        String imageFileName = "JPEG_" + timeStamp + "_" + longitude + "_" + latitude + "_";
        Log.d(TAG, timeStamp);
        Log.d(TAG, longitude);
        Log.d(TAG, latitude);
        Log.d(TAG, imageFileName);
        //setLocationFieldsAsync();
        File storageDir = view.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        //photo absolutepath
        currentPhoto = factory.findPhoto(image.getPath());
        Log.i(TAG,"Hello "+ currentPhoto.getPath());
        return image;
    }

    private Photo updatePhoto(Photo photo, String caption, Double lon, Double lat) {
        photo.setCaption(caption);
        photo.setLongitude(lon);
        photo.setLatitude(lat);
        photo.Save();
        return photo;
    }

    public void scrollPhotos(View v) {
        int oldIndex = index;
        switch (v.getId()) {
            case R.id.btnPrev:
                if (index > 0) {
                    index--;
                }
                break;
            case R.id.btnNext:
                if (index < (photos.size() - 1)) {
                    index++;
                }
                break;
            default:
                break;
        }
        if (index != oldIndex) {
            displayPhoto(photos.get(index));
        }
    }

    public void shareToSocialMedia() {
        File photoFile = new File(photos.get(index).getPath());
        Uri photo = FileProvider.getUriForFile(view, "com.example.photogallery", photoFile);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_STREAM, photo);
        sendIntent.setType("image/*");
        view.startActivity(Intent.createChooser(sendIntent, "Send"));
    }

    public void search() {
        Intent intent = new Intent(view, SearchActivity.class);
        view.startActivityForResult(intent, SEARCH_ACTIVITY_REQUEST_CODE);
    }

    public void searchResponse(Intent response) {
        DateFormat format = displayFormat;
        Date startTimestamp, endTimestamp;
        double lon = response.getDoubleExtra("LONGITUDE",0);
        double lat = response.getDoubleExtra("LATITUDE", 0);
        Log.d(TAG, String.valueOf(lon) + lat);
        String from = (String) response.getStringExtra("STARTTIMESTAMP");
        String to = (String) response.getStringExtra("ENDTIMESTAMP");
        try {
            startTimestamp = format.parse(from);
            endTimestamp = format.parse(to);
            Log.i(TAG, "OnActivity" + from);
        } catch (Exception ex) {
            startTimestamp = null;
            endTimestamp = null;
        }
        String keywords = response.getStringExtra("KEYWORDS");
        index = 0;
        photos = factory.findPhotos(keywords, startTimestamp, endTimestamp, lon, lat);

    }

    @SuppressLint("MissingPermission")
    public void updateLocation() {
        try {
            locationManager = (LocationManager) view.getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 5, this);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        properties.x = location.getLongitude();
        properties.y = location.getLatitude();
        Log.d(TAG, "onLocationChanged:" +properties.x);
        Toast.makeText(view, "Location Updated!: " + properties.x + " " + properties.y, Toast.LENGTH_SHORT).show();
    }


}
