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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */


public class JugadasFragment extends Fragment implements Updateable{

    private static TableLayout tableLayout;
    JSONArray jugadas = PrincipalFragment.jugadas;
    public static Context mContext;

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
        for(int i=0; i < PrincipalFragment.jugadas.length(); i++){
            try {
                JSONObject jugada = PrincipalFragment.jugadas.getJSONObject(i);
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
                    tableRow.addView(createTv(jugada.getString("descripcion"), false, mContext));
                    tableRow.addView(createTv(jugada.getString("jugada"), false, mContext));
                    tableRow.addView(createTv(jugada.getString("monto"), false, mContext));

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

                            PrincipalFragment.jugadasClase.deleteFromId(id);
                            PrincipalFragment.calcularTotal();
                        }
                    });

                    tableRow.addView(btn);




                /* add the row to the table */
                tableLayout.addView(tableRow);
                idRow++;
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    @Override
    public void onResume() {
        super.onResume();

//        LinearLayout.LayoutParams tableRowParams = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT);
//
//        /* create a table row */
//        TableRow tableRow = new TableRow(mContext);
//        tableRow.setLayoutParams(tableRowParams);
//        tableRow.addView(createTv("Loteria", false, getActivity()));
//        tableRow.addView(createTv("Jugada", false, getActivity()));
//        tableRow.addView(createTv("Monto", false, getActivity()));
//        tableRow.addView(createTv("Eliminar", false, getActivity()));
//
//
//
//
//
//
//    /* add the row to the table */
//                tableLayout.addView(tableRow);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View viewRoot = inflater.inflate(R.layout.fragment_jugadas, container, false);
//        tableLayout = (TableLayout)view.findViewById(R.id.tableJugadas);
        LinearLayout linearLayout = viewRoot.findViewById(R.id.contenedor);





        tableLayout = (TableLayout)viewRoot. findViewById(R.id.tableJugadas);





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
        if(PrincipalFragment.jugadas == null){
            return viewRoot;
        }
        for(int i=0; i < PrincipalFragment.jugadas.length(); i++){
            try {
                JSONObject jugada = PrincipalFragment.jugadas.getJSONObject(i);
                LinearLayout.LayoutParams tableRowParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

                /* create a table row */
                TableRow tableRow = new TableRow(getActivity());
                tableRow.setLayoutParams(tableRowParams);
                int idRow = i;
                tableRow.setId(idRow);




                if(i == 0){
                    /* add views to the row */
                    tableRow.addView(createTv("Loteria", false, getActivity()));
                    tableRow.addView(createTv("Jugada", false, getActivity()));
                    tableRow.addView(createTv("Monto", false, getActivity()));
                    tableRow.addView(createTv("Eliminar", false, getActivity()));
                }else{
                    /* add views to the row */
                    tableRow.addView(createTv(jugada.getString("descripcion"), false, getActivity()));
                    tableRow.addView(createTv(jugada.getString("jugada"), false, getActivity()));
                    tableRow.addView(createTv(jugada.getString("monto"), false, getActivity()));

                    /* create cell element - button */
                    Button btn = new Button(getActivity());
                    btn.setText("x");
                    btn.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
                    btn.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
                    //btn.setBackgroundColor(0xff12dd12);
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int id = ((View)view.getParent()).getId();
                            TableRow r = (TableRow)viewRoot.findViewById(id);
                            TableLayout t = (TableLayout)viewRoot.findViewById(R.id.tableJugadas);
                            t.removeView(r);
                            Log.d("Pariente:" , String.valueOf(id));
                        }
                    });

                    tableRow.addView(btn);
                }



                /* add the row to the table */
                tableLayout.addView(tableRow);
            }catch (Exception e){
                e.printStackTrace();
            }

        }

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
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 26);
        else
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

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
