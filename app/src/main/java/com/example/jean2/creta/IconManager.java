package com.example.jean2.creta;

import android.content.Context;
import android.graphics.Typeface;

import java.util.Hashtable;

//Tutorial of the icons
//https://www.youtube.com/watch?v=-aBgVYEjOtc
public class IconManager {

    private static Hashtable<String, Typeface> cached_icons = new Hashtable<>();
    public static Typeface getIcons(String path, Context context){
        Typeface icons = cached_icons.get(path);

        if(icons == null){
            icons = Typeface.createFromAsset(context.getAssets(), path);
            cached_icons.put(path, icons);
        }

        return icons;
    }
}
