package com.example.photogallery;

public class App {
    double x = 0;
    double y = 0;


    private static App instance;

    public static App getInstance()
    {
        if (instance== null) {
            synchronized(App.class) {
                if (instance == null)
                    instance = new App();
            }
        }
        // Return the instance
        return instance;
    }

    private App()
    {
        // Constructor hidden because this is a singleton
    }
}
