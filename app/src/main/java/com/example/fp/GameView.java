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
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.example.fp.GameButton.OnButtonClickListener;
import com.example.tools.BitmapUtil;
import com.example.tools.UITools;
import com.example.tools.Logger;
import android.util.Log;

public class GameView extends SurfaceView implements Callback, Runnable {
    private SurfaceHolder mHolder;
    // private Thread mThread;
    private ExecutorService mPool;
    private Canvas mCanvas;
    private boolean isRunnging;//状态

    // 二.设置背景
    private Bitmap mBgBitmap;
    //当前View的尺寸
    private int mWidth;
    private int mHeight;
    private RectF mGamePanelRect = new RectF();
    // 三、设置鸟
    private Bird mBird;
    private Bitmap mBirdBitmap;

    // 四、添加地板
    private Floor mFloor;
    private Bitmap mFloorBitmap;
    // 五、添加管道
    /** 管道的宽度 60dp */
    private static final int PIPE_WIDTH = 60;
    private Pipe mPipe;
    /** 上管道的图片 */
    private Bitmap mPipeTopBitmap;
    /** 下管道的图片 */
    private Bitmap mPipeBotBitmap;
    /** 管道的宽度 */
    private int mPipeWidth;
    /** 管道矩阵 */
    private RectF mPipeRectF;
    /** 管道集合 */
    private List<Pipe> mPipeList;
    /** 管道移动的速度 */
    private int mSpeed = UITools.dip2px(getContext(), 5);

    // 六、添加分数
    /** 分数 */
    private final int[] mNums = new int[] { R.drawable.n0, R.drawable.n1, R.drawable.n2, R.drawable.n3, R.drawable.n4, R.drawable.n5,
            R.drawable.n6, R.drawable.n7, R.drawable.n8, R.drawable.n9 };
    private Grade mGrade;
    /** 分数图片组 */
    private Bitmap[] mNumBitmap;
    /** 分值 */
    private int mScore = 100;
    /** 单个数字的高度的1/15 */
    private static final float RADIO_SINGLE_NUM_HEIGHT = 1 / 15f;
    /** 单个数字的宽度 */
    private int mSingleGradeWidth;
    /** 单个数字的高度 */
    private int mSingleGradeHeight;
    /** 单个数字的范围 */
    private RectF mSingleNumRectF;

    // --七、添加游戏的状态-------------------------------------------------------------------------
    /** 刚进入游戏时是等待静止的状态 */
    private GameStatus mStatus = GameStatus.WAITING;

    private enum GameStatus {
        WAITING, RUNNING, OVER
    }

    /** 触摸上升的距离，因为是上升，所以为负值 */
    private static final int TOUCH_UP_SIZE = -16;
    /** 将上升的距离转化为px；这里多存储一个变量，变量在run中计算 */
    private final int mBirdUpDis = UITools.dip2px(getContext(), TOUCH_UP_SIZE);
    /** 跳跃的时候的临时距离 */
    private int mTmpBirdDis;

    // --八、按钮----------------------------------------
    private GameButton mStart;
    private Bitmap mStartBitmap;
    private Bitmap mStartPressBitmap;// 开始按下图片

    private GameButton mRestart;
    private Bitmap mRestartBitmap;
    private Bitmap mRestartPressBitmap;// 从新开始按下图片

    // --九、游戏中的变量---------------------------
    /** 两个管道间距离 **/
    private final int PIPE_DIS_BETWEEN_TWO = UITools.dip2px(getContext(), 300);
    /** 鸟自动下落的距离 */
    private final int mAutoDownSpeed = UITools.dip2px(getContext(), 2);
    //private Handler mHandler = new Handler();



    // ----构造函数处理---------------------------------------------
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

