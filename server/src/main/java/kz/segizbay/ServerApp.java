package kz.segizbay;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerApp {
    public static void main( String[] args ) throws Exception {
        try(ServerSocket serverSocket = new ServerSocket(8189)){
            System.out.println("Сервер запущен на порту 8189. Ожидаем подключение клиента...");
            Socket socket = serverSocket.accept();
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            System.out.println("Клиент подключился");

            String msg;
            int counter = 0;
            while(true){
                msg = in.readUTF();
                System.out.println(msg);
                if (msg.equals("/stat")){
                    out.writeUTF("Общ количество слов: " + counter);
                } else {
                    out.writeUTF("ECHO: " + msg);
                    counter++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
