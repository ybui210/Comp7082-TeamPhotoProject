package com.example.photogallery;

import java.util.Date;

public interface Photo {
    String getCaption();
    String getPath();
    float getLongitude();
    float getLatitude();
    Date getTimeStamp();

    void setCaption(String caption);
    void setTimeStamp(Date timeStamp);
    void setLongitude(float longitude);
    void setLatitude(float latitude);

    void Save();
}
