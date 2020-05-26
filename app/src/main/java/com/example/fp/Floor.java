package com.example.fp;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;

public class Floor {

	// 地板位置游戏面板高度的4/5到底部
	private static final float FLOOR_Y_POS_RADIO = 4 / 5F;// height of 4/5
	private int x, y;// 坐标
	private BitmapShader mBitmapShader;// 填充物
	private int mGameWidth;// 地板宽度
	private int mGameHeight;// 地板高度


	public Floor(int gameWidth, int gameHeight, Bitmap bgBitmap) {
		this.mGameHeight = gameHeight;
		this.mGameWidth = gameWidth;
		this.y = (int) (mGameHeight * FLOOR_Y_POS_RADIO);
		mBitmapShader = new BitmapShader(bgBitmap, TileMode.CLAMP, TileMode.CLAMP);
	}

	/**
	 * 绘制自己
	 * 
	 * @param canvas
	 */
	public void draw(Canvas canvas, Paint paint) {
		// 进行平移
		if (-x > mGameWidth) {
			x = x % mGameWidth;
		}
		/**
		 * save() : 用来保存Canvas的状态,save()方法之后的代码，可以调用Canvas的平移、放缩、旋转、裁剪等操作！
		 */
		canvas.save();
		//平移到指定位置
		canvas.translate(x, y);
		paint.setShader(mBitmapShader);
		canvas.drawRect(x, 0, -x + mGameWidth, mGameHeight - y, paint);
		/**
		 * restore()：用来恢复Canvas之前保存的状态(可以想成是保存坐标轴的状态),防止save()方法代码之后对Canvas执行的操作，继续对后续的绘制会产生影响，通过该方法可以避免连带的影响
		 */
		canvas.restore();
		paint.setShader(null);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}
}
