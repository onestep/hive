package hive.game;

import hive.event.HiveMouseAdapter;
import hive.event.HiveMouseEvent;
import hive.event.HiveMouseListener;
import hive.game.providers.NegaMaxMoveProvider;
import hive.gui.*;
import hive.intf.MoveHighlighter;
import hive.intf.MoveProvider;
import hive.intf.Thinker;
import hive.plaf.HiveMetalTheme;
import hive.plaf.NuHiveMetalTheme;
import hive.tests.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.metal.MetalLookAndFeel;

public class MainApplet extends JFrame
        implements Constants, MoveHighlighter, Thinker {

    private PrintWriter out;
    boolean ok = true;
    private OpeningDB db = null;
    GameThread gameThread;
    GetMoveState getMoveState;
    Game game;
    int playerColor = 0;
    private HashMap<Integer, Image> imagesMap = new HashMap<Integer, Image>();
    private String imagesDir = "resources/images/";
    private String soundDir = "resources/sounds/";
    private boolean moveTests = false;
    private boolean newLook = false;
    private String pieceImagePrefix;
    private Color stackIndicatorColor = Color.black;
    private Color normalHighlightColor = Color.black;
    JAnimation anim;
    JWindow thinkingWindow;
    public HivePane tablePane;
    public HivePane[] boxPane = new HivePane[2];
    public HiveScrollPane tableScroll;
    private JPanel toolBar;
    private JPanel gameContentPane;
    private JPanel aboutContentPane;
    private JWindow aboutWindow;
    private Image logoImage;
    Border thickBorder = null;
    Border fatBorder = null;
    TitledBorder[] borders = {null, null};
    //private AudioClip moveClip;
    private HiveLabel[][] labels = new HiveLabel[2][];
    static final int[][] imageID = {{1, 2, 3, 4, 5}, {6, 7, 8, 9, 10}};
    private static int testIndex = 0;
    private static HiveTest[] tests = new HiveTest[10];

    public MainApplet() {
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent event) {
                System.exit(0);
            }
        });
    }

    public void init() {
        setSize(new Dimension(700, 500));
        initCommon();
        //initHiveUI();
        buildBasicGui();
        initGameMode();
        initAbout();
        initAnimation();

        if (moveTests)
            nextTest();
        else
            try {
                SwingUtilities.invokeAndWait(new Runnable() {

                    @Override
                    public void run() {
                        newGame();
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        System.out.println("Ready");
    }

    public void newGame() {
        game = new Game();
        playerColor = 0;
        getMoveState = new GetMoveState();

        GameProcess gameProcess = new GameProcess(game, getMoveState, new OpeningDBMoveProvider(db, new NegaMaxMoveProvider(game, this)), playerColor, out, this, newLook);

        gameThread = new GameThread(gameProcess);
        resetHivePanes();
        calibratePanes();

        gameThread.start();
        tablePane.requestFocus();
    }

    public void cleanUpGame() {
        highlightTurn(-1);
        if (gameThread != null)
            gameThread.finish();
    }

    public void destroy() {
        cleanUpGame();
    }

    public void start() {
        if (ok)
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    MainApplet.this.calibratePanes();
                }
            });
    }

    private void initCommon() {
        String str = getParameter("new_look");
        if ((str != null) && str.equals("true")) {
            newLook = true;
            pieceImagePrefix = "nu";
            stackIndicatorColor = Color.orange;
            normalHighlightColor = Color.orange;
        } else {
            newLook = false;
            pieceImagePrefix = "v";
            stackIndicatorColor = Color.black;
            normalHighlightColor = Color.black;
        }

        String localObject = getParameter("test_mode");
        if (localObject == null)
            moveTests = false;
        else if ("move_tests".equals(localObject))
            moveTests = true;
        else
            moveTests = false;

        System.out.println("Loading piece images...");

        imagesMap.put(new Integer(1), getToolkit().getImage(imagesDir + pieceImagePrefix + "_blue_bee.gif"));
        imagesMap.put(new Integer(2), getToolkit().getImage(imagesDir + pieceImagePrefix + "_blue_spider.gif"));
        imagesMap.put(new Integer(3), getToolkit().getImage(imagesDir + pieceImagePrefix + "_blue_beetle.gif"));
        imagesMap.put(new Integer(4), getToolkit().getImage(imagesDir + pieceImagePrefix + "_blue_ant.gif"));
        imagesMap.put(new Integer(5), getToolkit().getImage(imagesDir + pieceImagePrefix + "_blue_hopper.gif"));
        imagesMap.put(new Integer(6), getToolkit().getImage(imagesDir + pieceImagePrefix + "_silver_bee.gif"));
        imagesMap.put(new Integer(7), getToolkit().getImage(imagesDir + pieceImagePrefix + "_silver_spider.gif"));
        imagesMap.put(new Integer(8), getToolkit().getImage(imagesDir + pieceImagePrefix + "_silver_beetle.gif"));
        imagesMap.put(new Integer(9), getToolkit().getImage(imagesDir + pieceImagePrefix + "_silver_ant.gif"));
        imagesMap.put(new Integer(10), getToolkit().getImage(imagesDir + pieceImagePrefix + "_silver_hopper.gif"));
    }

    void buildBasicGui() {
        for (int i = 0; i < 2; i++)
            labels[i] = new HiveLabel[5];
        tableScroll = new HiveScrollPane();

        tablePane = new HivePane(imagesMap);
        tablePane.setPreferredSize(new Dimension(45 * tablePane.getCellWidth(), 45 * tablePane.getCellHeight()));

        boxPane[0] = new HivePane(imagesMap);
        boxPane[0].setMinimumSize(new Dimension(boxPane[0].getCellWidth(), boxPane[0].getCellHeight()));

        boxPane[1] = new HivePane(imagesMap);
        boxPane[1].setMinimumSize(new Dimension(boxPane[0].getCellWidth(), boxPane[0].getCellHeight()));

        thickBorder = boxPane[0].getBorder();
        fatBorder = BorderFactory.createLineBorder(Color.black, 2);
        String str1;
        String str2;
        if (newLook) {
            str1 = "Ivory Set";
            str2 = "Black Set";
        } else {
            str1 = "Blue Set";
            str2 = "Silver Set";
        }

        borders[0] = BorderFactory.createTitledBorder(thickBorder, str1);
        borders[1] = BorderFactory.createTitledBorder(thickBorder, str2);
        boxPane[0].setBorder(borders[0]);
        boxPane[1].setBorder(borders[1]);

        tableScroll.setViewportView(tablePane);
        tableScroll.setBorder(BorderFactory.createTitledBorder(tableScroll.getBorder(), "Hive"));

        gameContentPane = new JPanel(new BorderLayout());

        getContentPane().setLayout(new BorderLayout());

        gameContentPane.add(boxPane[0], "West");
        gameContentPane.add(boxPane[1], "East");
        gameContentPane.add(tableScroll, "Center");

        toolBar = new JPanel();
        toolBar.setLayout(new FlowLayout(0));
        gameContentPane.add(toolBar, "North");
        JLabel localJLabel = new JLabel();
        localJLabel.setPreferredSize(new Dimension(20, 20));
        gameContentPane.add(localJLabel, "South");
        out = new PrintWriter(new JLabelOut(localJLabel), true);

        tablePane.addHiveMouseListener(new StackShower());

        setContentPane(gameContentPane);

        System.out.println("Loading sound...");

        //moveClip = getAudioClip(resURL, soundDir + "move.wav");
    }

    public void initGameMode() {
        if (moveTests) {
            JButton button = new JButton("Next Test");
            button.addActionListener(new ActionListener() {

                @Override
                public synchronized void actionPerformed(ActionEvent event) {
                    MainApplet.this.nextTest();
                }
            });
            toolBar.add(button);
        } else {
            JButton button = new JButton("New Game");
            button.addActionListener(new ActionListener() {

                @Override
                public synchronized void actionPerformed(ActionEvent event) {
                    cleanUpGame();
                    newGame();
                }
            });
            toolBar.add(button);
        }

        JButton centerButton = new JButton("Center");
        centerButton.addActionListener(new ActionListener() {

            @Override
            public synchronized void actionPerformed(ActionEvent event) {
                tableScroll.initHiveScrollPane();
            }
        });
        toolBar.add(centerButton);

        System.out.println("Loading openings...");
        readOpeningDB();

        JButton aboutButton = new JButton("About...");
        aboutButton.addActionListener(new ActionListener() {

            @Override
            public synchronized void actionPerformed(ActionEvent event) {
                aboutWindow.setLocationRelativeTo(tableScroll);
                aboutWindow.setVisible(true);
                MainApplet.this.repaint();
            }
        });
        toolBar.add(aboutButton);
    }

    private void initAbout() {
        System.out.println("Loading additional data...");
        logoImage = getToolkit().getImage(imagesDir + "alces_logo.png");
        ImageIcon localImageIcon = new ImageIcon(logoImage);

        aboutContentPane = new JPanel(new BorderLayout());
        aboutContentPane.setBorder(BorderFactory.createRaisedBevelBorder());

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {

            @Override
            public synchronized void actionPerformed(ActionEvent paramAnonymousActionEvent) {
                aboutWindow.setVisible(false);
            }
        });
        JPanel localJPanel1 = new JPanel(new FlowLayout());
        localJPanel1.add(closeButton);
        aboutContentPane.add(localJPanel1, "South");

        JPanel localJPanel2 = new JPanel();
        JPanel localJPanel3 = new JPanel();

        JLabel localJLabel1 = new JLabel();

        localJLabel1.setIcon(localImageIcon);
        localJPanel3.add(localJLabel1, 0);

        JLabel localJLabel2 = new JLabel("<html>V Game Applet by: Alces Group. </html>");

        localJPanel2.add(localJLabel2);

        aboutContentPane.add(localJPanel2, "Center");
        aboutContentPane.add(localJPanel3, "North");

        aboutWindow = new JWindow();
        aboutWindow.setContentPane(aboutContentPane);
        aboutWindow.setLocationRelativeTo(tableScroll);
        aboutWindow.pack();
    }

    private void initAnimation() {
        System.out.println("Loading animation...");
        Image localImage1 = getToolkit().getImage(imagesDir + "thinking1.png");
        Image localImage2 = getToolkit().getImage(imagesDir + "thinking2.png");
        Image localImage3 = getToolkit().getImage(imagesDir + "thinking3.png");
        Image localImage4 = getToolkit().getImage(imagesDir + "thinking4.png");
        Image[] arrayOfImage = {localImage1, localImage2, localImage3, localImage4};

        anim = new JAnimation(arrayOfImage, 250L);
        anim.setText("Thinking...");

        thinkingWindow = new JWindow();
        JPanel localJPanel = new JPanel();

        localJPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        localJPanel.add(anim);

        thinkingWindow.setContentPane(localJPanel);
        thinkingWindow.pack();
    }

    @Override
    public void startThinking(int paramInt) {
        thinkingWindow.setLocationRelativeTo(boxPane[paramInt]);
        thinkingWindow.show();
        anim.start();
    }

    @Override
    public void stopThinking() {
        thinkingWindow.setVisible(false);
        anim.stop();
    }

    private void initHiveUI() {
        if (!newLook)
            MetalLookAndFeel.setCurrentTheme(HiveMetalTheme.instance);
        else
            MetalLookAndFeel.setCurrentTheme(NuHiveMetalTheme.instance);
        try {
            UIManager.setLookAndFeel(new MetalLookAndFeel());
        } catch (Exception localException) {
        }
        try {
            String str = newLook ? "hive.plaf.NuHiveButtonUI" : "hive.plaf.HiveButtonUI";

            UIManager.put("ButtonUI", str);
            UIManager.put(str, Class.forName(str));
        } catch (ClassNotFoundException localClassNotFoundException) {
            System.out.println("Hive button UI not found");
            localClassNotFoundException.printStackTrace();
        }
    }

    private void readOpeningDB() {
        String str = "opening_db.bin";
        try {
            FileInputStream fis = new FileInputStream(str);
            db = OpeningDB.read(fis);
        } catch (Exception ex) {
            ex.printStackTrace();
            db = null;
            System.out.println("Error loading openings:" + str);
        }
    }

    public static void main(String[] paramArrayOfString) {
        MainApplet applet = new MainApplet();
        applet.init();
        applet.setSize(700, 500);
        applet.setVisible(true);
    }

    private void setTheme() {
        MetalLookAndFeel.setCurrentTheme(new HiveMetalTheme());
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (Exception ex) {
            out.println("Failed loading L&F");
            ex.printStackTrace();
        }
    }

    private void calibratePanes() {
        Dimension localDimension = tablePane.getSize();

        tablePane.setOriginX((int) (localDimension.getWidth() / 2.0D));
        tablePane.setOriginY((int) (localDimension.getHeight() / 2.0D));

        localDimension = boxPane[0].getSize();

        boxPane[0].setOriginX((int) (localDimension.getWidth() / 2.0D));
        boxPane[0].setOriginY((int) (localDimension.getHeight() / 2.0D));

        localDimension = boxPane[1].getSize();

        boxPane[1].setOriginX((int) (localDimension.getWidth() / 2.0D));
        boxPane[1].setOriginY((int) (localDimension.getHeight() / 2.0D));

        tableScroll.initHiveScrollPane();
    }

    @Override
    public void highlightBefore(Move paramMove) {
    }

    @Override
    public void highlightAfter(final Move move) {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {

                @Override
                public void run() {
                    tablePane.clearHighlights();
                    boxPane[0].clearHighlights();
                    boxPane[1].clearHighlights();

                    if (move.prevCoords == null)
                        updateBoxPane(move.piece.color);
                    else {
                        Piece localPiece = game.table.getPieceAt(move.prevCoords);
                        tablePane.setHivePiece(move.prevCoords.c1, move.prevCoords.c2, 0);
                        if (localPiece != null)
                            tablePane.setHivePiece(move.prevCoords.c1, move.prevCoords.c2, MainApplet.imageID[localPiece.color][localPiece.type], game.table.countPiecesAt(move.prevCoords) > 1, stackIndicatorColor);
                    }
                    tablePane.setHivePiece(move.newCoords.c1, move.newCoords.c2, MainApplet.imageID[move.piece.color][move.piece.type], game.table.countPiecesAt(move.newCoords) > 1, stackIndicatorColor);

                    if (move.prevCoords == null)
                        boxPane[move.piece.color].setHighlight(0, coordC2FromPieceType(move.piece), Color.green, 3.0F, true);
                    else
                        tablePane.setHighlight(move.prevCoords.c1, move.prevCoords.c2, Color.green, 3.0F, true);
                    tablePane.setHighlight(move.newCoords.c1, move.newCoords.c2, Color.green, 3.0F, true);

                    //moveClip.play();
                }
            });
        } catch (InterruptedException localInterruptedException) {
            System.err.println(localInterruptedException);
        } catch (InvocationTargetException localInvocationTargetException) {
            System.err.println(localInvocationTargetException);
        }
    }

    @Override
    public void highlightTurn(int i) {
        final int c = i;

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                if (c > -1) {
                    borders[c].setBorder(fatBorder);
                    borders[c > 0 ? 0 : 1].setBorder(thickBorder);
                } else {
                    borders[0].setBorder(thickBorder);
                    borders[1].setBorder(thickBorder);
                }
                boxPane[0].repaint();
                boxPane[1].repaint();
            }
        });
    }

    @Override
    public void highlightWin(int i) {
        final Coords winCoords = game.table.firstCoordsForPiece(Constants.pieces[i > 0 ? 0 : 1][0]);
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                for (int j = 0; j < 6; j++) {
                    Coords coords = winCoords.getNeighbour(j);
                    tablePane.setHighlight(coords.c1, coords.c2, Color.red, 3.0F, true);
                }
            }
        });
    }

    @Override
    public void repaintGame() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                MainApplet.this.synchronizeUI();
            }
        });
    }

    int pieceTypeFromCoordC2(int i) {
        if (i == 0)
            return 0;
        if (i == 2)
            return 2;
        if (i == -2)
            return 1;
        if (i == 4)
            return 4;
        if (i == -4)
            return 3;
        return -1;
    }

    int coordC2FromPieceType(Piece piece) {
        switch (piece.type) {
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
            default:
                throw new Error("Bad Piece");
        }
    }

    public void resetHivePanes() {
        tablePane.clear();
        for (int i = 0; i < 2; i++) {
            boxPane[i].clear();

            boxPane[i].setHivePiece(0, 0, imageID[i][0]);
            boxPane[i].setHivePiece(0, 2, imageID[i][2]);
            boxPane[i].setHivePiece(0, -2, imageID[i][1]);
            boxPane[i].setHivePiece(0, 4, imageID[i][4]);
            boxPane[i].setHivePiece(0, -4, imageID[i][3]);
            int j;
            if (i == 0)
                j = boxPane[i].getCellWidth() / 2 + 5;
            else
                j = -boxPane[i].getCellWidth() / 2 - 13;
            int k = 0;
            for (int m = 0; m < 5; m++) {
                int n = coordC2FromPieceType(pieces[i][m]);
                labels[i][m] = new HiveLabel(k, n, j, 0, new Integer(game.box.howMany(i, m)).toString());
                boxPane[i].addVisibleObject(labels[i][m]);
            }
        }
    }

    public void updateBoxPane(int paramInt) {
        if (!game.box.contains(pieces[paramInt][0]))
            boxPane[paramInt].setHivePiece(0, 0, 0);
        if (!game.box.contains(pieces[paramInt][2]))
            boxPane[paramInt].setHivePiece(0, 2, 0);
        if (!game.box.contains(pieces[paramInt][1]))
            boxPane[paramInt].setHivePiece(0, -2, 0);
        if (!game.box.contains(pieces[paramInt][4]))
            boxPane[paramInt].setHivePiece(0, 4, 0);
        if (!game.box.contains(pieces[paramInt][3]))
            boxPane[paramInt].setHivePiece(0, -4, 0);
        int i;
        if (paramInt == 0)
            i = boxPane[paramInt].getCellWidth() / 2 + 5;
        else
            i = -boxPane[paramInt].getCellWidth() / 2 - 13;
        int j = 0;
        for (int k = 0; k < 5; k++) {
            int m = coordC2FromPieceType(pieces[paramInt][k]);
            int n = game.box.howMany(paramInt, k);
            String str;
            if (n > 0)
                str = new Integer(n).toString();
            else
                str = " ";
            labels[paramInt][k].setText(str);
        }
    }

    void highlightSet(HashSet<Coords> set) {
        for (Coords coords : set)
            tablePane.setHighlight(coords.c1, coords.c2, normalHighlightColor, 3.0F, true);
    }

    private void synchronizeUI() {
        tablePane.clear();
        resetHivePanes();

        updateBoxPane(0);
        updateBoxPane(1);

        for (Coords localCoords = game.table.firstCoords(); localCoords != null; localCoords = game.table.nextCoords()) {
            Piece localPiece = game.table.getPieceAt(localCoords);
            tablePane.setHivePiece(localCoords.c1, localCoords.c2, imageID[localPiece.color][localPiece.type], game.table.countPiecesAt(localCoords) > 1, stackIndicatorColor);
        }
    }

    private void nextTest() {
        performTest(tests[testIndex]);
        testIndex = (testIndex + 1) % tests.length;
    }

    private void performTest(HiveTest paramHiveTest) {
        cleanUpGame();

        game = new Game();

        paramHiveTest.prepareGame(game);

        HashSet<Coords> set = paramHiveTest.getTestCoords(game);

        synchronizeUI();

        highlightSet(set);

        Coords localCoords = paramHiveTest.getCoords();
        if (localCoords != null)
            tablePane.setHighlight(localCoords.c1, localCoords.c2, Color.blue, 3.0F, true);
        else {
            Piece localPiece = paramHiveTest.getPiece();
            boxPane[localPiece.color].setHighlight(0, coordC2FromPieceType(localPiece), Color.blue, 3.0F, true);
        }
        out.println(paramHiveTest.getName());
    }

    static {
        tests[0] = new OneHiveRuleTest();
        tests[1] = new FreedomToMove1Test();
        tests[2] = new FreedomToMove2Test();
        tests[3] = new PlacingTest();
        tests[4] = new AntTest();
        tests[5] = new BeetleTest();
        tests[6] = new QueenBeeTest();
        tests[7] = new SpiderTest();
        tests[8] = new BuggySpiderTest();
        tests[9] = new HopperTest();
    }

    private String getParameter(String name) {
        // hive rules index
        if (name.equals("help_general"))
            return "http://www.hivemania.com/rules/";

        // placing help
        if (name.equals("help_insert"))
            return "http://www.hivemania.com/rules/";

        // move  help
        if (name.equals("help_move_bee"))
            return "http://www.hivemania.com/rules/queen_bee.jpg";
        if (name.equals("help_move_ant"))
            return "http://www.hivemania.com/rules/soldier_ant.jpg";
        if (name.equals("help_move_spider"))
            return "http://www.hivemania.com/rules/spider.jpg";
        if (name.equals("help_move_hopper"))
            return "http://www.hivemania.com/rules/grass_hopper.jpg";
        if (name.equals("help_move_beetle"))
            return "http://www.hivemania.com/rules/beetle.jpg";

        // v game description
        if (name.equals("help_vgame"))
            return "http://www.gen42.com/play/manual.html";

        // what L&F of applet (false:hivemania, true:designspot)
        if (name.equals("new_look"))
            return "true"; // <------------------ THIS

        return "";
    }

    class StackShower extends HiveMouseAdapter {

        HivePopupMenu popup = new HivePopupMenu(tablePane);
        HashMap<Integer, Image> scaledImages;

        StackShower() {
            MediaTracker localMediaTracker = new MediaTracker(MainApplet.this);
            scaledImages = new HashMap<Integer, Image>();
            for (Integer key : imagesMap.keySet()) {
                Image localImage1 = imagesMap.get(key);
                Image localImage2 = localImage1.getScaledInstance((int) (tablePane.getCellWidth() * 0.75D), (int) (tablePane.getCellHeight() * 0.75D), 4);

                localMediaTracker.addImage(localImage2, key.intValue());
                scaledImages.put(key, localImage2);
            }

            System.out.println("Scaling piece images...");
            try {
                localMediaTracker.waitForAll();
            } catch (Exception localException) {
                localException.printStackTrace();
            }
        }

        @Override
        public void hiveMouseClicked(HiveMouseEvent paramHiveMouseEvent) {
            Coords coords = Coords.instance(paramHiveMouseEvent.getP(), paramHiveMouseEvent.getQ());
            Piece piece = game.table.getPieceAt(coords);
            if ((piece != null) && (piece.type == BEETLE)) {
                Image[] images = new Image[game.table.countPiecesAt(coords)];

                for (int i = 0; i < images.length; i++) {
                    Piece stackedPiece = game.table.getPieceAt(coords, i);
                    images[i] = scaledImages.get(new Integer(MainApplet.imageID[stackedPiece.color][stackedPiece.type]));
                }
                if (paramHiveMouseEvent.originalEvent.isControlDown())
                    popup.show(tablePane, paramHiveMouseEvent.originalEvent.getX(), paramHiveMouseEvent.originalEvent.getY(), images);
            }
        }

        @Override
        public void hiveMouseReleased(HiveMouseEvent paramHiveMouseEvent) {
        }
    }

    class GetMoveState implements MoveProvider {

        private HiveMouseListener curListener = null;
        private Coords prevCoords;
        private Coords newCoords;
        private Game g;
        private int color;
        private Piece piece;
        private Move move;
        private final Object semaphore = new Object();
        private HiveMouseListener beginMove = new BeginMove();
        private HiveMouseListener endMove = new EndMove();
        private Set<Move> availableMoves;

        GetMoveState() {
        }

        @Override
        public Move findMove(Game game, int color) {
            synchronized (semaphore) {
                g = game;
                this.color = color;
                availableMoves = game.getMoves(color);

                if (game.mustInsertQueen(color)) {
                    out.println("You must insert Queen bee.");
                    piece = Constants.queens[color];
                    prevCoords = null;
                    boxPane[color].setHighlight(0, coordC2FromPieceType(piece), Color.blue, 3.0F, true);
                    highlightSet(game.getTargetCoords(piece, null));

                    curListener = endMove;
                } else
                    curListener = beginMove;

                tablePane.addHiveMouseListener(curListener);
                boxPane[0].addHiveMouseListener(curListener);
                boxPane[1].addHiveMouseListener(curListener);
                try {
                    semaphore.wait();
                } catch (InterruptedException ex) {
                }
                return this.move;
            }
        }

        @Override
        public void cleanUp() {
            tablePane.removeHiveMouseListener(curListener);
            boxPane[0].removeHiveMouseListener(curListener);
            boxPane[1].removeHiveMouseListener(curListener);

            tablePane.clearHighlights();

            boxPane[0].clearHighlights();
            boxPane[1].clearHighlights();

            curListener = null;
        }

        @Override
        public String getName() {
            return "User";
        }

        @Override
        public void _break() {
            synchronized (semaphore) {
                move = null;
                semaphore.notify();
            }
        }

        class EndMove extends HiveMouseAdapter {

            @Override
            public void hiveMousePressed(HiveMouseEvent event) {
                if (event.originalEvent.isControlDown() || event.originalEvent.isShiftDown() || event.originalEvent.isAltDown())
                    return;
                synchronized (semaphore) {
                    boolean bool = false;
                    if (event.sender == tablePane) {
                        newCoords = Coords.instance(event.getP(), event.getQ());
                        move = Move.instance(piece, prevCoords, newCoords);
                        if (availableMoves.contains(move))
                            bool = true;
                        else if (!game.mustInsertQueen(color))
                            beginMove.hiveMousePressed(event);
                    } else if (event.sender == boxPane[color]) {
                        if (!game.mustInsertQueen(color))
                            beginMove.hiveMousePressed(event);
                    } else
                        out.println("It's not your box");
                    if (bool) {
                        curListener = null;

                        tablePane.removeHiveMouseListener(this);
                        boxPane[0].removeHiveMouseListener(this);
                        boxPane[1].removeHiveMouseListener(this);

                        semaphore.notify();
                    }
                }
            }
        }

        class BeginMove extends HiveMouseAdapter {

            @Override
            public void hiveMousePressed(HiveMouseEvent event) {
                if (event.originalEvent.isControlDown() || event.originalEvent.isShiftDown() || event.originalEvent.isAltDown())
                    return;
                synchronized (semaphore) {
                    if (event.sender == tablePane) {
                        Coords localCoords = Coords.instance(event.getP(), event.getQ());
                        Piece localPiece = g.table.getPieceAt(localCoords);
                        if (localPiece == null)
                            return;
                        if (localPiece.color != color)
                            return;

                        tablePane.clearHighlights();
                        boxPane[0].clearHighlights();
                        boxPane[1].clearHighlights();

                        piece = localPiece;
                        prevCoords = localCoords;

                        highlightSet(g.getTargetCoords(piece, localCoords));
                        tablePane.setHighlight(localCoords.c1, localCoords.c2, Color.blue, 3.0F, true);
                    } else if (event.sender == boxPane[color]) {
                        int i = pieceTypeFromCoordC2(event.getQ());
                        if (i < 0)
                            return;
                        if (game.box.howMany(color, i) <= 0)
                            return;

                        tablePane.clearHighlights();
                        boxPane[0].clearHighlights();
                        boxPane[1].clearHighlights();

                        piece = Constants.pieces[color][i];
                        prevCoords = null;

                        boxPane[color].setHighlight(event.getP(), event.getQ(), Color.blue, 3.0F, true);
                        highlightSet(g.getTargetCoords(piece, null));
                    } else {
                        out.println("It's not your box");
                        return;
                    }
                    curListener = endMove;
                    tablePane.removeHiveMouseListener(this);
                    boxPane[0].removeHiveMouseListener(this);
                    boxPane[1].removeHiveMouseListener(this);

                    tablePane.removeHiveMouseListener(endMove);
                    boxPane[0].removeHiveMouseListener(endMove);
                    boxPane[1].removeHiveMouseListener(endMove);

                    tablePane.addHiveMouseListener(endMove);
                    boxPane[0].addHiveMouseListener(endMove);
                    boxPane[1].addHiveMouseListener(endMove);
                }
            }
        }
    }
}