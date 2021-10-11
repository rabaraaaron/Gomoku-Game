package gui;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

//The menu bar.
public class MenuBar extends JMenuBar implements ActionListener {

    //static DefaultListModel<String> myList2;
    static Dimension frameSize = new Dimension(400, 500);
    static Dimension tableSize = new Dimension(350, 350);
    String[] gamesList_Columns;
    String[][] viewableGamesList_Rows;
    String[][] playableGamesList_Rows;

    /**
     * The Main GUI
     */
    private GameView gui;

    /** Quit item */
    private JMenuItem quitMenuItem;

    /** New Game item, leaderboard item, other games item*/
    private JMenuItem login, newGame, newUser, leaderboard, viewOtherGame;

    /**
     * Constructs the menu bar
     *
     * @param gui the main GameWindow
     */
    public MenuBar(GameView gui) {
        this.gui = gui;

        // Build the "Game" menu
        this.add( buildGameMenu() );
    }

    public void updateGameListTables() {
        this.gamesList_Columns = gui.getGamesList_Columns();
        this.playableGamesList_Rows = gui.getPlayableGamesList_Rows();
        this.viewableGamesList_Rows = gui.getViewableGamesList_Rows();
    }


    private JMenu buildGameMenu() {
        JMenu menu = new JMenu("Game");
        menu.getAccessibleContext().setAccessibleDescription(
                "New game");

        login = new JMenuItem("Login");
        login.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, ActionEvent.META_MASK));
        login.getAccessibleContext().setAccessibleDescription(
                "Login");
        login.addActionListener(this);
        menu.add(login);

        newUser = new JMenuItem("Create New User");
        newUser.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_C, ActionEvent.META_MASK));
        newUser.getAccessibleContext().setAccessibleDescription(
                "Create a new user");
        newUser.addActionListener(this);
        menu.add(newUser);

        newGame = new JMenuItem("New Game");
        newGame.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_N, ActionEvent.META_MASK));
        newGame.getAccessibleContext().setAccessibleDescription(
                "Start a new game against the computer");
        newGame.addActionListener(this);
        menu.add(newGame);


        leaderboard = new JMenuItem("Leaderboard");
        leaderboard.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_L, ActionEvent.META_MASK));
        leaderboard.getAccessibleContext().setAccessibleDescription(
                "Do something interesting.");
        leaderboard.addActionListener(this);
        menu.add(leaderboard);

        viewOtherGame = new JMenuItem("View other games");
        viewOtherGame.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_V, ActionEvent.META_MASK));
        viewOtherGame.getAccessibleContext().setAccessibleDescription(
                "Do something interesting.");
        viewOtherGame.addActionListener(this);
        menu.add(viewOtherGame);

        menu.addSeparator();

        quitMenuItem = new JMenuItem("Quit");
        quitMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Q, ActionEvent.META_MASK));
        quitMenuItem.getAccessibleContext().setAccessibleDescription(
                "Exit game.");
        quitMenuItem.addActionListener(this);
        menu.add(quitMenuItem);

        return menu;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() == quitMenuItem)        { System.exit(0); }
        else if(e.getSource() == newGame)       { createNewGamePrompt(); }
        else if(e.getSource() == newUser)       { createNewUser(); }
        else if(e.getSource() == leaderboard)   { leaderboardPressed(); }
        else if(e.getSource() == viewOtherGame)  { viewOtherGamesPressed(); }
        else if(e.getSource() == login)         { loadUserInfo(); }
    }

    private String loadUserInfo(){
        JFrame createUserPrompt = new JFrame("Credentials Prompt");
        createUserPrompt.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        /**Get username upon clicking new game*/
        String username = JOptionPane.showInputDialog(createUserPrompt, "Your username\n");
        createUserPrompt.pack();
        createUserPrompt.setVisible(true);

        if(username != null) {
            try {
                gui.login(username);

            } catch (Exception e) {
                System.out.println("Error from createNewUser() in MenuBar class: " + e.getMessage());
            }
        }
        createUserPrompt.dispose();

        System.out.print(username + "\n");
        return username;
    }

    /**
     * Creates a JFrame for the username prompt, calls usernameEntered once the username has been entered,
     * then disposes of itself after returning the username
     * @return username entered form the prompt
     */
    private String createNewGamePrompt(){
        JFrame usernamePrompt = new JFrame("New Game Prompt");
        usernamePrompt.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        /**Get username upon clicking new game*/
        String player2 = JOptionPane.showInputDialog(usernamePrompt,
                "Enter Opponent's username");
        usernamePrompt.pack();

        //check if the user entered a name for player2
        if(!player2.equals("")) {
            //prepare to get the game-restriction type
            Object[] possibleValues = { "Private", "Public" };
            //put the selected value hare
            Object selectedValue = JOptionPane.showInputDialog(null,
                    "Choose game restriction type", "Game accessibility ",
                    JOptionPane.INFORMATION_MESSAGE, null,
                    possibleValues, possibleValues[0]);

            //convert the selected value to a boolean
            boolean gameRestriction;
            if (selectedValue.equals("Private"))
                 {gameRestriction = true; }
            else {gameRestriction = false; }
            try {
                //create the game
                gui.newGame(player2, gameRestriction);
            } catch (Exception e) {
                System.out.println("Error from createNewGame() in MenuBar class: " + e.getMessage());
                e.printStackTrace();
            }
            usernamePrompt.dispose();
            return player2;
        }
        return null;
    }

    /**
     * Creates a new JFrame after the username prompt. This JFrame allows the user to enter a username.
     * and disposes of the JFrame on clicking a button.
     */
    private void usernameEntered(){
        JFrame gametypeFrame = new JFrame("Gametype Select");
        JPanel gametypePanel = new JPanel();
        JButton connect6 = new JButton("Connect 6");
        connect6.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if(e.getSource() == connect6){
                            System.out.print("creating new Connect 6 game\n");
                            gametypeFrame.dispose();
                        }
                    }
                }
        );

        gametypePanel.add(connect6);
        gametypeFrame.add(gametypePanel);
        gametypeFrame.pack();
        gametypeFrame.setResizable(false);
        gametypeFrame.setLocationRelativeTo(null);
        gametypeFrame.setVisible(true);
    }

    /**
     * Creates a new User so that games can be played
     */
    private String createNewUser(){
        JFrame createUserPrompt = new JFrame("Create User Prompt");
        createUserPrompt.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        /**Get username upon clicking new game*/
        String username = JOptionPane.showInputDialog(createUserPrompt, "Your username\n");
        createUserPrompt.pack();
        createUserPrompt.setVisible(true);

        if(username != null) {
            try {
                gui.newUser(username);

            } catch (Exception e) {
                System.out.println("Error from createNewUser() in MenuBar class: " + e.getMessage());
            }
        }
        createUserPrompt.dispose();

        System.out.print(username + "\n");
        return username;
    }


    /**
     * Creates a new JFrame when the user clicks on the leaderboard option. The JFrame contains tabs that
     * corespond to the available gametypes. Different methods to create the leaderboard are called based on the
     * tab being pressed.
     */
    private void leaderboardPressed() {

        JFrame gametypeFrame = new JFrame("View leaderboard");
        gametypeFrame.setLayout(new BorderLayout());
        gametypeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        gametypeFrame.setResizable(false);
        gametypeFrame.setPreferredSize(frameSize);

        JTabbedPane gametypeTabs = new JTabbedPane();
        JPanel connect6Panel = new JPanel();

        gametypeTabs.addChangeListener(new ChangeListener() { //add the Listener
            public void stateChanged(ChangeEvent e) {
                switch (gametypeTabs.getSelectedIndex()){
                    case 0: createConnect6Leaderboard(connect6Panel);   break;
                    default: System.out.println("some error with leaderboard tab selection");break;
                }
            }
        });

        gametypeTabs.add("Connect 6",connect6Panel);
        gametypeFrame.add(gametypeTabs);
        gametypeFrame.pack();
        gametypeFrame.setLocationRelativeTo(null);
        gametypeFrame.setVisible(true);

    }

    private void viewOtherGamesPressed() {

        JFrame gameListFrame = new JFrame("View other games");
        gameListFrame.setLayout(new BorderLayout());
        gameListFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        gameListFrame.setResizable(true);
        gameListFrame.setPreferredSize(frameSize);

        JTabbedPane gameListTabs = new JTabbedPane();
        JPanel viewableGamesPanel = new JPanel();
        JPanel playableGamesPanel = new JPanel();


        gameListTabs.addChangeListener(new ChangeListener() { //add the Listener
            public void stateChanged(ChangeEvent e) {
                switch (gameListTabs.getSelectedIndex()){
                    case 0: displayPlayableGames(playableGamesPanel, gameListFrame);   break;
                    case 1: displayViewableGames(viewableGamesPanel, gameListFrame);   break;
                    default: System.out.println("some error with gamesView tab selection"); break;
                }
            }
        });
        gameListTabs.add("Play Games",playableGamesPanel);
        gameListTabs.add("View Games",viewableGamesPanel);

        gameListFrame.add(gameListTabs);
        gameListFrame.pack();
        gameListFrame.setLocationRelativeTo(null);
        gameListFrame.setVisible(true);
    }

    private void displayPlayableGames(JPanel playableGamesPanel, JFrame jFrame) {

        playableGamesPanel.removeAll(); //clear the panel
        List<Map<String, Object>> gamesList = gui.joinableUserGameList(gui.getPlayerName());

       String[][] gameListTable_Rows;

        if(gamesList != null) {
            gameListTable_Rows = new String[gamesList.size()][5];
            for (int i = 0; i < gamesList.size(); i++) {
                for (int l = 0; l < 5; l++) {
                    if (l == 0)
                        gameListTable_Rows[i][l] = String.valueOf((int) Double.parseDouble(gamesList.get(i).get("gameID").toString()));
                    else if (l == 1) gameListTable_Rows[i][l] = gamesList.get(i).get("white").toString();
                    else if (l == 2) gameListTable_Rows[i][l] = gamesList.get(i).get("black").toString();
                    else if (l == 3) gameListTable_Rows[i][l] = gamesList.get(i).get("completed").toString();
                    else gameListTable_Rows[i][l] = gamesList.get(i).get("restricted").toString();
                }
            }

            String[] gamesList_Columns = {"Game ID", "Player1", "Player2", "Completed", "Private"};
            JTable gamesTable = new JTable(gameListTable_Rows, gamesList_Columns);//populate the table.
            gamesTable.setEnabled(true);
            gamesTable.setRowSelectionAllowed(true);
            gamesTable.setColumnSelectionAllowed(false);

            JScrollPane sp = new JScrollPane(gamesTable);
            JButton joinButton = new JButton("Join");
            sp.setPreferredSize(tableSize);
            playableGamesPanel.add(sp);
            playableGamesPanel.add(joinButton);
            joinButton.setEnabled(false);

            gamesTable.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    onTable_mouseClicked(gamesTable, playableGamesPanel, joinButton, jFrame);
                }
            });
        }

    }

    private void displayViewableGames(JPanel viewableGamesPanel, JFrame jFrame) {

        viewableGamesPanel.removeAll(); //clear the panel
        List<Map<String, Object>> gamesList = gui.viewAllGames(gui.getPlayerName());

        String[][] gameListTable_Rows;

        if(gamesList != null) {
            gameListTable_Rows = new String[gamesList.size()][4];
            for (int i = 0; i < gamesList.size(); i++) {
                    for (int l = 0; l < 4; l++) {
                        if (l == 0)
                            gameListTable_Rows[i][l] = String.valueOf((int) Double.parseDouble(gamesList.get(i).get("gameID").toString()));
                        else if (l == 1) gameListTable_Rows[i][l] = gamesList.get(i).get("white").toString();
                        else if (l == 2) gameListTable_Rows[i][l] = gamesList.get(i).get("black").toString();
                        else gameListTable_Rows[i][l] = gamesList.get(i).get("completed").toString();
                }
            }

            String[] gamesList_Columns = {"Game ID", "Player1", "Player2", "completed"};

            JTable gamesTable = new JTable(gameListTable_Rows, gamesList_Columns);
            gamesTable.setEnabled(true);
            gamesTable.setRowSelectionAllowed(true);
            gamesTable.setColumnSelectionAllowed(false);

            JScrollPane sp = new JScrollPane(gamesTable);
            JButton button = new JButton("Join");
            sp.setPreferredSize(tableSize);
            viewableGamesPanel.add(sp);
            viewableGamesPanel.add(button);
            button.setEnabled(false);

            gamesTable.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    onTable_mouseClicked(gamesTable, viewableGamesPanel, button, jFrame);
                }
            });
        }

    }

    public void onTable_mouseClicked( JTable gamesTable){
        int index = gamesTable.getSelectedRow();
        TableModel model = gamesTable.getModel();
        String value = model.getValueAt(index, 0).toString();
        System.out.println(value + "has been selected");
    }


    public void onTable_mouseClicked( JTable gamesTable, JPanel panel, JButton btn, JFrame jFrame) {
        int index = gamesTable.getSelectedRow();
        TableModel model = gamesTable.getModel();

        String selectedGameID = model.getValueAt(index, 0).toString();
        String User1_FromSelectedRow = model.getValueAt(index, 1).toString();
        String User2_FromSelectedRow = model.getValueAt(index, 2).toString();
        String completionStatus = model.getValueAt(index, 3).toString();

        String currentUser = gui.getPlayerName();

        if (completionStatus.equals("false")) {
            if (currentUser.equals(User1_FromSelectedRow)
                    || currentUser.equals(User2_FromSelectedRow)){
                btn.setText("Join Game " + selectedGameID);
            } else {
                btn.setText("View Game " + selectedGameID);
            }
            btn.setEnabled(true);
            panel.revalidate();
            panel.repaint();

        } else if(completionStatus.equals("true")) {
            btn.setText("View Only ");
            btn.setEnabled(true);
        } else {
            btn.setEnabled(false);
        }
        btn.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if(e.getSource() == btn){
                            if(btn.getText().charAt(0)=='J'){
                                System.out.print("join Game Pressed\n");
                                gui.joinGame(Integer.parseInt(selectedGameID));
                                jFrame.dispose();
                            }
                            else{
                                System.out.println("view game pressed");
                                gui.viewGame(Integer.parseInt(selectedGameID));
                                jFrame.dispose();
                            }
                        }
                    }
                }
        );


        panel.revalidate();
        panel.repaint();
    }

    private void createConnect6Leaderboard( JPanel leaderboardPanel){

        leaderboardPanel.removeAll(); //clear the panel
        Vector<Hashtable<String, Object>> leaderboardList = gui.viewLeaderboard();
        String[][] leaderboardString = new String[leaderboardList.size()][5];
        for(int i = 0; i < leaderboardList.size(); i++){
            for(int l = 0; l < 5; l++){
                if(l == 0) leaderboardString[i][l] = Integer.toString(i + 1);
                else if(l == 1) leaderboardString[i][l] = leaderboardList.get(i).get("username").toString();
                else if(l == 2) leaderboardString[i][l] = Integer.toString((int)Double.parseDouble(leaderboardList.get(i).get("wins").toString()));
                else if(l == 3) leaderboardString[i][l] = Integer.toString((int)Double.parseDouble(leaderboardList.get(i).get("losses").toString()));
                else leaderboardString[i][l] = Integer.toString((int)Double.parseDouble(leaderboardList.get(i).get("ties").toString()));
            }
        }

        String[] leaderboard_TableColumns = {"Rank", "Username", "wins", "Losses", "Ties"};
        JTable leaderboard = new JTable(leaderboardString, leaderboard_TableColumns);
        leaderboard.setEnabled(false);
        JScrollPane sp = new JScrollPane(leaderboard);
        sp.setPreferredSize(tableSize);

        leaderboardPanel.add(sp);
    }
}