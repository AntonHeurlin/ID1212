package Client.Net;


import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class ServerConnectionHandler implements Runnable {
    private static BufferedReader input = null;
    private static ByteBuffer bbOut = ByteBuffer.allocate(1024);
    private static ByteBuffer bbIn = ByteBuffer.allocate(1024);
    private static Selector selector;
    private static SocketChannel sc;
    private static Boolean connected = false;
    private static final String ADRESS = "127.0.0.1";
    private static final int PORT = 29019;
    private InetSocketAddress serverAdress;
    private boolean timeToSend = false;

    public void run(){
        try{
            initConnection();
            initSelector();
            while(connected){
                if(timeToSend) {
                    sc.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
                    timeToSend=false;
                }
                selector.select();
                for (SelectionKey key : selector.selectedKeys()) {
                    selector.selectedKeys().remove(key);
                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isConnectable()) {
                        System.out.println("Entering connection phase");
                        completeConnection(key);
                    } else if (key.isReadable()) {
                        System.out.println("Entering reading phase");
                        recvFromServer(key);
                    } else if (key.isWritable()) {
                        System.out.println("Entering writing phase");
                        sendToServer(key);
                    }
                }

            }
        } catch (Exception e){
            System.out.println("Could not setup ServerConnection properly");
        }
    }

    public void connect(){
        serverAdress = new InetSocketAddress(ADRESS, PORT);
        new Thread(this).start();
    }

    public void initConnection() throws IOException {
        sc = SocketChannel.open();
        sc.configureBlocking(false);
        sc.connect(serverAdress);
        connected = true;
        System.out.println("Connection initialized: Socketchannel has connected to server");
    }

    public void initSelector() throws IOException {
        selector = Selector.open();
        sc.register(selector, SelectionKey.OP_CONNECT);
        System.out.println("Selector has been initialized");
    }

    public void completeConnection(SelectionKey key) throws IOException {
        System.out.println("Trying: completed the Connection to Server");
        sc.finishConnect();
        System.out.println("Why cant we connect?");
        key.interestOps(SelectionKey.OP_WRITE);
        System.out.println("completed the Connection to Server");
    }

    public void recvFromServer(SelectionKey key) throws IOException {
        System.out.println("Waiting on info from server");
        SocketChannel sc = (SocketChannel) key.channel();
        System.out.println("Kommer vi hit? i recv");
        bbIn.clear();
        bbIn = ByteBuffer.allocate(4096);
        System.out.println("recv print 2");
        sc.read(bbIn);
        System.out.println("kan vi läsa från sc");
        String result = new String(bbIn.array()).trim();
        ViewOutput(result);
        /*System.out.println(result);
        System.out.println("Message received from Server: " + result);*/
        bbIn.clear();
        bbOut.clear();
    }

    public void playerInput(String msg){
        this.bbOut.clear();
        this.bbOut = ByteBuffer.wrap(msg.getBytes());
    }

    public void Guess(){
        timeToSend = true;
        selector.wakeup();
    }

    public void sendToServer(SelectionKey key) throws IOException{
        System.out.println("Sending buffer to server");
        SocketChannel sc = (SocketChannel) key.channel();
        sc.write(bbOut);
        bbOut.compact();
        bbOut.clear();
        key.interestOps(SelectionKey.OP_READ);

    }

    public void ViewOutput(String serverOutput){
        String[] output = serverOutput.split("##");
        if(output[0].equals("6")){
            System.out.println("You lost the game, the word was: " +output[4]);
            System.out.println("Current score: " +output[3]);
            System.out.println("TYPE 'PLAY' TO CONTINUE WITH A NEW WORD OR 'QUIT' TO EXIT GAME");
        }
        if(output[0].equals("1")){
            System.out.println("You have started a new game!");
            System.out.println("Current word: " +output[1]);
            System.out.println("Remaining Attempts: " + output[2]);
            System.out.println("Current score: " +output[3]);
        }
        if(output[0].equals("3")){
            System.out.println("You guess correctly!");
            System.out.println("Current word: " +output[1]);
            System.out.println("Remaining Attempts: " + output[2]);
        }
        if(output[0].equals("5")){
            System.out.println("You guess wrong!");
            System.out.println("Current word: " +output[1]);
            System.out.println("Remaining Attempts: " + output[2]);
        }
        if(output[0].equals("4")){
            System.out.println("You won the game, the word was: " +output[4]);
            System.out.println("Current score: " +output[3]);
            System.out.println("TYPE 'PLAY:' TO CONTINUE WITH A NEW WORD OR 'QUIT:' TO EXIT GAME");
        }
    }

}
