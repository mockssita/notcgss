package com.example.notimas.notimas;

import java.util.List;

import android.util.Log;

class FloatObject {

    public float flPosX = 0;
    public float flPosY = 0;
    public float flScale = 10;
    public float flAngle = 0;
    public float flpTime = 0;
    public float flpPeriod = 960;
    public float floatObjCount = 0;
    public float flRotateAngle = 0;
    public float processFrqPara = 0;
	public float targetX = 0;
	public float targetY = 0;
	public List<Dan> dans;

	public FloatObject( List<Dan> dans){
		this.dans = dans;
	}
	public FloatObject(){}
	
	public void step(float processFrqPara){
		this.processFrqPara = processFrqPara;
		flAngle += flRotateAngle;
		flpTime += processFrqPara * 0.05;
		if(flpTime > 360f){
			flpTime -= 360f;
		}else if(flpTime < 0){
			flpTime += 360f;
		}
		if(floatObjCount <= 0){
			shoot(targetX, targetY);
	    	floatObjCount = 600f/processFrqPara;
		}else{
			floatObjCount--;
		}
	}

	public void setTarget(float targetX, float targetY) {
		Log.d("FLOAT", "setTarget: " + targetX + ", " + targetY);
		Log.d("FLOAT", "pos: " + flPosX + ", " + flPosY);
		this.targetX = targetX;
		this.targetY = targetY;
	}

	public void shoot(float dx, float dy){
		if(dans == null){ return;}
		BezierCurveDan bcd = (BezierCurveDan) new BezierCurveDan(
				flPosX,
				flPosY,
				dx,
				0.6f,
				dx,
				dy,
				processFrqPara)
				.setScale(0.3f)
				.setScaleGrowSpeed(0.0015f);
		dans.add(0, bcd);
	}

	public void shoot(FloatObject target){
		if(dans == null){ return;}
		Note note = (Note) new Note(
				flPosX,
				flPosY,
				target,
				0.0083f)
				.setScale(0);
		dans.add(0, note);
	}
}
