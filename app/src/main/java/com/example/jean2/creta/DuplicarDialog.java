package com.example.jean2.creta;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class DuplicarDialog extends AppCompatDialogFragment {
    private EditText editTextCodigoBarra;
    private static DuplicarDialogListener listener;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_duplicar, null);


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
                        String codigo = editTextCodigoBarra.getText().toString();
                        listener.setCodigoBarra(codigo);
                    }
                });

        editTextCodigoBarra= view.findViewById(R.id.editTextCodigoBarraDuplicar);


        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //try {
            listener = (DuplicarDialogListener) context;
            Log.e("DuplicarDialog", "onAttach:");
//        }catch (ClassCastException e){
//            Log.e("DuplicarDialog", "onAttachError: " + e.toString());
////            throw new ClassCastException(context.toString() + "Must implement DuplicarDialogListener");
//            e.printStackTrace();
//        }
    }

    public interface DuplicarDialogListener{
        void setCodigoBarra(String codigoBarra);
    }
}
