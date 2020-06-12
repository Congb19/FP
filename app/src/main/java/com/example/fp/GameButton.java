package com.example.fp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

// 按钮
public class GameButton {
	private int x; // 所在坐标
	private int y;
	private Bitmap mBitmap; // 原来按钮图片
	private Bitmap mPressBitmap; // 按下的按钮图片
	private RectF mRectF; // 按钮所在的范围
	private boolean isClick = false; // 判断是否被点击了
	
	public GameButton(Bitmap bitmap, Bitmap pressBitmap, int gameWidth, int gameHeight){
		this.mBitmap = bitmap;
		this.mPressBitmap = pressBitmap;
		this.x = gameWidth/2-mBitmap.getWidth()/2;//左边距
		this.y = gameHeight;//初始的位置在屏幕最下端
		this.mRectF = new RectF();
	}
	public void draw(Canvas canvas){
		canvas.save();
		mRectF.set(x, y, x + mBitmap.getWidth(), y + mBitmap.getHeight());
		if (isClick) {
			canvas.drawBitmap(mBitmap, null , mRectF, null);
		}else {
			canvas.drawBitmap(mPressBitmap, null , mRectF, null);
		}
		canvas.restore();
	}
	//判断按钮是否可点击
    public boolean isClick(int newX, int newY) {
        Rect rect = new Rect(x, y, x + mPressBitmap.getWidth(), y + mPressBitmap.getHeight());
        isClick = rect.contains(newX, newY);
        return isClick;
    }

    public void setClick(boolean isClick) {
        this.isClick = isClick;
    }

    public void click(){
        if (mListener != null) {
            mListener.click();
        }
    }
    private OnButtonClickListener mListener;
    public interface OnButtonClickListener{
        void click();
    }
    public void setOnButtonClickListener(OnButtonClickListener listener){
        this.mListener = listener;
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
	public void setY(int y) {
		this.y = y;
	}
}
