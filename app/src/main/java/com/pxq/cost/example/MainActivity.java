package com.pxq.cost.example;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.pxq.cost.api.MethodCost;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    testCost(1000);
                    JavaBean javaBean = testCostWithReturn(2000);
                    Log.d(TAG, "run: " + javaBean.toString());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @MethodCost
    public void testCost(int x) throws InterruptedException {
        Thread.sleep(x);
    }

    @MethodCost
    public JavaBean testCostWithReturn(int x) throws InterruptedException {
        Thread.sleep(x);
        return new JavaBean("testCostReturn", 1);
    }
}