    private void init() {
        // -初始化holder-----------------------
        mHolder = getHolder();
        mHolder.addCallback(this);
        setZOrderOnTop(true);
        // 设置画布 背景透明
        mHolder.setFormat(PixelFormat.TRANSLUCENT);

        // --焦点设置----------------------------
        setFocusable(true);
        // 设置触屏
        setFocusableInTouchMode(true);
        // 设置常亮
        setKeepScreenOn(true);

        // --背景设置--------------------------------
        mGamePanelRect = new RectF();
        mBgBitmap = loadImageByResId(R.drawable.bg1);

        // --添加鸟的图片---
        mBirdBitmap = loadImageByResId(R.drawable.b1);
        // --添加地板---
        mFloorBitmap = loadImageByResId(R.drawable.floor_bg2);
        // --管道的宽度初始化--
        mPipeWidth = UITools.dip2px(getContext(), PIPE_WIDTH);
        // --添加管道图片--
        mPipeTopBitmap = loadImageByResId(R.drawable.g2);
        mPipeBotBitmap = loadImageByResId(R.drawable.g1);
        mPipeList = new ArrayList<Pipe>();

        // -------------------------------------------------------

        // 初始化分数图片
        mNumBitmap = new Bitmap[mNums.length];
        for (int i = 0; i < mNums.length; i++) {
            mNumBitmap[i] = loadImageByResId(mNums[i]);
        }

        // ---初始化按钮图片-------------------------------------
        mStartBitmap = BitmapUtil.getImageFromAssetsFile(getContext(), "start.png");
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
        // 初始化 管道
        mPipe = new Pipe(getContext(), mWidth, mHeight, mPipeTopBitmap, mPipeBotBitmap);
        mPipeList.add(mPipe);

        // 初始化分数
        mSingleGradeHeight = (int) (h * RADIO_SINGLE_NUM_HEIGHT);// 屏幕的1/15
        mSingleGradeWidth = (int) (mNumBitmap[0].getWidth() * (1.0f * mSingleGradeHeight / mNumBitmap[0].getHeight()));
        mSingleNumRectF = new RectF(0, 0, mSingleGradeWidth, mSingleGradeHeight);
        mGrade = new Grade(mNumBitmap, mSingleNumRectF, mSingleGradeWidth, mWidth, mHeight);

        // 初始化按钮
        mStart = new GameButton(mStartBitmap, mStartPressBitmap, mWidth, mHeight);
        // 从新开始按钮
        mRestart = new GameButton(mRestartBitmap, mRestartPressBitmap, mWidth, mHeight);
//
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
                    // 按下的时候，游戏进入运行状态
                    mStatus = GameStatus.RUNNING;
                }
            }
        });
        mRestart.setOnButtonClickListener(new OnButtonClickListener() {
            @Override
            public void click() {
                mStatus = GameStatus.WAITING;
                resetBirdStatus();
            }
        });

    }


    // ---初始化结束 ----------------------------------------------------------

    // --处理触碰事件------------------------------------------------------------------------
    private int mDownX = 0;
    private int mDownY = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:// 按下
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
            case MotionEvent.ACTION_MOVE:// 移动
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: // 抬起
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




    // --处理逻辑事物------------------------------------------------------------------------
    /** 记录要移除的管道 为什么不用CopyOnWriteArrayList，因为其是线程安全的 */
    private List<Pipe> mNeedRemovePipe = new ArrayList<Pipe>();
    /** 记录要移动的距离 */
    private int mTmpMoveDistance = 0;
    /** 记录要移除的管的个数 */
    private int mRemovedPipe = 0;

    /**
     * 处理逻辑事物
     */
    private void logic() {
        switch (mStatus) {
            case WAITING:// 刚进入游戏的状态

                break;
            case RUNNING:// 正在玩的状态]
                mScore = 0;

                // ---.移动地板-----------
                mFloor.setX(mFloor.getX() - mSpeed);

                // ---不断移动管道--------
                logicPipe();

                // ----处理鸟逻辑----
                mTmpBirdDis += mAutoDownSpeed;
                mBird.setY(mBird.getY() + mTmpBirdDis);

                // ---处理分数---
                mScore += mRemovedPipe;
                for (Pipe pipe : mPipeList) {
                    if (pipe.getX() + mPipeWidth < mBird.getX()) {
                        mScore++;
                    }
                }

                // ----判断游戏是否结束----
                checkGameOver();

                break;

            case OVER:// 鸟落下
                // 如果鸟还在空中，先让它掉下来
                if (mBird.getY() < mFloor.getY() - mBird.getHeight()) {
                    mTmpBirdDis += mAutoDownSpeed;
                    mBird.setY(mBird.getY() + mTmpBirdDis);
                } else {
                    // 清除生成的管道
                    clearAndInit();
                }
                break;
        }
    }

    /**
     * 重置鸟的位置等数据
     */
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

    /**
     * 处理管道逻辑
     */
    private void logicPipe() {
        // 1.遍历所有的管道
        for (Pipe pipe : mPipeList) {
            // 2.如果管子已经在屏幕外
            if (pipe.getX() < -mPipeWidth) {
                mNeedRemovePipe.add(pipe);
                mRemovedPipe++;
                continue;
            }
            pipe.setX(pipe.getX() - mSpeed);
        }
        // 3.移除管道
        mPipeList.removeAll(mNeedRemovePipe);
        // 4.记录移动距离
        mTmpMoveDistance += mSpeed;
        // 5.生成一个管道
        if (mTmpMoveDistance >= PIPE_DIS_BETWEEN_TWO) {
            Pipe pipe = new Pipe(getContext(), getWidth(), getHeight(), mPipeTopBitmap, mPipeBotBitmap);
            mPipeList.add(pipe);
            mTmpMoveDistance = 0;
        }
    }

    /**
     * 判断游戏是否结束
     */
    private void checkGameOver() {
        // 判断小鸟是否触碰到了地板
        if (mBird.getY() > mFloor.getY() - mBird.getHeight()) {
            mStatus = GameStatus.OVER;
        }
        // 判断是否触碰到了管道
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



    // ---游戏引擎------------------------------------------------------------

    @Override
    public void run() {
        while (isRunnging) {
            long start = System.currentTimeMillis();
            draw();
            long end = System.currentTimeMillis();
            if (start - end < 50) {
                SystemClock.sleep(50 - (start - end));
            }
        }
    }

    private void draw() {
        try {
            Logger.i("bird", "mHolder==" + mHolder);
            if (mHolder != null) {
                mCanvas = mHolder.lockCanvas();
                Logger.i("bird", "mCanvas==" + mCanvas);

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
                        drawGameOver();
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

    private FontMetrics fm;
    private int mTextHeight = 0;// 游戏结束时文本的高度

    private void drawGameOver() {
        String mGameOver = "GAME OVER";
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "BRITANIC.TTF");
        Paint paint = new Paint();
        paint.setAntiAlias(true); // 是否抗锯齿
        paint.setTypeface(typeface);
        paint.setStrokeWidth(3);
        paint.setColor(Color.RED);
        paint.setTextSize(50);
        // paint.setShader(shader);//设置字体
        paint.setShadowLayer(5, 3, 3, 0xFFFF00FF);// 设置阴影
        paint.setTextAlign(Paint.Align.CENTER);
        // paint.setStyle(Paint.Style.STROKE); //空心
        paint.setStyle(Paint.Style.FILL); // 实心
        paint.setDither(true);
        fm = paint.getFontMetrics();
        mTextHeight = (int) (Math.ceil(fm.descent - fm.ascent) + UITools.dip2px(getContext(), 4));
        mCanvas.drawText(mGameOver, mWidth / 2, mHeight / 2, paint);
    }


    /**
     * 绘制开始按钮
     */
    private void drawStart() {
        mStart.draw(mCanvas);
    }
    /**
     * 绘制重新开始按钮
     */
    private void drawRestart() {
        mRestart.setY(mHeight/2 + mTextHeight);
        mRestart.draw(mCanvas);

    }
    /**
     * 绘制分数
     */
    private void drawGrades() {
        mGrade.draw(mCanvas, mScore);
    }
    private void drawFloor() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        mFloor.draw(mCanvas, paint);
        // 更新我们地板绘制的x坐标
        mFloor.setX(mFloor.getX() - mSpeed);
    }
    private void drawBird() {
        mBird.draw(mCanvas);
    }
    private void drawBg() {
        mCanvas.drawBitmap(mBgBitmap, null, mGamePanelRect, null);
    }
    private void drawPipes() {
        for (Pipe pipe : mPipeList) {
            // 先设定x坐标
            pipe.setX(pipe.getX() - mSpeed);
            pipe.draw(mCanvas, mPipeRectF);
        }
    }


    // ---callback监听------------------------------------------------------
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



    /**
     * 根据resId加载图片
     *
     * @param resId
     * @return
     */
    private Bitmap loadImageByResId(int resId) {
        Log.v("to get img", "!!!");
        return BitmapFactory.decodeResource(getResources(), resId);
    }
}