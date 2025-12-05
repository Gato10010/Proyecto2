package A_Inicio;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javax.swing.JOptionPane;

public class E_Gestion_Controller implements Initializable {

    @FXML private Button btncerrarsesion;
    @FXML private Button btnPersonal;
    @FXML private Button btnInventario;
    @FXML private Button btnVentas;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicialización
    }

    @FXML
    private void cerrarSesion(ActionEvent event) {
        // Regresar al Login (que está en B_Escenas según tus fotos)
        abrirVentana(event, "/B_Escenas/B_Login.fxml", "Login");
    }

    @FXML
    private void abrirPersonal(ActionEvent event) {
        abrirVentana(event, "/B_Escenas/D_Usuarios.fxml", "Gestión de Personal");
    }

    @FXML
    private void abrirInventario(ActionEvent event) {
        abrirVentana(event, "/B_Escenas/C_Inventario.fxml", "Gestión de Inventario");
    }

    @FXML
    private void abrirVentas(ActionEvent event) {
        abrirVentana(event, "/B_Escenas/F_Ventas.fxml", "Punto de Venta");
    }

    private void abrirVentana(ActionEvent event, String rutaFXML, String titulo) {
        try {
            URL url = getClass().getResource(rutaFXML);
            if (url == null) {
                JOptionPane.showMessageDialog(null, "No se encuentra: " + rutaFXML);
                return;
            }
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.show();
            
            ((Node) event.getSource()).getScene().getWindow().hide();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al abrir ventana: " + e.getMessage());
        }
    }
}