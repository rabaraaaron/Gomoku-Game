package gui;

import local.ClientMove;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;
import java.util.Vector;

/**
 * The JPanel containing the board and its edges.
 */
public class BoardView extends JPanel implements MouseListener {

    enum Space { white, black ,gray, empty};
    private int xCoord = -1;
    private int yCoord = -2;
    ClientMove tempMove = null;
    GameView gui;
    String whitePlayerName, blackPlayerName;
    Space[][] grid = new Space[19][19];


    /** Number of cells on the board in each dimension */
    private int boardSize;

    /* Default colors and font */
    private Color boardBackgroundColor = new Color(206, 133, 12);
    private Color panelBackground = new Color(214, 215, 213);
    private Color gridColor = new Color(43, 40, 43);
    private BasicStroke gridStroke = new BasicStroke(2.0f);
    private Font labelFont = new Font("SansSerif", Font.PLAIN, 12);

    /* Size in pixels of the border around the grid */
    private static final int BORDER_SIZE = 25;

    /**
     * Constructs a new BoardView panel with the given size.
     *
     * @param size width/height in pixels
     */
    public BoardView( int size, GameView gui) {
        this.boardSize = 19;
        this.setPreferredSize(new Dimension(size,size) );
        this.setBackground(panelBackground);
        this.addMouseListener(this);
        this.gui = gui;

        // initialize grid  array and place default peices
        for(int r=0; r<19; r++) {
            for(int c=0; c<19; c++) {
                grid[r][c] = Space.empty;
            }
        }
    }

    /**
     * Draws the board.
     *
     * @param g Graphics context
     */
    @Override
    public void paintComponent( Graphics g ) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        g2d.setFont(labelFont);

        int w = this.getWidth();  // width == height for this component
        int size = w - (2 * BORDER_SIZE);

        // Draw board
        drawBackground(g, size);
        drawGrid(g2d, size);
        drawLabels(g2d, size);

