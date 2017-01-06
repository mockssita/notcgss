package com.example.notimas.notimas;

import android.util.Log;

public class DanFunction {
	
	static float XY2V(float x, float y){
		float vector = (float) Math.sqrt(x * x + y * y);
//		Log.d("XY2V", x + " " + y);
		return vector;
	}
	
	static float XY2A(float x, float y){
		float angle = (float) Math.atan2(y, x);
		return angle;
	}
	
	static float VA2X(float vector, float angle){
		
		float x = (float) Math.cos(angle) * vector;
//		Log.d("XY2X", "" + x);
		return x;
	}
	
	static float VA2Y(float vector, float angle){

		float y = (float) Math.sin(angle) * vector;
//		Log.d("XY2Y", "" + y);
		return y;
	}
	
	static float XY2A360(float x, float y){
		
		float angle = (float) Math.atan2(y, x) * 57.325f;
		return angle;
	}
}
