package kz.segizbay;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private int port;
    private List<ClientHandler> clients;

    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<ClientHandler>();
         try(ServerSocket serverSocket = new ServerSocket(port)){
            System.out.println("Сервер запущен на порту 8189. Ожидаем подключение клиента...");
            while(true){
                Socket socket = serverSocket.accept();
                subscribe(new ClientHandler(this, socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcastMessage(String message){
        for(ClientHandler client : clients){
            client.sendMessage(message);
        }
    }

    public void subscribe(ClientHandler client){
        clients.add(client);
    }

    public void ubsubscribe(ClientHandler client){
        clients.remove(client);
    }
}
