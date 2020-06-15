package com.example.fp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.example.fp.GameButton.OnButtonClickListener;
import com.example.tools.BitmapUtil;
import com.example.tools.UITools;
import android.util.Log;

// 游戏主程序
public class GameView extends SurfaceView implements Callback, Runnable {
    private SurfaceHolder mHolder;
    private ExecutorService mPool;
    private Canvas mCanvas;
    private boolean isRunnging; //状态

    // ---背景---
    private Bitmap mBgBitmap;
    private int mWidth;
    private int mHeight;
    private RectF mGamePanelRect = new RectF();

    // ---鸟---
    private Bird mBird;
    private Bitmap mBirdBitmap;

    // ---地板---
    private Floor mFloor;
    private Bitmap mFloorBitmap;

    // ---管道---
    private static final int PIPE_WIDTH = 90; // 管道的宽度 dp
    private Pipe mPipe;
    private Bitmap mPipeTopBitmap; // 上管道的图片
    private Bitmap mPipeBotBitmap; // 下管道的图片
    private int mPipeWidth; // 管道的宽度
    private RectF mPipeRectF; // 管道矩阵
    private List<Pipe> mPipeList; // 管道集合
    private int mSpeed = UITools.dip2px(getContext(), 5); // 管道移动的速度 px

    // ---分数---
    private final int[] mNums = new int[] { R.drawable.n0, R.drawable.n1, R.drawable.n2, R.drawable.n3, R.drawable.n4, R.drawable.n5,
            R.drawable.n6, R.drawable.n7, R.drawable.n8, R.drawable.n9 };
    private Grade mGrade;
    private Bitmap[] mNumBitmap;// 分数图片组
    private int mScore = 100;//分值
    private static final float RADIO_SINGLE_NUM_HEIGHT = 1 / 15f;// 单个数字的高度的1/15
    private int mSingleGradeWidth;// 单个数字的宽度
    private int mSingleGradeHeight;// 单个数字的高度
    private RectF mSingleNumRectF;// 单个数字的范围

    // ---游戏状态---
    private enum GameStatus {
        WAITING, RUNNING, OVER
    }
    private GameStatus mStatus = GameStatus.WAITING;

    private static final int TOUCH_UP_SIZE = -20; //触摸上升的距离 dp
    private final int mBirdUpDis = UITools.dip2px(getContext(), TOUCH_UP_SIZE); //触摸上升的距离 px
    private int mTmpBirdDis;// 跳跃的时候的临时距离

    // ---按钮---
    private GameButton mStart;
    private Bitmap mStartBitmap;
    private Bitmap mStartPressBitmap;// 开始按下图片

    private GameButton mRestart;
    private Bitmap mRestartBitmap;
    private Bitmap mRestartPressBitmap;// 重新开始按下图片

    // ---游戏中的变量---
    private final int PIPE_DIS_BETWEEN_TWO = UITools.dip2px(getContext(), 150);// 两个管道间距离
    private final int mAutoDownSpeed = UITools.dip2px(getContext(), 2);// 鸟自动下落的距离

    // ---工具函数们---
    // 根据resId加载图片
    private Bitmap loadImageByResId(int resId) {
        Log.i("to get img", "!!!");
        return BitmapFactory.decodeResource(getResources(), resId);
    }

