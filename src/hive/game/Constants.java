package hive.game;

public interface Constants {

    int QUEEN = 0;
    int SPIDER = 1;
    int BEETLE = 2;
    int ANT = 3;
    int HOPPER = 4;

    int BLUE = 0;
    int SILVER = 1;

    int WIN = 4000;
    int LOSS = -4000;
    int INFINITY = 1000000000;

    String[] pieceNames = {"bee", "spider", "beetle", "ant", "hopper"};
    String[] colorNames = {"blue", "silver"};
    int[] howManyPieces = {1, 2, 2, 3, 3};
    boolean[] mustHaveFreedom = {true, true, false, true, false};
    Piece[][] pieces = Piece.pieces;
    Piece[] queens = {pieces[BLUE][QUEEN], pieces[SILVER][QUEEN]};
    int[] neighbourToC1 = {0, 1, 1, 0, -1, -1};
    int[] neighbourToC2 = {2, 1, -1, -2, -1, 1};
}