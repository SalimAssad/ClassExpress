package com.chacostak.salim.classexpress.Utilities;

import java.util.ArrayList;

/**
 * Created by Salim on 09/04/2015.
 */
public class Sorter {

    public ArrayList bubbleSortRemainingTime(ArrayList<EventData> array){
        EventData aux;
        for(int i = 0; i < array.size(); i++){
            for(int j = 0; j < array.size()-1; j++){
                if(array.get(j).remainingTime > array.get(j+1).remainingTime){
                    aux = array.get(j);
                    array.set(j, array.get(j+1));
                    array.set(j+1, aux);
                }
            }
        }
        return array;
    }
}
