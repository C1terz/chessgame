package org.chess;

public class Board {
    public static int[] transformIndex(String index) {
        int col = index.charAt(0) - 'a';
        int row = index.charAt(1) - '1';
        return new int[]{row, col};
    }

    public static String transformIndex(int[] index) {
        char col = (char) ('a' + index[1]);
        char row = (char) ('1' + index[0]);
        return "" + col + row;
    }
    public static boolean validateMove(String move){
        if (move.length()!=2)return false;
        char ch1 = move.charAt(0);
        char ch2 = move.charAt(1);
        return ('h' >= ch1 && ch1 >= 'a') && ('8' >= ch2 && ch2 >= '1');
    }
}
