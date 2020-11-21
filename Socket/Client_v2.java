package Socket;

import java.net.*;
import java.io.*;
import java.util.*;

public class Client_v2 {
    final String LIST_FILE_ADDR = "C:\\Users\\leejy\\javaworkspace\\Socket\\list.txt";

    BufferedReader  in              = null;
    BufferedWriter  out             = null;
    Socket          socket          = null;
    Scanner         sc              = new Scanner(System.in);
    boolean         isStart         = true;
    String          inputMessage    = null;
    String          outputMessage   = null;
    FileReader      fin             = null;
    FileWriter      fout            = null;

    public Client_v2(){
        Reset();
        while(true){
            if(!send()) break;
            if(!receive()) break;
        }
        close();
    }

    public void Reset(){
        try{
            socket = new Socket("localhost",9999);
            System.out.println("Connection!\n");
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            fout = new FileWriter(LIST_FILE_ADDR);
            fout.close();
        } catch(IOException e){
            System.out.println(e.getMessage());
        }
    }

    public boolean send(){
        try{
            System.out.print("send >> ");
            outputMessage = sc.nextLine();

            if(outputMessage.equalsIgnoreCase("bye")){
                out.write(outputMessage+"\n");
                out.flush();
                return false;
            }
            else if(!isStart && !isPass()){
                System.out.println("\ndifferent word! You Lose");
                System.out.print("Restart? (y,n) ");
                String ans = sc.nextLine();
                if(ans.equals("y")){
                    out.write("restart\n");
                    out.flush();
                    isStart = true;
                    fout = new FileWriter(LIST_FILE_ADDR);
                    fout.close();
                }
                else{
                    out.write("Good game, bye");
                    out.flush();
                    return false;
                }
            }
            else{
                isSimilar();
                Record();
            }
            return true;
        }catch(IOException e){
            System.out.println("Error_Send");
            return false;
        }
    }
    
    public boolean isPass(){
        if(outputMessage.charAt(0) != inputMessage.charAt(inputMessage.length()-1))
            return false;
        else
            return true;
    }

    public void isSimilar(){
        try{
            fin = new FileReader(LIST_FILE_ADDR);
            char[] str = new char[1024];
            fin.read(str);
            String[] word = String.valueOf(str).split("\n");
            for(int i=0;i<word.length;i++){
                if(outputMessage.equals(word[i])){
                    System.out.println("used word, change please..\n");
                    System.out.print("Send >> ");
                    outputMessage = sc.nextLine();
                    break;
                }
            }
            fin.close();
        } catch(IOException e){
            System.out.println("Error_isSimilar");
        }
    }
    
    public void Record(){
        try{
            fout = new FileWriter(LIST_FILE_ADDR,true);
            fout.write(outputMessage+"\n");
            fout.close();
            out.write(outputMessage+"\n");
            out.flush();
        } catch(IOException e){
            System.out.println("Error_Record");
        }
    }

    public boolean receive(){
        try{
            inputMessage = in.readLine();
            if(inputMessage.equalsIgnoreCase("bye")){
                System.out.println("Server exit");
                return false;
            }
            else if(inputMessage.equalsIgnoreCase("Good game, bye")){
                System.out.println("You win, close game");
                return false;
            }
            else if(inputMessage.equalsIgnoreCase("restart")){
                System.out.println("You win, restart game\n");
                isStart = true;
            }
            else{
                System.out.println("Server: "+inputMessage);
                isStart = false;
            }
            return true;
        } catch(IOException e){
            System.out.println("Error_Receive");
            return false;
        }    
    }

    public void close(){
        try{
            sc.close();
            if(socket != null) socket.close();
        } catch(IOException e){
            System.out.println("Error_close");
        }
    }

    public static void main(String[] args){
        new Client_v2();
    }
}
