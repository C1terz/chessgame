package org.chess;

import java.util.ArrayList;

public class PlayerData {
    private String id;
    private String name;
    public PlayerData(String name, String id){
        this.name = name;
        this.id = id;
    }
    public void setId(String id){
        this.id = id;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getId(){
        return this.id;
    }
    public String getName(){
        return this.name;
    }


}