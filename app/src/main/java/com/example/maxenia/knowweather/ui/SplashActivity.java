package com.example.maxenia.knowweather.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.example.maxenia.knowweather.MainActivity;
import com.example.maxenia.knowweather.R;

public class SplashActivity extends AppCompatActivity {

    private TextView mTV_SplashName;
    public static int ANIM_TIME = 2000;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1001:
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                    break;
                default:
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        bindView();
    }

    private void bindView() {

        mTV_SplashName = (TextView) findViewById(R.id.splash_name);
        //是否共用一个动画补间
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.setFillAfter(true);
        animationSet.setDuration(ANIM_TIME);
        //缩放动画
        ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1);
        scale.setDuration(ANIM_TIME);
        //scale.setFillAfter(true);
        animationSet.addAnimation(scale);
        //平移动画
        TranslateAnimation translate = new TranslateAnimation(0, 0, 0, -100);
        translate.setDuration(ANIM_TIME);
        animationSet.addAnimation(translate);
        //执行动画
        mTV_SplashName.startAnimation(animationSet);

        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mHandler.sendEmptyMessageDelayed(1001, 500);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }
}
