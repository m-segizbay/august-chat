package kz.segizbay;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;


    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        System.out.println("Client connected");

        new Thread(() -> {
            System.out.println("111");
            int counter = 0;
            try {
                while(true){
                    String msg = in.readUTF();
                    System.out.println(msg + "\n");
                    if (msg.equals("/stat")){
                        out.writeUTF("Общ количество слов: " + counter);
                    } else {
                        out.writeUTF("ECHO: " + msg);
                        counter++;
                    }
                }
            } catch (IOException e){
                e.printStackTrace();
            } finally {
                disconnect();
            }
        }).start();
    }

    public void disconnect(){
        if (socket != null){
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
