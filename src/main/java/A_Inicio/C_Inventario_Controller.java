package A_Inicio; // O el nombre correcto de tu paquete

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class C_Inventario_Controller implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private void cerrarsesion() throws IOException {
        App.setRoot("/B_Escenas/B_Login.fxml");
    }

    @FXML
    private void addProduct(ActionEvent event) { // Añade (ActionEvent event) aquí
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/C_SubEscenas/C_SubInventario.fxml"));
            Parent root = loader.load();
            Stage popupStage = new Stage();
            popupStage.setTitle("Agregar Nuevo Producto");
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setResizable(false);
            Scene popupScene = new Scene(root);
            popupScene.getStylesheets().add(getClass().getResource("/E_CSS/Estilo_C_Sub.css").toExternalForm());
            popupStage.setScene(popupScene);
            popupStage.showAndWait(); 

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}