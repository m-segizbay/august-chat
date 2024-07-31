package kz.segizbay;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerApp {
    public static void main( String[] args ) throws Exception {
        new Server(8189);
    }
}
