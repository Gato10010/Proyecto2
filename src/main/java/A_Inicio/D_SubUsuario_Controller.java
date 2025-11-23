package A_Inicio;

import A_Logica_Y_Metodos.PersonalDAO;
import A_Logica_Y_Metodos.UsuarioDAO;
import E_Modelos.Personal;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controlador para la escena D_SubUsuario.fxml (Formulario de Registro).
 * Esta es la versión REVERTIDA (simple).
 * 1. Guarda el registro en la tabla 'personal'.
 * 2. Si la categoría no es "Otros", crea una cuenta en la tabla 'usuarios'.
 */
public class D_SubUsuario_Controller implements Initializable {

    // --- Vinculación FXML ---
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtApellidoPaterno;
    @FXML
    private TextField txtApellidoMaterno;
    @FXML
    private TextField txtEdad;
    @FXML
    private TextField txtSexo;
    @FXML
    private TextField txtTelefono;
    @FXML
    private ComboBox<String> cmbCategoria;
    @FXML
    private Button btnGuardar;

    // DAOs para manejar la lógica de negocio
    private PersonalDAO personalDAO;
    private UsuarioDAO usuarioDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.personalDAO = new PersonalDAO();
        this.usuarioDAO = new UsuarioDAO();
        
        // Carga las categorías en el ComboBox
        cmbCategoria.setItems(FXCollections.observableArrayList(
            "Administrador", "Almacenista", "Vendedor", "Otros"
        ));
    }    

    /**
     * Método llamado por el onAction del botón "Guardar"
     */
    @FXML
    private void guardarPersonal(ActionEvent event) {
        
        try {
            // 1. Validar y obtener datos de los campos
            String nombre = txtNombre.getText();
            String apPaterno = txtApellidoPaterno.getText();
            String apMaterno = txtApellidoMaterno.getText();
            String edadStr = txtEdad.getText();
            String sexo = txtSexo.getText();
            String telefono = txtTelefono.getText();
            String categoria = cmbCategoria.getValue();

            // Validación simple
            if (nombre.isEmpty() || apPaterno.isEmpty() || edadStr.isEmpty() || categoria == null || telefono.isEmpty()) {
                mostrarAlerta(AlertType.ERROR, "Error de validación", "Todos los campos (excepto Ap. Materno y Sexo) son obligatorios.");
                return;
            }

            int edad = Integer.parseInt(edadStr);

            // 2. Crear el objeto Personal
            Personal nuevoPersonal = new Personal(nombre, apPaterno, apMaterno, edad, sexo, telefono, categoria);

            // 3. LLAMAR AL PersonalDAO para guardar
            boolean exitoPersonal = personalDAO.guardarPersonal(nuevoPersonal);
            
            if (!exitoPersonal) {
                mostrarAlerta(AlertType.ERROR, "Error al Guardar", "No se pudo guardar el registro de personal.");
                return;
            }

            // 4. Si se guardó el personal, AHORA crear el usuario (si aplica)
            boolean exitoUsuario = true; // Asumir éxito si la categoría es "Otros"
            
            if (!categoria.equals("Otros")) {
                // El 'Usuario' será el Nombre y la 'Contraseña' será el Teléfono
                exitoUsuario = usuarioDAO.crearUsuario(nombre, telefono, categoria);
            }

            // 5. Mostrar retroalimentación final
            if (exitoUsuario) {
                mostrarAlerta(AlertType.INFORMATION, "Registro Exitoso", "El nuevo personal se ha guardado correctamente.");
                closeWindow(); // Cierra la ventana emergente
            } else {
                mostrarAlerta(AlertType.ERROR, "Error al Guardar Usuario", "Se guardó el personal, pero no se pudo crear su cuenta de login (quizás el usuario ya existe).");
            }

        } catch (NumberFormatException e) {
            mostrarAlerta(AlertType.ERROR, "Error de Formato", "El campo 'Edad' solo acepta números.");
        }
    }

    // Método auxiliar para mostrar alertas
    private void mostrarAlerta(AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    // Método para cerrar la ventana emergente
    private void closeWindow() {
        // Obtiene el 'Stage' (ventana) actual desde el botón
        Stage stage = (Stage) btnGuardar.getScene().getWindow();
        stage.close();
    }
}
