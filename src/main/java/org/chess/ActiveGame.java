package org.chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class ActiveGame {
    private Figure[][] gameState = new Figure[8][8];
    private String gameHistory = "";
    private boolean isGameActive = true;
    private Player white;
    private Player black;
    public void startGame(){
        this.white = new Player(Color.WHITE);
        this.white.isPlayerTurn = true;
        this.black = new Player(Color.BLACK);
        for (int row=0;row<8;row++){
            for (int col=0;col<8;col++){
                Figure f = null;
                Color color = (row < 2) ? Color.WHITE : Color.BLACK;
                if (row==1||row == 6){
                    f= new Pawn(color);
                } else if (row==0||row==7) {
                    if (col==0||col==7){
                        f= new Rook(color);
                    }
                    if (col==1||col==6){
                        f = new Knight(color);
                    }
                    if (col==2||col==5){
                        f = new Bishop(color);
                    }
                    if (col==3){
                        f = new Queen(color);
                    }
                    if (col==4){
                        f = new King(color);
                    }
                }
                if (f!=null)f.position = Board.transformIndex(new int[]{row,col});
                this.gameState[row][col]=f;
            }
        }
    }
    private void endGame(Color lastcolor,String state){
        Player loser = (Objects.equals(lastcolor, Color.WHITE)) ? this.white:this.black;
        Player winner = (Objects.equals(lastcolor, Color.WHITE))?this.black:this.white;
        this.isGameActive = false;
        //THIS IS WIP
        switch (state){
            case "stalemate":

                System.out.println("STALEMATE");
                break;
            case "checkmate":
                System.out.println("WINNER:"+winner.color);
                break;
        }

    }
    public void playerAct(Player plr, String posFrom, String posTo){
        if (!this.isGameActive) return;
        if (!plr.isPlayerTurn) return;
        if (Objects.equals(posTo, " 0")) return;
        int[] posFromIndex = Board.transformIndex(posFrom);
        int[] posToIndex = Board.transformIndex(posTo);
        if (this.gameState[posFromIndex[0]][posFromIndex[1]]==null) return;
        Figure onBoardFigure = this.gameState[posToIndex[0]][posToIndex[1]];
        Figure pieceToMove = this.gameState[posFromIndex[0]][posFromIndex[1]];
        if (!Objects.equals(pieceToMove.color, plr.color)) return;
        Figure[][] board = this.gameState[posFromIndex[0]][posFromIndex[1]].Move(this.gameState,posTo);
        if (board[posFromIndex[0]][posFromIndex[1]]!=null) return;
        Figure king = getKing(board,plr.color);
        assert king != null;
        boolean checked = this.isKingChecked(king,board);
        if (checked) {
            board[posFromIndex[0]][posFromIndex[1]] = pieceToMove;
            board[posToIndex[0]][posToIndex[1]] = onBoardFigure;
            pieceToMove.position = posFrom;
            if (onBoardFigure!=null) {
                onBoardFigure.position = posTo;
            }
            return;
        }
        for (Figure[] row:board){
            for (Figure piece:row){
                if (piece!=null&&piece.type==Pieces.PAWN&&piece.color==plr.color&&piece.hasMoved==PieceState.ENPASSANT){
                    piece.hasMoved=PieceState.MOVED;
                }
            }
        }
        pieceToMove.hasMoved = PieceState.MOVED;
        if (pieceToMove.type==Pieces.KING&&Math.abs(posFromIndex[0]-posToIndex[0])>1){
            int row = posFromIndex[0];
            boolean isKingside = posToIndex[1] > posFromIndex[1];
            int oldRookCol = isKingside ? 7 : 0;
            int newRookCol = isKingside ? posToIndex[1] - 1 : posToIndex[1] + 1;
            Figure rook = board[row][oldRookCol];
            if (rook != null) {
                board[row][newRookCol] = rook;
                board[row][oldRookCol] = null;
                rook.position = Board.transformIndex(new int[]{row, newRookCol});
                rook.hasMoved = PieceState.MOVED;
            }
        } else if (pieceToMove.type==Pieces.PAWN) {
            //EN PASSANT LOGIC
            if (Math.abs(posFromIndex[1]-posToIndex[1])>1) {
                pieceToMove.hasMoved = PieceState.ENPASSANT;
            } else if (posFromIndex[0]!=0&&board[posToIndex[0]][posToIndex[1]]==null&&board[posToIndex[0]][posFromIndex[1]].type==Pieces.PAWN) {
                plr.advantage.add(board[posToIndex[0]][posFromIndex[1]]);
                board[posToIndex[0]][posFromIndex[1]]=null;
            }
        }

        this.gameState = board;
        if ((pieceToMove.type==Pieces.PAWN)&&(posToIndex[0]==0||posToIndex[0]==7)){
            pieceToMove.Promote(board,plr.promoteTo);
        }
        board[posToIndex[0]][posToIndex[1]].position=posTo;
        plr.isPlayerTurn=false;
        Player opponent = Objects.equals(plr.color, Color.WHITE) ? this.black : this.white;
        opponent.isPlayerTurn = true;
        if (onBoardFigure != null){
            plr.advantage.add(onBoardFigure);
        }
        if (!playerHasLegalMoves(opponent,this.gameState)){
            if (isKingChecked(Objects.requireNonNull(getKing(this.gameState, opponent.color)),this.gameState)){
                this.endGame(opponent.color,"checkmate");
            }
            else{
                this.endGame(opponent.color,"stalemate");
            }
        }
        this.gameHistory += (posFrom+" "+posTo+" ");
    }
    public boolean isKingChecked(Figure king,Figure[][] board){
      Color enemycolor = Objects.equals(king.color, Color.WHITE) ? Color.BLACK:Color.WHITE;
      ArrayList<int[]> enemyMoves = ActiveGame.getBoardMoves(board,enemycolor);
      for (int[] move:enemyMoves){
          if (Arrays.equals(Board.transformIndex(king.position), move)){
              return true;
          }
      }
      return false;
    }
    public boolean playerHasLegalMoves(Player plr, Figure[][] board){
        Figure king = getKing(board, plr.color);
        assert king != null;

        for (Figure[] row:board) {
            for (Figure fig : row) {
                if (fig != null && Objects.equals(fig.color, plr.color)) {
                    ArrayList<int[]> posMoves = fig.pieceMoves(board);
                    int[] figPos = Board.transformIndex(fig.position);
                    for (int[] move : posMoves) {
                        board[figPos[0]][figPos[1]] = null;
                        Figure prevFig = board[move[0]][move[1]];
                        board[move[0]][move[1]] = fig;
                        fig.position = Board.transformIndex(move);
                        boolean inCheck = isKingChecked(king, board);
                        board[move[0]][move[1]]=prevFig;
                        board[figPos[0]][figPos[1]]=fig;
                        fig.position = Board.transformIndex(figPos);
                        if (!inCheck) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    public static Figure getKing(Figure[][]board,Color color){
        for (Figure[] row:board){
            for (Figure fig:row){
                if (fig!=null&& Objects.equals(fig.type, Pieces.KING) && Objects.equals(fig
                        .color, color))return fig;
            }
        }
        return null;
    }
    public static ArrayList<int[]> getBoardMoves(Figure[][]board,Color color){
        ArrayList<int[]> boardMoves = new ArrayList<>();
        for (Figure[] row:board){
            for (Figure piece:row){
                if (piece!=null&& Objects.equals(piece.color, color)){
                    ArrayList<int[]> pieceMoves = new ArrayList<>();

                    if (Objects.equals(piece.type,Pieces.PAWN)){
                        int direction = Objects.equals(color, Color.WHITE) ? 1 : -1;
                        int[][] possibleCapture = new int[][]{{direction, -1},{direction,1}};
                        int[] index = piece.getIndex();
                        for (int[] posCap:possibleCapture){
                            int nextRow = index[0]+posCap[0];
                            int nextCol = index[1]+posCap[1];
                            if (nextRow < 0 || nextRow > 7||nextCol < 0 || nextCol > 7) {
                                continue;
                            }
                            pieceMoves.add(new int[]{nextRow, nextCol});
                        }
                    } else if (piece.type==Pieces.KING&& piece instanceof King king) {
                        king.getKingMoves(board,pieceMoves);
                    } else{
                        pieceMoves = piece.pieceMoves(board);
                    }
                    boardMoves.addAll(pieceMoves);
                }
            }
        }
        return boardMoves;
    }


}