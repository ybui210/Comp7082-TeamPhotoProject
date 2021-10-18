package com.example.photogallery;

import java.util.Date;

public interface Photo {
    String getCaption();
    String getPath();
    double getLongitude();
    double getLatitude();
    Date getTimeStamp();

    void setCaption(String caption);
    void setTimeStamp(Date timeStamp);
    void setLongitude(double longitude);
    void setLatitude(double latitude);

    void Save();
}
