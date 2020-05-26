package com.example.fp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

//by Congb19 2020.5.26

//main vars list

//int score 得分
//int gamestate 状态 0=没开始 1=游戏中 2=已去世


//main function list

//void init() 初始化各种变量 score等
//void gameStart() 游戏开始
//void onTap() 单击tap跳跃
//void scorePlus() 得分+1
//void scoreTest() 检测得分
//void hitTest() 检测撞击函数
//void gameOver() 游戏结束

public class MainActivity extends AppCompatActivity {

    public int score=0;
    public int gamestate=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void init() {
        //要做的事情：铺好画布，重置得分、状态，水管位置
        gamestate=0;
        score=0;

        return;
    }

    public void gameStart() {
        //要做的事情：水管开始运动，鸟开始赋予重力
        init();

        return;
    }

    public void onTap() {
        //要做的事情：鸟的速度重置为固定值
        return;
    }
    public void scorePlus() {
        //要做的事情：
        this.score+=1;
    }

    public void scoreTest() {
        //要做的事情：
        this.score+=1;
    }
    public void hitTest() {

    }

    public void gameOver() {
        //要做的事情：画面定格，显示分数和重新开始按钮
    }

    class GameView extends SurfaceView implements SurfaceHolder.Callback {

        private SurfaceHolder holder;
        private MyThread myThread;

        public GameView(Context context) {
            super(context);
            holder = this.getHolder();
            holder.addCallback(this);
            myThread = new MyThread(holder);//创建一个绘图线程

        }

        /**
         * @param holder 在创建时激发，一般在这里调用画图的线程
         */
        @Override
        public void surfaceCreated(SurfaceHolder holder) {

            myThread.isRun = true;
            myThread.start();
        }

        /**
         * @param holder
         * @param format
         * @param width
         * @param height 在surface的大小发生改变时激发
         */
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        /**
         * @param holder 销毁时激发，一般在这里将画图的线程停止、释放
         */
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

            myThread.isRun = false;
        }

        //线程内部类
        class MyThread extends Thread {
            private SurfaceHolder holder;
            public boolean isRun;

            public MyThread(SurfaceHolder holder) {
                this.holder = holder;
                isRun = true;
            }

            @Override
            public void run() {

                int count = 0;
                while (isRun) {
                    Canvas c = null;
                    try {
                        synchronized (holder) {
                            c = holder.lockCanvas();//锁定画布，一般在锁定后就可以通过其返回的画布对象Canvas，在其上面画图等操作了。
                            c.drawColor(Color.BLACK);//设置画布背景颜色
                            Paint p = new Paint(); //创建画笔
                            p.setColor(Color.WHITE);
                            Rect r = new Rect(100, 50, 300, 250);
                            c.drawRect(r, p);
                            c.drawText("这是第" + (count++) + "秒", 100, 310, p);
                            Thread.sleep(1000);//睡眠时间为1秒
                        }
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    } finally
                    {
                        if (c != null)
                        {
                            holder.unlockCanvasAndPost(c);//结束锁定画图，并提交改变。
                        }
                    }
                }
            }
        }
    }
}
