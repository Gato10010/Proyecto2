package A_Inicio;

import A_Logica_Y_Metodos.ConexionDB;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javax.swing.JOptionPane;

public class B_Login_Controller {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasena;

    @FXML
    private void iniciarSesion(ActionEvent event) {
        String usuario = txtUsuario.getText();
        String password = txtContrasena.getText();

        if (usuario.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Escribe usuario y contraseña.");
            return;
        }

        if (validarLogin(usuario, password)) {
            JOptionPane.showMessageDialog(null, "¡Bienvenido " + usuario + "!");

            try {
                // --- BUSCADOR INTELIGENTE DE ARCHIVOS ---
                // Intentamos buscar el archivo en las dos carpetas posibles
                URL url = getClass().getResource("/B_Escenas/E_Gestion.fxml"); // Opción 1
                
                if (url == null) {
                    System.out.println("No estaba en B_Escenas, buscando en A_Inicio...");
                    url = getClass().getResource("/A_Inicio/E_Gestion.fxml"); // Opción 2
                }

                // Si después de buscar en los dos lados sigue siendo null, es un error real
                if (url == null) {
                    String errorMsg = "CRÍTICO: No se encuentra 'E_Gestion.fxml' ni en A_Inicio ni en B_Escenas.";
                    System.out.println(errorMsg);
                    JOptionPane.showMessageDialog(null, errorMsg + "\nAsegúrate de haber hecho 'Clean and Build'.");
                    return;
                }

                System.out.println("¡Archivo encontrado en!: " + url); // Esto saldrá en la consola
                
                FXMLLoader loader = new FXMLLoader(url);
                Parent root = loader.load();

                // Cerrar Login
                javafx.scene.Node source = (javafx.scene.Node) event.getSource();
                Stage stageActual = (Stage) source.getScene().getWindow();
                stageActual.close();

                // Abrir Menú
                Stage stageMenu = new Stage();
                stageMenu.setScene(new Scene(root));
                stageMenu.setTitle("Sistema de Gestión");
                stageMenu.show();
                
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al abrir menú: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos.");
        }
    }

    private boolean validarLogin(String user, String pass) {
        Connection con = ConexionDB.conectar();
        if (con == null) return false;
        String sql = "SELECT * FROM usuarios WHERE usuario = ? AND password = ?";
        try {
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, user);
            pst.setString(2, pass);
            ResultSet rs = pst.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.out.println("Error SQL: " + e.getMessage());
            return false;
        }
    }
}