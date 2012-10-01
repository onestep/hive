package hive.game;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class OpeningDB
        implements Serializable, Constants {

    private HashMap map;

    public static OpeningDB read(InputStream paramInputStream)
            throws IOException, ClassNotFoundException {
        ObjectInputStream localObjectInputStream = new ObjectInputStream(new BufferedInputStream(paramInputStream));
        OpeningDB localOpeningDB = (OpeningDB) localObjectInputStream.readObject();
        return localOpeningDB;
    }

    public OpeningDB() {
        this.map = new HashMap();
    }

    public void storeMove(Game game, Move move) {
        int i = 0;
        do {
            i++;
            TableRepresentation localTableRepresentation = new TableRepresentation(game.table);
            HashSet localHashSet = (HashSet) this.map.get(localTableRepresentation);

            if (localHashSet == null) {
                localHashSet = new HashSet();
                this.map.put(localTableRepresentation, localHashSet);
            }
            localHashSet.add(move);

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
        return (Collection) map.get(new TableRepresentation(game.table));
    }

    public void write(OutputStream paramOutputStream)
            throws IOException {
        ObjectOutputStream localObjectOutputStream = new ObjectOutputStream(new BufferedOutputStream(paramOutputStream));
        localObjectOutputStream.writeObject(this);
        localObjectOutputStream.flush();
        localObjectOutputStream.close();
    }

    @Override
    public String toString() {
        return map.toString();
    }

    public static void main(String[] paramArrayOfString)
            throws ClassNotFoundException, IOException {
        Game game = new Game();
        Move move = Move.instance(Constants.pieces[0][0], null, Coords.instance(0, 0));
        OpeningDB openingDB = new OpeningDB();

        System.out.println("Original table:");
        System.out.println(game.table);

        openingDB.storeMove(game, move);
        game.doMove(move);

        System.out.println("DB:");
        System.out.println(openingDB);
        FileOutputStream localFileOutputStream = new FileOutputStream("opening_db.ser");
        openingDB.write(localFileOutputStream);
        FileInputStream localFileInputStream = new FileInputStream("opening_db.ser");
        openingDB = read(localFileInputStream);
        System.out.println("rereaded DB:");
        System.out.println(openingDB);

        game = new Game();
        System.out.println("Stored moves: ");
        System.out.println(openingDB.retrieveMoves(game));

        System.out.println("Testing table representations:");
        Game localGame2 = new Game();
        Game localGame3 = new Game();
        TableRepresentation localTableRepresentation1 = new TableRepresentation(localGame2.table);
        TableRepresentation localTableRepresentation2 = new TableRepresentation(localGame3.table);

        System.out.println("Hashed OK ??:" + (localTableRepresentation1.hashCode() == localTableRepresentation2.hashCode()));
        System.out.println("Equal  OK ??:" + localTableRepresentation1.equals(localTableRepresentation2));
    }
}