       //  Draw some pieces as examples
        double cellSize = (double)size / boardSize;
        for(int r=0; r<19; r++) {
            for(int c=0; c<19; c++) {
                if(grid[r][c]!=Space.empty) {
                    if(grid[r][c]==Space.white) {
                        this.drawPiece(g2d,r, c, Color.white, cellSize);
                    } else if(grid[r][c]==Space.gray) {
                        this.drawPiece(g2d,r, c, Color.gray, cellSize);
                    }  else {
                        this.drawPiece(g2d,r, c, Color.black, cellSize);
                    }
                }

            }
        }
    }

    private void drawBackground( Graphics g, int size ) {
        g.setColor( boardBackgroundColor );
        g.fillRect(BORDER_SIZE, BORDER_SIZE, size, size );
    }

    private void drawGrid( Graphics2D g, int size ) {
        GeneralPath path = new GeneralPath();
        double cellSize = (double)size / boardSize;
        double start = BORDER_SIZE, end = size + BORDER_SIZE;

        for( int i = 0; i < boardSize; i++ ) {
            double x = (i * cellSize) + BORDER_SIZE + (cellSize / 2.0);
            double y = x;
            path.moveTo(start,y);
            path.lineTo(end,y);
            path.moveTo(x,start);
            path.lineTo(x,end);
        }

        g.setStroke(gridStroke);
        g.setColor( gridColor );
        g.draw(path);
    }

    private void drawLabels( Graphics2D g, int sz ) {
        Rectangle2D.Double rect = new Rectangle2D.Double();
        rect.width = (double)sz / boardSize;
        rect.height = BORDER_SIZE;

        rect.x = BORDER_SIZE;
        rect.y = 0;
        for( int i = 0; i < boardSize; i++ ) {
            rect.x = BORDER_SIZE + (i * rect.width);
            drawCenteredString(g, "" + (char)(i  + 'A'), rect);
        }
        rect.x = 0;
        rect.height = rect.width;
        rect.width = BORDER_SIZE;
        for( int i = 0; i < boardSize; i++ ) {
            rect.y = (i + 1) * rect.height - (BORDER_SIZE / 4);
            drawCenteredString(g, "" + (i + 1), rect);
        }
    }

    private void drawPiece(Graphics2D g, int row, int col, Color color, double cellSize ) {
        Ellipse2D.Double el = new Ellipse2D.Double();
        el.width = cellSize;
        el.height = cellSize;
        el.x = BORDER_SIZE + (col * cellSize);
        el.y = BORDER_SIZE + (row * cellSize);
        g.setColor(color);
        g.fill(el);
    }

    /**
     * Mouse clicked event.  Determines the cell where the mouse
     * was clicked and prints the row/column to the console.
     */
    public void mouseClicked(MouseEvent e) {
        if(gui.getCurrentGameID() == 0){ return; }
        Point point = null;

        int size = this.getWidth();
        int mouseX = e.getX();
        int mouseY = e.getY();


        //to clear the previous temp move (if any)
        if (yCoord >= 0 && xCoord >= 0 && grid[yCoord][xCoord] == Space.gray) {
            grid[yCoord][xCoord] = Space.empty;
        }
        if (tempMove != null) {
            tempMove = null;
        }

        //Makes sure the client is a player in order to be able to set grey pieces
        if(whitePlayerName.equals(gui.getPlayerName()) || blackPlayerName.equals(gui.getPlayerName())) {

            // Calculate the cell that corresponds to the click location
            if (mouseX >= BORDER_SIZE && mouseX <= size - BORDER_SIZE &&
                    mouseY >= BORDER_SIZE && mouseY <= size - BORDER_SIZE) {
                double bSize = size - (2.0 * BORDER_SIZE);
                double cellSize = bSize / boardSize;
                int cellRow = (int) Math.floor((mouseY - BORDER_SIZE) / cellSize);
                int cellCol = (int) Math.floor((mouseX - BORDER_SIZE) / cellSize);

                this.xCoord = cellCol;
                this.yCoord = cellRow;

                if (grid[cellRow][cellCol] == Space.empty) {
                    grid[cellRow][cellCol] = Space.gray;
                    point = new Point(cellCol, cellRow);
                } else if (grid[cellRow][cellCol] == Space.gray) {
                    grid[cellRow][cellCol] = Space.empty;
                }

                // Print a message to the console.
                if (point == null) {
                    System.out.println("Mouse click is outside the board");
                } else {
                    System.out.printf("Cell row = %d, column = %d\n", point.y, point.x);
                    this.repaint();
                }
            }
        }
    }

    public void mousePressed(MouseEvent e) { }
    public void mouseReleased(MouseEvent e) { }
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }

    /**
     * Draw a String centered in the middle of a Rectangle.
     *
     * @param g2d The Graphics instance.
     * @param text The String to draw.
     * @param rect The Rectangle to center the text in.
     */
    private void drawCenteredString(Graphics2D g2d, String text, Rectangle2D.Double rect) {
        Font font = g2d.getFont();
        FontRenderContext frc = g2d.getFontRenderContext();
        GlyphVector gv = font.createGlyphVector(frc, text);
        Rectangle2D box = gv.getVisualBounds();

        int x = (int)Math.round((rect.getWidth() - box.getWidth()) / 2.0 - box.getX() + rect.x);
        int y = (int)Math.round((rect.getHeight() - box.getHeight()) / 2.0 - box.getY() + rect.y);
        g2d.drawString(text, x, y);
    }

    public int getxCoord(){ return xCoord; }
    public int getyCoord() {return yCoord; }

    /**
     * Clear invalid move by setting the selected location to empty and seting last move to null
     */
    public void clearInvalidMove(){
        tempMove = null;
        grid[yCoord][xCoord] = Space.empty;
        repaint();
    }

    public void loadBoard(Vector<Hashtable<String, Object>> moveList, String whitePlayerName, String blackPlayerName) {
        this.whitePlayerName = whitePlayerName;
        this.blackPlayerName = blackPlayerName;
        for(int i = 0; i < grid.length; i++) {
            for(int j = 0; j < grid[i].length; j++) {
                grid[i][j] = Space.empty;
            }
        }

        int x,y;
        String playerName;
        //Populate Board using subList
        for(int i = 0; i < moveList.size(); i++) {
            x = (int)Double.parseDouble(moveList.get(i).get("x").toString());
            y = (int)Double.parseDouble(moveList.get(i).get("y").toString());
            playerName = moveList.get(i).get("user").toString();

            if(playerName.equals(whitePlayerName)) grid[y][x] = Space.white;
            else grid[y][x] = Space.black;

        }
        repaint();
    }
}
