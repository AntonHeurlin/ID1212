package Server;

import Server.Controller.Controller;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class HangmanServer {
    private static ByteBuffer fromClientBuffer = ByteBuffer.allocate(4096);
    private static ByteBuffer outToClientBuffer = ByteBuffer.allocate(4096);
    private static Selector selector;
    private static ServerSocketChannel serverSocketChannel;
    //private static boolean serverRunning = false;

    public static void main(String[] args){
        HangmanServer server = new HangmanServer();
        server.ConnectionHandler();
    }

    public void ConnectionHandler(){
        try{
            startServer();
            while (true) {
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while(iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if(!key.isValid()) {
                        continue;
                    }
                    if(key.isAcceptable()) {
                        setupClient(key);
                    }else if(key.isReadable()){
                        fromClient(key);
                    }else if(key.isWritable()) {
                        System.out.println("OP_WRITE");
                        toClient(key);
                    }
                }
            }
        }catch (Exception e){
            System.out.println(e);
            System.out.println("Fault during ClientHandler process");
        }
    }

    private static void startServer() throws IOException {
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress("127.0.0.1", 29019));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("ServerStartUp succesful!");
    }

    public void setupClient(SelectionKey key) throws IOException {
        System.out.println("Server attempting to setup ClientHandler");
        SocketChannel clientSocketChannel = serverSocketChannel.accept();
        clientSocketChannel.configureBlocking(false);
        Controller controller = new Controller(clientSocketChannel);
        clientSocketChannel.register(selector, SelectionKey.OP_READ, controller);
        System.out.println("ClientHandlerSetup successful!");
    }

    public void fromClient(SelectionKey key) throws IOException {
        System.out.println("Kommer vi till fromClient?");
        SocketChannel clientSocketChannel = (SocketChannel) key.channel();
        Controller clientController = (Controller) key.attachment();
        System.out.println("Kommer vi åt kontroller?");
        ByteBuffer fromClientBuffer = ByteBuffer.allocate(4096);
        this.outToClientBuffer = ByteBuffer.allocate(4096);
        if(clientSocketChannel.read(fromClientBuffer) != -1){
            System.out.println("fromclient buffer ej tom");
            clientSocketChannel.read(fromClientBuffer);
            String result = new String(fromClientBuffer.array()).trim();
            System.out.println("client input: " +result);
            String gameResult = clientController.run(result);
            System.out.println("gameResult = " +gameResult);
            System.out.println(gameResult);
            outToClientBuffer = ByteBuffer.wrap(gameResult.getBytes());
            System.out.println("GameState wrote: "+gameResult);
            fromClientBuffer.compact();
            fromClientBuffer.clear();
            key.interestOps(SelectionKey.OP_WRITE);
            System.out.println("Entering OP_WRITE");
        }else{

            fromClientBuffer.clear();
            clientSocketChannel.close();
        }

    }

    private static void toClient(SelectionKey key) throws Exception{
        SocketChannel client = (SocketChannel) key.channel();
        String result = new String(outToClientBuffer.array()).trim();
        System.out.println(result);
        String msg = "Hello this is server " + result;
        System.out.println(msg);
        ByteBuffer toClient = ByteBuffer.allocate(4096);
        toClient = ByteBuffer.wrap(result.getBytes());
        client.write(toClient);
        toClient.compact();
        toClient.clear();
        key.interestOps(SelectionKey.OP_READ);
    }

}
