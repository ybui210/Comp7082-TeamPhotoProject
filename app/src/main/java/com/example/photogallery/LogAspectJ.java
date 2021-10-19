package com.example.photogallery;

import static android.content.ContentValues.TAG;

import android.util.Log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

public class LogAspectJ {
    @Pointcut("execution(* com.example.photogallery.TestingObj.help())")
    public void log1() {
        Log.e(TAG, "Before calling: " );
    }

    @Before("log1()")
    public void log2(final JoinPoint joinPoint) {
        Log.e(TAG, "AA--: "  + joinPoint.toLongString());
    }



}
