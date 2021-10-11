package gui;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.String.valueOf;

public class SidePanel extends JPanel{
    static DefaultListModel<String> sideBoxMovesList;
    private JTextArea chatbox;
    private GameView gui;
    private final String[] columns = new String[] { "", ""};
    private String [][] userInfo = new String[][] {
                {"Username", "" },
                {"Total Wins", "" },
                {"Total losses", "" },
                {"Total ties", "" },
                {"Current Opponent", ""},
                {"Current GameID", ""},
                {"Game Status", ""}
                };

    JTable table = new JTable(userInfo, columns);

    public SidePanel(GameView g) {

        this.gui = g;
        this.setPreferredSize( new Dimension(250, 0));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        createNorthOfSidePanel();
        createCenterOfSidePanel();
        createSouthOfSidePanel();
    }

    /**
     * Updates the number of wins, losses and ties in the table of the sidePanel.
     *  @param map of contains the userInfo that came from the server side.
     */
    public void updateUserInfoTable( Map map ){

        double dblWins = (double)map.get("wins");
        double dblLosses = (double)map.get("losses");
        double dblTies = (double)map.get("ties");

        int wins = (int)dblWins;
        int losses = (int)dblLosses;
        int ties = (int)dblTies;

        table.setValueAt(gui.getPlayerName(), 0,1);
        table.setValueAt(valueOf(wins), 1,1);
        table.setValueAt(valueOf(losses), 2,1);
        table.setValueAt(valueOf(ties), 3,1);
        table.setValueAt(gui.getCurrentOpponent(), 4, 1);
        table.setValueAt(valueOf(gui.getCurrentGameID()), 5, 1);
        table.setValueAt(gui.getCurrentGameRestricted(), 6, 1);
    }

