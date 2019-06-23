package com.example.jean2.creta;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.graphics.Typeface;

public class CheckBox_Icon extends android.support.v7.widget.AppCompatCheckBox {
    public CheckBox_Icon(Context context){
        super(context);
    }

    public CheckBox_Icon(Context context, AttributeSet attrs) {
        super(context, attrs);
        String path = "fonts/ionicons.ttf";
        Typeface icons = Typeface.createFromAsset(context.getAssets(),path);
        setTypeface(icons);
    }
}
