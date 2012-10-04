package hive.game;

import java.util.*;

public final class Game implements Constants {

    public Box box;
    public Table table;
    int[] moves = {0, 0};
    private static final int[] neighbourCountLT = {0, 0, 6, 32, 160, 800, 4000};

    private HashSet<Coords> freeCoords = new HashSet<Coords>(32);
    private HashSet<Coords> insertCoords = new HashSet<Coords>(32);
    private HashSet<Coords> busyCoords = new HashSet<Coords>(32);
    private HashSet<Coords> notAllowedCoords = new HashSet<Coords>(32);

    private boolean freeReady = false;
    private boolean insertReady = false;
    private boolean busyReady = false;
    private boolean notAllowedReady = false;

    private int insertColor = -1;
    private Coords notAllowed = null;

    private HashSet<Coords> _busyCoords = new HashSet<Coords>(32);
    private HashSet<Coords> _freeCoords = new HashSet<Coords>(32);

    private HashSet<Coords> set = new HashSet<Coords>(32);
    private HashSet<Coords> phaseSet = new HashSet<Coords>(32);
    private HashSet<Coords> resultSet = new HashSet<Coords>(64);

    public Game() {
	this.box = new Box();
	this.table = new Table();
    }

    public synchronized void reset() {
	this.box.reset();
	this.table.reset();
	this.moves[0] = 0;
	this.moves[1] = 0;
	this.freeReady = (this.busyReady = this.insertReady = this.notAllowedReady = false);
    }

    public synchronized Set<Move> getMoves(int color, Comparator comparator) {
	Set<Move> moveSet;
	if (comparator != null)
	    moveSet = new TreeSet(comparator);
	else
	    moveSet = new HashSet(64);

	getBusyCoords();
	getFreeCoords();

	if (canMove(color))
	    for (Coords busyCoord : busyCoords) {
		Piece piece = this.table.getPieceAt(busyCoord);

		if (piece.color == color) {
		    HashSet<Coords> targetCoords = null;

		    getNotAllowedCoords(busyCoord);

		    switch (piece.type) {
			case QUEEN:
			    targetCoords = getTargetCoordsForQueen(busyCoord);
			    break;
			case BEETLE:
			    targetCoords = getTargetCoordsForBeetle(busyCoord);
			    break;
			case ANT:
			    targetCoords = getTargetCoordsForAnt(busyCoord);
			    break;
			case SPIDER:
			    targetCoords = getTargetCoordsForSpider(busyCoord);
			    break;
			case HOPPER:
			    targetCoords = getTargetCoordsForHopper(busyCoord);
			    break;
			default:
			    System.out.println("Zly typ pionka");
		    }

		    for (Coords targetCoord : targetCoords)
			moveSet.add(Move.instance(piece, busyCoord, targetCoord));
		}
	    }

	getInsertCoords(color);

	for (Piece piece = box.first(color); piece != null; piece = box.next())
	    if ((!mustInsertQueen(color)) || (piece.type == QUEEN))
		for (Coords insertCoord : this.insertCoords)
		    moveSet.add(Move.instance(piece, null, insertCoord));

	return moveSet;
    }

    public Set<Move> getMoves(int paramInt) {
	return getMoves(paramInt, null);
    }

    public HashSet<Coords> getTargetCoords(Piece paramPiece, Coords paramCoords) {
	getBusyCoords();
	getFreeCoords();

	if (paramCoords == null) {
	    if ((!mustInsertQueen(paramPiece.color)) || (paramPiece.type == QUEEN)) {
		getInsertCoords(paramPiece.color);
		return (HashSet<Coords>) insertCoords.clone();
	    }
	    return new HashSet();
	}

	if (!canMove(paramPiece.color))
	    return new HashSet();
	getNotAllowedCoords(paramCoords);
	switch (paramPiece.type) {
	    case QUEEN:
		return (HashSet<Coords>) getTargetCoordsForQueen(paramCoords).clone();
	    case BEETLE:
		return (HashSet<Coords>) getTargetCoordsForBeetle(paramCoords).clone();
	    case ANT:
		return (HashSet<Coords>) getTargetCoordsForAnt(paramCoords).clone();
	    case SPIDER:
		return (HashSet<Coords>) getTargetCoordsForSpider(paramCoords).clone();
	    case HOPPER:
		return (HashSet<Coords>) getTargetCoordsForHopper(paramCoords).clone();
	}

	return null;
    }

