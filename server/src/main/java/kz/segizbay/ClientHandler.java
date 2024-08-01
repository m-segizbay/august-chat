package kz.segizbay;

import org.w3c.dom.ls.LSOutput;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private Server server;
    private String username;


    public ClientHandler(Server server, Socket socket) throws IOException {
        this.socket = socket;
        this.server = server;
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        System.out.println("Client connected");

        new Thread(() -> {
            try {
                while(true){
                    String msg = in.readUTF();
                    if (msg.startsWith("/login ")){
                        String usernameFromClient = msg.split("\\s+")[1];
                        if (server.isUserOnline(usernameFromClient)){
                            sendMessage("/login_failed this username is already in use: " + usernameFromClient);
                            continue;
                        }
                        username = usernameFromClient;
                        sendMessage("/login_ok " + username);
                        server.subscribe(this);
                        break;
                    }
                }

                while(true){
                    String msg = in.readUTF();
                    if (msg.startsWith("/")){
                        executeCmd(msg);
                        continue;
                    }
                    server.broadcastMessage(username + ": " + msg);
                }
            } catch (IOException e){
                e.printStackTrace();
            } finally {
                disconnect();
            }
        }).start();
    }

    public String getUsername() {
        return username;
    }

    public void sendMessage(String msg){
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void disconnect(){
        server.ubsubscribe(this);
        if (socket != null){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void executeCmd(String msg){
        if (msg.startsWith("/p ")){
            String[] tokens = msg.split("\\s+", 3);
            if (tokens[1].equals(username)){
                sendMessage("Its your username!!!");
                return;
            }
            server.sendPrivateMsg(this, tokens[1], tokens[2]);
            return;
        }

    }
}
