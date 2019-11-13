package com.example.jean2.creta.Clases;

import android.util.Log;


public class JSONMANAGERCLASS {



        String json;

        public JSONMANAGERCLASS(String json) {
            this.json = json;
        }

        public String getJson() {
            return json;
        }

        public void setJson(String json) {
            this.json = json;
        }

        public String getString(String key)
        {
            String retornar = "";
            key = "\"" + key + "\"";
            int idx = indexOfJson(key);
            Log.e("JSONMANAGER", "getString:" + String.valueOf(idx));
            if(idx == -1)
                return retornar;

            int idxFromStart = this.json.indexOf(":", idx);
            if(idxFromStart == -1)
                return retornar;

            //como la variable idxFromStart contiene del index del caracter : entonces le sumamos 1 para que tome el index del siguiente caracter
            idxFromStart ++;

            boolean primero = true;
            int contadorCorchetesAbiertos = 0;
            int contadorCorchetesCerrados = 0;
            int contadorLlavesAbiertas = 0;
            int contadorLlavesCerradas = 0;
            int contadorStringAbiertos = 0;
            String tipo = null;
            for(int i=idxFromStart; i < this.json.length(); i++){
                char c = json.charAt (i);
                if(c == ' '){
                    if(tipo != null){

                    }
                    else{
                        continue;
                    }
                }


                if(c == '{'){
                    if(primero == true){
                        tipo = "objecto";
                        primero = false;
                    }
                    contadorLlavesAbiertas++;
                }
                else if(c == '['){
                    if(primero == true){
                        tipo = "arreglo";
                        primero = false;
                    }
                    contadorCorchetesAbiertos++;
                }
                else if(c == '"'){
                    if(primero == true){
                        tipo = "string";
                        primero = false;
                    }
                    if(json.charAt (i - 1) != '\\')
                        contadorStringAbiertos++;
                }
                else if(c == '}'){
                    contadorLlavesCerradas++;
                }
                else if(c == ']'){
                    contadorCorchetesCerrados++;
                }
                else{
                    if(primero == true){
                        tipo = "otro";
                        primero = false;
                    }
                }

                if((tipo.equals("otro") && c == ',') || (tipo.equals("otro") && c == '}')){
                    return retornar;
                }
                retornar += c;
                //Si el tipo de dato es string y hay dos comillas dobles abiertas entonces el string se ha cerrado, osea que ya el
                // valor se ha obtenido por lo tanto se debe retorar
                if(tipo.equals("string")){
                    if(contadorStringAbiertos == 2)
                        return retornar;
                }

                if(tipo.equals("objecto")){
                    if(contadorLlavesAbiertas == contadorLlavesCerradas)
                        return retornar;
                }

                if(tipo.equals("arreglo")){
                    if(contadorCorchetesAbiertos == contadorCorchetesCerrados)
                        return retornar;
                }


            }

            return retornar;
        }

        //Se busca coincidencia solo en la rama principal
        //Cuando la llavesAbiertas es igual a 1 entonces estamos en la rama principal
        public int indexOfJson(String cadena)
        {

            int llavesAbiertas = 0;
            int llavesCerradas = 0;
            for(int i=0; i < this.json.length(); i++) {
                char caracterJson = json.charAt(i);

                if(caracterJson == '{'){
                    llavesAbiertas++;
                }
                if(caracterJson == '}'){
                    llavesAbiertas--;
                }
                //Cuando la variable llavesAbiertas = 1 eso quiere decir que estamos en la rama principal, de lo contrario sera
                // otro objecto asi que continuamos con el siguiente index porque es en la rama principal que queremos buscar
                if(llavesAbiertas != 1){
                    continue;
                }

                int contadorCaracteresEncontrados =0;
                boolean salir = false;

                for(int c=0; c < cadena.length() && salir == false; c++) {


                    char caracterCadena = cadena.charAt(c);
                    if(c > 0)
                        caracterJson = json.charAt(i + c);

                    if(caracterJson == caracterCadena){
                        contadorCaracteresEncontrados++;
                        if(contadorCaracteresEncontrados == cadena.length())
                            return i;
                    }else{
                        salir = true;
                    }
                }
            }

            return -1;
        }


}
