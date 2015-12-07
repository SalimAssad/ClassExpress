package com.chacostak.salim.classexpress.Utilities;/*
18/11/2014

Author: Salim Assad Sánchez
*/

public class Validate{

    public Validate(){

    }

    //Retorna true si el String es nulo o si esta lleno de espacios
    public boolean isEmpty(String value){
        if(value == null)
            return true;
        for(int i = 0; i < value.length(); i++){
            if(value.charAt(i) != ' ')
                return false;
        }
        return true;
    }

    //Regresa si el valor dado es un número o no
    public boolean validateNumber(String value){
        try{
            double aux = Double.parseDouble(value);
            return true;
        }catch(NumberFormatException e){
            return false;
        }
    }

    //Regresa si encontro el caracter a buscar en el valor String dado
    public boolean findChar(String value, char c){
        for(int i = 0; i < value.length(); i++){
            if(value.charAt(i) == c)
                return true;
        }
        return false;
    }

    //Regresa si encontro el caracter a buscar en el valor String dado, debiendo sobrepasar el valor minimo y sin sobrepasar el valor maximo
    public boolean validateChar(String value, char c, int min, int max){
        boolean validate = true;
        int counter = 0;
        for(int i = 0; i < value.length(); i++){
            if(value.charAt(i) == c)
                counter++;
        }

        if(counter >= min && counter <= max)
            return true;
        else
            return false;
    }

    //Regresa el String tiene formato de email
    public boolean validateEmail(String value) {
        boolean hasDot = false;
        boolean dotBeforeAT = false;
        int counter = 0;
        if (isEmpty(value))
            return false;
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) == '@')
                counter++;
            if (value.charAt(i) == '.') {
                if (counter == 0) {
                    dotBeforeAT = false;
                    break;
                }
                hasDot = true;
            }
        }
        if (!dotBeforeAT && hasDot && counter == 1)
            return true;
        else
            return false;
    }

    //Regresa true si no hay números en el String, y false si tiene algún número
    public boolean noNumbersInString(String value){
        for(int i = 0; i < value.length(); i++){
            try {
                Integer.parseInt(String.valueOf(value.charAt(i)));
                return false;
            }catch(NumberFormatException e){

            }
        }
        return true;
    }
}