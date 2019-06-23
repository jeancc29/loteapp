package com.example.jean2.creta;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextView_Icon extends android.support.v7.widget.AppCompatTextView {
    public TextView_Icon(Context context){
        super(context);
    }

    public TextView_Icon(Context context, AttributeSet attrs) {
        super(context, attrs);
        String path = "fonts/ionicons.ttf";
        Typeface icons = Typeface.createFromAsset(context.getAssets(),path);
        setTypeface(icons);
    }
}