    /**
     * Creates the north panel containing both drop down menu's, and both the previous move and next move buttons
     */
    private void createNorthOfSidePanel(){

        this.add(table);

//        //Add drop down menu for choosing in progress games
        JPanel northOfSidePanel = new JPanel();

        //Create make move button
        JButton makeMoveButton = new JButton("Make Move");
        makeMoveButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if(e.getSource() == makeMoveButton){

                            System.out.println("make move pressed");
                            System.out.println(gui.getPlayerName());

                            BoardView bv = gui.getBoardView();
                            //ClientMove move = bv.getTempMove()

                            int x = bv.getxCoord();
                            int y = bv.getyCoord();
                            System.out.println("x = " + x + "    y = " + y );

                           GameView.Result result =  gui.makeMove(x,y, gui.getPlayerName(), gui.getCurrentGameID());
                            if (result== GameView.Result.Valid) {
//                                bv.commitMove();
                            } else if(result == GameView.Result.NotValid) {
                                bv.clearInvalidMove();
                            }

                        }
                    }
                }
        );

        makeMoveButton.setPreferredSize(new Dimension(240, 50));
        northOfSidePanel.add(makeMoveButton);
        northOfSidePanel.setPreferredSize(new Dimension(240,100));
        northOfSidePanel.setMaximumSize(new Dimension(240, 200));

        this.add(northOfSidePanel, BorderLayout.NORTH);
    }

    /**
     * Creates the center panel consisting of only the make move button
     */
    private void createCenterOfSidePanel(){

        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList listBox = new JList(listModel);
        sideBoxMovesList = listModel;
        JScrollPane listBoxScroller = new JScrollPane(listBox);

        //for auto scrolling down listBox when new lines are added to the listBox.
        AtomicInteger verticalScrollBarMaximumValue = new AtomicInteger(listBoxScroller.getVerticalScrollBar().getMaximum());
        listBoxScroller.getVerticalScrollBar().addAdjustmentListener(
                e -> {
                    if ((verticalScrollBarMaximumValue.get() - e.getAdjustable().getMaximum()) == 0)
                        return;
                    e.getAdjustable().setValue(e.getAdjustable().getMaximum());
                    verticalScrollBarMaximumValue.set(listBoxScroller.getVerticalScrollBar().getMaximum());
                }
        );

        //Add Previous move and next move buttons
        JButton previousMoveButton = new JButton("Prev. Move");

        previousMoveButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if(e.getSource() == previousMoveButton){
                            System.out.println("previous move pressed");
                            gui.previousMove();
                        }
                    }
                }
        );

        JButton nextMoveButton = new JButton("Next Move");
        nextMoveButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if(e.getSource() == nextMoveButton){
                            System.out.println("next move pressed");
                            gui.nextMove();
                        }
                    }
                }
        );

        JPanel centerOfSidePanel = new JPanel();
        Dimension dimension = new Dimension(240, 180);
        centerOfSidePanel.setPreferredSize(dimension);

        GroupLayout layout = new GroupLayout(centerOfSidePanel);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);


        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(listBoxScroller,GroupLayout.PREFERRED_SIZE, 2, Short.MAX_VALUE)//, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 39,  Short.MAX_VALUE)
                                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(previousMoveButton)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(nextMoveButton)
                                        )
                                )
                        )
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(listBoxScroller, GroupLayout.PREFERRED_SIZE, 12, Short.MAX_VALUE)
                                .addGap(0)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(previousMoveButton, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, 33)
                                        .addGap(0)
                                        .addComponent(nextMoveButton, GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)
                                )
                        )
        );

        centerOfSidePanel.setLayout(layout);
        this.add(centerOfSidePanel, BorderLayout.CENTER);
    }

    /**
     * Creates the south panel that contains all matters concerning the chat option
     */
    private void createSouthOfSidePanel(){
        JPanel southOfSidePanel = new JPanel();
        JLabel chatLabel = new JLabel("Chat");

        chatbox = new JTextArea("This is the chatbox\n" ,12 , 20);
        chatbox.setBounds(10, 10, 10, 30);
        chatbox.setEditable(false);
        chatbox.setLineWrap (true);
        chatbox.setWrapStyleWord (false);
        DefaultCaret caret = (DefaultCaret)chatbox.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane chatboxScroller = new JScrollPane(chatbox);


        JTextArea chatPrompt = new JTextArea();
        chatPrompt.setLineWrap (true);
        chatPrompt.setWrapStyleWord (false);
        chatPrompt.setColumns (20);
        chatPrompt.setRows(2);
        JScrollPane chatPromptScroller = new JScrollPane(chatPrompt);


        JButton sendMessage = new JButton("Send");
        sendMessage.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if(e.getSource() == sendMessage){
                            if(chatPrompt.getText().length() != 0) {
                                gui.sendChat(chatPrompt.getText());
                                chatPrompt.setText("");
                            }
                        }
                    }
                }
        );

        southOfSidePanel.add(chatLabel);
        southOfSidePanel.add(chatboxScroller);
        southOfSidePanel.add(chatPromptScroller);
        southOfSidePanel.add(sendMessage);
        this.add(southOfSidePanel, BorderLayout.SOUTH);
    }

    void addMessage(String message){
        Scanner scan = new Scanner(message);
        scan.useDelimiter(":\\s");
        String username = scan.next();
        chatbox.append(username+ ":\n");
        chatbox.append(scan.next()+ "\n\n");
    }

    void populateMoveListBox(){

        sideBoxMovesList.clear();
        Vector <Hashtable<String, Object>> moveList = gui.getCurrentGameMoveList();

        String player = "";
        String time = "";
        int x = -1;
        int y = -1;
        int moveNumber = 0;

        for(int i=0; i<moveList.size(); i++) {
            player = (String) moveList.get(i).get("user");
            time = (String) moveList.get(i).get("time");
            x = ((Double)moveList.get(i).get("x")).intValue();
            y = ((Double)moveList.get(i).get("y")).intValue();
            y++;
            moveNumber = i+1;
            sideBoxMovesList.addElement("Move#" + moveNumber + " by (" + player + ") at (" + getCharForNumber(x) + y + "), " + time.substring(5));
            this.repaint();
        }
    }

    private String getCharForNumber(int i) {
        char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        if (i > 25) {
            return null;
        }
        return Character.toString(alphabet[i]);
    }

    void populateChatLog(Vector<Hashtable<String, Object>> chatLogList){
        chatbox.setText("");
        String message;
        for(int i = 0; i < chatLogList.size(); i++){
            message = "";
            message += chatLogList.get(i).get("sender").toString() + ":\n";
            message += chatLogList.get(i).get("words").toString() + "\n\n";
            chatbox.append(message);
        }
    }
}