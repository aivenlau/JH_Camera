package com.joyhonest.jh_fpv;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joyhonest.jh_ui.R;


/**
 * Created by aivenlau on 2017/2/16.
 */

public class WaitView extends LinearLayout {

    private TextView   TitleViewA;
    private ImageView  spaceshipImage;
    Animation hyperspaceJumpAnimation;
    public WaitView(Context context, AttributeSet attrs) {
         super(context, attrs);
        // 加载布局
        LayoutInflater.from(context).inflate(R.layout.loading_dialog, this);
        // 获取控件
        TitleViewA = (TextView)findViewById(R.id.tipTextView);
        spaceshipImage = (ImageView)findViewById(R.id.img);
        hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.loading_animation);
        //spaceshipImage.startAnimation(hyperspaceJumpAnimation);

        }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if(visibility==VISIBLE)
        {
            spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        }
        else
        {
            spaceshipImage.clearAnimation();
        }
    }

    private   void setTitleView(String str)
    {
        TitleViewA.setText(str);
    }
    private  void setTitleView(int idStr)
    {
        TitleViewA.setText(idStr);
    }
}
