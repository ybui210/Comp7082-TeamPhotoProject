package com.example.photogallery;


import android.annotation.SuppressLint;
import android.os.Environment;

import androidx.annotation.NonNull;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PhotoFactory {
    private static DateFormat storedFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
    private static final int CAPTION_INDEX = 1;
    private static final int TIMESTAMP_INDEX = 2;
    private static final int LON_INDEX = 3;
    private static final int LAT_INDEX = 4;


    @SuppressLint("NewApi")
    private <T1> T1 firstOrDefault(Stream<T1> items, T1 defaultValue, Predicate<T1> predicate) {
        Optional<T1> item = items.filter(predicate).findFirst();
        if (item.isPresent())
            return item.get();
        return defaultValue;
    }

    @SuppressLint("NewApi")
    public Photo findPhoto(@NonNull String path) {
        return firstOrDefault(findPhotos().stream(), null, item -> path.equals(item.getPath()) );
    }

    public List<Photo> findPhotos(String keywords, Date startTimestamp, Date endTimestamp, double longitude, double latitude) {
            return findPhotos().stream().filter(e ->  startTimestamp == null || e.getTimeStamp().compareTo(startTimestamp) > 0)
                    .filter(e -> endTimestamp == null || e.getTimeStamp().compareTo(endTimestamp) < 0)
                    .filter(e -> keywords == null || e.getCaption().contains(keywords))
                    .filter(e -> longitude == 0 ||  Math.abs(e.getLongitude() - longitude) < 1)
                    .filter(e -> latitude == 0 || Math.abs(e.getLatitude() - latitude) < 1)
                    .collect(Collectors.toList());
    }

    public List<Photo> findPhotos() {
        File[] fileList = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath(), "/Android/data/com.example.photogallery/files/Pictures").listFiles();
        List<Photo> photos = new ArrayList<>();
        if(fileList != null) {
            for(File f : fileList) {
                Photo photo = new GalleryPhoto(f.getPath(), f.getAbsolutePath());
                String[] segment = f.getName().split("_");
                try {
                    photo.setCaption(segment[CAPTION_INDEX]);
                    photo.setTimeStamp(storedFormat.parse(segment[CAPTION_INDEX] + "_" + segment[TIMESTAMP_INDEX]));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                try {
                    photo.setLongitude(Float.parseFloat(segment[LON_INDEX]));
                    photo.setLatitude(Float.parseFloat(segment[LAT_INDEX]));
                } catch (Exception e) {

                }
                photos.add(photo);
            }
        }
        return photos;
    }
}
