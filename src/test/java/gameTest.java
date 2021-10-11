import org.junit.Assert;
import org.junit.Test;
import user.User;
import game.Game;

import java.sql.Timestamp;

public class gameTest {

    /**
     * Tests for the Game class
     */
    @Test
    public void testGameConstructors() {
        User p1 = new User("Player 1");
        User p2 = new User("Player 2");
        Game g = new Game(p1, p2, Game.gameEnum.CONNECTSIX, true, 1);
        Game g2 = new Game(p1, p2, Game.gameEnum.CONNECTSIX, true, 1);

        Assert.assertNotNull(g);
        Assert.assertNotNull(g2);
    }

    @Test
    public void testGameGettersAndSetters() {
        User p1 = new User("Player 1");
        User p2 = new User("Player 2");
        Game g = new Game(p1, p2, Game.gameEnum.CONNECTSIX, true, 1);

        g.setLoser(p1);
        g.setWinner(p2);
        Assert.assertEquals(p1, g.getLoser());
        Assert.assertEquals(p2, g.getWinner());

        g.setLoser(p2);
        Assert.assertEquals(p2, g.getLoser());
        Assert.assertTrue(g.isViewable());

        g.commitMove(5, 10);
        Assert.assertTrue(g.getMutable());

        Timestamp t = g.getTimestamp();
        Assert.assertNotNull(t);
    }

    @Test
    public void testGamePlayerCount() {
        User p1 = new User("Player 1");
        User p2 = new User("Player 2");

        Game g = new Game(p1, p2, Game.gameEnum.CONNECTSIX, true, 1);
        Assert.assertEquals(2, g.getPlayers().length); //Player count matches expected players in a game
    }


    @Test
    public void testCommitMove() {
        User p1 = new User("Player 1");
        User p2 = new User("Player 2");
        Game g = new Game(p1, p2, Game.gameEnum.CONNECTSIX, true, 1);

        Assert.assertTrue(g.commitMove(2, 4));//Test that move was made and returned True
        Assert.assertTrue(g.isOccupied(2, 4));//Test that move was saved
        Assert.assertFalse(g.commitMove(20, 21));//Test for false on out of bounds commit
    }

    @Test
    public void testGetGameID() {
        User p1 = new User("Player 1");
        User p2 = new User("Player 2");
        Game g = new Game(p1, p2, Game.gameEnum.CONNECTSIX, true, 1);

        Assert.assertNotNull(g.getGameID()); //check game id
    }


    @Test
    public void testIsOccupied() {
        User p1 = new User("Player 1");
        User p2 = new User("Player 2");
        Game g = new Game(p1, p2, Game.gameEnum.CONNECTSIX, true,  1);

        g.commitMove(10, 10);
        g.commitMove(18, 1);
        g.commitMove(10, 10);


        Assert.assertTrue(g.isOccupied(18, 1));//Test for a True value for an occupied space
        Assert.assertTrue(g.isOccupied(10, 10));//Occupied space
        Assert.assertFalse(g.isOccupied(1, 1));//Test that an unoccupied space returns False
    }

    @Test
    public void testGetMoveCoords(){
        User p1 = new User("Player 1");
        User p2 = new User("Player 2");
        Game g = new Game(p1, p2, Game.gameEnum.CONNECTSIX, true,  1);

        g.commitMove(10, 10);
        g.commitMove(15, 9);
        g.commitMove(5, 7);
        int[] coordinates = g.getMoveCoordinates();

        Assert.assertEquals(5, coordinates[0]); //test that x coord of last move can be recorded
        Assert.assertEquals(7, coordinates[1]); //test that x coord of last move can be recorded
    }

    /**
     * Tests for the User Class
     */
    @Test
    public void testUserConstructor() {
        User u = new User("abc");
        User p1 = new User("Aaron Rabara");

        Assert.assertEquals("abc", u.getUserName());
        Assert.assertNotEquals("def", u.getUserName());
        Assert.assertEquals("Aaron Rabara", p1.getUserName());
    }

