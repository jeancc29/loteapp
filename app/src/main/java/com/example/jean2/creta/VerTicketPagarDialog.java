package com.example.jean2.creta;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jean2.creta.Clases.JugadaClass;
import com.example.jean2.creta.Clases.LoteriaClass;
import com.example.jean2.creta.Clases.VentasClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VerTicketPagarDialog extends AppCompatDialogFragment {
    Context mContext;
    TableLayout tableLayoutJugadas;
    LinearLayout linealLayoutJugadas;
    TextView txtMonto;
    TextView txtPendientePago;
    TextView txtPremioTotal;
    VentasClass ventasClass;

    boolean tienePremios = false;
    boolean tienePendientes = false;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_ver_ticket_pagar, null);
        ventasClass = getArguments().getParcelable("venta");


        linealLayoutJugadas = (LinearLayout) view.findViewById(R.id.linealLayoutJugadas);
        txtMonto = (TextView) view.findViewById(R.id.txtMonto);
        txtPendientePago = (TextView) view.findViewById(R.id.txtPendientePago);
        txtPremioTotal = (TextView) view.findViewById(R.id.txtPremioTotal);

        updateTableTotalesPorLoteria();
        builder.setView(view)
                .setTitle("Duplicar ticket")
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Pagar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Main2Activity.pagarTicket(ventasClass.getCodigoQr(), true);
                    }
                });




        return builder.create();
    }

    private String obtenerAtributoJsonObjectTicket(String atributo){
        try{
            return Main2Activity.pagarTicketDatos.getString(atributo);
        }catch (Exception e){
            e.printStackTrace();

            return "";
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }


    public List<JugadaClass> jugadasPertenecientesALoteria(int idLoteria) {
        int contadorJugadas = 0;
        List<JugadaClass> jugadasListaRetornar = new ArrayList<JugadaClass>();

        for (JugadaClass j: ventasClass.getJugadas()) {
            if(j.getIdLoteria() == idLoteria){
                jugadasListaRetornar.add(j);
            }
        }

        return jugadasListaRetornar;
    }

    public  void updateTableTotalesPorLoteria(){



        if(ventasClass == null){
            return ;
        }
        linealLayoutJugadas.removeAllViews();
        int idRow = 0;

        if(ventasClass.getJugadas().size() == 0){
            linealLayoutJugadas.removeAllViews();
            Toast.makeText(mContext, "No hay datos", Toast.LENGTH_SHORT).show();
            return;
        }



        try{


            txtMonto.setText(String.valueOf(ventasClass.getTotal()));
            txtPendientePago.setText(String.valueOf(ventasClass.getMontoAPagar()));
            txtPremioTotal.setText(String.valueOf(ventasClass.getPremio()));

            if(ventasClass.getMontoAPagar() > 0 ) {
                txtPendientePago.setTextColor(getResources().getColor(R.color.bgRed));
            }
            else{
                txtPendientePago.setTextColor(getResources().getColor(R.color.bgInfo));
            }

            for(LoteriaClass loteria : ventasClass.getLoterias()) {
                TableLayout tableLayout = createTableLayout(mContext);
                boolean esPrimeraJugadaAInsertar = true;
                List<JugadaClass> jugadas = jugadasPertenecientesALoteria(loteria.getId());

                TextView txtLoteria = createTv(loteria.getDescripcion(), 3, mContext, true);
                linealLayoutJugadas.addView(txtLoteria);
                int c=0;
                for (JugadaClass jugada : jugadas) {


                    LinearLayout.LayoutParams tableRowParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);

                    /* create a table row */
                    TableRow tableRow = new TableRow(mContext);
                    tableRow.setLayoutParams(tableRowParams);



                    tableRow.setId(idRow);


                    if (c == 0) {
                        /* add views to the row */
                        TableRow tableRow1 = new TableRow(mContext);
                        tableRow1.setId(idRow);
                        tableRow1.setLayoutParams(tableRowParams);
                        idRow++;
                        tableRow1.addView(createTv("Jugada", 1, mContext, true));
                        tableRow1.addView(createTv("Tipo jugada", 1, mContext, true));
                        tableRow1.addView(createTv("Importe", 1, mContext, true));
                        tableRow1.addView(createTv("Monto", 1, mContext, true));
//                        tableRow1.addView(createTv("Pagado", 1, mContext, true));
//                    tableRow1.addView(createTv("Neto", true, mContext, true));
                        tableLayout.addView(tableRow1);
                    }
                    /* add views to the row */
                    tableRow.setId(idRow);
                    tableRow.addView(createTv(jugada.getJugada(), 2, mContext, true));
                    tableRow.addView(createTv(jugada.getSorteo(), 2, mContext, true));
                    tableRow.addView(createTv(String.valueOf(jugada.getMonto()), 2, mContext, true));
                    tableRow.addView(createTv(String.valueOf(jugada.getPremio()), 2, mContext, true));
//                    tableRow.addView(createTv(jugada.getString("pagado"), 2, mContext, true));
//                tableRow.addView(createTv(dato.getString("neto"), false, mContext, true));


                    if(jugada.getStatus() == 1 && jugada.getPremio() <= 0) {
                        tableRow.setBackgroundColor(getResources().getColor(R.color.bgRosa));
                    }
                    else if(jugada.getStatus() == 1 && jugada.getPremio() > 0){
                        tableRow.setBackgroundColor(getResources().getColor(R.color.bgInfo));
                    }else{
                        tableRow.setBackgroundColor(getResources().getColor(R.color.bgGris));
                    }
                    tableLayout.addView(tableRow);
                    idRow++;
                    c++;

                }

                linealLayoutJugadas.addView(tableLayout);

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private static TextView createTv(String text, int es_normal_header_grande, Context context, boolean center){
        /* create cell element - textview */
        TextView tv = new TextView(context);
        TableRow.LayoutParams cellParams = new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        cellParams.weight = 1;


        //tv.setBackgroundColor(0xff12dd12);
        tv.setText(text);

        if(es_normal_header_grande == 1){
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
//            tv.setBackgroundResource(R.color.colorPrimary);
//            tv.setTextColor(Color.WHITE);
            tv.setPadding(2, 10, 2, 10);
        }
        else if(es_normal_header_grande == 2){
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            tv.setTextColor(Color.BLACK);
        }
        else{
            cellParams = new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
            tv.setTextColor(Color.BLACK);
            tv.setPadding(2, 10, 2, 10);
        }

        if(center)
            tv.setGravity(Gravity.CENTER);

        tv.setLayoutParams(cellParams);
        return tv;
    }

    private TableLayout createTableLayout(Context context){
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TableLayout tableLayout = new TableLayout(context);
        tableLayout.setLayoutParams(layoutParams);

        return tableLayout;
    }
}
