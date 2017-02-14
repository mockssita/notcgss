package com.example.notimas.notimas;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

public class ActivityGLSurfaceView extends GLSurfaceView {
	
	private static final String TAG = "ActivityGLSurfaceView";
	public GLES20TriangleRenderer mRenderer;
	
	ActivityGLSurfaceView(Context context) {
		super(context);
		Log.d(TAG, "WallpaperGLSurfaceView(" + context + ")");
	}


	@Override
	public boolean onTouchEvent(MotionEvent e) {
		// TODO Auto-generated method stub
		if (e != null)
		{
			mRenderer.touchEvent(e);
	        return true;
		}
		else
		{
			return super.onTouchEvent(e);
		}
	}
	
	public void setRenderer(Renderer renderer) {
		mRenderer = (GLES20TriangleRenderer) renderer;
		super.setRenderer(renderer);
	}
	
	public void onDestroy() {
			Log.d(TAG, "onDestroy()");
		

		super.onDetachedFromWindow();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		Log.d(TAG, "onPause");
		super.onPause();
	}
}
