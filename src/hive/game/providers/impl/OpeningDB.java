package hive.game;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class OpeningDB implements Serializable, Constants {

    private HashMap<TableRepresentation, HashSet<Move>> map;

    public static OpeningDB read(InputStream is) throws IOException {
        OpeningDB db = new OpeningDB();
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        while (in.ready()) {
            byte[] rep = new byte[54];
            for (int i = 0; i < 54; i++)
                rep[i] = (byte) in.read();
            byte numMoves = (byte) in.read();
            HashSet<Move> moves = new HashSet<Move>(numMoves);
            for (int i = 0; i < numMoves; i++) {
                /* TODO: read prevCoords and piece color - now null and SILVER */
                byte type = (byte) in.read();
                byte c1 = (byte) in.read();
                byte c2 = (byte) in.read();
                moves.add(Move.instance(Constants.pieces[SILVER][type], null, Coords.instance(c1, c2)));
            }
            db.map.put(new TableRepresentation(rep), moves);
        }
        return db;
    }

    public void write(OutputStream outputStream) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(outputStream));
        out.writeObject(this);
        out.flush();
        out.close();
    }    
    
    public OpeningDB() {
        map = new HashMap<TableRepresentation, HashSet<Move>>();
    }

    public void storeMove(Game game, Move move) {
        int i = 0;
        do {
            i++;
            TableRepresentation tr = new TableRepresentation(game.table);
            HashSet<Move> moves = map.get(tr);

            if (moves == null) {
                moves = new HashSet<Move>();
                map.put(tr, moves);
            }
            moves.add(move);

            game = rotateGameOnce(game);
            move = rotateMoveOnce(move);
        } while (i < 6);
    }

    private Coords rotateCoordsOnce(Coords coords) {
        int i = (coords.c1 + coords.c2) / 2;
        int j = (coords.c2 - 3 * coords.c1) / 2;
        return Coords.instance(i, j);
    }

    private Move rotateMoveOnce(Move move) {
        Piece piece = move.piece;
        Coords prevCoords = null;
        if (move.prevCoords != null)
            prevCoords = rotateCoordsOnce(move.prevCoords);
        Coords newCoords = rotateCoordsOnce(move.newCoords);
        return Move.instance(piece, prevCoords, newCoords);
    }

    private Game rotateGameOnce(Game game) {
        Game rotatedGame = new Game();
        for (Coords coords = game.table.firstCoords(); coords != null; coords = game.table.nextCoords()) {
            Coords rotatedCoords = rotateCoordsOnce(coords);
            for (int i = game.table.countPiecesAt(coords) - 1; i >= 0; i--) {
                Move move = Move.instance(game.table.getPieceAt(coords, i), null, rotatedCoords);
                rotatedGame.doMove(move);
            }
        }
        return rotatedGame;
    }

    public Collection retrieveMoves(Game game) {
        System.out.println(map.get(new TableRepresentation(game.table)));
        return map.get(new TableRepresentation(game.table));
    }

    @Override
    public String toString() {
        return map.toString();
    }

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        Game game = new Game();
        Move move = Move.instance(Constants.pieces[BLUE][QUEEN], null, Coords.instance(0, 0));
        OpeningDB openingDB = new OpeningDB();

        System.out.println("Original table:");
        System.out.println(game.table);

        openingDB.storeMove(game, move);
        game.doMove(move);

        System.out.println("DB:");
        System.out.println(openingDB);
        FileOutputStream fos = new FileOutputStream("opening_db.ser");
        openingDB.write(fos);
        FileInputStream fis = new FileInputStream("opening_db.ser");
        openingDB = read(fis);
        System.out.println("rereaded DB:");
        System.out.println(openingDB);

        game = new Game();
        System.out.println("Stored moves: ");
        System.out.println(openingDB.retrieveMoves(game));

        System.out.println("Testing table representations:");
        Game game1 = new Game();
        Game game2 = new Game();
        TableRepresentation tr1 = new TableRepresentation(game1.table);
        TableRepresentation tr2 = new TableRepresentation(game2.table);

        System.out.println("Hash OK ??:" + (tr1.hashCode() == tr2.hashCode()));
        System.out.println("Equals OK ??:" + tr1.equals(tr2));
    }
}