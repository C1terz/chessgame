package org.chess;

//FOLLOWING CODE IS AI GENERATED,I GUIDED IT INTO MAKING THESE TESTS, WHICH ARE TO VALIDATE CHESS HANDLER WORKING PROPERLY. I DOUBLE CHECKED THE CODE TO MAKE SURE IT'S VALID. WRITING TESTS IS BORING.

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class PieceTest {
    private Figure[][] emptyBoard;
    private PlayerData plr1;
    private PlayerData plr2;
    //we set up our tests before actually testing by creating empty figure board and mock plrs
    @BeforeEach
    void setUp() {
        emptyBoard = new Figure[8][8];
        plr1 = new PlayerData("Carl", "id1");
        plr2 = new PlayerData("Bob", "id2");
    }

    // This method is used to see if generated moves by a piece contain the one we need.
    private boolean containsMove(ArrayList<int[]> moves, int row, int col) {
        return moves.stream().anyMatch(m -> m[0] == row && m[1] == col);
    }

    // ==========================================
    // 1. PAWN TESTS
    // ==========================================

    @Test
    void testPawnInitialDoubleStep() {
        Pawn whitePawn = new Pawn(Color.WHITE);
        whitePawn.setPosition(emptyBoard, new int[]{1, 4}); // e2

        ArrayList<int[]> moves = whitePawn.pieceMoves(emptyBoard);
        assertTrue(containsMove(moves, 2, 4), "Pawn should move forward 1 step");
        assertTrue(containsMove(moves, 3, 4), "Pawn on home row should move forward 2 steps");
    }

    @Test
    void testPawnForwardBlockade() {
        Pawn whitePawn = new Pawn(Color.WHITE);
        whitePawn.setPosition(emptyBoard, new int[]{1, 4}); // e2

        Rook blocker = new Rook(Color.BLACK); // Block right in front
        blocker.setPosition(emptyBoard, new int[]{2, 4}); // e3

        ArrayList<int[]> moves = whitePawn.pieceMoves(emptyBoard);
        assertFalse(containsMove(moves, 2, 4), "Pawn cannot capture or move into a forward blocker");
        assertFalse(containsMove(moves, 3, 4), "Pawn cannot skip over a forward blocker");
    }

    @Test
    void testPawnStandardAndEnPassantCaptures() {
        Pawn whitePawn = new Pawn(Color.WHITE);
        whitePawn.setPosition(emptyBoard, new int[]{4, 4}); // e5

        Pawn targetPawn = new Pawn(Color.BLACK);
        targetPawn.setPosition(emptyBoard, new int[]{5, 5}); // f6

        Pawn enPassantTarget = new Pawn(Color.BLACK);
        enPassantTarget.setPosition(emptyBoard, new int[]{4, 3}); // d5
        enPassantTarget.hasMoved = PieceState.ENPASSANT; // Set en passant vulnerability state

        ArrayList<int[]> moves = whitePawn.pieceMoves(emptyBoard);
        assertTrue(containsMove(moves, 5, 5), "Pawn should be able to capture diagonally");
        assertTrue(containsMove(moves, 5, 3), "Pawn should be able to capture via En Passant");
    }

    // ==========================================
    // 2. KNIGHT TESTS
    // ==========================================

    @Test
    void testKnightLShapedMovementAndLeaping() {
        Knight knight = new Knight(Color.WHITE);
        knight.setPosition(emptyBoard, new int[]{3, 3}); // d4

        // Surround the knight with blocking allies to verify it leaps over them
        for (int r = 2; r <= 4; r++) {
            for (int c = 2; c <= 4; c++) {
                if (r == 3 && c == 3) continue;
                Pawn ally = new Pawn(Color.WHITE);
                ally.setPosition(emptyBoard, new int[]{r, c});
            }
        }

        ArrayList<int[]> moves = knight.pieceMoves(emptyBoard);
        assertEquals(8, moves.size(), "Knight should jump over pieces to all 8 standard legal positions");
        assertTrue(containsMove(moves, 5, 4));
        assertTrue(containsMove(moves, 1, 2));
    }

    // ==========================================
    // 3. SLIDING PIECES (BISHOP, ROOK, QUEEN)
    // ==========================================

    @Test
    void testBishopDiagonalObstacles() {
        Bishop bishop = new Bishop(Color.WHITE);
        bishop.setPosition(emptyBoard, new int[]{3, 3}); // d4

        Pawn ally = new Pawn(Color.WHITE);
        ally.setPosition(emptyBoard, new int[]{5, 5}); // Friendly block on f6

        Pawn enemy = new Pawn(Color.BLACK);
        enemy.setPosition(emptyBoard, new int[]{1, 1}); // Enemy target on b2

        ArrayList<int[]> moves = bishop.pieceMoves(emptyBoard);

        assertFalse(containsMove(moves, 5, 5), "Cannot occupy friendly sliding space");
        assertFalse(containsMove(moves, 6, 6), "Cannot skip past a friendly blockade");

        assertTrue(containsMove(moves, 1, 1), "Can capture enemy at end of a clear path");
        assertFalse(containsMove(moves, 0, 0), "Cannot slide further through an enemy piece");
    }

    @Test
    void testRookOrthogonalMovement() {
        Rook rook = new Rook(Color.BLACK);
        rook.setPosition(emptyBoard, new int[]{0, 0}); // a1

        ArrayList<int[]> moves = rook.pieceMoves(emptyBoard);
        // From a1 on an empty board, it should command 7 vertical squares and 7 horizontal squares
        assertEquals(14, moves.size());
        assertTrue(containsMove(moves, 7, 0));
        assertTrue(containsMove(moves, 0, 7));
    }

    // ==========================================
    // 4. KING & CASTLING TESTS
    // ==========================================

    @Test
    void testKingStandardMovement() {
        King king = new King(Color.WHITE);
        king.setPosition(emptyBoard, new int[]{4, 4}); // e5

        ArrayList<int[]> moves = king.pieceMoves(emptyBoard);
        assertEquals(8, moves.size(), "Unconstrained center king should have 8 moves");
    }

    @Test
    void testKingValidCastling() {
        King king = new King(Color.WHITE);
        king.setPosition(emptyBoard, new int[]{0, 4}); // e1

        Rook kingsideRook = new Rook(Color.WHITE);
        kingsideRook.setPosition(emptyBoard, new int[]{0, 7}); // h1

        ArrayList<int[]> moves = king.pieceMoves(emptyBoard);
        assertTrue(containsMove(moves, 0, 6), "King should be allowed to castle kingside (g1)");
    }

    @Test
    void testKingCastlingBlockedOrAttacked() {
        King king = new King(Color.WHITE);
        king.setPosition(emptyBoard, new int[]{0, 4}); // e1

        Rook kingsideRook = new Rook(Color.WHITE);
        kingsideRook.setPosition(emptyBoard, new int[]{0, 7}); // h1

        // Place a black rook raycasting an attack onto the f1 transit square
        Rook enemyRook = new Rook(Color.BLACK);
        enemyRook.setPosition(emptyBoard, new int[]{4, 5}); // f5 attacking f1

        ArrayList<int[]> moves = king.pieceMoves(emptyBoard);
        assertFalse(containsMove(moves, 0, 6), "King cannot castle through a square under active attack");
    }

    // ==========================================
    // 5. SYSTEM GAME INTEGRATION TESTS
    // ==========================================

    @Test
    void testInvalidMoveStringFailsGracefully() {
        ActiveGame game = new ActiveGame();
        game.startGame(plr1, plr2);

        // Assert that executing off-board invalid coordinates won't crash execution loop
        assertDoesNotThrow(() -> game.playerAct(plr1, "z9", "e4"));
        assertDoesNotThrow(() -> game.playerAct(plr1, "e2", " 0"));
    }

    @Test
    void testSelfCheckPrevention() {
        ActiveGame game = new ActiveGame();
        game.startGame(plr1, plr2);

        // Advance a basic setup sequence to prepare a pinned execution space
        game.playerAct(plr1, "e2", "e4"); // White Pawn
        game.playerAct(plr2, "e7", "e5"); // Black Pawn
        game.playerAct(plr1, "d1", "h5"); // White Queen out to h5

        // Try to move black's f7 pawn which exposes the black king directly to white's queen
        Figure[][] boardBefore = game.getBoard();
        game.playerAct(plr2, "f7", "f6");

        // Validate that because moving f7 causes a check, the game rolls the state back
        assertSame(boardBefore, game.getBoard(), "Action sequence should revert if move puts own king in check");
    }
    // ==========================================
    // EXTRA ADVANCED MECHANICS TESTS
    // ==========================================

    @Test
    void testComprehensiveCastlingRules() {
        // 1. Setup a clean baseline: White King on e1 (0,4), White Rook on h1 (0,7)
        King king = new King(Color.WHITE);
        king.setPosition(emptyBoard, new int[]{0, 4});

        Rook rook = new Rook(Color.WHITE);
        rook.setPosition(emptyBoard, new int[]{0, 7});

        // Test A: Normal clear path, King has not moved -> Castling should be ALLOWED (g1 / 0,6)
        ArrayList<int[]> movesBefore = king.pieceMoves(emptyBoard);
        assertTrue(movesBefore.stream().anyMatch(m -> m[0] == 0 && m[1] == 6),
                "King should be able to castle kingside under ideal conditions.");

        // Test B: Blocked path -> Place a Knight on g1 (0,6)
        Knight blocker = new Knight(Color.WHITE);
        blocker.setPosition(emptyBoard, new int[]{0, 6});

        ArrayList<int[]> movesBlocked = king.pieceMoves(emptyBoard);
        assertFalse(movesBlocked.stream().anyMatch(m -> m[0] == 0 && m[1] == 6),
                "King should NOT be allowed to castle if pieces block the path.");

        // Remove blocker for the next phase
        emptyBoard[0][6] = null;

        // Test C: Path is clear, but an enemy Bishop is attacking f1 (0,5) from c4 (3,2)
        Bishop enemyBishop = new Bishop(Color.BLACK);
        enemyBishop.setPosition(emptyBoard, new int[]{3, 2});

        ArrayList<int[]> movesUnderAttack = king.pieceMoves(emptyBoard);
        assertFalse(movesUnderAttack.stream().anyMatch(m -> m[0] == 0 && m[1] == 6),
                "King should NOT be allowed to castle through an attacked square (f1).");
    }

    @Test
    void testCheckRaycastAndBlockading() {
        // 1. Setup White King on e1 (0,4)
        King king = new King(Color.WHITE);
        king.setPosition(emptyBoard, new int[]{0, 4});

        // 2. Place an enemy Queen far away on the same file: e8 (7,4)
        Queen enemyQueen = new Queen(Color.BLACK);
        enemyQueen.setPosition(emptyBoard, new int[]{7, 4});

        // Test A: Verify the King detects it is under Check via open vertical raycast
        boolean isCheckedOpenLine = king.raycastThreats(emptyBoard, king.getPosition());
        assertTrue(isCheckedOpenLine, "King should detect a check from an open vertical file raycast.");

        // Test B: Interpose a shield piece on e4 (3,4) to block the Queen's raycast line of sight
        Pawn blockingPawn = new Pawn(Color.WHITE);
        blockingPawn.setPosition(emptyBoard, new int[]{3, 4});

        // Verify the King is no longer in check because the file raycast gets broken by the pawn
        boolean isCheckedBlockedLine = king.raycastThreats(emptyBoard, king.getPosition());
        assertFalse(isCheckedBlockedLine, "King should NOT register a check if a piece blocks the raycast path.");
        Board.printBoardState(emptyBoard);
    };

}
