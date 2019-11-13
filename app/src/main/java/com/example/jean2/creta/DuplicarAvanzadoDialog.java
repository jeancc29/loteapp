package com.example.jean2.creta;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.JsonArrayRequest;
import com.example.jean2.creta.Clases.JugadaClass;
import com.example.jean2.creta.Clases.LoteriaClass;
import com.example.jean2.creta.Clases.VentasClass;
import com.itextpdf.text.pdf.parser.Line;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DuplicarAvanzadoDialog extends AppCompatDialogFragment {

    Context mContext;
    VentasClass ventasClass;
    public static String[] listDescripcionLoterias = new String[PrincipalFragment.listDescripcionLoterias.length + 2];
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_duplicar_avanzado, null);
        LinearLayout linearLayoutPrincipal = (LinearLayout)view.findViewById(R.id.linealLayoutPrincipal);
        llenarListaLoterias();


//        JSONArray loterias = obtenerAtributoJsonArrayDuplicar("loterias");
//        JSONArray jsonArrayJugadas = obtenerAtributoJsonArrayDuplicar("jugadas");
        try{
            for(LoteriaClass loteria : Main2Activity.loteriasLista){
                LinearLayout linearLayout = createLinealLayout(mContext);

                Spinner spinner = createSpinner(mContext);
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, listDescripcionLoterias);
                spinner.setAdapter(spinnerArrayAdapter);

                TextView textView;
                int itemIndex = Arrays.asList(PrincipalFragment.listDescripcionLoterias).indexOf(loteria.getDescripcion());
                if(itemIndex == -1){
                     textView = createTv(loteria.getDescripcion(), 2, mContext, true);
                }else{
                     textView = createTv(loteria.getDescripcion(), 1, mContext, true);
                }
                //Log.d("duplicarIndex", String.valueOf(itemIndex));
//                int idLoteria = Utilidades.toInt(PrincipalFragment.idLoteriasMap.get(itemIndex));
//                spinner.setId(idLoteria);
                spinner.setTag(loteria.getDescripcion());
                linearLayout.addView(textView);
                linearLayout.addView(spinner);
                linearLayoutPrincipal.addView(linearLayout);
//                JSONArray jugadas = jugadasPertenecientesALoteria(loteria.getString("id"), jsonArrayJugadas);

            }
        }catch (Exception e){
            e.printStackTrace();
        }


        builder.setView(view)
                .setTitle("Duplicar ticket")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try{
//                            JSONArray loterias = obtenerAtributoJsonArrayDuplicar("loterias");
//                            JSONArray jsonArrayJugadas = obtenerAtributoJsonArrayDuplicar("jugadas");

                            List<JugadaClass> jugadasQueYaEstabanInsertadasAntesDeDuplicar = new ArrayList<JugadaClass>();
                            if(PrincipalFragment.jugadasClase.size() > 0){
                                jugadasQueYaEstabanInsertadasAntesDeDuplicar = Utilidades.clonarJugadasList(PrincipalFragment.jugadasClase);
                            }
                            for(LoteriaClass loteria: Main2Activity.loteriasLista){
                                String descripcion;
                                int idLoteria = 0;

                                Spinner spinner = (Spinner)view. findViewWithTag(loteria.getDescripcion());
                                if(spinner.getSelectedItem().toString().equals("- NO COPIAR -")){
                                    continue;
                                }

                                int itemIndex = Arrays.asList(PrincipalFragment.listDescripcionLoterias).indexOf(loteria.getDescripcion());

                                if(spinner.getSelectedItem().toString().equals("- NO MOVER -")){
                                    if(itemIndex == -1){
                                        continue;
                                    }else{
                                        descripcion = loteria.getDescripcion();
                                        idLoteria = loteria.getId();
                                    }
                                }
                                else{
                                    descripcion = spinner.getSelectedItem().toString();
                                    itemIndex = Arrays.asList(PrincipalFragment.listDescripcionLoterias).indexOf(descripcion);
                                    idLoteria = Utilidades.toInt(PrincipalFragment.idLoteriasMap.get(itemIndex));
                                }

                                List<JugadaClass> jugadas = jugadasPertenecientesALoteria(loteria.getId());

                                for(JugadaClass jugadaClass : jugadas){
                                    JSONObject jugadaObject = new JSONObject();
                                    String jugada = jugadaClass.getJugada();
                                    //jugada = Utilidades.ordenarMenorAMayor(jugada);
                                    jugada = Utilidades.agregarGuionPorSorteo(jugada, jugadaClass.getSorteo());
                                    Log.e("Duplicar", String.valueOf(jugadasQueYaEstabanInsertadasAntesDeDuplicar.size()));
//                                    jugada = Utilidades.agregarGuionPorSorteo(jugada, ((JSONObject) jugadas.get(contadorJugada)).getString("sorteo"));
                                    if(jugadasQueYaEstabanInsertadasAntesDeDuplicar.size() > 0){
                                        if(Utilidades.jugadaExiste(jugadasQueYaEstabanInsertadasAntesDeDuplicar,jugada, String.valueOf(idLoteria))){
                                            PrincipalFragment.aceptaInsertarJugadaExistente(jugada, String.valueOf(idLoteria), descripcion, String.valueOf(jugadaClass.getMonto()), jugadasQueYaEstabanInsertadasAntesDeDuplicar.size());
                                            continue;
                                        }
                                    }

//                                    Log.d("duplicarExiste", jugada);
//                                    jugadaObject.put("jugada", jugada);
//
//                                    jugadaObject.put("descripcion", descripcion);
//                                    jugadaObject.put("idLoteria", idLoteria);
//                                    jugadaObject.put("monto", Float.parseFloat(((JSONObject) jugadas.get(contadorJugada)).getString("monto").toString()));
//                                    Log.d("PrincipalFragment", "Duplicar jugada: " + jugadaObject.getString("descripcion") + " " +jugadaObject.getString("jugada"));
//                                    PrincipalFragment.jugadasClase.add(jugadaObject);

                                    JugadaClass jugadaClassInsertar = new JugadaClass();
                                    jugadaClassInsertar.setIdLoteria(idLoteria);
                                    jugadaClassInsertar.setDescripcion(descripcion);
                                    jugadaClassInsertar.setJugada(jugada);
                                    jugadaClassInsertar.setSorteo("no");
//                        jugadaClassInsertar.setTam(txtJugada.getText().length());
                                    jugadaClassInsertar.setMonto(jugadaClass.getMonto());
                                    jugadaClassInsertar.setIdBanca(Utilidades.getIdBanca(mContext));
//                                    PrincipalFragment.jugadasClase.add(jugadaClassInsertar);
                                    Utilidades.addJugada(jugadaClassInsertar);
                                }
//                                Toast.makeText(mContext, "spinner: " + spinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();


                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        PrincipalFragment.calcularTotal();

                    }
                });





        return builder.create();
    }

    private JSONArray obtenerAtributoJsonArrayDuplicar(String atributo){
        try{
            return Main2Activity.duplicarDatos.getJSONArray(atributo);
        }catch (Exception e){
            e.printStackTrace();

            return new JSONArray();
        }
    }

    private String obtenerAtributoJsonObjectDuplicar(String atributo){
        try{
            return MonitoreoActivity.selectedTicket.getString(atributo);
        }catch (Exception e){
            e.printStackTrace();

            return "";
        }
    }

    private void llenarListaLoterias(){
        listDescripcionLoterias[0] = "- NO MOVER -";
        listDescripcionLoterias[1] = "- NO COPIAR -";
        for(int i=0; i < PrincipalFragment.listDescripcionLoterias.length; i++){
            listDescripcionLoterias[i + 2] = PrincipalFragment.listDescripcionLoterias[i];
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;


    }

    public static List<JugadaClass> jugadasPertenecientesALoteria(int idLoteria) {
        int contadorJugadas = 0;
        List<JugadaClass> jugadasListaRetornar = new ArrayList<JugadaClass>();

        for (JugadaClass j: Main2Activity.jugadasLista) {
            if(j.getIdLoteria() == idLoteria){
                jugadasListaRetornar.add(j);
            }
        }

        return jugadasListaRetornar;
    }

    private LinearLayout createLinealLayout(Context context){
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout tableLayout = new LinearLayout(context);
        tableLayout.setLayoutParams(layoutParams);

        return tableLayout;
    }

    private Spinner createSpinner(Context context){
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Spinner spinner = new Spinner(context);
        spinner.setLayoutParams(layoutParams);

        return spinner;
    }

    private static TextView createTv(String text, int es_loteria_cerrada_o_no, Context context, boolean center){
        /* create cell element - textview */
        TextView tv = new TextView(context);
        TableRow.LayoutParams cellParams = new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        cellParams.weight = 1;


        //tv.setBackgroundColor(0xff12dd12);
        tv.setText(text);

        if(es_loteria_cerrada_o_no == 1){
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
//            tv.setBackgroundResource(R.color.colorPrimary);
//            tv.setTextColor(Color.WHITE);
            tv.setPadding(2, 10, 2, 10);
        }
        else if(es_loteria_cerrada_o_no == 2){
//            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            tv.setPadding(2, 10, 2, 10);
            tv.setTextColor(Color.RED);
        }
        else{
            cellParams = new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
            tv.setTextColor(Color.BLACK);
        }

        if(center)
            tv.setGravity(Gravity.CENTER);

        tv.setLayoutParams(cellParams);
        return tv;
    }


}
