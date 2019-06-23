package com.example.jean2.creta;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class PagarTicketDialog extends AppCompatDialogFragment {
    private EditText editTextCodigoBarra;
    private static PagarTicketDialogListener listener;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_pagar, null);

        builder.setView(view)
                .setTitle("Pagar ticket")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String codigo = editTextCodigoBarra.getText().toString();
                        listener.setCodigoBarraPagar(codigo);
                    }
                });

        editTextCodigoBarra= view.findViewById(R.id.editTextCodigoBarra);


        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //try {
        listener = (PagarTicketDialogListener) context;
//        }catch (ClassCastException e){
//            Log.e("DuplicarDialog", "onAttachError: " + e.toString());
////            throw new ClassCastException(context.toString() + "Must implement DuplicarDialogListener");
//            e.printStackTrace();
//        }
    }

    public interface PagarTicketDialogListener{
        void setCodigoBarraPagar(String codigoBarra);
    }
}
