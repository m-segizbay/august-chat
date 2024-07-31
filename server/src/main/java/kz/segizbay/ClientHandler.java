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

    public ClientHandler(Server server,Socket socket) throws IOException {
        this.socket = socket;
        this.server = server;
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        System.out.println("Client connected");

        new Thread(() -> {
            try {
                while(true){
                    String msg = in.readUTF();
                    server.broadcastMessage(msg);
                }
            } catch (IOException e){
                e.printStackTrace();
            } finally {
                disconnect();
            }
        }).start();
    }

    public void sendMessage(String msg) throws IOException {
        out.writeUTF(msg);
    }

    public void disconnect(){
        server.unsubscribe(this);
        if (socket != null){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
