package Server.Controller;

import Server.Model.Game;

import java.io.IOException;

public class Controller {

    private Game game;

    public String run(String start) throws IOException {

        if (start.equals("START")) {
            this.game = new Game();
            int gameState = 1;

            return msgHandler(gameState);
        }

        if(start.equals("PLAY:")){
            this.game.NewGame();
            int gameState = 1;
            return msgHandler(gameState);
        }

        else {
            int gameState = this.game.PlayerInput(start);
            return msgHandler(gameState);
        }

    }

    public String msgHandler(int gameState){
        String msg = "";
        String state = new Integer(gameState).toString();
        String currentWord =this.game.GetWord();
        String currentAttempts = new Integer(this.game.GetAttempts()).toString();
        String currentScore = new Integer(this.game.GetScore()).toString();
        String uncensoredWord = this.game.UncensorWord();
        msg += state +"##" +currentWord +"##" +currentAttempts +"##" +currentScore +"##" +uncensoredWord;
        return msg;
    }

}
