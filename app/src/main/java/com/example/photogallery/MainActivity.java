package com.example.photogallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.Bundle;

import static android.content.ContentValues.TAG;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;

import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity implements LocationListener {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int SEARCH_ACTIVITY_REQUEST_CODE = 2;
    String mCurrentPhotoPath;
    private ArrayList<String> photos = null;
    private int index = 0;
    LocationManager locationManager;
    App properties = App.getInstance();
    Button btn_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "-----------------------Oncreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_location = findViewById(R.id.etLocation);
        //request permission
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);


        }

        getLocation();
        btn_location.performClick();

        photos = findPhotos(new Date(Long.MIN_VALUE), new Date(), "","","");
        if (photos.size() == 0) {
            Log.i(TAG, "-----------------------Oncreate:NO PHOTO");
            displayPhoto(null);
        } else {
            Log.i(TAG, "-----------------------Oncreate: DISPLAY");
            displayPhoto(photos.get(index));
        }

    }

    private ArrayList<String> findPhotos(Date startTimestamp, Date endTimestamp, String keywords, String longitude, String latitude) {
        File file = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath(), "/Android/data/com.example.photogallery/files/Pictures");
        ArrayList<String> photos = new ArrayList<String>();
        List<String> test =  new ArrayList<String>();
        File[] fList = file.listFiles();
        if (fList != null) {
                Log.i(TAG, "i am here");
            photos = (ArrayList<String>) Arrays.asList(fList).stream().filter(e ->  startTimestamp == null || e.lastModified() >= startTimestamp.getTime())
                    .filter(e -> endTimestamp == null || e.lastModified() <= endTimestamp.getTime())
                    .filter(e -> keywords == null || e.getPath().contains(keywords))
                    .filter(e -> longitude == null ||  e.getPath().contains(latitude))
                    .filter(e -> latitude == null || e.getPath().contains(latitude)).map(x -> x.getPath()).collect(Collectors.toList());

            }

        return photos;
    }

    private void updatePhoto(String path, String caption) {
        String[] attr = path.split("_");
        if (attr.length >= 3) {
            File to = new File(attr[0] + "_" + caption + "_" + attr[2] + "_" + attr[3] + "_" + attr[4] + "_" + attr[5]);
            File from = new File(path);
            from.renameTo(to);
        }
    }

    public void scrollPhotos(View v) {
        getLocation();
        updatePhoto(photos.get(index), ((EditText) findViewById(R.id.etCaption)).getText().toString());
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
        displayPhoto(photos.get(index));
    }

    private void displayPhoto(String path) {
        btn_location.performClick();
        ImageView iv = (ImageView) findViewById(R.id.ivGallery);
        TextView tv = (TextView) findViewById(R.id.tvTimestamp);
        EditText et = (EditText) findViewById(R.id.etCaption);
        TextView lo = (TextView)  findViewById(R.id.longTextView);
        TextView li = (TextView)  findViewById(R.id.latiTextView);
        if (path == null || path =="") {
            iv.setImageResource(R.mipmap.ic_launcher);
            et.setText("");
            tv.setText("");
            lo.setText("Logitude: "+0.0);
            li.setText("Latitude: "+0.0);
        } else {
            iv.setImageBitmap(BitmapFactory.decodeFile(path));
            String[] attr = path.split("_");
            et.setText(attr[1]);
            tv.setText(attr[2]);
            lo.setText("Logitude: " + attr[3]);
            li.setText("Latitude: " + attr[4]);


        }
    }

    public void takePhoto(View v) {
        btn_location.performClick();
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
            Uri photoURI = FileProvider.getUriForFile(this, "com.example.photogallery", photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            Log.i(TAG, "does this work?---6");
        }

    }

    private File createImageFile() throws IOException {
        btn_location.performClick();
        // Create an image file name
        DecimalFormat df = new DecimalFormat("#.###");
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String longitude = df.format(properties.x);
        String latitude = df.format(properties.y);
        String imageFileName = "JPEG_" + timeStamp + "_"+longitude+"_"+latitude+"_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.i(TAG,"Hello "+mCurrentPhotoPath);
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SEARCH_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                DateFormat format = new SimpleDateFormat("yyyy‐MM‐dd HH:mm:ss");
                Date startTimestamp, endTimestamp;

                try {
                    String from = (String) data.getStringExtra("STARTTIMESTAMP");
                    String to = (String) data.getStringExtra("ENDTIMESTAMP");
                    startTimestamp = format.parse(from);
                    endTimestamp = format.parse(to);
                    Log.i(TAG, "OnActivity" + from);
                } catch (Exception ex) {
                    startTimestamp = null;
                    endTimestamp = null;
                }
                String keywords = (String) data.getStringExtra("KEYWORDS");
                String getlongitudes =  (String)data.getStringExtra("LONGITUDE");
                String getlatitudes = (String)data.getStringExtra("LATITUDE");
                index = 0;
                photos = findPhotos(startTimestamp, endTimestamp, keywords, getlongitudes, getlatitudes);
                if (photos.size() == 0) {
                    displayPhoto(null);
                } else {
                    displayPhoto(photos.get(index));
                }
            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            ImageView mImageView = (ImageView) findViewById(R.id.ivGallery);
            mImageView.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath));
            photos = findPhotos(new Date(Long.MIN_VALUE), new Date(), "","","");
        }
    }

    public void onClickSearch(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivityForResult(intent, SEARCH_ACTIVITY_REQUEST_CODE);

    }

    public void shareToSocialMedia(View view) {
        File photoFile = new File(photos.get(index));
        Uri photo = FileProvider.getUriForFile(this, "com.example.photogallery", photoFile);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_STREAM, photo);
        sendIntent.setType("image/*");
        startActivity(Intent.createChooser(sendIntent, "Send"));
    }


    @SuppressLint("MissingPermission")
    private void getLocation() {
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 5, MainActivity.this);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        properties.x = location.getLatitude();
        properties.y = location.getLongitude();
        Log.d(TAG, "onLocationChanged:" +properties.x);
    }

    public void onClickLocation(View view) {
        getLocation();
    }
}