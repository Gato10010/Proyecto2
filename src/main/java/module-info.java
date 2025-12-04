module Inicio {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.sql;
    requires mysql.connector.java;

    requires java.desktop;  // <--- Esta es la clave para que funcione JOptionPane

    opens A_Inicio to javafx.fxml;
    opens A_Logica_Y_Metodos to javafx.fxml;
    opens E_Modelos to javafx.fxml;

    exports A_Inicio;
}