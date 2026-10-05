package org.chess;

import java.util.ArrayList;

public class Player {
    public ArrayList<Figure> advantage = new ArrayList<>();
    public Color color;
    private PlayerData attachedPlayer;
    public Pieces promoteTo=Pieces.QUEEN;
    public boolean isPlayerTurn = false;
    private Figure king;
    public Player(Color color){
        this.color= color;
    }
    public void setKing(Figure king){
        this.king = king;
    }
    public Figure getKing(){
        return this.king;
    }
    public void attachPlayer(PlayerData plr){
        this.attachedPlayer = plr;
    }
    public PlayerData getAttachedPlayer(){
        return this.attachedPlayer;
    }
}