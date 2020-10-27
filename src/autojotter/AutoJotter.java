package autojotter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;
import org.ejml.simple.SimpleMatrix;

/**
 *
 * @author Jae
 */
public class AutoJotter {

    private static HashMap<String, Glyph> alphabet = new HashMap<>();

    /**
     * arg1 = total width arg2 = total height arg3 = num cols arg4 = num rows
     * arg5 = upcommand arg6 = downcommand, arg7=feedrate, arg8=rotated(y/n),
     * arg9=italics(y/n)
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            autojotter.gui.MainWindow.main(args);
        } else {
            run(Double.parseDouble(args[0]), Double.parseDouble(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), args[4], args[5], Integer.parseInt(args[6]), args[7].equals("Y"), args[8].equals("Y"), System.out, System.err, new BufferedReader(new InputStreamReader(System.in)));
        }
    }

    public static void run(double totalWidth, double totalHeight, int cols, int rows, String upcommand, String downcommand, int feedrate, boolean rotated, boolean italics, PrintStream out, PrintStream err, BufferedReader in) {
        // load glyphs from svg
        alphabet.put(".", new Glyph(new BufferedReader(new InputStreamReader(AutoJotter.class.getResourceAsStream("/glyphs/period.svg"))).lines().reduce("", (s1, s2) -> s1 + "\r\n" + s2), '.', upcommand, downcommand));
        alphabet.put("?", new Glyph(new BufferedReader(new InputStreamReader(AutoJotter.class.getResourceAsStream("/glyphs/question.svg"))).lines().reduce("", (s1, s2) -> s1 + "\r\n" + s2), '?', upcommand, downcommand));
        alphabet.put("\"", new Glyph(new BufferedReader(new InputStreamReader(AutoJotter.class.getResourceAsStream("/glyphs/doublequote.svg"))).lines().reduce("", (s1, s2) -> s1 + "\r\n" + s2), '"', upcommand, downcommand));
        alphabet.put("`", new Glyph(new BufferedReader(new InputStreamReader(AutoJotter.class.getResourceAsStream("/glyphs/'.svg"))).lines().reduce("", (s1, s2) -> s1 + "\r\n" + s2), '`', upcommand, downcommand));
        alphabet.put("*", new Glyph(new BufferedReader(new InputStreamReader(AutoJotter.class.getResourceAsStream("/glyphs/asterisk.svg"))).lines().reduce("", (s1, s2) -> s1 + "\r\n" + s2), '*', upcommand, downcommand));
        alphabet.put(":", new Glyph(new BufferedReader(new InputStreamReader(AutoJotter.class.getResourceAsStream("/glyphs/colon.svg"))).lines().reduce("", (s1, s2) -> s1 + "\r\n" + s2), ':', upcommand, downcommand));
        alphabet.put("\\", new Glyph(new BufferedReader(new InputStreamReader(AutoJotter.class.getResourceAsStream("/glyphs/slash.svg"))).lines().reduce("", (s1, s2) -> s1 + "\r\n" + s2), '\\', upcommand, downcommand));
        alphabet.put("/", new Glyph(new BufferedReader(new InputStreamReader(AutoJotter.class.getResourceAsStream("/glyphs/fslash.svg"))).lines().reduce("", (s1, s2) -> s1 + "\r\n" + s2), '/', upcommand, downcommand));
        alphabet.put("|", new Glyph(new BufferedReader(new InputStreamReader(AutoJotter.class.getResourceAsStream("/glyphs/bar.svg"))).lines().reduce("", (s1, s2) -> s1 + "\r\n" + s2), '|', upcommand, downcommand));

        for (char c = 'a'; c <= 'z'; c++) {
            alphabet.put("" + c, new Glyph(new BufferedReader(new InputStreamReader(AutoJotter.class.getResourceAsStream("/glyphs/" + c + ".svg"))).lines().reduce("", (s1, s2) -> s1 + "\r\n" + s2), c, upcommand, downcommand));
        }
        for (char c = 'A'; c <= 'Z'; c++) {
            alphabet.put("" + c, new Glyph(new BufferedReader(new InputStreamReader(AutoJotter.class.getResourceAsStream("/glyphs/" + Character.toString(c).toLowerCase() + "cap.svg"))).lines().reduce("", (s1, s2) -> s1 + "\r\n" + s2), c, upcommand, downcommand));
        }
        for (char c = '0'; c <= '9'; c++) {
            alphabet.put("" + c, new Glyph(new BufferedReader(new InputStreamReader(AutoJotter.class.getResourceAsStream("/glyphs/" + Character.toString(c).toLowerCase() + ".svg"))).lines().reduce("", (s1, s2) -> s1 + "\r\n" + s2), c, upcommand, downcommand));
        }
        for (char c : new char[]{',', ';', '(', '{', '}', ')', '!', '\'', '&', ']', '[', '@', '#', '$', '%', '^', '-', '+', '~', '=', '_'}) {
            alphabet.put("" + c, new Glyph(new BufferedReader(new InputStreamReader(AutoJotter.class.getResourceAsStream("/glyphs/" + c + ".svg"))).lines().reduce("", (s1, s2) -> s1 + "\r\n" + s2), c, upcommand, downcommand));
        }

        String text = in.lines().reduce("", (s1, s2) -> s1.length() == 0 ? (s2) : (s1 + "\r\n" + s2));
        //String text = "hello world this is longer example for pagetext to gcode";
        if (cols * rows < text.length()) {
            throw new IllegalArgumentException("Not enough space for text.  Increase rows or remove text.");
        }
        Glyph[][] grid = new Glyph[rows][cols];
        for (int i = 0; i < grid.length; i++) {
            Arrays.fill(grid[i], new Glyph());
        }
        // load text
        int textCursor = 0;
        int gridCursor = 0;
        double cellWidth = totalWidth / cols;
        double cellHeight = totalHeight / rows;
        StringBuilder wordBuffer = new StringBuilder();
        ArrayList<Character> whiteSpace = new ArrayList<Character>();
        whiteSpace.addAll(Arrays.asList(new Character[]{' ', '\n', '\t'}));
        for (textCursor = 0; textCursor < text.length() && gridCursor < rows * cols; textCursor++) {
            //make sure we haven't run out of space
            if (Math.floor((gridCursor) / cols) > rows - 1) {
                throw new IllegalArgumentException("Not enough space for text.  Increase rows or remove text.");
            }
            char c = text.charAt(textCursor);
            if (c == '\r') {
                continue;
            }
            if (!whiteSpace.contains(c) && wordBuffer.length() < cols) {
                wordBuffer.append(c);
            }
            if (whiteSpace.contains(c) || textCursor == text.length() - 1) {
                boolean wrap = false;
                //check for room
                if (cols - (gridCursor % cols) >= wordBuffer.length()) {
                    for (char el : wordBuffer.toString().toCharArray()) {
                        //make sure we haven't run out of space
                        if (Math.floor((gridCursor) / cols) > rows - 1) {
                            throw new IllegalArgumentException("Not enough space for text.  Increase rows or remove text.");
                        }
                        //translation to page posisiont
                        SimpleMatrix trans = SimpleMatrix.identity(3);
                        trans.set(0, 0, cellWidth / 12.7);//convert font unit to [0,1], then times by cell width
                        trans.set(1, 1, cellHeight / 25.4);//convert font unit to [0,1], then times by cell height
                        trans.set(0, 2, (gridCursor % cols) * cellWidth);
                        trans.set(1, 2, cellHeight * rows - (gridCursor / cols) * cellHeight);// translate to cell pos

                        //italics
                        SimpleMatrix italicsSkew = SimpleMatrix.identity(3);
                        if (italics) {
                            italicsSkew.set(0, 1, cellWidth * 3);
                        }

                        SimpleMatrix rotate = SimpleMatrix.identity(3);
                        if (rotated) {
                            rotate.set(0, 0, Math.cos(-Math.PI / 2));
                            rotate.set(1, 0, Math.sin(-Math.PI / 2));
                            rotate.set(0, 1, -Math.sin(-Math.PI / 2));
                            rotate.set(1, 1, Math.cos(-Math.PI / 2));
                            rotate.set(0, 2, 0);
                            rotate.set(1, 2, cellWidth * cols);
                        }

                        SimpleMatrix scaleDown = SimpleMatrix.identity(3);
                        scaleDown.set(0, 0, .85);
                        scaleDown.set(1, 1, .85);
                        if (alphabet.get("" + el) != null) {
                            grid[gridCursor / cols][gridCursor % cols]//this grid position is
                                    = new Glyph(
                                            alphabet.get("" + el).getPaths().stream()
                                                    .map(path
                                                            -> //the alphabet letter
                                                            new Path(path.getPoints().stream().map(p -> rotate.mult(trans.mult(italicsSkew.mult(scaleDown.mult(p)))))//process list of points, transform them
                                                            .collect(Collectors.toList()), upcommand, downcommand))
                                                    .collect(Collectors.toList()), el, upcommand, downcommand);
                        }
                        gridCursor++;
                    }
                    wordBuffer = new StringBuilder();
                    if (gridCursor % cols == 0 && c == ' ') {//ran right to edge
                        //do nothing
                    } else if (c == ' ') {
                        gridCursor++;//leave a space
                    } else if (c == '\n') {
                        gridCursor = (int) Math.floor((gridCursor + cols) / cols) * cols;
                    } else if (c == '\t') {
                        gridCursor = Math.min(gridCursor + 5, ((int) Math.floor((gridCursor + cols) / cols) * cols) - 1);
                    }
                    //make sure we haven't run out of space
                    if (Math.floor((gridCursor) / cols) > rows - 1) {
                        throw new IllegalArgumentException("Not enough space for text.  Increase rows or remove text.");
                    }
                } else {
                    //drop a line, re run
                    gridCursor = (int) Math.floor((gridCursor + cols) / cols) * cols;
                    //make sure we haven't run out of space
                    if (Math.floor((gridCursor) / cols) > rows - 1) {
                        throw new IllegalArgumentException("Not enough space for text.  Increase rows or remove text.");
                    }
                    textCursor--;
                    if (c != ' ') {
                        wordBuffer.deleteCharAt(wordBuffer.length() - 1); // remove from word buffer and reproces
                    }
                }
            }
        }
        StringBuilder preview = new StringBuilder();
        out.println("; Generated by AutoJotter. ");
        out.println("G20 (Units are in Inches)");
        out.println("G61 (Go to exact corners)");
        out.println("\r\n");
        out.println("F" + feedrate + ".00000");
        //preview.append(";--------- The text preview below was generated by AutoJotter \r\n");
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                preview.append(grid[i][j].toString());
            }
            preview.append("\r\n");
        }
        //preview.append(";--------- End text preview \r\n");
        //out.println(preview.toString());
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                for (Path path : grid[i][j].getPaths()) {
                    out.println(path.toString());
                }
            }
        }

        err.println(preview.toString());
    }

}
