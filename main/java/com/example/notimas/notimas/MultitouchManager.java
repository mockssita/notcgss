package com.example.notimas.notimas;

import java.util.ArrayList;

/**
 * Created by Tom on 2017/2/7.
 */

public class MultitouchManager {
    int nowPointers = 0;
    ArrayList<TouchPointer> mTouchPointers= new ArrayList<TouchPointer>();

    public MultitouchManager(){
    }

    public void pointerDown(float x, float y){
        mTouchPointers.add(new TouchPointer(x, y));
    }

    public void pointerMove(int i, float x, float y){
        mTouchPointers.get(i).setNowLocation(x, y);
    }

    public void pointerEvent(int i, float x, float y){
        mTouchPointers.get(i).setEventLocation(x, y);
    }

    public void pointerUp(int i){
        mTouchPointers.remove(i);
    }

    public TouchPointer getPointer(int i){
        return mTouchPointers.get(i);
    }

    public int getPointerCount(){
        return mTouchPointers.size();
    }

    public void clear(){
        mTouchPointers.clear();
    }

    class TouchPointer{
        float downX = -1;
        float downY = -1;
        float nowX = -1;
        float nowY = -1;
        float eventX = -1; //for handle some event
        float eventY = -1;
        public float eventTimer = 0;
        public int eventCounter = 0;

        public TouchPointer(float x, float y){
            this.downX = x;
            this.downY = y;
            this.nowX = x;
            this.nowY = y;
        }

        public void setNowLocation(float x, float y){
            this.nowX = x;
            this.nowY = y;
        }

        public void setEventLocation(float x, float y){
            this.eventX = x;
            this.eventY = y;
        }
    }
}