    @Test
    public void testUserGettersAndSetters() {
        User p1 = new User("Player 1");
        User p2 = new User("Player 2");
        Game g = new Game(p1, p2, Game.gameEnum.CONNECTSIX, true, 1);
        Game g2 = new Game(p1, p2, Game.gameEnum.CONNECTSIX, true, 2);

        p1.addWin();
        p1.addLoss();
        p1.addTie();
        p1.addWin();
        p1.addWin();
        p1.addLoss();

        //Test Add wins, losses, and ties
        Assert.assertEquals(3, p1.getWins());
        Assert.assertEquals(2, p1.getLosses());
        Assert.assertEquals(1, p1.getTies());

        //Test that users store games
        Assert.assertEquals(1 ,p1.getActiveGame(g.getGameID()));
        Assert.assertEquals(2 ,p2.getActiveGame(g2.getGameID()));
    }

//    /**
//     * Tests for the Leaderboard Class
//     */
//    @Test
//    public void testLeaderboard(){
//        User p1 = new User("player 1");
//        User p2 = new User("player 2");
//        User p3 = new User("player 3");
//        User p4 = new User("player 4");
//        User p5 = new User("player 5");
//
//        //p1: 3-2-1
//        p1.addWin();
//        p1.addLoss();
//        p1.addTie();
//        p1.addWin();
//        p1.addWin();
//        p1.addLoss();
//
//        //p2: 4-1-1
//        p2.addWin();
//        p2.addWin();
//        p2.addTie();
//        p2.addWin();
//        p2.addWin();
//        p2.addLoss();
//
//        //p3: 0-4-2
//        p3.addLoss();
//        p3.addLoss();
//        p3.addTie();
//        p3.addTie();
//        p3.addLoss();
//        p3.addLoss();
//
//        //p5: 3-3-0
//        p5.addWin();
//        p5.addLoss();
//        p5.addLoss();
//        p5.addWin();
//        p5.addWin();
//        p5.addLoss();
//
//        Leaderboard l = new Leaderboard();
//        l.updateConnect6Leaderboard(p1);
//        l.updateConnect6Leaderboard(p2);
//        l.updateConnect6Leaderboard(p3);
//        l.updateConnect6Leaderboard(p4);
//        l.updateConnect6Leaderboard(p5);
//        l.updateConnect6Leaderboard(p5);
//        List<Map<String, Object>> ldrboard = l.getConnect6Rankings();
//        System.out.println(ldrboard.toString());
//    }

//    /**
//     * Tests for the Leaderboard Class again
//     */
//    @Test
//    public void testLeaderboard2(){
//        Leaderboard ldrboard = new Leaderboard();
//        List<User> users = new ArrayList<User>();
//        Random rand = new Random();
//        int num;
//
//        for(int i = 1; i < 21; i++){
//            users.add(new User("player " + Integer.toString(i)));
//            for(int l = 0; l < 20; l++) {
//                num = rand.nextInt((10 - 0) + 1);
//                if (num % 2 == 0) {
//                    users.get(i - 1).addWin();
//                } else{
//                        users.get(i-1).addLoss();
//                }
//            }
//        }
//
//
//
//        for(int i = 0; i < users.size(); i++){
//            ldrboard.updateConnect6Leaderboard(users.get(i));
//        }
//
//        User aaron = new User("Aaron");
//        User gil = new User("gil");
//        for(int i = 0; i < 15;i++){
//            aaron.addWin();
//        }
//        for(int i = 0; i < 15;i++){
//            gil.addLoss();
//        }
//        ldrboard.updateConnect6Leaderboard(aaron);
//        ldrboard.updateConnect6Leaderboard(gil);
//
//        List<Map<String, Object>> leaderboard = ldrboard.getConnect6Rankings();
//        System.out.println(leaderboard.toString());
//    }

    /**
     * Tests for Connect6Rules Class
     */

