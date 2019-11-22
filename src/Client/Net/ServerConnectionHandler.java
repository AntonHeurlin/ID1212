package Client.Net;


import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class ServerConnectionHandler implements Runnable {
    private static final String PROMPT = ">> ";
    private static BufferedReader input = null;
    private static ByteBuffer fromClientBuffer = ByteBuffer.allocate(4096);
    private static ByteBuffer fromServerBuffer = ByteBuffer.allocate(4096);
    private static Selector selector;
    private static SocketChannel socketChannel;
    private static Boolean connected = false;
    private static final String ADDRESS = "127.0.0.1";
    private static final int PORT = 29019;
    private InetSocketAddress serverAdress;
    private boolean timeToSend = false;

    public void run(){
        try{
            initConnection();
            initSelector();
            while(connected){
                if(timeToSend) {
                    socketChannel.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
                    timeToSend=false;
                }
                selector.select();
                for (SelectionKey key : selector.selectedKeys()) {
                    selector.selectedKeys().remove(key);
                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isConnectable()) {
                        finishConnection(key);
                    } else if (key.isReadable()) {
                        fromServer(key);
                    } else if (key.isWritable()) {
                        ToServer(key);
                    }
                }

            }
            this.disconnect();
        } catch (Exception e){ }
    }

    public void connect(){
        serverAdress = new InetSocketAddress(ADDRESS, PORT);
        new Thread(this).start();
    }

    public void disconnect() throws IOException {
        socketChannel.close();
        socketChannel.keyFor(selector).cancel();
        connected = false;
    }

    public void initConnection() throws IOException {
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(serverAdress);
        connected = true;
    }

    public void initSelector() throws IOException {
        selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
    }

    public void finishConnection(SelectionKey key) throws IOException {
        socketChannel.finishConnect();
        key.interestOps(SelectionKey.OP_WRITE);
    }

    public void fromServer(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        fromClientBuffer.clear();
        fromClientBuffer = ByteBuffer.allocate(4096);
        sc.read(fromClientBuffer);
        String result = new String(fromClientBuffer.array()).trim();
        ViewOutput(result);
        fromClientBuffer.clear();
    }

    public void playerInput(String msg){
        this.fromServerBuffer.clear();
        this.fromServerBuffer = ByteBuffer.wrap(msg.getBytes());
    }

    public void Guess(){
        timeToSend = true;
        selector.wakeup();
    }

    public void ToServer(SelectionKey key) throws IOException{
        SocketChannel sc = (SocketChannel) key.channel();
        sc.write(fromServerBuffer);
        fromServerBuffer.compact();
        fromServerBuffer.clear();
        key.interestOps(SelectionKey.OP_READ);

    }

    public void ViewOutput(String serverOutput){
        String[] output = serverOutput.split("##");
        if(output[0].equals("6")){
            System.out.println("You lost the game, the word was: " +output[4]);
            System.out.println("Current score: " +output[3]);
            System.out.println("TYPE 'PLAY' TO CONTINUE WITH A NEW WORD OR 'QUIT' TO EXIT GAME");
            System.out.print(PROMPT);
        }
        if(output[0].equals("1")){
            System.out.println("You have started a new game, start guessing!");
            System.out.println("Current word: " +output[1]);
            System.out.println("Remaining Attempts: " + output[2]);
            System.out.println("Current score: " +output[3]);
            System.out.print(PROMPT);
        }
        if(output[0].equals("3")){
            System.out.println("You guess correctly!");
            System.out.println("Current word: " +output[1]);
            System.out.println("Remaining Attempts: " + output[2]);
            System.out.print(PROMPT);
        }
        if(output[0].equals("5")){
            System.out.println("You guess wrong!");
            System.out.println("Current word: " +output[1]);
            System.out.println("Remaining Attempts: " + output[2]);
            System.out.print(PROMPT);
        }
        if(output[0].equals("4")){
            System.out.println("You won the game, the word was: " +output[4]);
            System.out.println("Current score: " +output[3]);
            System.out.println("TYPE 'PLAY:' TO CONTINUE WITH A NEW WORD OR 'QUIT:' TO EXIT GAME");
            System.out.print(PROMPT);
        }
    }

}
