package Client.View;


import Client.Net.ServerConnectionHandler;

import java.util.Scanner;

public class View implements Runnable{
    private static final String PROMPT = ">> ";
    private final Scanner clientInput = new Scanner(System.in);
    private boolean receivingInput = false;
    private ServerConnectionHandler serverConnection;

    public void start(){
        if(receivingInput){
            return;
        }
        receivingInput = true;
        serverConnection = new ServerConnectionHandler();
        new Thread(this).start();
    }

    public void run(){
        System.out.println("Welcome to Hangman, type 'START' to start guessing, type 'QUIT:' to end the game");
        System.out.print(PROMPT);
        while(receivingInput){
            try{
                String playerInput = clientInput.nextLine().toUpperCase();
                if(playerInput.equals("QUIT:")) {
                    System.out.println("Disconnecting from the Game,");
                    serverConnection.disconnect();
                    receivingInput = false;
                }
                if(playerInput.equals("START")){
                    serverConnection.playerInput(playerInput);
                    serverConnection.connect();
                } else {
                    serverConnection.playerInput(playerInput);
                    serverConnection.Guess();
                }

            } catch (Exception e){
                System.out.println("Unable to process clientInput");
            }
        }
    }

}
