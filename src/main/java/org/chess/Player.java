package org.chess;

import java.util.ArrayList;

public class Player {
    public ArrayList<Figure> advantage = new ArrayList<>();
    public Color color;
    public Pieces promoteTo=Pieces.QUEEN;
    public boolean isPlayerTurn = false;
    public Player(Color color){
        this.color= color;
    }

}