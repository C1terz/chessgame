package org.chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Figure {
    public String position;
    public Color color;
    public int advantageValue;
    public PieceState hasMoved = PieceState.IDLE;
    protected Pieces type;
    public int[] getIndex(){
        return Board.transformIndex(position);
    }
    public ArrayList<int[]>  pieceMoves(Figure[][] board){
        return new ArrayList<>();
    }
    public void Promote(Figure[][] board, Pieces promoteTo){
    }
    public ArrayList<int[]> slidingPieceMoves(Figure[][] board,int[][]directions){
        int[] index = this.getIndex();
        ArrayList<int[]> posMoves = new ArrayList<>();
        for (int[] dir : directions) {
            int nextRow = index[0] + dir[0];
            int nextCol = index[1] + dir[1];
            while (nextRow >= 0 && nextRow < 8 && nextCol >= 0 && nextCol < 8) {
                Figure target = board[nextRow][nextCol];
                if (target != null) {
                    if (Objects.equals(target.color, this.color)) {
                        break;
                    }
                    posMoves.add(new int[]{nextRow, nextCol});
                    break;
                }
                posMoves.add(new int[]{nextRow, nextCol});
                nextRow += dir[0];
                nextCol += dir[1];
            }
        }
        return posMoves;
    }
    public Figure[][] Move(Figure[][] board, String moveTo){
        int[] userMoveIndex = Board.transformIndex(moveTo);
        ArrayList<int[]> possibleMoves=this.pieceMoves(board);
        int[] index = this.getIndex();
        for (int[] pos:possibleMoves){
            if (Arrays.equals(userMoveIndex, pos)){
                board[pos[0]][pos[1]]=this;
                board[index[0]][index[1]]=null;
                return board;
            }
        }
        return board;
    }

}

