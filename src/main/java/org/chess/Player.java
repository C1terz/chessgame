package org.chess;

import java.util.ArrayList;

public class Player {
    public ArrayList<Figure> advantage = new ArrayList<>();
    private Color color;
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
    public Color getColor(){
        return this.color;
    }
    public void setColor(Color setTo){
        this.color = setTo;
    }
    public PlayerData getAttachedPlayer(){
        return this.attachedPlayer;
    }
}