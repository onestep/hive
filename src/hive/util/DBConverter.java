package hive.util;

import java.io.*;
import java.util.Arrays;

public class DBConverter {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        FileInputStream fis = new FileInputStream("opening_db.txt");
        FileOutputStream fos = new FileOutputStream("opening_db.bin");
        BufferedReader in = new BufferedReader(new InputStreamReader(fis));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos));
        String s;
        while ((s = in.readLine()) != null) {
            String[] data = s.split(":");
            if (data.length == 1)
                continue;
            String tableArrayStr = data[0];
            String movesArrayStr = data[1];
            String[] bytes = tableArrayStr.substring(1, tableArrayStr.length() - 1).split(",");
            for (String b : bytes)
                out.write((byte) Integer.parseInt(b));
            String[] moves = movesArrayStr.substring(1, movesArrayStr.length() - 2).split("],");
            out.write((byte) moves.length);
            for (String m : moves) {
                String[] moveData = m.split("\\[");
                out.write((byte) Integer.parseInt(moveData[0]));
                String[] coords = moveData[1].split(",");
                out.write((byte) Integer.parseInt(coords[0]));
                out.write((byte) Integer.parseInt(coords[1]));
            }
        }
        out.flush();
        out.close();
        
        fis = new FileInputStream("opening_db.bin");
        in = new BufferedReader(new InputStreamReader(fis));
        while (in.ready()) {
            byte[] rep = new byte[54];
            for (int i = 0; i < 54; i++)
                rep[i] = (byte) in.read();
            byte numMoves = (byte) in.read();
            byte[][] moves = new byte[numMoves][3];
            for (int i = 0; i < numMoves; i++)
                for (int j = 0; j < 3; j++)
                    moves[i][j] = (byte) in.read();
            System.out.println(Arrays.toString(rep) + ":" + Arrays.deepToString(moves));
        }
    }
}
