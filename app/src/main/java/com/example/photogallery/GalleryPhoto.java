package com.example.photogallery;

import java.io.File;
import java.util.Date;

public class GalleryPhoto implements Photo{

    private static final int TIMESTAMP_INDEX = 1;
    private String caption;
    private String filePath;
    private String absolutePath;
    private Date timeStamp;
    private double longitude;
    private double latitude;

    public GalleryPhoto(String filePath, String absolutePath) {
       this.filePath = filePath;
       this.absolutePath = absolutePath;
    }

    @Override
    public String getCaption() {
        return caption;
    }

    @Override
    public String getPath() {
        return filePath;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }

    @Override
    public double getLatitude() {
        return latitude;
    }

    @Override
    public Date getTimeStamp() {
        return timeStamp;
    }

    @Override
    public void setCaption(String caption) {
        this.caption = caption;
    }

    @Override
    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public void Save() {
        String[] attr = filePath.split("_");
        String path = this.getPath();
        String newPath = attr[0] + "_" + attr[TIMESTAMP_INDEX] + "_" + longitude + "_" + latitude + "_" + attr[attr.length - 1];
        File to = new File(newPath);
        File from = new File(filePath);
        from.renameTo(to);
        absolutePath = to.getAbsolutePath();
        filePath = to.getPath();

    }
}
