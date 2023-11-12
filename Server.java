import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server
{
    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket)
    {
        this.serverSocket = serverSocket;
    }
    public void startServer()
    {
        try
        {
            while(!serverSocket.isClosed())
            {
                Socket socket = serverSocket.accept();  //Waiting for client socket to connect with server socket.
                System.out.println("\t\tA new client has connected");
                ClientHandler clientHandler = new ClientHandler(socket);    //Reference to the client handler class 
                Thread thread = new Thread(clientHandler);  //Creating a new thread for client handler reference
                thread.start();     //starting the thread
            }
        }
        catch(IOException e)
        {
            System.err.println("\t\tCould not listen on port: 1234 " +e.getMessage());
        }
    }
    public void clientServer() 
    {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) throws IOException
    {
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();
        server.clientServer();
    }
}