    @Test
    public void testIsLegal() {
        User p1 = new User("Player 1");
        User p2 = new User("Player 2");
        Game g = new Game(p1, p2, Game.gameEnum.CONNECTSIX, true, 1);

        Assert.assertTrue(g.commitMove(3, 5));//Test that a valid move returns True
        Assert.assertTrue(g.isOccupied(3, 5));//Test that a move to an occupied space can be sensed
        Assert.assertFalse(g.commitMove(-1, 20));//Test out of bounds returns False
        Assert.assertFalse(g.commitMove(22222222, 0000 - 10));//Out of bounds
        Assert.assertFalse(g.commitMove(19, 19));//Out of bounds
    }

    @Test
    public void testPassTurn(){
        User p1 = new User("Player 1");
        User p2 = new User("Player 2");
        Game g = new Game(p1, p2, Game.gameEnum.CONNECTSIX, true, 1);

        g.commitMove(5, 10); //first player to go makes a move
        Assert.assertNotEquals(g.getTurnPlayer() ,g.getLastMovePlayer()); //Test that order changes in abba sequence
        User p3 = g.getLastMovePlayer(); //keep track of first player

        g.commitMove(1, 1); //Second player to go makes a move
        Assert.assertEquals(g.getTurnPlayer() ,g.getLastMovePlayer()); //Test that Second player to go is next up again

        g.commitMove(0, 10); //Second player to go makes another move
        g.commitMove(0, 1); //First player makes final move in rotation of abba
        Assert.assertEquals(g.getTurnPlayer() ,p3); //check that a is the next player again in order abba
    }

    @Test
    public void testGameOver(){
        User p1 = new User("Player 1");
        User p2 = new User("Player 2");
        Game g = new Game(p1, p2, Game.gameEnum.CONNECTSIX, true, 1);

        Assert.assertTrue(g.commitMove(0,0)); //a
        Assert.assertTrue(g.commitMove(1,0)); //b
        Assert.assertTrue(g.commitMove(1,1)); //b
        Assert.assertTrue(g.commitMove(0,1)); //a
        Assert.assertTrue(g.commitMove(0,2)); //a
        Assert.assertTrue(g.commitMove(1,2)); //b
        Assert.assertTrue(g.commitMove(1,3)); //b
        Assert.assertTrue(g.commitMove(0,3)); //a
        Assert.assertTrue(g.commitMove(0,4)); //a
        Assert.assertTrue(g.commitMove(1,4)); //b
        Assert.assertTrue(g.commitMove(1,6)); //b
        Assert.assertTrue(g.commitMove(0,5)); //a
        Assert.assertFalse(g.commitMove(0,6)); //a TODO: This should not be legal.
/*
        Assert.assertTrue(g.isGameCompleted()); //Check that win condition was reached
        Assert.assertEquals(g.getLastMovePlayer(), g.getWinner()); //Check that player b won

        g.commitMove(0,1); //a
        g.commitMove(18,0); //b
        g.commitMove(17,0); //b
        g.commitMove(0,2); //a
        g.commitMove(0,3); //a
        g.commitMove(16,0); //b
        g.commitMove(15,0); //b
        g.commitMove(0,4); //a
        g.commitMove(0,5); //a
        g.commitMove(14,0); //b
        g.commitMove(13,0); //b


        Assert.assertTrue(g.isGameCompleted()); //Check that win condition was reached
        Assert.assertEquals(g.getLastMovePlayer(), g.getWinner()); //Check that player b won

        g.commitMove(0,1); //a
        g.commitMove(0, 18); //b
        g.commitMove(0, 17); //b
        g.commitMove(0,2); //a
        g.commitMove(0,3); //a
        g.commitMove(0, 16); //b
        g.commitMove(0, 15); //b
        g.commitMove(0,4); //a
        g.commitMove(0,5); //a
        g.commitMove(0, 14); //b
        g.commitMove(0, 13); //b

        Assert.assertTrue(g.isGameCompleted()); //Check that win condition was reached
        Assert.assertEquals(g.getLastMovePlayer(), g.getWinner()); // Check that player a won*/
    }
}