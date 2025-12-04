package A_Inicio;

// 1. IMPORTANTE: Si esta línea sale roja, borra ".ConexionDB" y vuelve a escribirlo para que NetBeans te ayude.
import A_Logica_Y_Metodos.ConexionDB; 

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// 2. Aquí está la clave: El nombre coincide con el archivo B_Login_Controller
public class B_Login_Controller {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasena;

    // 3. Esta función coincide con tu FXML (onAction="#iniciarSesion")
    @FXML
    private void iniciarSesion(ActionEvent event) {
        String usuario = txtUsuario.getText();
        String password = txtContrasena.getText();

        if (usuario.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Llena los campos por favor.");
            return;
        }

        if (validarLogin(usuario, password)) {
            JOptionPane.showMessageDialog(null, "¡Bienvenido " + usuario + "!");
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
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }
}