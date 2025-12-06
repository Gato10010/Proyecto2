package A_Inicio;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

// ESTE CÓDIGO ESTÁ SIMPLIFICADO SOLO PARA QUE EL PROYECTO COMPILE
public class D_SubUsuario_Controller implements Initializable {

    @FXML private TextField txtNombre;
    @FXML private TextField txtApellidoPaterno;
    @FXML private TextField txtApellidoMaterno;
    @FXML private TextField txtEdad;
    @FXML private TextField txtSexo;
    @FXML private TextField txtTelefono;
    @FXML private ComboBox<String> cmbCategoria;
    @FXML private Button btnGuardar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    

    @FXML
    private void guardarPersonal(ActionEvent event) {
        // Pendiente
    }
}