    // ---构造函数处理---
    public GameView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }
    public GameView(Context context) {
        this(context, null);
        init();
    }
    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    //---初始化---
    private void init() {
        // 初始化holder
        mHolder = getHolder();
        mHolder.addCallback(this);
        setZOrderOnTop(true);
        // 设置画布 背景透明
        mHolder.setFormat(PixelFormat.TRANSLUCENT);
        // 焦点设置
        setFocusable(true);
        // 设置触屏
        setFocusableInTouchMode(true);
        // 设置常亮
        setKeepScreenOn(true);
        // 背景设置
        mGamePanelRect = new RectF();
        mBgBitmap = loadImageByResId(R.drawable.bg1);
        // 添加鸟的图片
        mBirdBitmap = loadImageByResId(R.drawable.b2);
        // 添加地板图片
        mFloorBitmap = loadImageByResId(R.drawable.floor_bg2);
        // 初始化管道的宽度
        mPipeWidth = UITools.dip2px(getContext(), PIPE_WIDTH);
        // 添加管道图片
        mPipeTopBitmap = loadImageByResId(R.drawable.g4);
        mPipeBotBitmap = loadImageByResId(R.drawable.g3);
        mPipeList = new ArrayList<Pipe>();
        // 添加分数图片
        mNumBitmap = new Bitmap[mNums.length];
        for (int i = 0; i < mNums.length; i++) {
            mNumBitmap[i] = loadImageByResId(mNums[i]);
        }
        // 添加按钮图片
        mStartBitmap = BitmapUtil.getImageFromAssetsFile(getContext(), "start1.png");
        mStartPressBitmap = BitmapUtil.getImageFromAssetsFile(getContext(), "start2.png");
        mRestartBitmap = BitmapUtil.getImageFromAssetsFile(getContext(), "restart1.png");
        mRestartPressBitmap = BitmapUtil.getImageFromAssetsFile(getContext(), "restart2.png");
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mGamePanelRect.set(0, 0, w, h);
        // 初始化鸟
        mBird = new Bird(getContext(), mBirdBitmap, mWidth, mHeight);
        // 初始化地板
        mFloor = new Floor(mWidth, mHeight, mFloorBitmap);
        // 初始化管道范围
        mPipeRectF = new RectF(0, 0, mPipeWidth, mHeight);
        // 初始化分数
        mSingleGradeHeight = (int) (h * RADIO_SINGLE_NUM_HEIGHT);// 屏幕的1/15
        mSingleGradeWidth = (int) (mNumBitmap[0].getWidth() * (1.0f * mSingleGradeHeight / mNumBitmap[0].getHeight()));
        mSingleNumRectF = new RectF(0, 0, mSingleGradeWidth, mSingleGradeHeight);
        mGrade = new Grade(mNumBitmap, mSingleNumRectF, mSingleGradeWidth, mWidth, mHeight);
        // 初始化按钮
        mStart = new GameButton(mStartBitmap, mStartPressBitmap, mWidth, mHeight);
        // 重新开始按钮
        mRestart = new GameButton(mRestartBitmap, mRestartPressBitmap, mWidth, mHeight);
        if (mStatus == GameStatus.WAITING && mStart != null) {
            ObjectAnimator anim = ObjectAnimator.ofInt(mStart, "Y", mHeight, mHeight / 2);
            anim.setDuration(2000);
            anim.start();
        }
        // 添加事件
        mStart.setOnButtonClickListener(new OnButtonClickListener() {
            @Override
            public void click() {
                if (mStatus == GameStatus.WAITING) {
                    // 按下 游戏状态进入运行
                    mStatus = GameStatus.RUNNING;
                }
            }
        });
        mRestart.setOnButtonClickListener(new OnButtonClickListener() {
            @Override
            public void click() {
                // 按下 游戏状态重新进入等待
                mStatus = GameStatus.WAITING;
                resetBirdStatus();
            }
        });
    }

    // ---处理触碰事件---
    private int mDownX = 0;
    private int mDownY = 0;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = (int) event.getX();
                mDownY = (int) event.getY();
                if (mStatus == GameStatus.WAITING) {
                    if (mStart.isClick(mDownX, mDownY)) {
                        mStart.click();
                    }
                } else if (mStatus == GameStatus.RUNNING) {
                    // 记录临时跳跃的高度
                    mTmpBirdDis = mBirdUpDis;
                    // --增加难度---
                    if (mScore > 20) {
                        mSpeed += UITools.dip2px(getContext(), 1);
                    } else if (mScore > 40) {
                        mSpeed += UITools.dip2px(getContext(), 2);
                    } else if (mScore > 60) {
                        mSpeed += UITools.dip2px(getContext(), 3);
                    } else if (mScore > 80) {
                        mSpeed += UITools.dip2px(getContext(), 4);
                    }
                } else if (mStatus == GameStatus.OVER) {// 游戏结束时
                    // 判断是否点击了重新开始图片
                    if (mRestart.isClick(mDownX, mDownY)) {
                        mRestart.click();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mStart != null) {
                    mStart.setClick(false);
                }
                if (mRestart != null) {
                    mRestart.setClick(false);
                }
                break;
        }
        return true;
    }

    private void resetBirdStatus() {
        // 设置鸟的高度
        mBird.setY((int) (mHeight * Bird.RADIO_POS_HEIGHT));
        // 重置下落速度
        mTmpBirdDis = 0;
    }

    // ---处理逻辑们---
    private List<Pipe> mNeedRemovePipe = new ArrayList<Pipe>();// 记录要移除的管道
    private int mTmpMoveDistance = 0; // 记录要移动的距离
    private int mRemovedPipe = 0; // 记录要移除的管的个数
    // 主进程逻辑
    private void logic() {
        switch (mStatus) {
            case WAITING:
                break;
            case RUNNING:
                mScore = 0;
                // 移动地板
                mFloor.setX(mFloor.getX() - mSpeed);
                // 移动管道
                logicPipe();
                // 鸟重力下落
                // 著名公式Δs=vΔt
                mTmpBirdDis += mAutoDownSpeed;
                mBird.setY(mBird.getY() + mTmpBirdDis);
                // 分数更新
                mScore += mRemovedPipe;
                for (Pipe pipe : mPipeList) {
                    if (pipe.getX() + mPipeWidth < mBird.getX()) {
                        mScore++;
                    }
                }
                // 检查游戏结束
                checkGameOver();
                break;
            case OVER:
                // 鸟如果撞的是管子，就落下
                if (mBird.getY() < mFloor.getY() - mBird.getHeight()) {
                    // 著名公式Δs=vΔt
                    mTmpBirdDis += mAutoDownSpeed;
                    mBird.setY(mBird.getY() + mTmpBirdDis);
                } else {
                    // 清除生成的管道
                    clearAndInit();
                }
                break;
        }
    }
    // 重新初始化
    private void clearAndInit() {
        // 清除生成的管道
        mPipeList.clear();
        // 需要移除的管道集合
        mNeedRemovePipe.clear();
        // 清除移动的距离
        mTmpMoveDistance = 0;
        // 管道的个数
        mRemovedPipe = 0;
    }
    // 管道逻辑
    private void logicPipe() {
        // 遍历所有的管道
        for (Pipe pipe : mPipeList) {
            // 如果管子已经在屏幕外
            if (pipe.getX() < -mPipeWidth) {
                mNeedRemovePipe.add(pipe);
                mRemovedPipe++;
                continue;
            }
            pipe.setX(pipe.getX() - mSpeed);
        }
        // 移除管道
        mPipeList.removeAll(mNeedRemovePipe);
        // 记录移动距离
        mTmpMoveDistance += mSpeed;
        // 生成一个管道
        if (mTmpMoveDistance >= PIPE_DIS_BETWEEN_TWO) {
            Pipe pipe = new Pipe(getContext(), getWidth(), getHeight(), mPipeTopBitmap, mPipeBotBitmap);
            mPipeList.add(pipe);
            mTmpMoveDistance = 0;
        }
    }
    // 游戏结束检测逻辑
    private void checkGameOver() {
        // 判断鸟是否触碰到地板
        if (mBird.getY() > mFloor.getY() - mBird.getHeight()) {
            mStatus = GameStatus.OVER;
        }
        // 判断是否触碰到管道
        for (Pipe pipe : mPipeList) {
            // 已经穿过的
            if (pipe.getX() + mPipeWidth < mBird.getX()) {
                continue;
            }
            // 如果是碰到了，游戏结束
            if (pipe.touchBird(mBird)) {
                mStatus = GameStatus.OVER;
                break;
            }
        }
    }

    // ---游戏引擎---
    @Override
    public void run() {
        while (isRunnging) {
            long start = System.currentTimeMillis();
            logic();
            draw();
            long end = System.currentTimeMillis();
            //更新速率（也就是最高帧率）
            long fps = 45;
            if (start - end < 1000/fps) {
                SystemClock.sleep(1000/fps - (start - end));
            }
        }
    }
    // ---绘制函数们---
    // 绘制主程序
    private void draw() {
        try {
            if (mHolder != null) {
                mCanvas = mHolder.lockCanvas();
                if (mCanvas != null) {
                    drawBg();//绘制背景
                    drawBird();// 绘制鸟
                    drawFloor();// 绘制地板
                    drawPipes();// 绘制管道
                    drawGrades();// 绘制分数
                    if (mStatus == GameStatus.WAITING) {
                        drawStart();
                    }
                    if (mStatus == GameStatus.RUNNING) {
                        drawPipes();// 绘制管道
                    }
                    if (mStatus == GameStatus.OVER) {
//                        drawGameOver();
                        drawRestart();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mHolder != null && mCanvas != null) {
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }
    private int mTextHeight = 0; // 游戏结束时文本的高度
    // 绘制开始按钮
    private void drawStart() {
        mStart.draw(mCanvas);
    }
    // 绘制重新开始按钮
    private void drawRestart() {
        mRestart.setY(mHeight/2 + mTextHeight);
        mRestart.draw(mCanvas);
    }
    // 绘制分数
    private void drawGrades() {
        mGrade.draw(mCanvas, mScore);
    }
    // 绘制地板
    private void drawFloor() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        mFloor.draw(mCanvas, paint);
        // 更新地板的x坐标
        mFloor.setX(mFloor.getX() - mSpeed);
    }
    // 绘制鸟
    private void drawBird() {
        mBird.draw(mCanvas);
    }
    // 绘制背景
    private void drawBg() {
        mCanvas.drawBitmap(mBgBitmap, null, mGamePanelRect, null);
    }
    // 绘制管道
    private void drawPipes() {
        for (Pipe pipe : mPipeList) {
            // 设定x坐标
            pipe.setX(pipe.getX() - mSpeed);
            pipe.draw(mCanvas, mPipeRectF);
        }
    }

    // ---回调们---
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // -线程处理--------------------------
        isRunnging = true;
        mPool = Executors.newFixedThreadPool(5);
        mPool.execute(this);
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // 通知关闭线程
        isRunnging = false;
    }
}