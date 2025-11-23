package A_Inicio;

import A_Logica_Y_Metodos.UsuarioDAO; // Asegúrate de que esta sea la ruta correcta a tu DAO
import E_Modelos.Usuario;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class B_Login_Controller implements Initializable {

    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtContrasena;
    @FXML
    private Button btnIniciarSesion;

    private UsuarioDAO usuarioDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.usuarioDAO = new UsuarioDAO();
    }    

    @FXML
    private void iniciarSesion(ActionEvent event) {
        String usuario = txtUsuario.getText();
        String contrasena = txtContrasena.getText();

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            mostrarAlerta("Error", "Los campos no pueden estar vacíos.");
            return;
        }

        Usuario usuarioLogueado = usuarioDAO.validarUsuario(usuario, contrasena);

        if (usuarioLogueado != null) {
            // ¡Login exitoso! Redirigir según la categoría
            System.out.println("Login exitoso como: " + usuarioLogueado.getCategoria());

            try {
                String fxmlCargar;

                // --- LÓGICA DE CATEGORÍAS ---
                switch (usuarioLogueado.getCategoria()) {
                    case "Administrador":
                        // El Administrador ahora va a la escena de Gestión
                        fxmlCargar = "/B_Escenas/E_Gestion.fxml";
                        break;
                        
                    case "Almacenista":
                        // Los almacenistas ven el inventario
                        fxmlCargar = "/B_Escenas/C_Inventario.fxml";
                        break;
                        
                    case "Vendedor":
                        // Los vendedores verán una escena de "Ventas" (debes crearla)
                        // fxmlCargar = "/B_Escenas/D_Ventas.fxml";
                        System.out.println("Acceso de Vendedor. Cargando vista temporal...");
                        fxmlCargar = "/B_Escenas/C_Inventario.fxml"; // Temporal
                        break;
                        
                    case "Otros":
                    default:
                        // Si es "Otros" o una categoría no reconocida
                        mostrarAlerta("Acceso denegado", "No tienes permisos asignados.");
                        return; // Detiene la ejecución
                }
                
                // Carga el FXML determinado
                App.setRoot(fxmlCargar);

            } catch (IOException e) {
                mostrarAlerta("Error", "No se pudo cargar la siguiente escena.");
            }
        } else {
            // Si 'usuarioLogueado' es null, el login falló
            mostrarAlerta("Login fallido", "Usuario o contraseña incorrectos.");
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

