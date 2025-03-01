package kz.segizbay.client;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private TextField msgField, loginField;

    @FXML
    private TextArea msgArea;

    @FXML
    private HBox textBox, loginBox;

    @FXML
    private ListView<String> clientList;

    @FXML
    private PasswordField passwordField;


    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String username;


    public void setUsername(String username){
        this.username = username;
        if (this.username == null){
            loginBox.setVisible(true);
            loginBox.setManaged(true);
            textBox.setVisible(false);
            textBox.setManaged(false);
            clientList.setVisible(false);
            clientList.setManaged(false);
        } else {
            loginBox.setVisible(false);
            loginBox.setManaged(false);
            textBox.setVisible(true);
            textBox.setManaged(true);
            clientList.setVisible(true);
            clientList.setManaged(true);
        }
    }


    public void sendMsg(){
        try {
            out.writeUTF(msgField.getText());
            msgField.clear();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Невозможно отправить сообщение");
            alert.showAndWait();
        }
    }

    public void connect(){
        try {
            socket = new Socket("localhost", 8189);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> {
                try {
                    // Цикл авторизации
                    while (true){
                        String msg = in.readUTF();
                        if (msg.startsWith("/login_ok ")){
                            setUsername(msg.split("\\s+")[1]);
                            msgArea.clear();
                            break;
                        }
                        if (msg.startsWith("/login_failed ")){
                            String reason = msg.split("\\s+",2)[1];
                            msgArea.appendText(reason + "\n");
                        }
                    }

                    // Цикл общения
                    while(true){
                        String msg = in.readUTF();
                        if (msg.startsWith("/clients_list ")){
                            Platform.runLater(() -> {
                                clientList.getItems().clear();
                                String[] tokens = msg.split("\\s+");
                                for (int i = 1; i < tokens.length; i++) {
                                    clientList.getItems().add(tokens[i]);
                                }
                            });
                            continue;
                        }
                        msgArea.appendText(msg+"\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    disconnect();
                }
            }).start();

        } catch (IOException e){
            e.printStackTrace();
            throw new RuntimeException("Unable to connect to server");
        }
    }

    public void login(){
        if (socket == null || socket.isClosed()){
            connect();
        }

        if (loginField.getText().equals(" ")){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Имя пользователя не может быть пустым");
            alert.showAndWait();
            return;
        }

        try {
            out.writeUTF("/login " + loginField.getText() + " " + passwordField.getText());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void disconnect(){
        setUsername(null);
        if (socket != null){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUsername(null);
    }
}