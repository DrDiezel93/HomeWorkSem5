package org.Server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientManager implements Runnable{
    private final Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String name;

    public final static ArrayList<ClientManager> clients = new ArrayList<>();

    public ClientManager(Socket socket) {
        this.socket = socket;
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            name = bufferedReader.readLine();
            clients.add(this);
            System.out.println(name + " подключился к чату.");
            broadcastMessage("Server: " + name + " подключился к чату.");
        }
        catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }


    }

    @Override
    public void run() {
        String massageFromClient;
        while (socket.isConnected()) {
            try {
                massageFromClient = bufferedReader.readLine();
                if(CheckSymbolPres(massageFromClient)){
                    if(!clientMessage(ReturnMessage(massageFromClient), ReturnName(massageFromClient))){
                        clientError();
                    }
                }
                else{
                    broadcastMessage(massageFromClient);
                }
            }
            catch (IOException e){
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    private void broadcastMessage(String message){
        for (ClientManager client: clients) {
            try {
                if (!client.name.equals(name)) {
                    client.bufferedWriter.write(message);
                    client.bufferedWriter.newLine();
                    client.bufferedWriter.flush();
                }
            }
            catch (IOException e){
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }


    private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        // Удаление клиента из коллекции
        removeClient();
        try {
            // Завершаем работу буфера на чтение данных
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            // Завершаем работу буфера для записи данных
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            // Закрытие соединения с клиентским сокетом
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeClient(){
        clients.remove(this);
        System.out.println(name + " покинул чат.");
        broadcastMessage("Server: " + name + " покинул чат.");
    }

    private boolean CheckSymbolPres(String message){
        String[] words = message.split(" ");
        int indexM = words[1].indexOf("@");
        return indexM == 0;
    }

    private String ReturnName(String message){
        String[] words = message.split(" ");
        String str = words[1].substring(1);
        return str;
    }

    private String ReturnMessage(String message){
        String[] words = message.split(" ");
        String newStr = message.replaceAll(words[1] + " ", "");
        return newStr;
    }

    private boolean clientMessage(String message, String str) {
        for (ClientManager client : clients) {
            try {
                if (!client.name.equals(name) & client.name.equals(str)) {
                    client.bufferedWriter.write(message);
                    client.bufferedWriter.newLine();
                    client.bufferedWriter.flush();
                    return true;
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }return false;
    }

    private void clientError() {
        for (ClientManager client : clients) {
            try {
                if (client.name.equals(name)) {
                    client.bufferedWriter.write("Такого имени не существует");
                    client.bufferedWriter.newLine();
                    client.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    private void clientError2() {
        for (ClientManager client : clients) {
            try {
                if (client.name.equals(name)) {
                    client.bufferedWriter.write("Ваше личное сообщение доставлено");
                    client.bufferedWriter.newLine();
                    client.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }
}
