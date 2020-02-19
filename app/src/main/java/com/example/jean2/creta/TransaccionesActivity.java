package com.example.jean2.creta;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class TransaccionesActivity extends AppCompatActivity {
    private Toolbar toolbar;

    private ViewPager viewPager;
    private ViewPagerAdapterTransaccion adapter;
    private static TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transacciones);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Transaccion");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = findViewById(R.id.pagerTransaccion);
        adapter = new ViewPagerAdapterTransaccion(getSupportFragmentManager());

        viewPager.setAdapter(adapter);

        tabLayout = findViewById(R.id.tabsTransaccion);
        tabLayout.setupWithViewPager(viewPager);
    }


}
