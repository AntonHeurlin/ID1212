package Client.View;


import Client.Net.ServerConnectionHandler;

import java.util.Scanner;

public class View implements Runnable{
    private static final String PROMPT = ">>";
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
        while(receivingInput){
            try{
                String playerInput = clientInput.nextLine().toUpperCase();
                if(playerInput.equals("QUIT:")) {
                    receivingInput = false;
                    //erver.disconnect();
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
