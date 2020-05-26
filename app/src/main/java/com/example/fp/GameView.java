package com.example.fp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import android.util.AttributeSet;

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