package com.example.jean2.creta;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Jugadas {
    JSONArray jsonArrayJugadas;
    public Jugadas(){
        jsonArrayJugadas = new JSONArray();
    }
    public void add(JSONObject jsonObject){
        jsonArrayJugadas.put(jsonObject);
    }

    public int length(){
        return jsonArrayJugadas.length();
    }

    public void deleteFromId(int idFilaJugadaFragment){
        JSONArray lista = new JSONArray();
        int posicionJugada = idFilaJugadaFragment - 1;
        if(jsonArrayJugadas != null){
            for(int i=0; i < jsonArrayJugadas.length(); i++){
                if(i != posicionJugada){
                    try {
                        lista.put(jsonArrayJugadas.get(i));
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }//End for
            jsonArrayJugadas = lista;
        }
    }

    public JSONArray getJsonArrayJugadas() {
        return jsonArrayJugadas;
    }

    public String jugadaInvertir(String jugada){
        String primerDigito = jugada.toString().substring(0, 1);
        String segundoDigito = jugada.toString().substring(1, 2);

        //Invertimos la jugada
        return segundoDigito + primerDigito;
    }

    public boolean jugadaExiste(String jugada, String idLoteria){
        boolean existe = false;
        if(jugada.length() == 0)
            return existe;

        try {
            for (int i=0; i < jsonArrayJugadas.length(); i++){
                JSONObject item = (JSONObject)jsonArrayJugadas.get(i);

                if(item.getString("jugada").toString().equals(jugada) && item.getString("idLoteria").toString().equals(idLoteria)){
                    existe = true;
                }

            }
        }catch (JSONException e){
            Log.e("jugadaExisteError:", e.toString());
            return false;
        }

        return existe;
    }
    public boolean siJugadaExisteActualizar(String jugada, String idLoteria, String monto){
        boolean existe = false;
        if(jugada.length() == 0)
            return existe;

        if(jsonArrayJugadas.length() == 0)
            return existe;

        try {
            for (int i=0; i < jsonArrayJugadas.length(); i++){
                JSONObject item = (JSONObject)jsonArrayJugadas.get(i);

                if(item.getString("jugada").toString().equals(jugada) && item.getString("idLoteria").toString().equals(idLoteria)){
                    item.put("monto", Float.parseFloat(item.getString("monto")) + Float.parseFloat(monto));
                    existe = true;
                }

            }
        }catch (JSONException e){
            Log.e("sijugadaExisteActError:", e.toString());
            return false;
        }

        return existe;
    }

    public String jugadaQuitarPunto(String jugada){
        if(jugada.length() != 3)
            return jugada;

        StringBuilder validarJugadaSeaNumerica = new StringBuilder(jugada);
        validarJugadaSeaNumerica.setLength(jugada.length() - 1);

        return validarJugadaSeaNumerica.toString();
    }



    public boolean validarJugadaSeaCorrecta(String jugada){
        boolean correcta = true;
        if(jugada.length() > 1 && jugada.length() <= 6){





        }
        else
            correcta =false;

        return correcta;
    }



    public static boolean isInteger(String cadena) {



        try {
            Integer.parseInt(cadena);
        } catch(Exception e) {
            return false;
        }


        return true;
    }

    public int calcularTotal(){
        int total = 0;
        try {
            for (int i=0; i < jsonArrayJugadas.length(); i++){
                JSONObject item = (JSONObject)jsonArrayJugadas.get(i);
                int monto = (int)Float.parseFloat(item.getString("monto").toString());

                Log.d("Jugadas", "calcularTotal monto: " + monto);
//                if(isInteger(monto))
//                    total += Integer.parseInt(monto);
                total += monto;
            }
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }

        return total;
    }


    public void removeAll(){
        jsonArrayJugadas = new JSONArray(new ArrayList<String>());
    }

    public void duplicar(JSONArray jsonArray){
        jsonArrayJugadas = jsonArray;
    }


}
