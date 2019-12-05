package common;

public class MsgIntepreter {

    public String[] Intepret(String msg){
        String[] finalMsg = msg.split("#");
        return finalMsg;
    }
}
