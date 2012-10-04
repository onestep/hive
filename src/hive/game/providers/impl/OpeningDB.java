package hive.game.providers.impl;

import hive.game.Constants;
import hive.game.Coords;
import hive.game.Game;
import hive.game.Move;
import hive.game.Piece;
import hive.game.TableRepresentation;
import java.io.*;
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

    public void write(OutputStream os) throws IOException {
	BufferedWriter out = new BufferedWriter(new OutputStreamWriter(os));
	for (TableRepresentation tr : map.keySet()) {
	    for (int i = 0; i < 54; i++)
		out.write(tr.rep[i]);
	    HashSet<Move> moves = map.get(tr);
	    out.write(moves.size());
	    for (Move move : moves) {
		/* TODO: store prevCoords and piece color */
		out.write(move.piece.type);
		out.write(move.newCoords.c1);
		out.write(move.newCoords.c2);
	    }
	}
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

    public HashSet<Move> retrieveMoves(Game game) {
	return map.get(new TableRepresentation(game.table));
    }

    @Override
    public String toString() {
	return map.toString();
    }
}