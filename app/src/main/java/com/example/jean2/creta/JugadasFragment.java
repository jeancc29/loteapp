package com.example.jean2.creta;


import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jean2.creta.Clases.JugadaClass;
import com.example.jean2.creta.Clases.PrinterClass;
import com.example.jean2.creta.Servicios.PrintService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */


public class JugadasFragment extends Fragment{

    private static TableLayout tableLayout;
    JSONArray jugadas = PrincipalFragment.jugadas;
    public static Context mContext;
    Button btnEliminarTodasLasJugadas;

    public JugadasFragment() {
        // Required empty public constructor
    }
    public void update(){

        jugadas = PrincipalFragment.jugadas;
        //Log.d("JugadasFragment:", mContext.toString());
        //onResume();
        //Toast.makeText(getContext(), "Resume", Toast.LENGTH_SHORT).show();
//        Fragment frg = null;
//        frg = new JugadasFragment();
//        final FragmentTransaction ft = getFragmentManager().beginTransaction();
//        ft.detach(frg);
//        ft.attach(frg);
//        ft.commit();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public static void updateTable(){
        Log.d("JugadasFragment:", mContext.toString());

//                LinearLayout.LayoutParams tableRowParams = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT);
//
//        /* create a table row */
//        TableRow tableRow = new TableRow(mContext);
//        tableRow.setLayoutParams(tableRowParams);
//        tableRow.addView(createTv("Loteria", false, mContext));
//        tableRow.addView(createTv("Jugada", false, mContext));
//        tableRow.addView(createTv("Monto", false, mContext));
//        tableRow.addView(createTv("Eliminar", false, mContext));




        if(PrincipalFragment.jugadas == null){
            return ;
        }
        tableLayout.removeAllViews();
        int idRow = 0;
        int i = 0;
        for(JugadaClass jugada: PrincipalFragment.jugadasClase){
            try {

                LinearLayout.LayoutParams tableRowParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

                /* create a table row */
                TableRow tableRow = new TableRow(mContext);
                tableRow.setLayoutParams(tableRowParams);

                tableRow.setId(idRow);




                if(i == 0){
                    /* add views to the row */
                    TableRow tableRow1 = new TableRow(mContext);
                    tableRow1.setId(idRow);
                    tableRow1.setLayoutParams(tableRowParams);
                    idRow ++;
                    tableRow1.addView(createTv("Loteria", true, mContext));
                    tableRow1.addView(createTv("Jugada", true, mContext));
                    tableRow1.addView(createTv("Monto", true, mContext));
                    tableRow1.addView(createTv("Eliminar", true, mContext));
                    tableLayout.addView(tableRow1);
                }
                    /* add views to the row */
                tableRow.setId(idRow);
                    tableRow.addView(createTv(jugada.getDescripcion(), false, mContext));
                    tableRow.addView(createTv(Utilidades.agregarGuion(jugada.getJugada()), false, mContext));
                    tableRow.addView(createTv(String.valueOf(jugada.getMonto()), false, mContext));

                    /* create cell element - button */
                    Button btn = new Button(mContext);
                    btn.setText("x");
                    btn.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
                    btn.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
                    //btn.setBackgroundColor(0xff12dd12);
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int id = ((View)view.getParent()).getId();
                            TableRow r = (TableRow)((Activity) mContext).findViewById(id);
                            TableLayout t = (TableLayout)((Activity) mContext).findViewById(R.id.tableJugadas);
                            t.removeView(r);
                            Log.d("Pariente:" , String.valueOf(id));

//                            PrincipalFragment.jugadasClase.deleteFromId(id);
//                            PrincipalFragment.jugadasClase.remove(id - 1);
                            PrincipalFragment.jugadasClase.remove(jugada);
                            PrincipalFragment.calcularTotal();
                        }
                    });

                    tableRow.addView(btn);




