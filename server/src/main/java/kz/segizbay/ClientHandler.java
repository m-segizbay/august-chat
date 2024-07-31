package kz.segizbay;

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
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        System.out.println("Client connected");

        new Thread(() -> {
            try {
                while(true){
                    String msg = in.readUTF();
                    if (msg.startsWith("/login ")){
                        username = msg.split("\\s+")[1];
                        sendMessage("/login_ok " + username);
                        break;
                    }
                }

                while(true){
                    String msg = in.readUTF();
                    server.broadcastMessage(username + ": " + msg);
                }
            } catch (IOException e){
                e.printStackTrace();
            } finally {
                disconnect();
            }
        }).start();
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
}
