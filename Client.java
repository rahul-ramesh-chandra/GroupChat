import java.net.Socket;
import java.util.*;
import java.io.*;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    public Client(Socket socket,String username)    // Initialize client and establish a connection to the server.
    {
        try{
            this.socket=socket; //initialize the socket
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); //Write into the socket
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));   //Read from the socket
            this.username = username;   //initialize the username 
        }
        catch(IOException e)
        {
            closeEverythingh(socket,bufferedReader,bufferedWriter);
        }
    }
    public void sendMessage()   
    {
        // Send the client's username to the server
        try{
           
            //Continuously send messages to the server
            try (Scanner scanner = new Scanner(System.in)) {    //Writing the message into the socket
                while(socket.isConnected())
                {
                    String messageToSend = scanner.nextLine();
                    bufferedWriter.write(">"+username+":"+messageToSend);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }
            }
        }
            catch(IOException e)
            {
                closeEverythingh(socket,bufferedReader,bufferedWriter);
            }
    }
    public void listenForMessage()
    {
        //Separate thread to listen the messages from server and other clients
        new Thread(new Runnable(){
            public void run()
            {
                String msgFromGroupChat;
                while(socket.isConnected())
                {
                    try{
                        //Continously listen for incoming messages
                        msgFromGroupChat= bufferedReader.readLine();    //message from socket is read and displayed
                        System.out.println(msgFromGroupChat);
                    }
                    catch(IOException e)
                    {
                        closeEverythingh(socket,bufferedReader,bufferedWriter);
                    }
                }
            }
        }).start();
    }
    public void closeEverythingh(Socket socket, BufferedReader bufferedReader,BufferedWriter bufferedWriter)
    {
        try{
            if(bufferedReader != null)
            bufferedReader.close();
            if(bufferedWriter != null)
            bufferedWriter.close();
            if(socket != null)
            socket.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    public static void main(String args[]) throws IOException
    {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Enter your username for the group chat: ");
            String username = scanner.nextLine();
            Socket socket = new Socket("localhost",1234);
            Client client = new Client(socket,username);
            client.listenForMessage();
            client.sendMessage();
        }
    }
}