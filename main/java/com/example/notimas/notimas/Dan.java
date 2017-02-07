package com.example.notimas.notimas;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;


class Dan {
	protected static final String TAG = "Dan Debug";
	float danX;
	float danY;
	float danVelocityX;
	float danVelocityY;
	float acclenrationX;
	float acclenrationY;
	float scaleGrowSpeed = 0;
	float danScale = 1;
	float processFrqPara;
	float liveDecline = 0;
	float radian360 = 0;
	int danTextureNum = 3;
	boolean alpha = true;
	boolean reverseAlpha = false;
	List<ChildDan> childDans = null;
	
	static final int maxLiveCount = 480;
	float maxLiveCountInv = 1f / (float)maxLiveCount;
	int liveCount = maxLiveCount;
	
	Dan(float dX, float dY, float dV, float dAngel, float processFrqPara) {
		float tempVX = DanFunction.VA2X(dV, dAngel) * processFrqPara;
		float tempVY = DanFunction.VA2Y(dV, dAngel) * processFrqPara;
		this.processFrqPara = processFrqPara;
		danX = dX;
		danY = dY;
		danVelocityX = tempVX;
		danVelocityY = tempVY;
	}

	Dan(float dX, float dY) {
		danX = dX;
		danY = dY;
	}
	
	int getDantextureNum(){
		return danTextureNum;
	}
	
	public interface OnCollisionListener {
		void OnCollision(Dan dan, float collisionAngle);
	}

	private OnCollisionListener mOnDeviceStateChangeListener = null;
	
	Dan setOnCollisionListener(OnCollisionListener mOnCollisionListener){
		mOnDeviceStateChangeListener = mOnCollisionListener;
		return this;
	}
	
	Dan collision(Dan dan, float collisionAngle){
		if(mOnDeviceStateChangeListener != null){
			mOnDeviceStateChangeListener.OnCollision(dan, collisionAngle);
		}
		return this;
	}
	
	void danStep(){
		danX += danVelocityX;
		danVelocityX += acclenrationX;
		danY += danVelocityY;
		danVelocityY += acclenrationY;
		danScale += scaleGrowSpeed;
		if(danX < -1.5 || danX > 1.5 || danY < -1.5 || danY > 1.5){
			liveCount = 0;
		}
		liveCount -= liveDecline;
	}
	
	Dan push(float pushX, float pushY){
		danVelocityX += pushX;
		danVelocityY += pushY;
		return this;
	}
	
	Dan setDecline(int liveCount){
		liveDecline = processFrqPara;
		setLiveCount(liveCount);
		return this;
	}

	Dan setObstacle(float dAcc){
		acclenrationX = -danVelocityX * dAcc * processFrqPara;
		acclenrationY = -danVelocityY * dAcc * processFrqPara;
		return this;
	}
	
	boolean isAlive(){
		return liveCount > 0;
	}
	
	void danBirth(List<Dan> dans) {
		if(this.hasChild())
			dans.addAll(this.getChildDans(processFrqPara));
	}
	
	void danDie() {
		liveCount = -1;
	}
	
	Dan setDanAlpha(Boolean alpha){
		this.alpha = alpha;
		return this;
	}
	
	Dan setScaleGrowSpeed(float scaleGrowSpeed){
		this.scaleGrowSpeed = scaleGrowSpeed;
		return this;
	}
	
	Dan setScale(float scale){
		this.danScale = scale;
		return this;
	}

	Dan setLiveCount(int liveCount){
		this.liveCount = liveCount;
		return this;
	}
	
	Dan locationOffset(float minRadian, float maxRadian){
		float r = (float) (Math.random()*(maxRadian - minRadian) + minRadian);
    	float angle = (float) (Math.random()*2*Math.PI);
		this.danX += (float) Math.sin(angle)*r;
		this.danY += (float) Math.cos(angle)*r;
		return this;
	}
	
	Dan addChildDan(ChildDan childDan){
		if(childDans == null) childDans = new ArrayList<ChildDan>();
		childDans.add(childDan);
		return this;
	}
	
	boolean isStoped(){
		return danVelocityX == 0 && danVelocityY == 0;
	}
	
	boolean hasChild(){
		return childDans != null;
	}
	
	List<Dan> getChildDans(float processFrqPara){
		List<Dan> newDans = new ArrayList<Dan>();
		for(int i = 0; i < childDans.size(); i++){
			newDans.add(childDans.get(i).birth(this));
		}
		
		return newDans;
	}
}