    public synchronized void doMove(Move move) {
	freeReady = false;
	busyReady = false;
	insertReady = false;
	notAllowedReady = false;

	if (move.prevCoords != null)
	    table.removePieceAt(move.prevCoords);
	else
	    box.remove(move.piece);
	table.putPieceAt(move.newCoords, move.piece);
	moves[move.piece.color] += 1;
    }

    public synchronized void unDoMove(Move move) {
	freeReady = false;
	busyReady = false;
	insertReady = false;
	notAllowedReady = false;

	table.removePieceAt(move.newCoords);
	if (move.prevCoords != null)
	    table.putPieceAt(move.prevCoords, move.piece);
	else
	    box.putback(move.piece);
	moves[move.piece.color]--;
    }

    private int min(int i, int j) {
	return i <= j ? i : j;
    }

    public final synchronized int evaluate(int color) {
	int i = halfEvaluate(color);
	int j = halfEvaluate(opponent(color));
	int k = (i < WIN ? j < WIN ? i - j : LOSS : j < WIN ? WIN : i - j);
	return k;
    }

    public final int halfEvaluate(int color) {
	int i = 0;

	Coords coords = table.firstCoordsForPiece(Constants.pieces[opponent(color)][0]);

	getBusyCoords();
	getFreeCoords();

	if (coords != null)
	    i = countNeighbours(coords);
	if ((i < 6) && (canMove(color)))
	    return neighbourCountLT[i] + (Constants.howManyPieces[ANT] - box.howMany(color, ANT)) << 1;
	return neighbourCountLT[i];
    }

    public boolean isWin(int color) {
	if (plyes() < 7)
	    return false;
	Coords coords = table.firstCoordsForPiece(Constants.queens[opponent(color)]);
	if (coords != null) {
	    for (int i = 0; i < 6; i++)
		if (table.countPiecesAt(coords.getNeighbour(i)) == 0)
		    return false;
	    return true;
	}
	return false;
    }

    public boolean hasFreedom(Coords coords) {
	getBusyCoords();
	getFreeCoords();

	switch (table.getPieceAt(coords).type) {
	    case BEETLE:
	    case HOPPER:
		return canRemove(coords);
	    case QUEEN:
	    case SPIDER:
	    case ANT:
		return (countNeighbours(coords) <= 4) && canRemove(coords);
	}
	return false;
    }

    public int countMoves(int color) {
	return this.moves[color];
    }

    public int plyes() {
	return this.moves[BLUE] + this.moves[SILVER];
    }

    public boolean canMove(int color) {
	return !box.contains(Constants.queens[color]);
    }

    public boolean mustInsertQueen(int color) {
	return box.contains(Constants.queens[color]) && (countMoves(color) == 3);
    }

    public static int opponent(int color) {
	return color == BLUE ? SILVER : BLUE;
    }

    void getBusyCoords() {
	if (busyReady)
	    return;

	busyCoords.clear();
	for (Coords localCoords = table.firstCoords(); localCoords != null; localCoords = table.nextCoords())
	    busyCoords.add(localCoords);

	busyReady = true;
	freeReady = false;
	insertReady = false;
	notAllowedReady = false;
    }

    private void getFreeCoords() {
	if (freeReady)
	    return;

	getBusyCoords();

	freeCoords.clear();
	if (table.size() == 0)
	    freeCoords.add(Coords.instance(0, 0));
	else
	    for (Coords busyCoord : busyCoords)
		for (int i = 0; i < 6; i++) {
		    Coords coords = busyCoord.getNeighbour(i);
		    if (table.countPiecesAt(coords) == 0)
			freeCoords.add(coords);
		}

	freeReady = true;
	insertReady = false;
	notAllowedReady = false;
    }

