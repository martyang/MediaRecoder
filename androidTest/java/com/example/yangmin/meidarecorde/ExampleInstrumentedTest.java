package com.example.yangmin.meidarecorde;

import android.content.Context;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.SearchCondition;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiSelector;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    UiDevice device;
    int width;
    int height;

    @Before
    public void setUp(){
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        width = device.getDisplayWidth();
        height = device.getDisplayHeight();


    }
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.

        Log.i("Debug","初始化");
        startRecoder();
        if(waitfor(By.text("立即开始"))){
            device.findObject(By.text("立即开始")).click();
        }
        SystemClock.sleep(1000);
        device.pressHome();

        device.swipe(width/2,height/2,width,height/2,10);
        SystemClock.sleep(1000);
        device.swipe(width/2,height/2,width,height/2,10);
        SystemClock.sleep(1000);
        device.swipe(width/2,height/2,width,height/2,10);
        SystemClock.sleep(1000);
        device.swipe(width/2,height/2,width,height/2,10);
        SystemClock.sleep(1000);
        device.pressRecentApps();
        if(waitfor(By.res("com.android.systemui:id/progress_circle_view"))){
            device.findObject(By.res("com.android.systemui:id/progress_circle_view")).click();
            SystemClock.sleep(2000);
        }
        device.swipe(width/2,height/2,width,height/2,10);
        SystemClock.sleep(1000);
        device.swipe(width/2,height/2,width,height/2,10);
        SystemClock.sleep(1000);
    }
    @After
    public void tearDown(){
        Log.i("Debug","结束");
        stopRecoder();
    }

    private void startRecoder(){
        String cmd = " am start -n com.example.yangmin.meidarecorde/com.example.yangmin.meidarecorde.MainActivity --ei type 1 --es casename case_12";
        try {
            device.executeShellCommand(cmd);
            Log.i("Main","start recoder activity");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecoder(){
        String cmd = "am start -n com.example.yangmin.meidarecorde/com.example.yangmin.meidarecorde.MainActivity --ez result "+false;
        try {
           device.executeShellCommand(cmd);
            Log.i("Main","stop recoder activity");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean waitfor(BySelector bySelector){
        long starttime = SystemClock.currentThreadTimeMillis();

        while(true){
            UiObject2 object2 = device.findObject(bySelector);
            if(object2!=null){
                return true;
            }
            SystemClock.sleep(1000);
            if(SystemClock.currentThreadTimeMillis()-starttime>5000){
                return false;
            }
        }
    }
}