class Pawn extends Figure{
    public Pawn(Color color){
        this.color = color;
        this.type = Pieces.PAWN;
        this.advantageValue = 1;
    }
    @Override
    public ArrayList<int[]> pieceMoves(Figure[][] board){
        int direction = Objects.equals(this.color, Color.WHITE) ? 1 : -1;
        int[] index = this.getIndex();
        int steps = ((index[0]==1&&Objects.equals(this.color, Color.WHITE))||(index[0]==6&& Objects.equals(this.color, Color.BLACK))) ? 2:1;
        int[][] possibleCapture = new int[][]{{direction,-1},{direction,1},{0,-1},{0,1}};
        ArrayList<int[]> posMoves = new ArrayList<>();
        for (int[] posCap:possibleCapture){
            int nextRow = index[0]+posCap[0];
            int nextCol = index[1]+posCap[1];
            if (nextRow < 0 || nextRow > 7||nextCol < 0 || nextCol > 7) {
                continue;
            }
            if ((board[nextRow][nextCol]!=null)&&!(Objects.equals(board[nextRow][nextCol].color, this.color))) {
                if (posCap[0]==0&&board[nextRow][nextCol].hasMoved!=PieceState.ENPASSANT) continue;
                posMoves.add(new int[]{nextRow, nextCol});
            }
        }
        for (int i = 1; i <= steps; i++) {
            int nextRow = index[0] + (i * direction);
            if (nextRow < 0 || nextRow > 7) {
                break;
            }
            if (board[nextRow][index[1]] == null) {
                posMoves.add(new int[]{nextRow, index[1]});
            } else {
                break;
            }
        }
        return posMoves;
    }
    @Override
    public void Promote(Figure[][] board,Pieces promoteTo){
        Figure p = null;
        switch (promoteTo){
            case Pieces.BISHOP -> p = new Bishop(this.color);
            case Pieces.KNIGHT -> p =new Knight(this.color);
            case Pieces.ROOK -> p = new Rook(this.color);
            default -> p = new Queen(this.color);
        }
        p.position = this.position;
        int[] pos = Board.transformIndex(p.position);
        board[pos[0]][pos[1]]=p;
    }
}
class Knight extends Figure{
    public Knight(Color color){
        this.type = Pieces.KNIGHT;
        this.color = color;
        this.advantageValue = 3;
    }
    @Override
    public ArrayList<int[]>  pieceMoves(Figure[][] board){
        ArrayList<int[]> posMoves = new ArrayList<>();
        int[] index = this.getIndex();
        int[][] knightMoves = new int[][]{{-1,2},{-2,1},{1,2},{2,1},{-1,-2},{-2,-1},{1,-2},{2,-1}};
        for (int[] knightMovePos:knightMoves) {
            int nextRow = index[0]+knightMovePos[0];
            int nextCol = index[1]+knightMovePos[1];
            if (nextRow < 0 || nextRow > 7||nextCol < 0 || nextCol > 7) {
                continue;
            }
            if ((board[nextRow][nextCol]!=null)&&(Objects.equals(board[nextRow][nextCol].color, this.color))) {
                continue;
            } else {
                posMoves.add(new int[]{nextRow, nextCol});
            }
        }
        return posMoves;
    }

}
class Bishop extends Figure{
    public Bishop(Color color){
        this.color = color;
        this.type = Pieces.BISHOP;
        this.advantageValue = 3;
    }
    @Override
    public ArrayList<int[]>  pieceMoves(Figure[][] board){

        int[] index = this.getIndex();
        int[][] bishopMovesDirection = new int[][]{{1,1},{1,-1},{-1,1},{-1,-1}};
        return this.slidingPieceMoves(board,bishopMovesDirection);
    }

}
class Rook extends Figure{
    public Rook(Color color){
        this.color = color;
        this.type = Pieces.ROOK;
        this.advantageValue = 5;
    }
    @Override
    public ArrayList<int[]>  pieceMoves(Figure[][] board){
        int[] index = this.getIndex();
        int[][] rookMovesDirection = new int[][]{{1,0},{0,-1},{-1,0},{0,1}};
        return this.slidingPieceMoves(board,rookMovesDirection);
    }

}
class Queen extends Figure{
    public Queen(Color color){
        this.color = color;
        this.type = Pieces.QUEEN;
        this.advantageValue = 9;
    }
    @Override
    public ArrayList<int[]>  pieceMoves(Figure[][] board){
        int[] index = this.getIndex();
        int[][] rookMovesDirection = new int[][]{{1,0},{0,-1},{-1,0},{0,1},{1,1},{1,-1},{-1,1},{-1,-1}};
        return this.slidingPieceMoves(board,rookMovesDirection);
    }
}
class King extends Figure{
    public King(Color color){
        this.color = color;
        this.type = Pieces.KING;
        this.advantageValue = 11;
    }
    @Override
    public ArrayList<int[]>  pieceMoves(Figure[][] board){
        int[] index = this.getIndex();
        ArrayList<int[]> posMoves = new ArrayList<>();
        String startPos = (this.color==Color.WHITE) ? "e1":"e8";
        boolean kingNotMoved = (Objects.equals(this.position, startPos));
        this.getKingMoves(board,posMoves);
        if (kingNotMoved){
            if (canCastle(board,index[0],index[1],7)){
                posMoves.add(new int[]{index[0],index[1]+2});
            }
            if (canCastle(board,index[0],index[1],0)){
                posMoves.add(new int[]{index[0],index[1]-2});
            }
        }
        return posMoves;
    }
    public void getKingMoves(Figure[][] board,ArrayList<int[]> posMoves){
        int[] index = this.getIndex();
        int[][] kingMovesDirection = new int[][]{{1,0},{0,-1},{-1,0},{0,1},{1,1},{1,-1},{-1,1},{-1,-1}};
        for (int[] dir:kingMovesDirection){
            int nextRow = index[0]+dir[0];
            int nextCol = index[1]+dir[1];
            if (nextRow < 0 || nextRow > 7||nextCol < 0 || nextCol > 7) {
                continue;
            }
            if (board[nextRow][nextCol] == null || !(Objects.equals(board[nextRow][nextCol].color, this.color))) {
                posMoves.add(new int[]{nextRow, nextCol});
            }
        }
    }
    private boolean canCastle(Figure[][] board, int row, int kingCol, int rookCol) {
        Figure rook = board[row][rookCol];
        if (rook == null || rook.type != Pieces.ROOK || rook.hasMoved==PieceState.MOVED || !Objects.equals(rook.color, this.color)) {
            return false;
        }

        int direction = (rookCol > kingCol) ? 1 : -1;
        for (int col = kingCol + direction; col != rookCol; col += direction) {
            if (board[row][col] != null) {
                return false;
            }
        }
        Color enemycolor = Objects.equals(this.color, Color.WHITE) ? Color.BLACK:Color.WHITE;
        ArrayList<int[]> enemyMoves = ActiveGame.getBoardMoves(board,enemycolor);
        for (int step = 0; step <= 2; step++) {
            int checkCol = kingCol + (step * direction);
            for (int[] mov:enemyMoves){
                if (Arrays.equals(mov, new int[]{row, checkCol})){
                    return  false;
                }
            }
        }
        return true;
    }
}