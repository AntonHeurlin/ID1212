package Server.Controller;


import Server.Model.Game;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;


public class Controller {

    private final SocketChannel socketChannel;
    private Game game;

    public Controller(SocketChannel socketChannel) throws IOException {
        this.socketChannel = socketChannel;
    }


    public String run(String start) throws IOException {

        System.out.println("var: " + start);

        if (start.equals("START")) {
            this.game = new Game();
            System.out.println("Stage 2");
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


    public void println(String message) {
        System.out.println("Printing messageString in Println function: " +message);
        ByteBuffer buffer = ByteBuffer.wrap((message + "\n").getBytes());
        try {
            socketChannel.write(buffer);
        } catch (IOException e) {
            System.out.println("Failed to send message!\n");
            e.printStackTrace();
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


    private String readMessage(ByteBuffer buffer) {
        buffer.flip();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return new String(bytes);
    }
}
