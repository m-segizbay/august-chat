package kz.segizbay;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Server {
    private int port;
    private List<ClientHandler> clients;
    private AuthenticationProvider authProvider;

    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<ClientHandler>();
        this.authProvider = new InMemoryAuthProvider();
         try(ServerSocket serverSocket = new ServerSocket(port)){
            System.out.println("Сервер запущен на порту 8189. Ожидаем подключение клиента...");
            while(true){
                Socket socket = serverSocket.accept();
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<ClientHandler> getClients(){
        return this.clients;
    }

    public void broadcastMessage(String message){
        for(ClientHandler client : clients){
            client.sendMessage(message);
        }
    }

    public void subscribe(ClientHandler client){
        clients.add(client);
        sendClientList();
    }

    public void ubsubscribe(ClientHandler client){
        clients.remove(client);
        sendClientList();
    }

    public boolean isUserOnline(String username){
        for(ClientHandler client : clients){
            if (username.equals(client.getUsername())) {
                return true;
            }
        }
        return false;
    }

    public void sendPrivateMsg(ClientHandler client, String receiver, String message){
        for (ClientHandler c : clients){
            if (c.getUsername().equals(receiver)) {
                c.sendMessage("From: " + client.getUsername() + " || Message: " + message);
                client.sendMessage("Receiver: " + receiver + " || Message: " + message);
                return;
            }
        }
        client.sendMessage("Server: Unable to send message to " + receiver);
    }


    public void sendClientList(){
        StringBuilder builder = new StringBuilder("/clients_list ");
        for(ClientHandler c : clients){
            builder.append(c.getUsername()).append(" ");
        }
        builder.setLength(builder.length() - 1);
        String clientList = builder.toString();
        for(ClientHandler c : clients){
            c.sendMessage(clientList);
        }
    }

    public AuthenticationProvider getAuthProvider() {
        return authProvider;
    }
}
