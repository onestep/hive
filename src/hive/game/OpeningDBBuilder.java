package hive.game;

import hive.game.providers.impl.OpeningDB;
import hive.intf.MoveHighlighter;
import hive.intf.MoveProvider;
import hive.event.HiveMouseAdapter;
import hive.event.HiveMouseEvent;
import hive.event.HiveMouseListener;
import hive.gui.GameThread;
import hive.gui.HiveLabel;
import hive.gui.HivePane;
import hive.gui.HivePopupMenu;
import hive.gui.HiveScrollPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class OpeningDBBuilder extends JFrame
        implements Constants, MoveHighlighter, ActionListener {

    private OpeningDB db = new OpeningDB();
    GameThread gameThread;
    GetMoveState getMoveState;
    Game game;
    int playerColor = 0;
    int nonameInt = 0;
    File currentFile;
    boolean isNew = true;
    HashMap<Integer, Image> imagesMap = new HashMap<Integer, Image>();
    public HivePane tablePane;
    public HivePane[] boxPane = new HivePane[2];
    public HiveScrollPane tableScroll;
    private JPanel toolBar;
    private JPanel p;
    private HiveLabel[][] labels = new HiveLabel[2][];
    static final int[][] imageID = {{1, 2, 3, 4, 5}, {6, 7, 8, 9, 10}};
    private static final Color _coordsC = Color.black;
    private final String newGameMenuText = "New Game";
    private final String newDBMenuText = "New DB";
    private final String openDBMenuText = "Open DB...";
    private final String saveDBMenuText = "Save DB...";
    private final String exitMenuText = "Exit";
    private URL codeBase;
    private String imagesDir = "resources/images/";
    private String soundDir = "resources/sounds/";
    private String archive;
    private URL resURL;
    private JMenuItem miSaveDB;
    private JFileChooser fileChooser;
    private JMenuItem miExit;
    private JMenuBar menuBar;
    private JMenu mFile;
    private JMenuItem miNewGame;
    private JMenuItem miNewDB;

    public void newOpening() {
        this.game = new Game();
        this.playerColor = 0;
        this.getMoveState = new GetMoveState();
        GameProcess localGameProcess = new GameProcess(this.game, this.getMoveState, this.getMoveState, this.playerColor, new PrintWriter(System.out, true), this, true);

        this.gameThread = new GameThread(localGameProcess);
        resetHivePanes();
        calibratePanes();
        this.gameThread.start();
    }

    public void cleanUpGame() {
        if (this.gameThread != null)
            this.gameThread.finish();
    }

    private void readOpeningDB(File paramFile) {
        try {
            FileInputStream localFileInputStream = new FileInputStream(paramFile);
            this.db = OpeningDB.read(localFileInputStream);
        } catch (Exception localException) {
            localException.printStackTrace();
            this.db = new OpeningDB();
        }
        this.currentFile = paramFile;
    }

    private void saveOpeningDB(File paramFile) {
        try {
            FileOutputStream localFileOutputStream = new FileOutputStream(paramFile);
            this.db.write(localFileOutputStream);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        this.currentFile = paramFile;
    }

    private void calibratePanes() {
        Dimension localDimension = this.tablePane.getSize();

        System.out.println(localDimension);
        this.tablePane.setOriginX((int) (localDimension.getWidth() / 2.0D));
        this.tablePane.setOriginY((int) (localDimension.getHeight() / 2.0D));

        localDimension = this.boxPane[0].getSize();
        System.out.println(localDimension);
        this.boxPane[0].setOriginX((int) (localDimension.getWidth() / 2.0D));
        this.boxPane[0].setOriginY((int) (localDimension.getHeight() / 2.0D));

        localDimension = this.boxPane[1].getSize();
        System.out.println(localDimension);
        this.boxPane[1].setOriginX((int) (localDimension.getWidth() / 2.0D));
        this.boxPane[1].setOriginY((int) (localDimension.getHeight() / 2.0D));

        this.tableScroll.initHiveScrollPane();
    }

    public void highlightBefore(Move paramMove) {
        if (paramMove.piece.color != this.playerColor)
            this.db.storeMove(this.game, paramMove);
    }

    public void highlightAfter(final Move move) {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    OpeningDBBuilder.this.tablePane.clearHighlights();
                    OpeningDBBuilder.this.boxPane[0].clearHighlights();
                    OpeningDBBuilder.this.boxPane[1].clearHighlights();

                    if (move.prevCoords == null)
                        OpeningDBBuilder.this.updateBoxPane(move.piece.color);
                    else {
                        Piece localPiece = OpeningDBBuilder.this.game.table.getPieceAt(move.prevCoords);
                        OpeningDBBuilder.this.tablePane.setHivePiece(move.prevCoords.c1, move.prevCoords.c2, 0);
                        if (localPiece != null)
                            OpeningDBBuilder.this.tablePane.setHivePiece(move.prevCoords.c1, move.prevCoords.c2, OpeningDBBuilder.imageID[localPiece.color][localPiece.type]);
                    }
                    OpeningDBBuilder.this.tablePane.setHivePiece(move.newCoords.c1, move.newCoords.c2, OpeningDBBuilder.imageID[move.piece.color][move.piece.type]);

                    if (move.prevCoords == null)
                        OpeningDBBuilder.this.boxPane[move.piece.color].setHighlight(0, OpeningDBBuilder.this.coordC2FromPieceType(move.piece), Color.green, 3.0F, true);
                    else
                        OpeningDBBuilder.this.tablePane.setHighlight(move.prevCoords.c1, move.prevCoords.c2, Color.green, 3.0F, true);
                    OpeningDBBuilder.this.tablePane.setHighlight(move.newCoords.c1, move.newCoords.c2, Color.green, 3.0F, true);
                }
            });
        } catch (InterruptedException localInterruptedException) {
            System.err.println(localInterruptedException);
        } catch (InvocationTargetException localInvocationTargetException) {
            System.err.println(localInvocationTargetException);
        }
    }

    public void highlightWin(int paramInt) {
        final Coords winCoords = this.game.table.firstCoordsForPiece(Constants.pieces[1][0]);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                for (int i = 0; i < 6; i++) {
                    Coords localCoords = winCoords.getNeighbour(i);
                    OpeningDBBuilder.this.tablePane.setHighlight(localCoords.c1, localCoords.c2, Color.red, 3.0F, true);
                }
            }
        });
    }

    public void highlightTurn(int paramInt) {
    }

    int pieceTypeFromCoordC2(int paramInt) {
        if (paramInt == 0)
            return 0;
        if (paramInt == 2)
            return 2;
        if (paramInt == -2)
            return 1;
        if (paramInt == 4)
            return 4;
        if (paramInt == -4)
            return 3;
        return -1;
    }

    int coordC2FromPieceType(Piece paramPiece) {
        switch (paramPiece.type) {
            case 0:
                return 0;
            case 2:
                return 2;
            case 1:
                return -2;
            case 4:
                return 4;
            case 3:
                return -4;
        }
        throw new Error("Bad Piece");
    }

    public void resetHivePanes() {
        this.tablePane.clear();
        for (int i = 0; i < 2; i++) {
            this.boxPane[i].clear();

            this.boxPane[i].setHivePiece(0, 0, imageID[i][0]);
            this.boxPane[i].setHivePiece(0, 2, imageID[i][2]);
            this.boxPane[i].setHivePiece(0, -2, imageID[i][1]);
            this.boxPane[i].setHivePiece(0, 4, imageID[i][4]);
            this.boxPane[i].setHivePiece(0, -4, imageID[i][3]);
            int j;
            if (i == 0)
                j = this.boxPane[i].getCellWidth() / 2 + 5;
            else
                j = -this.boxPane[i].getCellWidth() / 2 - 13;
            int k = 0;
            for (int m = 0; m < 5; m++) {
                int n = coordC2FromPieceType(Constants.pieces[i][m]);
                this.labels[i][m] = new HiveLabel(k, n, j, 0, new Integer(this.game.box.howMany(i, m)).toString());
                this.boxPane[i].addVisibleObject(this.labels[i][m]);
            }
        }
    }

    public void updateBoxPane(int paramInt) {
        if (!this.game.box.contains(Constants.pieces[paramInt][0]))
            this.boxPane[paramInt].setHivePiece(0, 0, 0);
        if (!this.game.box.contains(Constants.pieces[paramInt][2]))
            this.boxPane[paramInt].setHivePiece(0, 2, 0);
        if (!this.game.box.contains(Constants.pieces[paramInt][1]))
            this.boxPane[paramInt].setHivePiece(0, -2, 0);
        if (!this.game.box.contains(Constants.pieces[paramInt][4]))
            this.boxPane[paramInt].setHivePiece(0, 4, 0);
        if (!this.game.box.contains(Constants.pieces[paramInt][3]))
            this.boxPane[paramInt].setHivePiece(0, -4, 0);
        int i;
        if (paramInt == 0)
            i = this.boxPane[paramInt].getCellWidth() / 2 + 5;
        else
            i = -this.boxPane[paramInt].getCellWidth() / 2 - 13;
        int j = 0;
        for (int k = 0; k < 5; k++) {
            int m = coordC2FromPieceType(Constants.pieces[paramInt][k]);
            int n = this.game.box.howMany(paramInt, k);
            String str;
            if (n > 0)
                str = new Integer(n).toString();
            else
                str = " ";
            this.labels[paramInt][k].setText(str);
        }
    }

    void highlightSet(Collection paramCollection) {
        Iterator localIterator = paramCollection.iterator();
        while (localIterator.hasNext()) {
            Coords localCoords = (Coords) localIterator.next();
            this.tablePane.setHighlight(localCoords.c1, localCoords.c2, _coordsC, 3.0F, true);
        }
    }

    public void repaintGame() {
        synchronizeUI();
    }

    private void synchronizeUI() {
        this.tablePane.clear();
        resetHivePanes();

        updateBoxPane(0);
        updateBoxPane(1);

        for (Coords localCoords = this.game.table.firstCoords(); localCoords != null; localCoords = this.game.table.nextCoords()) {
            Piece localPiece = this.game.table.getPieceAt(localCoords);
            this.tablePane.setHivePiece(localCoords.c1, localCoords.c2, imageID[localPiece.color][localPiece.type]);
        }
    }

    public OpeningDBBuilder() {
        initComponents();
    }

    private void initComponents() {
        setSize(new Dimension(700, 500));
        fileChooser = new JFileChooser(".");

        menuBar = new JMenuBar();
        mFile = new JMenu();
        miNewGame = new JMenuItem();
        miNewGame.setText("New Game");
        miNewGame.addActionListener(this);
        miNewDB = new JMenuItem();
        miNewDB.setText("New DB");
        miNewDB.addActionListener(this);
        miSaveDB = new JMenuItem();
        miSaveDB.setText("Save DB...");
        miSaveDB.addActionListener(this);
        miExit = new JMenuItem();
        miExit.setText("Exit");
        miExit.addActionListener(this);

        fileChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                fileChooserActionPerformed(event);
            }
        });
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent event) {
                formComponentResized(event);
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                exitForm(event);
            }
        });
        mFile.setText("File");
        mFile.add(miNewGame);
        mFile.add(miNewDB);
        mFile.add(miSaveDB);
        mFile.add(miExit);
        menuBar.add(mFile);
        setJMenuBar(menuBar);

        for (int i = 0; i < 2; i++)
            this.labels[i] = new HiveLabel[5];
        this.tableScroll = new HiveScrollPane();

        Toolkit localToolkit = getToolkit();
        MediaTracker localMediaTracker = new MediaTracker(this);

        imagesMap.put(new Integer(1), localToolkit.getImage(imagesDir + "nu_blue_bee.gif"));
        imagesMap.put(new Integer(2), localToolkit.getImage(imagesDir + "nu_blue_spider.gif"));
        imagesMap.put(new Integer(3), localToolkit.getImage(imagesDir + "nu_blue_beetle.gif"));
        imagesMap.put(new Integer(4), localToolkit.getImage(imagesDir + "nu_blue_ant.gif"));
        imagesMap.put(new Integer(5), localToolkit.getImage(imagesDir + "nu_blue_hopper.gif"));
        imagesMap.put(new Integer(6), localToolkit.getImage(imagesDir + "nu_silver_bee.gif"));
        imagesMap.put(new Integer(7), localToolkit.getImage(imagesDir + "nu_silver_spider.gif"));
        imagesMap.put(new Integer(8), localToolkit.getImage(imagesDir + "nu_silver_beetle.gif"));
        imagesMap.put(new Integer(9), localToolkit.getImage(imagesDir + "nu_silver_ant.gif"));
        imagesMap.put(new Integer(10), localToolkit.getImage(imagesDir + "nu_silver_hopper.gif"));

        tablePane = new HivePane(imagesMap);
        tablePane.setPreferredSize(new Dimension(45 * tablePane.getCellWidth(), 45 * tablePane.getCellHeight()));

        boxPane[0] = new HivePane(imagesMap);
        boxPane[0].setMinimumSize(new Dimension(boxPane[0].getCellWidth(), boxPane[0].getCellHeight()));
        boxPane[0].setBorder(BorderFactory.createTitledBorder(boxPane[0].getBorder(), "Blue's Set"));

        boxPane[1] = new HivePane(imagesMap);
        boxPane[1].setMinimumSize(new Dimension(boxPane[0].getCellWidth(), boxPane[0].getCellHeight()));
        boxPane[1].setBorder(BorderFactory.createTitledBorder(boxPane[1].getBorder(), "Silver's Set"));

        tableScroll.setViewportView(tablePane);
        tableScroll.setBorder(BorderFactory.createTitledBorder(tableScroll.getBorder(), "Hive"));

        p = new JPanel(new BorderLayout());

        getContentPane().setLayout(new BorderLayout());

        p.add(boxPane[0], "West");
        p.add(boxPane[1], "East");
        p.add(tableScroll, "Center");
        p.setPreferredSize(new Dimension(700, 500));

        toolBar = new JPanel();
        toolBar.setLayout(new FlowLayout(0));
        p.add(toolBar, "North");

        getContentPane().add(p);

        tablePane.addHiveMouseListener(new StackShower());
        db = new OpeningDB();

        pack();
    }

    private void formComponentResized(ComponentEvent paramComponentEvent) {
        Dimension localDimension = this.boxPane[0].getSize();
        System.out.println(localDimension);
        this.boxPane[0].setOriginX((int) (localDimension.getWidth() / 2.0D));
        this.boxPane[0].setOriginY((int) (localDimension.getHeight() / 2.0D));

        localDimension = this.boxPane[1].getSize();
        System.out.println(localDimension);
        this.boxPane[1].setOriginX((int) (localDimension.getWidth() / 2.0D));
        this.boxPane[1].setOriginY((int) (localDimension.getHeight() / 2.0D));
    }

    private void fileChooserActionPerformed(ActionEvent paramActionEvent) {
    }

    private void exitForm(WindowEvent paramWindowEvent) {
        System.exit(0);
    }

    public static void main(String[] paramArrayOfString) {
        OpeningDBBuilder builder = new OpeningDBBuilder();
        builder.show();
    }

    public void actionPerformed(ActionEvent paramActionEvent) {
        if (paramActionEvent.getActionCommand().equals("New Game")) {
            cleanUpGame();
            newOpening();
            calibratePanes();
        }

        if (paramActionEvent.getActionCommand().equals("New DB")) {
            cleanUpGame();
            newOpening();
            calibratePanes();
            this.db = new OpeningDB();
        }

        if (paramActionEvent.getActionCommand().equals("Save DB...")) {
            int i = this.fileChooser.showSaveDialog(this);

            if (i == 0) {
                File localFile = this.fileChooser.getSelectedFile();
                System.out.println("\n" + this.db);
                saveOpeningDB(localFile);
            }

        }

        if (paramActionEvent.getActionCommand().equals("Exit"))
            System.exit(0);
    }

    class StackShower extends HiveMouseAdapter {

        HivePopupMenu popup = new HivePopupMenu(tablePane);
        HashMap<Integer, Image> scaledImages;

        StackShower() {
            MediaTracker localMediaTracker = new MediaTracker(OpeningDBBuilder.this);
            scaledImages = new HashMap<Integer, Image>();
            for (Integer key : imagesMap.keySet()) {
                Image localImage1 = imagesMap.get(key);
                Image localImage2 = localImage1.getScaledInstance((int) (tablePane.getCellWidth() * 0.75D), (int) (tablePane.getCellHeight() * 0.75D), 4);

                localMediaTracker.addImage(localImage2, key.intValue());
                scaledImages.put(key, localImage2);
            }
            try {
                localMediaTracker.waitForAll();
            } catch (Exception localException) {
                localException.printStackTrace();
            }
        }

        public void hiveMouseClicked(HiveMouseEvent paramHiveMouseEvent) {
            Coords localCoords = Coords.instance(paramHiveMouseEvent.getP(), paramHiveMouseEvent.getQ());
            Piece localPiece1 = game.table.getPieceAt(localCoords);
            if ((localPiece1 != null)
                    && (localPiece1.type == 2)) {
                Image[] arrayOfImage = new Image[game.table.countPiecesAt(localCoords)];

                for (int i = 0; i < arrayOfImage.length; i++) {
                    Piece localPiece2 = OpeningDBBuilder.this.game.table.getPieceAt(localCoords, i);
                    arrayOfImage[i] = ((Image) this.scaledImages.get(new Integer(OpeningDBBuilder.imageID[localPiece2.color][localPiece2.type])));
                }
                if (paramHiveMouseEvent.originalEvent.isControlDown())
                    this.popup.show(OpeningDBBuilder.this.tablePane, paramHiveMouseEvent.originalEvent.getX(), paramHiveMouseEvent.originalEvent.getY(), arrayOfImage);
            }
        }

        public void hiveMouseReleased(HiveMouseEvent paramHiveMouseEvent) {
        }
    }

    class GetMoveState
            implements MoveProvider {

        private HiveMouseListener curListener = null;
        private Coords prevCoords;
        private Coords newCoords;
        private Game g;
        private int color;
        private Piece piece;
        private Move move;
        private Object semaphore = new Object();
        private HiveMouseListener beginMove = new BeginMove();
        private HiveMouseListener endMove = new EndMove();
        private Collection availableMoves;

        GetMoveState() {
        }

        public Move findMove(Game paramGame, int paramInt) {
            synchronized (this.semaphore) {
                this.g = paramGame;
                this.color = paramInt;
                this.availableMoves = paramGame.getMoves(paramInt);
                this.curListener = this.beginMove;

                OpeningDBBuilder.this.tablePane.addHiveMouseListener(this.curListener);
                OpeningDBBuilder.this.boxPane[0].addHiveMouseListener(this.curListener);
                OpeningDBBuilder.this.boxPane[1].addHiveMouseListener(this.curListener);
                try {
                    this.semaphore.wait();
                } catch (InterruptedException localInterruptedException) {
                }
                Move localMove = this.move;
                return localMove;
            }
        }

        public void cleanUp() {
            OpeningDBBuilder.this.tablePane.removeHiveMouseListener(this.curListener);
            OpeningDBBuilder.this.boxPane[0].removeHiveMouseListener(this.curListener);
            OpeningDBBuilder.this.boxPane[1].removeHiveMouseListener(this.curListener);

            OpeningDBBuilder.this.tablePane.clearHighlights();

            OpeningDBBuilder.this.boxPane[0].clearHighlights();
            OpeningDBBuilder.this.boxPane[1].clearHighlights();

            System.out.println("User move provider cleaned properly..");
            this.curListener = null;
        }

        public String getName() {
            return "User";
        }

        public void _break() {
            synchronized (this.semaphore) {
                this.move = null;
                this.semaphore.notify();
            }
        }

        class EndMove extends HiveMouseAdapter {

            EndMove() {
            }

            public void hiveMousePressed(HiveMouseEvent paramHiveMouseEvent) {
                if ((paramHiveMouseEvent.originalEvent.isControlDown()) || (paramHiveMouseEvent.originalEvent.isShiftDown()) || (paramHiveMouseEvent.originalEvent.isAltDown()))
                    return;
                synchronized (OpeningDBBuilder.GetMoveState.this.semaphore) {
                    int i = 0;
                    if (paramHiveMouseEvent.sender == OpeningDBBuilder.this.tablePane) {
                        OpeningDBBuilder.GetMoveState.this.newCoords = Coords.instance(paramHiveMouseEvent.getP(), paramHiveMouseEvent.getQ());
                        OpeningDBBuilder.GetMoveState.this.move = Move.instance(OpeningDBBuilder.GetMoveState.this.piece, OpeningDBBuilder.GetMoveState.this.prevCoords, OpeningDBBuilder.GetMoveState.this.newCoords);
                        if (OpeningDBBuilder.GetMoveState.this.availableMoves.contains(OpeningDBBuilder.GetMoveState.this.move))
                            i = 1;
                        else
                            OpeningDBBuilder.GetMoveState.this.beginMove.hiveMousePressed(paramHiveMouseEvent);
                    } else if (paramHiveMouseEvent.sender == OpeningDBBuilder.this.boxPane[OpeningDBBuilder.GetMoveState.this.color])
                        OpeningDBBuilder.GetMoveState.this.beginMove.hiveMousePressed(paramHiveMouseEvent);
                    else
                        System.out.println("It's not your box");
                    if (i != 0) {
                        OpeningDBBuilder.GetMoveState.this.curListener = null;

                        OpeningDBBuilder.this.tablePane.removeHiveMouseListener(this);
                        OpeningDBBuilder.this.boxPane[0].removeHiveMouseListener(this);
                        OpeningDBBuilder.this.boxPane[1].removeHiveMouseListener(this);

                        OpeningDBBuilder.GetMoveState.this.semaphore.notify();
                    }
                }
            }
        }

        class BeginMove extends HiveMouseAdapter {

            BeginMove() {
            }

            public void hiveMousePressed(HiveMouseEvent paramHiveMouseEvent) {
                if ((paramHiveMouseEvent.originalEvent.isControlDown()) || (paramHiveMouseEvent.originalEvent.isShiftDown()) || (paramHiveMouseEvent.originalEvent.isAltDown()))
                    return;
                synchronized (OpeningDBBuilder.GetMoveState.this.semaphore) {
                    Piece localPiece;
                    if (paramHiveMouseEvent.sender == OpeningDBBuilder.this.tablePane) {
                        Coords localCoords = Coords.instance(paramHiveMouseEvent.getP(), paramHiveMouseEvent.getQ());
                        localPiece = OpeningDBBuilder.GetMoveState.this.g.table.getPieceAt(localCoords);
                        if (localPiece == null)
                            return;
                        if (localPiece.color != OpeningDBBuilder.GetMoveState.this.color)
                            return;

                        OpeningDBBuilder.this.tablePane.clearHighlights();
                        OpeningDBBuilder.this.boxPane[0].clearHighlights();
                        OpeningDBBuilder.this.boxPane[1].clearHighlights();

                        OpeningDBBuilder.GetMoveState.this.piece = localPiece;
                        OpeningDBBuilder.GetMoveState.this.prevCoords = localCoords;

                        OpeningDBBuilder.this.highlightSet(OpeningDBBuilder.GetMoveState.this.g.getTargetCoords(localPiece, localCoords));
                        OpeningDBBuilder.this.tablePane.setHighlight(localCoords.c1, localCoords.c2, Color.blue, 3.0F, true);
                    } else if (paramHiveMouseEvent.sender == OpeningDBBuilder.this.boxPane[OpeningDBBuilder.GetMoveState.this.color]) {
                        int i = OpeningDBBuilder.this.pieceTypeFromCoordC2(paramHiveMouseEvent.getQ());
                        if (i < 0)
                            return;

                        OpeningDBBuilder.this.tablePane.clearHighlights();
                        OpeningDBBuilder.this.boxPane[0].clearHighlights();
                        OpeningDBBuilder.this.boxPane[1].clearHighlights();

                        localPiece = Constants.pieces[OpeningDBBuilder.GetMoveState.this.color][i];
                        OpeningDBBuilder.GetMoveState.this.piece = localPiece;
                        OpeningDBBuilder.GetMoveState.this.prevCoords = null;

                        OpeningDBBuilder.this.boxPane[OpeningDBBuilder.GetMoveState.this.color].setHighlight(paramHiveMouseEvent.getP(), paramHiveMouseEvent.getQ(), Color.blue, 3.0F, true);
                        OpeningDBBuilder.this.highlightSet(OpeningDBBuilder.GetMoveState.this.g.getTargetCoords(localPiece, null));
                    } else {
                        System.out.println("It's not your box");
                        return;
                    }
                    OpeningDBBuilder.GetMoveState.this.curListener = OpeningDBBuilder.GetMoveState.this.endMove;
                    OpeningDBBuilder.this.tablePane.removeHiveMouseListener(this);
                    OpeningDBBuilder.this.boxPane[0].removeHiveMouseListener(this);
                    OpeningDBBuilder.this.boxPane[1].removeHiveMouseListener(this);

                    OpeningDBBuilder.this.tablePane.removeHiveMouseListener(OpeningDBBuilder.GetMoveState.this.endMove);
                    OpeningDBBuilder.this.boxPane[0].removeHiveMouseListener(OpeningDBBuilder.GetMoveState.this.endMove);
                    OpeningDBBuilder.this.boxPane[1].removeHiveMouseListener(OpeningDBBuilder.GetMoveState.this.endMove);

                    OpeningDBBuilder.this.tablePane.addHiveMouseListener(OpeningDBBuilder.GetMoveState.this.endMove);
                    OpeningDBBuilder.this.boxPane[0].addHiveMouseListener(OpeningDBBuilder.GetMoveState.this.endMove);
                    OpeningDBBuilder.this.boxPane[1].addHiveMouseListener(OpeningDBBuilder.GetMoveState.this.endMove);
                }
            }
        }
    }
}