package A_Inicio;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class F_Cobro_Controller implements Initializable {

    @FXML private Label lblTotal;
    @FXML private TextField txtRecibido;
    @FXML private Label lblCambio;
    @FXML private Button btnFinalizar;

    private double totalPagar = 0.0;
    private double cambioCalculado = 0.0;
    private F_Ventas_Controller ventasController; // Referencia a la ventana anterior

    @Override
    public void initialize(URL url, ResourceBundle rb) { }
    
    // Método que llama la ventana de ventas para pasarnos la info
    public void initData(double total, F_Ventas_Controller mainController) {
        this.totalPagar = total;
        this.ventasController = mainController;
        
        lblTotal.setText(String.format("$ %.2f", totalPagar));
        txtRecibido.setText("");
        txtRecibido.requestFocus();
    }

    @FXML
    private void calcularCambio(KeyEvent event) {
        try {
            String texto = txtRecibido.getText();
            if(texto.isEmpty()) { 
                lblCambio.setText("$ 0.00"); 
                return; 
            }
            
            double recibido = Double.parseDouble(texto);
            cambioCalculado = recibido - totalPagar;
            lblCambio.setText(String.format("$ %.2f", cambioCalculado));
            
            if(cambioCalculado < 0) {
                lblCambio.setStyle("-fx-text-fill: red;");
            } else {
                lblCambio.setStyle("-fx-text-fill: green;");
            }
            
        } catch (NumberFormatException e) { 
            lblCambio.setText("Error"); 
        }
    }

    @FXML
    private void finalizarVenta(ActionEvent event) {
        try {
            String texto = txtRecibido.getText();
            if (texto.isEmpty()) return;

            double recibido = Double.parseDouble(texto);
            
            if (recibido < totalPagar) {
                mostrarAlerta("Pago Insuficiente", "El cliente debe pagar el total completo.");
                return;
            }
            
            // Avisar a la ventana de Ventas para que imprima el ticket
            if(ventasController != null) {
                ventasController.realizarImpresionTicket(recibido, cambioCalculado);
            }
            
            // Cerrar esta ventanita
            ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
            
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Ingresa un monto válido.");
        }
    }
    
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}