    private void getInsertCoords(int color) {
	if (insertReady && insertColor == color)
	    return;

	getBusyCoords();
	getFreeCoords();

	insertCoords.clear();
	insertCoords.addAll(freeCoords);

	if (countMoves(color) > 0) {
	    int i = opponent(color);
	    for (Coords localCoords1 : busyCoords) {
		Piece localPiece = table.getPieceAt(localCoords1);
		if (localPiece == null || localPiece.color != i)
		    continue;
		for (int j = 0; j < 6; j++) {
		    Coords localCoords2 = localCoords1.getNeighbour(j);
		    insertCoords.remove(localCoords2);
		}
	    }

	}

	insertReady = true;
	insertColor = color;
    }

    private int countNeighbours(Coords paramCoords) {
	getBusyCoords();
	int i = 0;
	for (int j = 0; j < 6; j++)
	    if (this.table.countPiecesAt(paramCoords.getNeighbour(j)) > 0)
		i++;
	return i;
    }

    void getNotAllowedCoords(Coords coords) {
	if (notAllowedReady && coords.equals(this.notAllowed))
	    return;

	getBusyCoords();
	getFreeCoords();

	notAllowedCoords.clear();
	if (table.countPiecesAt(coords) == 1)
	    for (int i = 0; i < 6; i++) {
		Coords localCoords = coords.getNeighbour(i);
		if ((freeCoords.contains(localCoords)) && (countNeighbours(localCoords) == 1))
		    notAllowedCoords.add(localCoords);
	    }
	notAllowedReady = true;
	notAllowed = coords;
    }

    private boolean canRemove(Coords paramCoords) {
	int i = this.table.countPiecesAt(paramCoords);
	if (i > 1)
	    return true;
	if (!mustCheckIntegrity(paramCoords))
	    return true;
	return checkIntegrity(paramCoords);
    }

    private boolean mustCheckIntegrity(Coords coords) {
	int i = 0;
	int j = 0;

	boolean k = table.countPiecesAt(coords.getNeighbour(5)) > 0;
	for (int m = 0; m < 6; m++) {
	    boolean n = table.countPiecesAt(coords.getNeighbour(m)) > 0;
	    if (n != k)
		j++;
	    k = n;
	    if (n)
		i++;
	}
	return (i > 1) && (i < 5) && (j != 2);
    }

    private boolean checkIntegrity(Coords coords) {
	getBusyCoords();
	getFreeCoords();

	phaseSet.clear();

	_busyCoords.clear();
	_busyCoords.addAll(busyCoords);
	_busyCoords.remove(coords);

	set.clear();

	Iterator it = _busyCoords.iterator();
	if (it.hasNext())
	    phaseSet.add((Coords) it.next());
	else
	    return true;

	int i;
	do {
	    i = 0;

	    HashSet<Coords> localSet = set;
	    set = phaseSet;
	    phaseSet = localSet;
	    phaseSet.clear();

	    for (Coords localCoords1 : set) {
		for (int j = 0; j < 6; j++) {
		    Coords localCoords2 = localCoords1.getNeighbour(j);

		    if (_busyCoords.remove(localCoords2)) {
			phaseSet.add(localCoords2);
			i = 1;
		    }
		}
	    }

	} while (i != 0);

	return this._busyCoords.isEmpty();
    }

    private boolean isFreedomToMove(Coords prevCoords, Coords newCoords) {
	int i = prevCoords.getEdge(newCoords);

	if (i < 0)
	    throw new Error();

	int j = nextEdge(i);
	int k = prevEdge(i);

	return (table.countPiecesAt(prevCoords.getNeighbour(j)) == 0) ^ (table.countPiecesAt(prevCoords.getNeighbour(k)) == 0);
    }

    private boolean isFreedomToMoveBeetle(Coords prevCoords, Coords newCoords) {
	int i = prevCoords.getEdge(newCoords);

	if (i < 0)
	    throw new Error();

	int j = nextEdge(i);
	int k = prevEdge(i);

	int m = table.countPiecesAt(prevCoords);
	int n = table.countPiecesAt(prevCoords.getNeighbour(j));
	int i1 = table.countPiecesAt(prevCoords.getNeighbour(k));
	int i2 = table.countPiecesAt(newCoords);

	boolean bool = m > 1 || i2 > 0 || i1 > 0 || n > 0;

	int i4 = m > i2 + 1 ? m : i2 + 1;

	return bool && (i1 < i4 || n < i4);
    }

