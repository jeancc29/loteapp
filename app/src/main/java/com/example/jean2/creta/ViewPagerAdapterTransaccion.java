package com.example.jean2.creta;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

public class ViewPagerAdapterTransaccion extends FragmentPagerAdapter {

    public ViewPagerAdapterTransaccion(FragmentManager fm){
        super(fm);
    }
    @Override
    public Fragment getItem(int position) {
        TransaccionAgregarFragment transaccionAgregarFragment = new TransaccionAgregarFragment();
        TransaccionListaFragment transaccionListaFragment = new TransaccionListaFragment();


        position = position+1;
        Bundle bundle = new Bundle();
        bundle.putString("message", "Fragment: " + position);

        Log.d("Dentro view pagaer:", "pagaertransaccion");

        if(position == 1){
            transaccionAgregarFragment.setArguments(bundle);
            return transaccionAgregarFragment;
        }



        transaccionListaFragment.setArguments(bundle);
        return transaccionListaFragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        position = position+1;

        if(position == 1)
            return "Agregar transaccion";
//        else if(position == 2)
//            return "Jugar";

        return "Transacciones";
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
