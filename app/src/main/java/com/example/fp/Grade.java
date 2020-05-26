package com.example.fp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

/**
 * 分数
 * 
 * @Project App_View
 * @Package com.android.view.flybird
 * @author chenlin
 * @version 1.0
 * @Date 2016年5月16日
 */
public class Grade {
	private Bitmap[] mNumBitmap;//所有分数的图片集合
	private RectF mSingleNumRectF;//单个分数的矩阵
	private int mSingleGradeWidth;//单个分数的宽度
	private int mGameWidth;
	private int mGameHeight;

	public Grade(Bitmap[] numBitmap, RectF rectF, int singleGradeWidth, int gameWidth, int gameHeight) {
		this.mNumBitmap = numBitmap;
		this.mSingleNumRectF = rectF;
		this.mSingleGradeWidth = singleGradeWidth;
		this.mGameWidth = gameWidth;
		this.mGameHeight = gameHeight;
	}

	/**
	 * 绘制
	 * 
	 *
	 */
	public void draw(Canvas canvas, int score) {
		String grade = score + "";
		canvas.save();
//		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		//移动屏幕的中间，1/8的高度
		canvas.translate(mGameWidth / 2 - grade.length() * mSingleGradeWidth / 2, 1f / 8 * mGameHeight);
		// 依次绘制分数
		for (int i = 0; i < grade.length(); i++) {
			//100,先绘制1，
			String numStr = grade.substring(i, i + 1);
			int num = Integer.valueOf(numStr);
			canvas.drawBitmap(mNumBitmap[num], null, mSingleNumRectF, null);
			//移动到下一个分数0
			canvas.translate(mSingleGradeWidth, 0);
		}
		canvas.restore();
	}
}