    private HashSet<Coords> getTargetCoordsForQueen(Coords paramCoords) {
	resultSet.clear();

	if (hasFreedom(paramCoords))
	    for (int i = 0; i < 6; i++) {
		Coords localCoords = paramCoords.getNeighbour(i);
		if ((!notAllowedCoords.contains(localCoords)) && (freeCoords.contains(localCoords)) && (isFreedomToMove(paramCoords, localCoords)))
		    resultSet.add(localCoords);
	    }
	return resultSet;
    }

    private HashSet<Coords> getTargetCoordsForBeetle(Coords coords) {
	getFreeCoords();
	getBusyCoords();

	resultSet.clear();
	boolean bool = table.countPiecesAt(coords) > 1;

	if (hasFreedom(coords))
	    for (int j = 0; j < 6; j++) {
		Coords localCoords = coords.getNeighbour(j);
		if (isFreedomToMoveBeetle(coords, localCoords))
		    resultSet.add(localCoords);
	    }
	return resultSet;
    }

    private HashSet<Coords> getTargetCoordsForHopper(Coords coords) {
	resultSet.clear();

	if (hasFreedom(coords))
	    for (int i = 0; i < 6; i++) {
		Coords localCoords = coords.getNeighbour(i);

		if (busyCoords.contains(localCoords)) {
		    localCoords = localCoords.getNeighbour(i);
		    while (busyCoords.contains(localCoords))
			localCoords = localCoords.getNeighbour(i);

		    resultSet.add(localCoords);
		}
	    }

	return resultSet;
    }

    private HashSet<Coords> getTargetCoordsForAnt(Coords coords) {
	resultSet.clear();

	if (hasFreedom(coords)) {
	    Piece piece = table.removePieceAt(coords);
	    try {
		_freeCoords.clear();
		_freeCoords.addAll(freeCoords);
		_freeCoords.removeAll(notAllowedCoords);

		phaseSet.clear();
		set.clear();
		phaseSet.add(coords);
		boolean bool;
		do {
		    HashSet<Coords> localSet = set;
		    set = phaseSet;
		    phaseSet = localSet;
		    phaseSet.clear();

		    bool = false;
		    for (Coords setCoords : set) {
			for (int j = 0; j < 6; j++) {
			    Coords neighbour = setCoords.getNeighbour(j);

			    if (_freeCoords.contains(neighbour))
				if (isFreedomToMove(setCoords, neighbour)) {
				    phaseSet.add(neighbour);
				    bool = true;
				    _freeCoords.remove(neighbour);
				    resultSet.add(neighbour);
				}
			}
		    }

		} while (bool);
	    } finally {
		table.putPieceAt(coords, piece);
	    }
	}
	return resultSet;
    }

    private HashSet<Coords> getTargetCoordsForSpider(Coords paramCoords) {
	resultSet.clear();

	if (hasFreedom(paramCoords)) {
	    Piece localPiece = table.removePieceAt(paramCoords);
	    try {
		spiderStep(paramCoords, resultSet, -1, 0);
	    } finally {
		table.putPieceAt(paramCoords, localPiece);
	    }
	}
	return resultSet;
    }

    private void spiderStep(Coords paramCoords, Set<Coords> targetCoords, int paramInt1, int step) {
	if (step == 3)
	    targetCoords.add(paramCoords);
	else
	    for (int i = 0; i < 6; i++)
		if (i != paramInt1) {
		    Coords neighbour = paramCoords.getNeighbour(i);
		    if ((table.countPiecesAt(neighbour) == 0) && isFreedomToMove(paramCoords, neighbour))
			spiderStep(neighbour, targetCoords, (i + 3) % 6, step + 1);
		}
    }
    
    private static int nextEdge(int i) {
	return i == 5 ? 0 : ++i;
    }

    private static int prevEdge(int i) {
	return i == 0 ? 5 : --i;
    }    
}