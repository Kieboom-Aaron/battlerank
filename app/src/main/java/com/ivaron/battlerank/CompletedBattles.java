package com.ivaron.battlerank;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;

/**
 * Created by Aaron on 20-6-2015.
 */
public class CompletedBattles {
    private ArrayList<Integer> battlesCompleted;
    private static CompletedBattles instance;
    private CompletedBattles(){
        battlesCompleted = new ArrayList<Integer>();
    }

    public static CompletedBattles getInstance(){
        if(instance == null){
            instance = new CompletedBattles();
        }
        return instance;
    }

    public void addCompletedBattle(int id){
        if(!battlesCompleted.contains(id)){
            battlesCompleted.add(id);
        }
    }

    public boolean isBattleCompleted(int id){
        return battlesCompleted.contains(id);
    }

    public String getLocalStorage(){
        String s = "";
        for(int i = 0; i < battlesCompleted.size(); i++){
            s += battlesCompleted.get(i) + ",";
        }
        return s;
    }

    public void setLocalStorage(String data){
        String[] stringNumbers = data.split(",");
        for(int i = 0; i< stringNumbers.length; i++){
            addCompletedBattle(Integer.parseInt(stringNumbers[i]));
        }
    }
}
