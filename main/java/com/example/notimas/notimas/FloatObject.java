package com.example.notimas.notimas;

import java.util.List;

import android.util.Log;

class FloatObject {

    public float flPosX = 0;
    public float flPosY = 0;
    public float flRevoRadiu = 0;
    public float flRevoAngle = 0;
    public float flScale = 30;
    public float flAngle = 0;
    public float flpTime = 0;
    public float flpPeriod = 960;
    public float floatObjCount = 0;
    public float flRotateAngle = 0;
    public float processFrqPara = 0;
	public List<Dan> dans;
    
	FloatObject( List<Dan> dans){
		this.dans = dans;
	}
	
	void step(float processFrqPara){
		this.processFrqPara = processFrqPara;
		flAngle += flRotateAngle;
		flpTime += processFrqPara * 0.05;
		//flRevoAngle = (float) (60f * Math.cos(6.28f * 0.003 * flpTime)+180);
		if(flpTime > 360f){
			flpTime -= 360f;
		}else if(flpTime < 0){
			flpTime += 360f;
		}
		flRevoAngle = flpTime;
		if(floatObjCount <= 0){
			shoot();
	    	floatObjCount = 120f/processFrqPara;
		}else{
			floatObjCount--;
		}
	}

	void shoot(){
		StarDan sd = (StarDan) new StarDan(
				flPosX + DanFunction.VA2X(flRevoRadiu, flRevoAngle * 0.0175f +1.57f),
				flPosY + DanFunction.VA2Y(flRevoRadiu, flRevoAngle * 0.0175f +1.57f),
				0.001f,
				(float)(Math.random())*0.5f + 1.32f ,
				processFrqPara)
				.setRotateDecline(0.008f)
				.setScale(0.3f)
				.setScaleGrowSpeed(0.002f)
				.setDanAlpha(false).setOnCollisionListener(new Dan.OnCollisionListener(){

					@Override
					public void OnCollision(Dan dan, float fCollisionAngle) {
						// TODO Auto-generated method stub
						dan.addChildDan(new ChildDan(new StarDan(0, 0,
								(float) (DanFunction.XY2V(dan.danVelocityX, dan.danVelocityY)*0.1f),
								(float) (fCollisionAngle * 2 - DanFunction.XY2A(dan.danVelocityX, dan.danVelocityY) + (Math.random()*0.5) + 2.99f), FloatObject.this.processFrqPara)
								.setScale(0.25f).setDecline(0, 500) , true, false, false))
								.addChildDan(new ChildDan(new StarDan(0, 0,
										(float) (DanFunction.XY2V(dan.danVelocityX, dan.danVelocityY)*0.1f),
										(float) (fCollisionAngle * 2 - DanFunction.XY2A(dan.danVelocityX, dan.danVelocityY) + (Math.random()*0.5) + 2.99f), FloatObject.this.processFrqPara)
										.setScale(0.25f).setDecline(0, 500) , true, false, false))
								.addChildDan(new ChildDan(new StarDan(0, 0,
										(float) (DanFunction.XY2V(dan.danVelocityX, dan.danVelocityY)*0.1f),
										(float) (fCollisionAngle * 2 - DanFunction.XY2A(dan.danVelocityX, dan.danVelocityY) + (Math.random()*0.5) + 2.99f), FloatObject.this.processFrqPara)
										.setScale(0.25f).setDecline(0, 500) , true, false, false));
						dan.danDie();
					}

				});
		dans.add(sd);
	}
}
