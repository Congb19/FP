package com.example.fp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

//by Congb19 2020.5.26

public class MainActivity extends AppCompatActivity {

    //主游戏画面
    GameView mGameView;

    //运行GameView
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //绘制
        mGameView = new GameView(this);
        setContentView(mGameView);
    }

}
