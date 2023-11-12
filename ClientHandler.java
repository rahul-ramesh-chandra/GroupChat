import java.util.ArrayList;
import java.io.*;
import java.net.Socket;
public class ClientHandler implements Runnable
{
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;

    public ClientHandler(Socket socket)
    {
        //Initialize the client handler and communication resources
        try 
        {
            this.socket=socket; //initializing the this socket with that socket
            this.bufferedWriter= new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));  //Used to write msg into socket
            this.bufferedReader= new BufferedReader(new InputStreamReader(socket.getInputStream()));    //Used to read msg from socket
            this.clientUsername = bufferedReader.readLine();    //Used to read username from the socket
            clientHandlers.add(this);
            broadcastMessage("\t\tSERVER: "+ clientUsername + " has entered the chat!");
        }
        catch(IOException e)
        {
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }
    public void run()
    {
        // Continuously listen for messages from the client.
        String messageFromClient;
        while(socket.isConnected())
        try{
            messageFromClient = bufferedReader.readLine();
            broadcastMessage(messageFromClient);
        }
        catch(IOException e)
        {
            closeEverything(socket,bufferedReader,bufferedWriter);
            break;
        }
    }
    public void broadcastMessage(String messageToSend)  //Send meassages to everyone except to the client who have sent the message
    {
        //Broadcast meassage to other clients
        for(ClientHandler clientHandler : clientHandlers)
        {
                try{
                if(!clientHandler.clientUsername.equals(clientUsername))
                clientHandler.bufferedWriter.write(messageToSend);
                clientHandler.bufferedWriter.newLine();
                clientHandler.bufferedWriter.flush();
            }
            catch(IOException e)
            {
                closeEverything(socket,bufferedReader,bufferedWriter);
            }
        }
    }
    public void removeClientHandler()
    {
        // Remove the client handler and inform other clients about the departure.
        clientHandlers.remove(this);
        broadcastMessage("\t\tSERVER :"+ clientUsername + " has left the chat");
    }
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter)
    {
        // Close resources and handle the client's departure.
        removeClientHandler();
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
}