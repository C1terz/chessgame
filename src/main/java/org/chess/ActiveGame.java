package org.chess;

import java.util.ArrayList;
import java.util.Objects;

public class ActiveGame {
    private Figure[][] gameState = new Figure[8][8];
    private String gameHistory = "";
    private boolean isGameActive = true;
    private Player white;
    private Player black;
    public Figure[][] getBoard(){
        return  this.gameState;
    }
    public void startGame(PlayerData plr1, PlayerData plr2){
        this.white = new Player(Color.WHITE);
        this.white.attachPlayer(plr1);
        this.white.isPlayerTurn = true;
        this.black = new Player(Color.BLACK);
        this.black.attachPlayer(plr2);
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
                        Player selPlayer = (row<2)?this.white:this.black;
                        selPlayer.setKing(f);
                    }
                }
                if (f!=null)f.setPosition(this.gameState,new int[]{row,col});
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
    public void playerAct(PlayerData plrRequest, String posFrom, String posTo){
        if (!Board.validateMove(posFrom)||!Board.validateMove(posTo)||(posTo.equals(posFrom))) {
            System.out.println("Invalid Move."); return;}
        Player plr = this.white.getAttachedPlayer()==plrRequest?this.white:this.black;
        if (!this.isGameActive) return;
        if (!plr.isPlayerTurn) return;
        int[] posFromIndex = Board.transformIndex(posFrom);
        int[] posToIndex = Board.transformIndex(posTo);
        if (this.gameState[posFromIndex[0]][posFromIndex[1]]==null) return;
        Figure onBoardFigure = this.gameState[posToIndex[0]][posToIndex[1]];
        Figure pieceToMove = this.gameState[posFromIndex[0]][posFromIndex[1]];
        if (!Objects.equals(pieceToMove.color, plr.color)) return;
        Figure[][] board = this.gameState[posFromIndex[0]][posFromIndex[1]].move(this.gameState,posTo);
        if (board[posFromIndex[0]][posFromIndex[1]]!=null) return;
        Figure king = plr.getKing();
        Figure enPassantPawn = null;
        int[] enPassantPos = null;
        if (pieceToMove.type == Pieces.PAWN && posFromIndex[1] != posToIndex[1] && onBoardFigure == null) {
            enPassantPos = new int[]{posFromIndex[0], posToIndex[1]};
            enPassantPawn = board[enPassantPos[0]][enPassantPos[1]];
            board[enPassantPos[0]][enPassantPos[1]] = null;
        }
        assert king != null;
        boolean checked = this.isKingChecked(king,board);
        if (checked) {
            pieceToMove.setPosition(board,posFromIndex);
            if (enPassantPawn!=null) {
                enPassantPawn.setPosition(board,enPassantPos);
            }
            if (onBoardFigure!=null) onBoardFigure.setPosition(board,posToIndex);
            return;
        }
        Player opponent = Objects.equals(plr.color, Color.WHITE) ? this.black : this.white;
        for (Figure[] row:board){
            for (Figure piece:row){
                if (piece!=null&&piece.type==Pieces.PAWN&&piece.color==opponent.color&&piece.hasMoved==PieceState.ENPASSANT){
                    piece.hasMoved=PieceState.MOVED;
                }
            }
        }
        pieceToMove.hasMoved = PieceState.MOVED;
        if (pieceToMove.type==Pieces.KING&&Math.abs(posFromIndex[1]-posToIndex[1])>1){
            int row = posFromIndex[0];
            boolean isKingside = posToIndex[1] > posFromIndex[1];
            int oldRookCol = isKingside ? 7 : 0;
            int newRookCol = isKingside ? posToIndex[1] - 1 : posToIndex[1] + 1;
            Figure rook = board[row][oldRookCol];
            if (rook != null) {

                rook.setPosition(board,new int[]{row, newRookCol});
                rook.hasMoved = PieceState.MOVED;
            }
        } else if (pieceToMove.type==Pieces.PAWN) {
            //EN PASSANT LOGIC
            if (Math.abs(posFromIndex[0]-posToIndex[0])>1) {
                pieceToMove.hasMoved = PieceState.ENPASSANT;
            }

        }

        this.gameState = board;
        if ((pieceToMove.type==Pieces.PAWN)&&(posToIndex[0]==0||posToIndex[0]==7)){
            pieceToMove.promote(board,plr.promoteTo);
        }
        board[posToIndex[0]][posToIndex[1]].setPosition(board,Board.transformIndex(posTo));
        plr.isPlayerTurn=false;
        opponent.isPlayerTurn = true;
        if (enPassantPawn != null) {
            plr.advantage.add(enPassantPawn);
        }
        if (onBoardFigure != null){
            plr.advantage.add(onBoardFigure);
        }
        if (!playerHasLegalMoves(opponent,this.gameState)){
            if (isKingChecked(Objects.requireNonNull(opponent.getKing()),this.gameState)){
                this.endGame(opponent.color,"checkmate");
            }
            else{
                this.endGame(opponent.color,"stalemate");
            }
        }
        this.gameHistory += (posFrom+" "+posTo+" ");
    }
    public boolean isKingChecked(Figure king,Figure[][] board){
      return  king.raycastThreats(board,king.getPosition());
    }
    public boolean playerHasLegalMoves(Player plr, Figure[][] board){
        Figure king = plr.getKing();
        assert king != null;

        for (Figure[] row:board) {
            for (Figure fig : row) {
                if (fig != null && Objects.equals(fig.color, plr.color)) {
                    ArrayList<int[]> posMoves = fig.pieceMoves(board);
                    int[] figPos = fig.getPosition();
                    for (int[] move : posMoves) {
                        Figure prevFig = board[move[0]][move[1]];
                        fig.setPosition(board,move);
                        boolean inCheck = isKingChecked(king, board);
                        fig.setPosition(board,figPos);
                        board[move[0]][move[1]]=prevFig;
                        if (!inCheck) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


}