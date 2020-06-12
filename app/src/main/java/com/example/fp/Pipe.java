package com.example.fp;

import java.util.Random;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

// 管道
public class Pipe {
	private static final float RADIO_BETWEEN_UP_DOWN = 1 / 5F;// 上下管道间的距离
	private static final float RADIO_MAX_HEIGHT = 2.5F / 5F;// 上管道的最大高度
	private static final float RADIO_MIN_HEIGHT = 1F / 5F;// 上管道的最小高度
	private int x;// 管道x坐标
	private int mTopHeight;// 上管道高度
	private int mMargin;// 上下管道的距离
	private Bitmap mTopBitmap;// 上管道图片
	private Bitmap mBottomBitmap;// 下管道图片
	private static Random random = new Random();

	public Pipe(Context context, int gameWidth, int gameHeight, Bitmap topBitmap, Bitmap bottomBitmap) {
		mMargin = (int) (gameHeight * RADIO_BETWEEN_UP_DOWN);
		// 默认从最左边出现 ,小鸟往前飞时，管道往左移动
		x = gameWidth;
		mTopBitmap = topBitmap;
		mBottomBitmap = bottomBitmap;
		// 高度随机
		randomHeight(gameHeight);
	}
	private void randomHeight(int gameHeight) {
		mTopHeight = random.nextInt((int) (gameHeight * (RADIO_MAX_HEIGHT - RADIO_MIN_HEIGHT)));
		mTopHeight = (int) (mTopHeight + gameHeight * RADIO_MIN_HEIGHT);
	}
	public void draw(Canvas canvas, RectF rect) {
		canvas.save();
		// rect为整个管道，假设完整管道为100，需要绘制20，则向上偏移80 rect.bottom管的实际高度
		canvas.translate(x, -(rect.bottom - mTopHeight));
		// 绘制上管道
		canvas.drawBitmap(mTopBitmap, null, rect, null);
		// 下管道，偏移量为，上管道高度+margin
		canvas.translate(0, rect.bottom + mMargin);
		//canvas.translate(0, mTopHeight + mMargin);
		//绘制下管道
		canvas.drawBitmap(mBottomBitmap, null, rect, null);
		canvas.restore();
	}
	// 碰撞检测
	public boolean touchBird(Bird bird){
		if (bird.getX() + bird.getWidth() > x && (bird.getY() < mTopHeight || bird.getY() + bird.getHeight() > mTopHeight + mMargin)) {
			return true;
		}
		return false;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
}
