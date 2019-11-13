package com.example.jean2.creta;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fm){
        super(fm);
    }
    @Override
    public Fragment getItem(int position) {
        PrincipalFragment principalFragment = new PrincipalFragment();
        MenuFragment menuFragment = new MenuFragment();
        JugadasFragment jugadasFragment = new JugadasFragment();


        position = position+1;
        Bundle bundle = new Bundle();
        bundle.putString("message", "Fragment: " + position);

        Log.d("Dentro view pagaer:", "heyyyy");

        if(position == 1){
            menuFragment.setArguments(bundle);
            return menuFragment;
        }

        if(position == 3){
//            Intent intent = new Intent(principalFragment, new_activity.class);
//            intent.putExtra("jsonArray", principalFragment.jugadas.toString());
            jugadasFragment = new JugadasFragment();
            bundle.putString("jugadas", principalFragment.jugadas.toString());
            Log.d("Dentro view pagaer:", "heyyyy");
            jugadasFragment.setArguments(bundle);
            return jugadasFragment;
        }

        principalFragment.setArguments(bundle);
        return principalFragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        position = position+1;

        if(position == 1)
            return "Menu";
        else if(position == 2)
            return "Jugar";

        return "Jugadas";
    }

    @Override
    public int getItemPosition(Object object) {
        Log.e("ViewPagerPrincipalFrag", "hey");
//        JugadasFragment f = (JugadasFragment ) object;
//        if (f != null) {
//            Log.e("ViewPagerPrincipalFrag", PrincipalFragment.jugadas.toString());
//
//        }
        return super.getItemPosition(object);
    }


}
