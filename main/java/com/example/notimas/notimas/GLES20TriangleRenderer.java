/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.notimas.notimas;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;

class GLES20TriangleRenderer implements GLSurfaceView.Renderer{

    public GLES20TriangleRenderer(Context context) {
    	mContext = context;
    }
    
    float bgAngle = 0;
    float scrollX = 0;
    float scrollXSpeed = 0;
    float scrollY = 0;
    float scrollYSpeed = 0;
    boolean onDrawFrameWorkingFlag = false;
    BackGroundObj bgo;
    long endTime, startTime, dt;
    long FramePeriod = 16;
    float last = 0;
    MultitouchManager mMultitouchManager = new MultitouchManager();
    
    public void onDrawFrame(GL10 glUnused) {
        // Ignore the passed-in GL10 interface, and use the GLES20
        // class's static methods instead.
    	endTime = System.currentTimeMillis();
        dt = endTime - startTime;
        //Log.d(TAG, "" + dt);
        if (dt < FramePeriod)
			try {
				Thread.sleep(FramePeriod - dt);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        startTime = System.currentTimeMillis();
    	
        runObjectStep();
        runGL();
    }
    
    private void runGL() {
		// TODO Auto-generated method stub

    	onDrawFrameWorkingFlag = true;
        GLES20.glClearColor(BaseColor[0], BaseColor[1], BaseColor[2], 1.0f);
        GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glUseProgram(mProgram);
        

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        
        GLES20.glUniform1f(maAlphaHandle, 1.0f);
        GLES20.glEnableVertexAttribArray(maAlphaHandle);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
        Matrix.setRotateM(mMMatrix, 0, 0, 0, 0, 1.0f);
        Matrix.translateM(mMMatrix, 0, 0.0f, 0.0f, 0.0f);
        //X scroll
        bgo.xScroll(scrollX);

        //Y scroll
        bgo.yScroll(scrollY);
        //Log.d("Y", String.valueOf(scrollY));
        //Log.d("bgo", bgo.mScrollingBackgroundVerticesData[5] + ", " + 
        //		(bgo.mScrollingBackgroundVerticesData[5] - last));
        //last = bgo.mScrollingBackgroundVerticesData[5];
        
		mScrollingBackgroundVertices.clear();
		mScrollingBackgroundVertices.put(bgo.mScrollingBackgroundVerticesData).position(0);
        if(bgRotateAngle != 0){
	        Matrix.rotateM(mMMatrix, 0, bgAngle, 0, 0, 1.0f);
        }
        drawBackground(mScrollingBackgroundVertices);
        //float

        for(int i = 0; i < fobjs.size(); i++) {
            GLES20.glUniform1f(maAlphaHandle, 1.0f);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[1]);
            Matrix.setRotateM(mMMatrix, 0, 0, 0f, 0f, 1f);
            Matrix.translateM(mMMatrix, 0, fobjs.get(i).flPosX, fobjs.get(i).flPosY, 0f); 	//rotate center
            reRoSa(fobjs.get(i));
            drawobj(mTriangleVertices);
        }
        //float end
        /*
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[2]);
        Matrix.setRotateM(mMMatrix, 0, 0, 0, 0, 1.0f);
        Matrix.translateM(mMMatrix, 0, 0.0f, 0.0f, 0.0f);
        drawobj(mTriangleVertices2);
        */
        drawDans(dans);
        drawDans(notes);
        drawDans(touchDans);
        if(dans != null){
			for(int i = 0;i < dans.size();i++){
				//Log.d(TAG, i + "," + dans.get(i).danX + "," + 
				//dans.get(i).danY  + "," + dans.get(i).liveCount);
				Dan dan = dans.get(i);
				GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[dan.getDantextureNum()]);
		        Matrix.setRotateM(mMMatrix, 0, 0, 0, 0, 1.0f);
		        Matrix.translateM(mMMatrix, 0, dan.danX, dan.danY, 0.0f);
		        Matrix.rotateM(mMMatrix, 0, dan.radian360, 0, 0, 1.0f);
		        //Log.d(TAG, "" + dan.radian);
		        GLES20.glUniform1f(maAlphaHandle, dan.alpha);
		        Matrix.scaleM(mMMatrix, 0, dan.danScale, dan.danScale, 1); 			//scale
		        drawobj(mDanVertices);
			}
		}
        if(touchDans != null){
            for(int i = 0;i < touchDans.size();i++){
                //Log.d(TAG, i + "," + dans.get(i).danX + "," +
                //dans.get(i).danY  + "," + dans.get(i).liveCount);
                Dan dan = touchDans.get(i);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[dan.getDantextureNum()]);
                Matrix.setRotateM(mMMatrix, 0, 0, 0, 0, 1.0f);
                Matrix.translateM(mMMatrix, 0, dan.danX, dan.danY, 0.0f);
                Matrix.rotateM(mMMatrix, 0, dan.radian360, 0, 0, 1.0f);
                //Log.d(TAG, "" + dan.radian);
                GLES20.glUniform1f(maAlphaHandle, dan.alpha);
                Matrix.scaleM(mMMatrix, 0, dan.danScale, dan.danScale, 1); 			//scale
                drawobj(mDanVertices);
            }
        }

       onDrawFrameWorkingFlag = false;
	}

    private void drawDans(List<Dan> dans){
        if(dans != null){
            for(int i = 0;i < dans.size();i++){
                //Log.d(TAG, i + "," + dans.get(i).danX + "," +
                //dans.get(i).danY  + "," + dans.get(i).liveCount);
                Dan dan = dans.get(i);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[dan.getDantextureNum()]);
                Matrix.setRotateM(mMMatrix, 0, 0, 0, 0, 1.0f);
                Matrix.translateM(mMMatrix, 0, dan.danX, dan.danY, 0.0f);
                Matrix.rotateM(mMMatrix, 0, dan.radian360, 0, 0, 1.0f);
                //Log.d(TAG, "" + dan.radian);
                GLES20.glUniform1f(maAlphaHandle, dan.alpha);
                Matrix.scaleM(mMMatrix, 0, dan.danScale, dan.danScale, 1); 			//scale
                drawobj(mDanVertices);
            }
        }
    }

    int shootnumber = 0;

	private void runObjectStep() {
		// TODO Auto-generated method stub
		cooldown -= processFrqPara;
    	if(bgRotateAngle != 0){
    		bgAngle += bgRotateAngle;
    	} else {
    		bgAngle = 0;
    	}

		
		//background
		
		scrollX += scrollXSpeed;
		if(scrollX >= 1) scrollX -= 1f;
		else if(scrollX < 0f) scrollX += 1f;
		scrollY += scrollYSpeed;
		if(scrollY >= 1) scrollY -= 1f;
		else if(scrollY < 0f) scrollY += 1f;
		
		//float object
//        for(int i = 0; i < fobjs.size(); i++) {
//            fobjs.get(i).step(processFrqPara);
//        }
//        for(int i = 0; i < shootFobjs.size(); i++) {
//            shootFobjs.get(i).step(processFrqPara);
//        }
        if (cooldown < 0){
            shootnumber = (int)(Math.random() * 5f);
            if(shootnumber > 4){
                shootnumber = 4;
            }
            shootFobjs.get(shootnumber).shoot(fobjs.get(shootnumber));
            cooldown = 180;
        }

		//dan
        dansStep(dans);
        dansStep(touchDans);
        dansStep(notes);
	}

    private void dansStep(List<Dan> dans){
        if(dans != null){
            for(int i = 0;i < dans.size();i++){
                if(!dans.get(i).isAlive()){
                    if(!onDrawFrameWorkingFlag){
                        dans.get(i).danBirth(dans);
                        dans.remove(i);
                        i--;
                    }
                } else {
                    dans.get(i).danStep();
                }
            }
        }
    }

	void reRoSa(FloatObject fobj){
        Matrix.rotateM(mMMatrix, 0, fobj.flAngle, 0f, 0f, 1f);	//rotation
        Matrix.scaleM(mMMatrix, 0, fobj.flScale, fobj.flScale, 1); 			//scale
    }
    
    void drawobj(FloatBuffer TriangleVertices){

        
        TriangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false,
                TRIANGLE_VERTICES_DATA_STRIDE_BYTES, TriangleVertices);
        TriangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glVertexAttribPointer(maTextureHandle, 2, GLES20.GL_FLOAT, false,
                TRIANGLE_VERTICES_DATA_STRIDE_BYTES, TriangleVertices);
        GLES20.glEnableVertexAttribArray(maTextureHandle);
        
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mMMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4);
    }
    
    void drawBackground(FloatBuffer TriangleVertices){

        
        TriangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false,
                TRIANGLE_VERTICES_DATA_STRIDE_BYTES, TriangleVertices);
        TriangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glVertexAttribPointer(maTextureHandle, 2, GLES20.GL_FLOAT, false,
                TRIANGLE_VERTICES_DATA_STRIDE_BYTES, TriangleVertices);
        GLES20.glEnableVertexAttribArray(maTextureHandle);
        
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mMMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 16);
    }
    
    Timer timer;
    float ScreenScaleRatio = 0;
    float hwratio;
    float wratio = 1;
    float hratio = 1;
    float processFrqPara = 8;
    float bgScrollTick = 0.00005f;

    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        // Ignore the passed-in GL10 interface, and use the GLES20
        // class's static methods instead.
    	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
    	Log.d("test", "onSurfaceChanged");
        GLES20.glViewport(0, 0, width, height);
        if(mMultitouchManager != null) {
            mMultitouchManager.clear();
        }
        touchDans = new ArrayList<Dan>();
        hwratio = (float) height / width;
        if(height > width){
        	wratio = 1/hwratio;
        	hratio = 1;
        	ScreenScaleRatio = 1f / height;
        } else {
        	wratio = 1;
        	hratio = hwratio;
        	ScreenScaleRatio = 1f / width;
        }
        Matrix.orthoM(mProjMatrix, 0, -wratio, wratio, -hratio, hratio, 3, 7);
        //Matrix.frustumM(mProjMatrix, 0, -wratio, wratio, -hratio, hratio, 5, 10);
        
        bgo = new BackGroundObj(bgXYratio, bgImageScale, hwratio);

    }
	void vertecInit(){

        mTriangleVertices = ByteBuffer.allocateDirect(mTriangleVerticesData.length
                * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTriangleVertices.put(mTriangleVerticesData).position(0);
        
        mDanVertices = ByteBuffer.allocateDirect(mDanVerticesData.length
                * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mDanVertices.put(mDanVerticesData).position(0);
        
        mScrollingBackgroundVertices = ByteBuffer.allocateDirect(
        		new BackGroundObj().mScrollingBackgroundVerticesData.length
                * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mScrollingBackgroundVertices.put(
        		new BackGroundObj().mScrollingBackgroundVerticesData).position(0);
        
	}
	
	List<Dan> touchDans = null;
	List<Dan> dans = new ArrayList<Dan>();
    List<Dan> notes = new ArrayList<Dan>();
    List<FloatObject> fobjs = null;
    List<FloatObject> shootFobjs = null;
    int[] textures = new int[5];
    public float bgRotateAngle = 0;
    int bgImageScale = 0;
    float BaseColor[] = {0.6f, 0.33f, 0.4f};
    boolean flEnable = false;
    
    final static int spdefault = 25700;
	
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        // Ignore the passed-in GL10 interface, and use the GLES20
        // class's static methods instead.
    	Log.d("test", "onSurfaceCreated");
    	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
    	screenclear();
        processFrqPara = FramePeriod * 0.75f;
        Log.d("processFrqPara", "" + processFrqPara);

        FramePeriod = Integer.valueOf(sp.getString("FramePeriod", "16"));
        if(fobjs == null) {
            fobjs = new ArrayList<FloatObject>();
            for (int i = 0; i < 5; i++) {
                fobjs.add(new FloatObject(null));
                fobjs.get(i).flScale = 8;
                fobjs.get(i).flPosX = -0.7f + i * 0.35f;
                fobjs.get(i).flPosY = -0.35f;
            }
        }

        if(shootFobjs == null) {
            shootFobjs = new ArrayList<FloatObject>();
            for (int i = 0; i < 5; i++) {
                shootFobjs.add(new FloatObject(notes));
                shootFobjs.get(i).floatObjCount = 8 * i;
                shootFobjs.get(i).flScale = 8;
                shootFobjs.get(i).flPosX = -0.6f + i * 0.3f;
                shootFobjs.get(i).flPosY = 0.35f;
//                shootFobjs.get(i).setTarget(-0.7f + i * 0.35f, -0.35f);
            }
        }
        //base color
        int color = Color.parseColor(sp.getString("BaseColor", "#995566"));
        BaseColor[0] = ((color>>16)&0xff) / 255f;	//R
        BaseColor[1] = ((color>>8)&0xff) / 255f;	//G
        BaseColor[2] = (color&0xff) / 255f;			//B
        
        mProgram = createProgram(mVertexShader, mFragmentShader);
        if (mProgram == 0) {
            return;
        }
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        checkGlError("glGetAttribLocation aPosition");
        if (maPositionHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aPosition");
        }
        maTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");
        checkGlError("glGetAttribLocation aTextureCoord");
        if (maTextureHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aTextureCoord");
        }

        maAlphaHandle = GLES20.glGetUniformLocation(mProgram, "aAlpha");
        Log.d(TAG,"glGetUniformLocation aAlpha");
        if (maAlphaHandle == -1) {
        	Log.d(TAG,"Could not get attrib location for aAlpha");
        }
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        checkGlError("glGetUniformLocation uMVPMatrix");
        if (muMVPMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uMVPMatrix");
        }
        
        createTexture();
    	vertecInit();
        
        Matrix.setLookAtM(mVMatrix, 0, 0, 0, -5, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
    }
    
    public void createTexture(){

        /*
         * Create our texture. This has to be done each time the
         * surface is created.
         */
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
    	GLES20.glGenTextures(5, textures, 0);
    	
    	Uri uri = Uri.parse("file://" + sp.getString("BgCustomImg", ""));
    	Log.d("test", "uri" + uri.getPath());
        
        try {
			loadImage2Trxtures(textures[0], 
					mContext.getContentResolver().openInputStream(uri));
		} catch (FileNotFoundException e) {
	        loadImage2Trxtures(textures[0], mContext.getResources()
	                .openRawResource(R.raw.background));
		}
        bgXYratio = ImageXYratio;
        
        uri = Uri.parse("file://" + sp.getString("FlCustomImg", ""));
        try {
			loadImage2Trxtures(textures[1], 
					mContext.getContentResolver().openInputStream(uri));
		} catch (FileNotFoundException e) {
	        loadImage2Trxtures(textures[1], mContext.getResources()
	                .openRawResource(R.raw.test_button));
		}
        mTriangleVerticesDataScale(ImageXYratio);
        
        loadImage2Trxtures(textures[2], mContext.getResources()
                .openRawResource(R.raw.arrow));
        
        loadImage2Trxtures(textures[3], mContext.getResources()
                .openRawResource(R.raw.arrow));
        
        loadImage2Trxtures(textures[4], mContext.getResources()
                .openRawResource(R.raw.arrow));
    }
    
    float ImageXYratio = 1;
    float bgXYratio = 1;

    void mTriangleVerticesDataScale(float ImageXYratio){
    	mTriangleVerticesData = null;
    	mTriangleVerticesData = mTriangleVerticesOriginData.clone();
    	float Xratio = 1, Yratio = 1;
    	if(ImageXYratio < 1){
			Xratio = ImageXYratio;
		} else {
			Yratio = 1f / ImageXYratio;
		}
    	Log.i("XY", String.valueOf(Yratio * OBJRATIO));
		//XratioInv = 1f / Xratio;
		//YratioInv = 1f / Yratio;
		for(int i=0;i<20;i+=5){
			mTriangleVerticesData[i] *= Xratio;
		}
		for(int i=1;i<20;i+=5){
			mTriangleVerticesData[i] *= Yratio;
		}
    }
    
    private void loadImage2Trxtures(int textures, InputStream is){
        
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures);

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);

        Bitmap bitmap, bitmap2;
        try {
            bitmap = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
            } catch(IOException e) {
                // Ignore.
            }
        }
        Rect src, dest;
        if(4000000 < bitmap.getHeight() * bitmap.getWidth()){
        	src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            if(bitmap.getHeight() > bitmap.getWidth()){
            	
                float scaleratio = 2000f / ((float)bitmap.getHeight());
        		bitmap2 = Bitmap.createBitmap((int) (2000f * scaleratio), 2000,
        				bitmap.getConfig());
        		
	        	dest = new Rect(0, 0, bitmap2.getWidth(), bitmap2.getHeight());
        		new Canvas(bitmap2).drawBitmap(bitmap, src, dest, null);
        	} else {
        		float scaleratio = 2000f / bitmap.getWidth();
        		Log.d(TAG, "scaleratio" + scaleratio + ", " + bitmap.getWidth());
                
        		bitmap2 = Bitmap.createBitmap(2000, (int)(2000f * scaleratio),
        				bitmap.getConfig());
        		Log.d(TAG, "bitmap2 : " + bitmap2.getWidth() + ", " + bitmap2.getHeight());
        		
	        	dest = new Rect(0, 0, bitmap2.getWidth(), bitmap2.getHeight());
        		new Canvas(bitmap2).drawBitmap(bitmap, src, dest, null);
        	}
            bitmap.recycle();
            bitmap = bitmap2;
        }
        ImageXYratio = ((float)bitmap.getWidth()) / ((float)bitmap.getHeight());
    	Log.d(TAG, "bitmap1 : " + bitmap.getWidth() + ", " + bitmap.getHeight());
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
    } //loadImage2Trxtures

    public static int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                Log.e(TAG, "Could not compile shader " + shaderType + ":");
                Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    private int createProgram(String vertexSource, String fragmentSource) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }

        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) {
            return 0;
        }

        int program = GLES20.glCreateProgram();
        if (program != 0) {
            GLES20.glAttachShader(program, vertexShader);
            checkGlError("glAttachShader");
            GLES20.glAttachShader(program, pixelShader);
            checkGlError("glAttachShader");
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e(TAG, "Could not link program: ");
                Log.e(TAG, GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }

    private void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }
    
    public void timerCancel(){
    	if(null != timer){
    		timer.cancel();
    		timer = null;
    		Log.d(TAG, "timer cancel");
    	}
    }
    
    public void screenclear(){
    	GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        
    }
    
    int cooldown = 0;
    float LastCreateDanX;
    float LastCreateDanY;
    int slideCount = 0;

    public void touchEvent (MotionEvent e){

        // get pointer index from the event object
        int pointerIndex = e.getActionIndex();

        // get pointer ID
        int pointerId = e.getPointerId(pointerIndex);

        // get masked (not specific to a pointer) action
        int maskedAction = e.getActionMasked();

        switch (maskedAction) {

            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN: {
                Log.d(TAG, "Down:" + pointerIndex);
                mMultitouchManager.pointerDown(e.getX(pointerIndex), e.getY(pointerIndex));
                createTouchDan(mMultitouchManager.getPointer(pointerIndex));
                calculateKeyboard(e.getX(pointerIndex), e.getY(pointerIndex));
                break;
            }
            case MotionEvent.ACTION_MOVE: { // a pointer was moved
                Log.d(TAG, "Move:" + pointerIndex);
                for(int i = 0; i < e.getPointerCount();i++){
                    mMultitouchManager.pointerMove(i, e.getX(i), e.getY(i));
                    createTouchDan(mMultitouchManager.getPointer(i));
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL: {
                Log.d(TAG, "Up:" + pointerIndex);
                mMultitouchManager.pointerUp(pointerIndex);
                break;
            }
        }
    }
    
    public void createTouchDan(MultitouchManager.TouchPointer tp){
    	float radius = (float) Math.sqrt(Math.pow(tp.nowX - tp.eventX, 2)
    			+ Math.pow(tp.nowY - tp.eventY, 2));
//    	Log.d(TAG, LastCreateDanX + "," + LastCreateDanY + "," + radius);
    	
    	if(tp.eventTimer <= 0){
	    	float v = (float) (Math.random()*0.00125 + 0.00125f);
	    	float angle = (float) (Math.random()*2*Math.PI);

	    	//Log.d("aa", String.valueOf((float)(angle)));
//	    	touchDans.add(new StarDan(wratio - x * ScreenScaleRatio * 2,
//	    			hratio - y * ScreenScaleRatio * 2, v, angle,
//	    			processFrqPara).setScale(0.7f).setDecline(0.001f, 960)
//	    	);
	    	tp.eventTimer = 96;
            tp.setEventLocation(tp.nowX, tp.nowY);
            tp.eventCounter = 0;
    	}else if(120 < radius){
	    	float v = (float) (Math.random()*0.00125 + 0.00125f);
	    	float angle = (float) (Math.random()*2*Math.PI);
	    	//
            tp.eventCounter += 1;
	    	//Log.d(TAG, "" + slideCount);
	    	float temp = 120f/radius;
            tp.setEventLocation(tp.eventX + (tp.nowX - tp.eventX)*temp,
                    tp.eventY + (tp.nowY - tp.eventY)*temp);
//	    	touchDans.add(new StarDan(wratio - x * ScreenScaleRatio * 2,
//	    			hratio - y * ScreenScaleRatio * 2, v,
//	    			angle, processFrqPara).setScale(0.6f).setDecline(0.001f, 960)
//	    	);
	    	ArrowDan sd = (ArrowDan) new ArrowDan(wratio - tp.eventX * ScreenScaleRatio * 2,
	    			hratio - tp.eventY * ScreenScaleRatio * 2,
	    			0,
	    			DanFunction.XY2A(tp.nowX-tp.eventX, tp.nowY-tp.eventY) + 1.57f,
	    			processFrqPara)
	    			//.locationOffset(0.05f, 0.15f)
	    			.setScale(0.5f).setDanAlpha(1).setLiveCount(960).setDecline(processFrqPara);
//	    	sd.addChildDan(new ChildDan(new StarDan(0, 0, 
//	    			(float) (Math.random()*0.00125 + 0.00125f),
//	    			(float) DanFunction.XY2A(x - LastCreateDanX, LastCreateDanY - y), processFrqPara)
//	    			.setRotateDecline(0.015f).setScale(0.4f).setDecline(0.001f, 960) , true, false, false));
//	    	sd.addChildDan(new ChildDan(new StarDan(0, 0, 
//	    			(float) (Math.random()*0.00125 + 0.00125f),
//	    			(float) DanFunction.XY2A(x - LastCreateDanX, LastCreateDanY - y), processFrqPara)
//	    			.setRotateDecline(0.015f).setScale(0.4f).setDecline(0.001f, 960) , true, false, false));
//	    	sd.addChildDan(new ChildDan(new StarDan(0, 0, 
//	    			(float) (Math.random()*0.00125 + 0.00125f),
//	    			(float) DanFunction.XY2A(x - LastCreateDanX, LastCreateDanY - y), processFrqPara)
//	    			.setRotateDecline(0.015f).setScale(0.4f).setDecline(0.001f, 960) , true, false, false));
	    	sd.setLiveCount(sd.liveCount + 32 * (tp.eventCounter - 3));
	    	touchDans.add(sd);
	    	tp.eventTimer = 96;
            createTouchDan(tp);
    	}
    }

    void calculateKeyboard(float screenX, float screenY){
        Log.d(TAG, "calculateKeyboard" + screenX);
        float glX = wratio - screenX * ScreenScaleRatio * 2;
        if(glX >= -0.875f && glX < -0.525f){
            elimiNote(fobjs.get(0));
        }else if(glX >= -0.525f && glX < -0.175f){
            elimiNote(fobjs.get(1));
        }else if(glX >= -0.175f && glX < 0.175f){
            elimiNote(fobjs.get(2));
        }else if(glX >= 0.175f && glX < 0.525f){
            elimiNote(fobjs.get(3));
        }else if(glX >= 0.525f && glX < 0.875f){
            elimiNote(fobjs.get(4));
        }
    }

    void elimiNote(FloatObject fobj){
        Log.d(TAG, "elimiNote()");
        for(int i = notes.size() - 1; i >= 0; i--){
            Note mNote = (Note)notes.get(i);
            if(mNote.target == fobj){
                if(mNote.stepTime > 0.97f && mNote.stepTime < 1.03f){
                    dans.add(dans.size(), new EmiliEffectDan(mNote.target.flPosX, mNote.target.flPosY)
                            .setScale(0.2f).setScaleGrowSpeed(0.03f).setLiveCount(30).setDecline(1).setMaxLiveCount(30));
                    mNote.danDie();
                    break;
                }else if(mNote.stepTime > 0.94f && mNote.stepTime < 1.06f){
                    dans.add(dans.size(), new EmiliEffectDan(mNote.target.flPosX, mNote.target.flPosY)
                            .setScale(0.2f).setScaleGrowSpeed(0.015f).setLiveCount(30).setDecline(1).setMaxLiveCount(30));
                    mNote.danDie();
                }
            }else if(mNote.stepTime <= 0.9){
                break;
            }
        }
    }

    public void pressKeyboard(float x, float y, int touchIndex){

    }

    private static final int FLOAT_SIZE_BYTES = 4;
    private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;
    private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
    private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;
    private static final float OBJRATIO = 0.01f;
    private final float[] mTriangleVerticesOriginData = {
            // X, Y, Z, U, V
    		OBJRATIO, -OBJRATIO, 0, 0.0f, 1.0f,
            -OBJRATIO, -OBJRATIO, 0, 1.0f, 1.0f,
            -OBJRATIO,  OBJRATIO, 0, 1.0f, 0.0f,
            OBJRATIO, OBJRATIO, 0, 0.0f, 0.0f
            };
    private float[] mTriangleVerticesData = {
            // X, Y, Z, U, V
    		OBJRATIO, -OBJRATIO, 0, 0.0f, 1.0f,
            -OBJRATIO, -OBJRATIO, 0, 1.0f, 1.0f,
            -OBJRATIO,  OBJRATIO, 0, 1.0f, 0.0f,
            OBJRATIO, OBJRATIO, 0, 0.0f, 0.0f
            };
    
    private final float[] mTriangleVerticesData2 = {
            // X, Y, Z, U, V
    		1, -1, 0, 0.0f, 1.0f,
            -1, -1, 0, 1.0f, 1.0f,
            -1,  1, 0, 1.0f, 0.0f,
            1, 1, 0, 0.0f, 0.0f
            };
    
    private final float[] mDanVerticesData = {
            // X, Y, Z, U, V
    		0.15f, -0.15f, 0, 0.0f, 1.0f,
            -0.15f, -0.15f, 0, 1.0f, 1.0f,
            -0.15f,  0.15f, 0, 1.0f, 0.0f,
            0.15f, 0.15f, 0, 0.0f, 0.0f
            };
    
    private FloatBuffer mTriangleVertices;
    private FloatBuffer mTriangleVertices2;
    private FloatBuffer mDanVertices;
    private FloatBuffer mScrollingBackgroundVertices;

    private final String mVertexShader =
        "uniform mat4 uMVPMatrix;\n" +
        "uniform float aAlpha;\n" +	
        "attribute vec4 aPosition;\n" +
        "attribute vec2 aTextureCoord;\n" +	
        "varying float vAlpha;\n" +
        "varying vec2 vTextureCoord;\n" +		
        "void main() {\n" +
        "  vAlpha = aAlpha;\n" +
        "  gl_Position = uMVPMatrix * aPosition;\n" +
        "  vTextureCoord = aTextureCoord;\n" +
        "}\n";

    private final String mFragmentShader =
        "precision mediump float;\n" +
        "varying vec2 vTextureCoord;\n" +
        "varying float vAlpha;\n" +
        "uniform sampler2D sTexture;\n" +		
        "void main() {\n" +
        "  gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
        "  gl_FragColor.a *= vAlpha;\n" +
        "}\n";

    private float[] mMVPMatrix = new float[16];
    private float[] mProjMatrix = new float[16];
    private float[] mMMatrix = new float[16];
    private float[] mVMatrix = new float[16];

    private int mProgram;
    private int muMVPMatrixHandle;
    private int maPositionHandle;
    private int maTextureHandle;
    private int maAlphaHandle;

    private Context mContext;
    private static String TAG = "GLES20TriangleRenderer";

}
