package A_Inicio;

// Importamos TU clase de conexión correcta
import A_Inicio.Azz_Conexion; 

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

        // 1. OBTENER EL ROL (En lugar de solo validar)
        String rolDetectado = obtenerRol(usuario, password);

        if (rolDetectado != null) { 
            // Si rolDetectado NO es nulo, significa que el login fue exitoso
            JOptionPane.showMessageDialog(null, "¡Bienvenido " + usuario + "! \nRol: " + rolDetectado);

            try {
                // 2. ELEGIR EL ARCHIVO FXML SEGÚN EL ROL
                String archivoDestino = "";
                String tituloVentana = "";

                switch (rolDetectado) {
                    case "Almacenista": // Ejemplo: Isabel
                        archivoDestino = "C_Inventario.fxml";
                        tituloVentana = "Gestión de Recursos";
                        break;
                        
                    case "Vendedor": // Ejemplo: Lucia
                        archivoDestino = "F_Ventas.fxml";
                        tituloVentana = "Punto de Venta";
                        break;
                        
                    case "Administrador": // Ejemplo: Juan
                    case "Gerente":       // Ejemplo: Maria
                    default:              // Por defecto al menú completo
                        archivoDestino = "E_Gestion.fxml";
                        tituloVentana = "Menú Principal";
                        break;
                }

                // 3. BUSCADOR INTELIGENTE (Adaptado a tu código)
                // Buscamos el archivoDestino que elegimos arriba
                URL url = getClass().getResource("/B_Escenas/" + archivoDestino); 
                
                if (url == null) {
                    System.out.println("No estaba en B_Escenas, buscando en A_Inicio...");
                    url = getClass().getResource("/A_Inicio/" + archivoDestino);
                }

                if (url == null) {
                    String errorMsg = "CRÍTICO: No se encuentra '" + archivoDestino + "' en ninguna carpeta.";
                    JOptionPane.showMessageDialog(null, errorMsg);
                    return;
                }

                // 4. ABRIR LA VENTANA
                System.out.println("Abriendo: " + url);
                FXMLLoader loader = new FXMLLoader(url);
                Parent root = loader.load();

                // Cerrar Login
                javafx.scene.Node source = (javafx.scene.Node) event.getSource();
                Stage stageActual = (Stage) source.getScene().getWindow();
                stageActual.close();

                // Abrir la nueva ventana
                Stage stageMenu = new Stage();
                stageMenu.setScene(new Scene(root));
                stageMenu.setTitle(tituloVentana);
                stageMenu.show();
                
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al abrir ventana: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos.");
        }
    }

    // Este método ahora devuelve el ROL (String) en vez de true/false
    private String obtenerRol(String user, String pass) {
        // Usamos tu clase Azz_Conexion
        Connection con = Azz_Conexion.getConnection();
        
        if (con == null) return null;
        
        // Pedimos el campo 'rol' a la base de datos
        String sql = "SELECT rol FROM usuarios WHERE usuario = ? AND password = ?";
        try {
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, user);
            pst.setString(2, pass);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                // Devolvemos el texto que hay en la columna rol (ej: "Vendedor")
                return rs.getString("rol");
            } else {
                return null; // No se encontró usuario
            }
        } catch (Exception e) {
            System.out.println("Error SQL: " + e.getMessage());
            return null;
        }
    }
}