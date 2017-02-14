package com.example.notimas.notimas;

/**
 * Created by Tom on 2017/2/7.
 */

public class keyboard {
    float height = 1;
    float width =  0.07f;
    int pressingBy = -1;
    int rightSlidedBy = -1;
    int leftSlidedBy = -1;


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
