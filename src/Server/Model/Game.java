package Server.Model;



import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Game {
    public File wordFile = new File("C:\\Users\\Anton\\git\\HangmanServer2\\src\\HangmanModel\\words.txt");
    private ArrayList<String> words;

    private String word = "";
    private char[] wordCensor;
    private String displayWord;
    private char[] guessWord;
    public int attempts;
    public int score;

    public Game() throws IOException {
        this.words = this.WordList();
        this.NewGame();
        this.score = 0;
    }

    private char[] Censor(int length){
        char[] wordCensor = new char[length];
        for(int i = 0; i < length; i++){
            wordCensor[i] = '-';
        }
        return wordCensor;
    }
    private ArrayList<String> WordList() throws IOException{

        Scanner s = new Scanner(wordFile);

        ArrayList<String> wordList = new ArrayList<String>();

        while(s.hasNextLine()){
            wordList.add(s.nextLine());
        }
        return wordList;

    }

    public String UncensorWord() {
        return this.word;
    }

    public String GetWord() {
        return this.displayWord;
    }

    public int GetScore() {
        return this.score;
    }

    public int GetAttempts() {
        return this.attempts;
    }

    public void NewGame() {
        this.word = this.words.get((int)(this.words.size() * Math.random())).toUpperCase();
        System.out.println(this.word);
        this.wordCensor = this.Censor(this.word.length());
        this.displayWord = String.copyValueOf(this.wordCensor);
        this.guessWord = new char[this.word.length()];
        this.attempts = word.length();
        System.out.println("Attempts given: " +this.attempts);
    }

    public int PlayerInput(String arg) {
        System.out.println("We have collected input");
        System.out.println("This is the player input: " +arg);
        if(arg.length() > 1){
            if(arg.equals(this.word)){
                System.out.println("Gissade rätt på hela ordet");
                this.score++;
                System.out.println(this.score);
                this.displayWord = String.copyValueOf(this.wordCensor);
                return 4;
            }
            else{
                System.out.println("Gissade fel på hela ordet");
                this.attempts--;
                if(this.attempts > 0) { return 5; }
                else {
                    this.score--;
                    return 6;
                }
            }
        }
        else{
            while(this.attempts>0){
                char x = arg.charAt(0);
                System.out.println("Char x: " +x);
                if(this.word.contains(x+"")){
                    for(int y = 0; y < this.word.length(); y++){
                        if(this.word.charAt(y) == x){
                            this.wordCensor[y] = x;
                            System.out.println("Gissade rätt bokstav");

                        }
                    }
                    this.displayWord = String.copyValueOf(this.wordCensor);
                    return 3;
                }
                else{
                    System.out.println("Gissade fel bokstav");
                    this.attempts--;
                    System.out.println(this.attempts);
                    if(this.attempts < 1){
                        this.score--;
                        return 6;
                    }
                }
                if(this.word.equals(String.valueOf(this.wordCensor))){
                    System.out.println("Gissade rätt bokstav och fick hela ordet");
                    this.score++;
                    System.out.println(this.score);
                    return 4;
                }
                else{
                    System.out.println("Gissade felbokstav");
                    return 5;
                }
            }
            System.out.println("While loopen är slut, attempts 0");
            if(this.attempts==0){
                System.out.println("Förlorat spelet");
                return 6;
            }
        }
        System.out.println("Ingen if-sats");
        return 3;
    }
}
