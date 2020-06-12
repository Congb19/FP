package com.example.fp;

import com.example.tools.UITools;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

public class Bird {
	public static final float RADIO_POS_HEIGHT = 1 / 3f;// 鸟所在的默认屏幕高度
	private static final int BIRD_SIZE = 30; // 鸟的宽度 30dp
	private Bitmap mBirdBitmap;// 鸟图片
	private int mHeight;// 鸟高度
	private int mWidth;// 鸟宽度
	private RectF mBirdRectF; // 鸟所在的范围
	private int x, y;// 所在坐标
	private int mGameHeight;

	public Bird(Context context, Bitmap bitmap, int gameWidth, int gameHeight) {
		this.mBirdBitmap = bitmap;
		this.mWidth = UITools.dip2px(context, BIRD_SIZE);
		this.mHeight = (int) (mWidth * 1.0f / bitmap.getWidth() * bitmap.getHeight());
		// 给坐标赋值
		this.x = gameWidth / 3 - bitmap.getWidth() / 2;
		this.y = (int) (gameHeight * RADIO_POS_HEIGHT);
		this.mBirdRectF = new RectF();
		
		this.mGameHeight = gameHeight;
	}
	public void draw(Canvas canvas) {
		mBirdRectF.set(x, y, x + mWidth, y + mHeight);
		canvas.drawBitmap(mBirdBitmap, null, mBirdRectF, null);
	}
	public void resetHeight() {
		y = (int) (mGameHeight * RADIO_POS_HEIGHT);
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getWidth() {
		return mWidth;
	}

	public int getHeight() {
		return mHeight;
	}

}
