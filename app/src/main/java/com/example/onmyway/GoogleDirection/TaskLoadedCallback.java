package com.example.onmyway.GoogleDirection;

/**
 * Created by Vishal on 10/20/2018.
 */

public interface TaskLoadedCallback {
    void onTaskDone(String distance, String duration, Object... values);
}

