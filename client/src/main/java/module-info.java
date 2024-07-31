module kz.segizbay {
    requires javafx.controls;
    requires javafx.fxml;


    opens kz.segizbay.client to javafx.fxml;
    exports kz.segizbay.client;
}