                /* add the row to the table */
                tableLayout.addView(tableRow);
                idRow++;
                i++;
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }


    public static void addRowToTable(JugadaClass jugada){
        Log.d("JugadasFragment:", mContext.toString());






        int idRow = 0;
        int i = 0;
        try{
                LinearLayout.LayoutParams tableRowParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

                /* create a table row */
                TableRow tableRow = new TableRow(mContext);
                tableRow.setLayoutParams(tableRowParams);



                if(tableLayout.getChildCount() == 0){
                    /* add views to the row */
                    TableRow tableRow1 = new TableRow(mContext);
                    tableRow1.setId(0);
                    tableRow1.setLayoutParams(tableRowParams);

                    tableRow1.addView(createTv("Loteria", true, mContext));
                    tableRow1.addView(createTv("Jugada", true, mContext));
                    tableRow1.addView(createTv("Monto", true, mContext));
                    tableRow1.addView(createTv("Eliminar", true, mContext));
                    tableLayout.addView(tableRow1);
                }
                /* add views to the row */
            Log.e("JugadasFragment", "addRowToTable: " + PrincipalFragment.jugadasClase.size());
                tableRow.setId(PrincipalFragment.jugadasClase.size());
                tableRow.addView(createTv(jugada.getDescripcion(), false, mContext));
                tableRow.addView(createTv(Utilidades.agregarGuion(jugada.getJugada()), false, mContext));
                tableRow.addView(createTv(String.valueOf(jugada.getMonto()), false, mContext));

                /* create cell element - button */
                Button btn = new Button(mContext);
                btn.setText("x");
                btn.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
                btn.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
                //btn.setBackgroundColor(0xff12dd12);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int id = ((View)view.getParent()).getId();
                        TableRow r = (TableRow)((Activity) mContext).findViewById(id);
                        TableLayout t = (TableLayout)((Activity) mContext).findViewById(R.id.tableJugadas);
                        t.removeView(r);
                        Log.d("Pariente:" , String.valueOf(id));

//                            PrincipalFragment.jugadasClase.deleteFromId(id);
                        try{
                            Log.d("Pariente:" , String.valueOf(PrincipalFragment.jugadasClase.remove(jugada)));

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        PrincipalFragment.calcularTotal();
                    }
                });
                tableRow.addView(btn);

                /* add the row to the table */
                tableLayout.addView(tableRow);
            }catch (Exception e){
                e.printStackTrace();
            }



    }

//    @Override
//    public void onResume() {
//        super.onResume();
//
////        LinearLayout.LayoutParams tableRowParams = new LinearLayout.LayoutParams(
////                LinearLayout.LayoutParams.MATCH_PARENT,
////                LinearLayout.LayoutParams.WRAP_CONTENT);
////
////        /* create a table row */
////        TableRow tableRow = new TableRow(mContext);
////        tableRow.setLayoutParams(tableRowParams);
////        tableRow.addView(createTv("Loteria", false, getActivity()));
////        tableRow.addView(createTv("Jugada", false, getActivity()));
////        tableRow.addView(createTv("Monto", false, getActivity()));
////        tableRow.addView(createTv("Eliminar", false, getActivity()));
////
////
////
////
////
////
////    /* add the row to the table */
////                tableLayout.addView(tableRow);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View viewRoot = inflater.inflate(R.layout.fragment_jugadas, container, false);
//        tableLayout = (TableLayout)view.findViewById(R.id.tableJugadas);
        LinearLayout linearLayout = viewRoot.findViewById(R.id.contenedor);





        tableLayout = (TableLayout)viewRoot. findViewById(R.id.tableJugadas);

        btnEliminarTodasLasJugadas = (Button)viewRoot.findViewById(R.id.btnEliminarTodasLasJugadas);
        btnEliminarTodasLasJugadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tableLayout.removeAllViews();
                PrincipalFragment.jugadasClase.clear();
                PrincipalFragment.calcularTotal();
            }
        });


//        PrincipalFragment principalFragment = new PrincipalFragment();
//        JSONArray jugadas = principalFragment.jugadas;
//        Bundle bundle = this.getArguments();
//        Log.d("Jugadas fragment:", bundle.getString("jugadas"));
//        try{
//            jugadas = new JSONArray(bundle.getString("jugadas"));
//            Log.d("Try catchjugadas: ",jugadas.toString());
//        }catch (Exception e){
//            e.printStackTrace();
//        }

        Log.e("JugadasFragment", "onCreateView");
updateTable();
        return viewRoot;
    }


    private static TextView createTv(String text, boolean es_header, Context context){
        /* create cell element - textview */
        TextView tv = new TextView(context);
        TableRow.LayoutParams cellParams = new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        cellParams.weight = 1;
        tv.setLayoutParams(cellParams);
        tv.setGravity(Gravity.CENTER);
        //tv.setBackgroundColor(0xff12dd12);
        tv.setText(text);

        if(es_header)
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        else
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

        return tv;
    }
    private TextView crearTexView(String text, boolean es_header){
        TextView textView = new TextView(getContext());
        textView.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        textView.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        textView.setGravity(Gravity.CENTER);
        if(es_header)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 26);
        else
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

        return textView;
    }

    public void llenarTabla(){

    }
}