/**
 * @author Tom_Tseng
 *
 */
class ChildDan {
	
	boolean locationWithParent = true;
	boolean VelocityInherit = true;
	boolean angleInherit = true;
	float offsetMin = 0;
	float offsetMax = 0;
	private Dan dan = null;
	
	/**
	 * @param dan
	 * default inherit velocity, angle, location on birthed
	 */
	ChildDan(Dan dan){
		this.dan = dan;
	}
	
	ChildDan(Dan dan, boolean locationWithParent,
			boolean VelocityInherit, boolean angleInherit){
		this.dan = dan;
		this.locationWithParent = locationWithParent;
		this.VelocityInherit = VelocityInherit;
		this.angleInherit = angleInherit;
	}
	
	Dan birth(Dan parent){
		if(locationWithParent){
			dan.danX = parent.danX;
			dan.danY = parent.danY;
		}
		if(VelocityInherit){
			dan.danVelocityX = parent.danVelocityX;
			dan.danVelocityY = parent.danVelocityY;
		}
		if(angleInherit){
			float v = DanFunction.XY2V(dan.danVelocityX, dan.danVelocityY);
			float angle = DanFunction.XY2A(parent.danVelocityX, parent.danVelocityY);
			dan.danVelocityX = v * (float)Math.cos(angle);
			dan.danVelocityY = v * (float)Math.sin(angle);
		}
		if(offsetMax > 0){
			dan.locationOffset(offsetMin, offsetMax);
		}
		return dan;
	}
}

class StarDan extends Dan {
	
	float rotateVelocity = 3f;
	float rotateAcclenration = 0;
	
	StarDan(float dX, float dY, float dV, float dA, float processFrqPara) {
		super(dX, dY, dV, dA, processFrqPara);
		radian360 = (float) (Math.random() * 360);
		rotateVelocity = 0.375f * processFrqPara;
	}
	
	StarDan setRotateDecline(float rotateAcclenration){
		this.rotateAcclenration = -rotateAcclenration;
		return this;
	}
	
	StarDan noRotate(){
		rotateVelocity = 0;
		rotateAcclenration = 0;
		return this;
	}

	@Override
	void danStep() {
		//Log.d(TAG, "" + radian);
		radian360 += rotateVelocity;
		rotateVelocity += rotateAcclenration;
		super.danStep();
	}
	
}

class ArrowDan extends Dan {
	
	float whirl = 0;
	
	ArrowDan(float dX, float dY, float dV, float dA, float processFrqPara) {
		super(dX, dY, dV, dA, processFrqPara);
		radian360 = dA * 57.325f;
		danTextureNum = 4;
	}
	
	ArrowDan setWhirl(float whirlPara){
		whirl = whirlPara;
		return this;
	}

	@Override
	void danStep() {
		//Log.d(TAG, "" + radian);
		super.danStep();
		if(!isStoped()){
			radian360 = -DanFunction.XY2A360(danVelocityX, danVelocityY);
		}
		danVelocityX -= whirl * danVelocityY ;
		danVelocityY += whirl * danVelocityX;
	}
	
}


class BezierCurveDan extends Dan {

	float stepTime = 0;
	float dX0 = 0;
	float dX1 = 0;
	float dX2 = 0;
	float dY0 = 0;
	float dY1 = 0;
	float dY2 = 0;


	BezierCurveDan(float dX0, float dY0, float dX1, float dY1, float dX2, float dY2, float processFrqPara) {
		super(dX0, dY0);
		this.dX0 = dX0;
		this.dX1 = dX1;
		this.dX2 = dX2;
		this.dY0 = dY0;
		this.dY1 = dY1;
		this.dY2 = dY2;
		danTextureNum = 4;
	}

	@Override
	void danStep() {
		stepTime += 0.0056;
		danX = (1-stepTime*stepTime)*dX0 + 2 * stepTime * (1 - stepTime)*dX1 + stepTime * stepTime * dX2;
		danY = (1-stepTime*stepTime)*dY0 + 2 * stepTime * (1 - stepTime)*dY1 + stepTime * stepTime * dY2;
		if(danX < -1.5 || danX > 1.5 || danY < -1.5 || danY > 1.5){
			liveCount = 0;
		}
		liveCount -= liveDecline;
	}

}