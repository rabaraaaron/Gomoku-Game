package controller;

    /**
     * This class creates a single instance of the game controller
     */
    public class GameControllerManager {

        private static final GameController GAME_CONTROLLER =
                new GameController();

        /**
         * @return the GameController.
         */
        public static GameController getController() {
            return GAME_CONTROLLER;
        }
    }

