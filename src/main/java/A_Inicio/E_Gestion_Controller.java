package A_Inicio;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class E_Gestion_Controller implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    
    
    @FXML
    public void cerrarsesion()throws IOException {
        App.setRoot("/B_Escenas/B_Login.fxml");
    }
    
    @FXML
    public void irusuarios()throws IOException {
        App.setRoot("/B_Escenas/D_Usuarios.fxml");
    }
    
    @FXML
    public void irinventario()throws IOException {
        App.setRoot("/B_Escenas/C_Inventario.fxml");
    }
}
