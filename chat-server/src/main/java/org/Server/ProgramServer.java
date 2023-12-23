package org.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.rmi.UnknownHostException;

public class ProgramServer {
    public static void main(String[] args) {
        try
        {
            ServerSocket serverSocket = new ServerSocket(1400);
            Server server = new Server(serverSocket);
            server.runServer();
        }
        catch (UnknownHostException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}