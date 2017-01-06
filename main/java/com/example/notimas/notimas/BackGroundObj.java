package com.example.notimas.notimas;

import android.util.Log;

public class BackGroundObj {
	static final float BP = 1;
	public float[] mScrollingBackgroundVerticesData = {
            // X, Y, Z, U, V
    		/*
    		 *         |
    		 *   6----4|3----2
    		 *   |     | main|
    		 *   7    5|1    0
    		 * -----------------
    		 *   8    9|13  15
    		 *   |     |     |
    		 *   10--11|12--14
    		 *         |
    		 */
            -BP, -BP, 0, 1.0f, 1.0f,
            BP, -BP, 0, 0.0f, 1.0f,
            -BP,  BP, 0, 1.0f, 0.0f,
            BP, BP, 0, 0.0f, 0.0f,
            BP, BP, 0, 1.0f, 0.0f,
            BP, -BP, 0, 1.0f, 1.0f,
            BP, BP, 0, 0.0f, 0.0f,
            BP, -BP, 0, 0.0f, 1.0f,
            BP, -BP, 0, 0.0f, 0.0f,
            BP, -BP, 0, 1.0f, 0.0f,
            BP, -BP, 0, 0.0f, 1.0f,
            BP, -BP, 0, 1.0f, 1.0f,
            BP, -BP, 0, 0.0f, 1.0f,
            BP, -BP, 0, 0.0f, 0.0f,
            -BP, -BP, 0, 1.0f, 1.0f,
            -BP, -BP, 0, 1.0f, 0.0f};
	
	public float Xratio = 1;
	//float XratioInv = 1;
    public float tempX = 1;

	public float Yratio = 1;
	//float YratioInv = 1;
    public float tempY = 1;
	
	BackGroundObj(){
		;
	}
	
	BackGroundObj(float XYratio, int bgImageScale, float hwratio){
		//BackGroundObj bgo;
		switch(bgImageScale){
        case 0:
        	if(hwratio > 1)newBackGroundObj(XYratio, true, hwratio);
        	else if(1 > hwratio)newBackGroundObj(XYratio, false, hwratio);
        	break;
        case 1:
        	if(hwratio > 1)newBackGroundObj(XYratio, false, hwratio);
        	else if(1 > hwratio)newBackGroundObj(XYratio, true, hwratio);
        	break;
        case 2:
        	newBackGroundObj(XYratio, false, hwratio);
        	break;
        case 3:
        	newBackGroundObj(XYratio, true, hwratio);
        	break;
        case 4: //rotate
        	newBackGroundObjForRotate(XYratio, hwratio);
        	break;
        }
	}
	
	void newBackGroundObj(float XYratio , boolean bgmatchheight, float hwratio){
		
		if(bgmatchheight){
			Xratio = XYratio;
			if(hwratio < 1){
				Yratio = hwratio;
				Xratio *= Yratio;
			}
		} else {
			Yratio = 1f / XYratio;
			if(hwratio > 1){
				Xratio = 1 / hwratio;
				Yratio *= Xratio;
			}
		}
		//XratioInv = 1f / Xratio;
		//YratioInv = 1f / Yratio;
		for(int i=0;i<80;i+=5){
			mScrollingBackgroundVerticesData[i] *= Xratio;
		}
		for(int i=1;i<80;i+=5){
			mScrollingBackgroundVerticesData[i] *= Yratio;
		}
	}
	
	void newBackGroundObjForRotate(float XYratio , float hwratio){
		float diagonal;
		if(hwratio > 1){
			diagonal = (float)Math.sqrt(1 + Math.pow(hwratio,2));
		} else {
			diagonal = (float)Math.sqrt(1 + Math.pow(1 / hwratio,2));
		}
		
		//Log.d("diagonal", "" + diagonal);
		
		if(XYratio > 1){
			Yratio = diagonal / hwratio;
			Xratio = Yratio * XYratio;
		} else {
			Xratio = diagonal / hwratio;
			Yratio = Xratio / XYratio;
		}
		//XratioInv = 1f / Xratio;
		//YratioInv = 1f / Yratio;
		for(int i=0;i<80;i+=5){
			mScrollingBackgroundVerticesData[i] *= Xratio;
		}
		for(int i=1;i<80;i+=5){
			mScrollingBackgroundVerticesData[i] *= Yratio;
		}
	}
	
	public void ScreenSet(float width, float height){
		
	}
	
	public void yScroll(float scrollY){
		tempY = scrollY;
        mScrollingBackgroundVerticesData[14] = tempY;
        mScrollingBackgroundVerticesData[19] = tempY;
        mScrollingBackgroundVerticesData[24] = tempY;
        mScrollingBackgroundVerticesData[34] = tempY;
        mScrollingBackgroundVerticesData[54] = tempY;
        mScrollingBackgroundVerticesData[59] = tempY;
        mScrollingBackgroundVerticesData[64] = tempY;
        mScrollingBackgroundVerticesData[74] = tempY;
        
        tempY = (scrollY*2*Yratio - Yratio)*BP;
        mScrollingBackgroundVerticesData[1] = tempY;
        mScrollingBackgroundVerticesData[6] = tempY;
        mScrollingBackgroundVerticesData[26] = tempY;
        mScrollingBackgroundVerticesData[36] = tempY;
        mScrollingBackgroundVerticesData[41] = tempY;
        mScrollingBackgroundVerticesData[46] = tempY;
        mScrollingBackgroundVerticesData[66] = tempY;
        mScrollingBackgroundVerticesData[76] = tempY;
        
        tempY = (scrollY*2*Yratio);
	}
	
	public void xScroll(float scrollX){
		tempX = scrollX;
        mScrollingBackgroundVerticesData[3] = tempX;
        mScrollingBackgroundVerticesData[13] = tempX;
        mScrollingBackgroundVerticesData[33] = tempX;
        mScrollingBackgroundVerticesData[38] = tempX;
        mScrollingBackgroundVerticesData[43] = tempX;
        mScrollingBackgroundVerticesData[53] = tempX;
        mScrollingBackgroundVerticesData[73] = tempX;
        mScrollingBackgroundVerticesData[78] = tempX;
        tempX = (scrollX*2*Xratio - Xratio)*BP;
        mScrollingBackgroundVerticesData[5] = tempX;
        mScrollingBackgroundVerticesData[15] = tempX;
        mScrollingBackgroundVerticesData[20] = tempX;
        mScrollingBackgroundVerticesData[25] = tempX;
        mScrollingBackgroundVerticesData[45] = tempX;
        mScrollingBackgroundVerticesData[55] = tempX;
        mScrollingBackgroundVerticesData[60] = tempX;
        mScrollingBackgroundVerticesData[65] = tempX;

        tempX = (scrollX*2*Xratio);
	}
	
}
