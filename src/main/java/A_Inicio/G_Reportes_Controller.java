package A_Inicio;

import A_Logica_Y_Metodos.ConexionDB;
import E_Modelos.Venta;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class G_Reportes_Controller implements Initializable {

    @FXML private TableView<Venta> tablaVentas;
    @FXML private TableColumn<Venta, Integer> colFolio;
    @FXML private TableColumn<Venta, String> colFecha;
    @FXML private TableColumn<Venta, Double> colTotal;
    @FXML private TableColumn<Venta, Double> colPago;
    @FXML private TableColumn<Venta, Double> colCambio;
    
    @FXML private Label lblTotalVendido;
    @FXML private Button btnRegresar;

    ObservableList<Venta> listaVentas = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarVentasBD();
    }

    private void configurarTabla() {
        // Enlazamos las columnas con los datos del modelo Venta
        colFolio.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getId()).asObject());
        colFecha.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFecha()));
        colTotal.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getTotal()).asObject());
        colPago.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getPago()).asObject());
        colCambio.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getCambio()).asObject());
        
        tablaVentas.setItems(listaVentas);
    }

    @FXML
    private void actualizarDatos(ActionEvent event) {
        cargarVentasBD();
    }

    private void cargarVentasBD() {
        listaVentas.clear();
        double sumaTotal = 0;
        
        Connection con = ConexionDB.conectar();
        if (con != null) {
            // Traemos las ventas de la más reciente a la más antigua
            String sql = "SELECT * FROM ventas ORDER BY fecha DESC";
            
            try {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql);
                
                while (rs.next()) {
                    Venta v = new Venta(
                        rs.getInt("id"),
                        rs.getString("fecha"), 
                        rs.getDouble("total"),
                        rs.getDouble("pago"),
                        rs.getDouble("cambio")
                    );
                    listaVentas.add(v);
                    sumaTotal += v.getTotal();
                }
                
                // Actualizamos el total general en verde
                lblTotalVendido.setText(String.format("$ %.2f", sumaTotal));
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void regresarMenu(ActionEvent event) {
        try {
            // Regresa al menú principal (asegúrate que el nombre del FXML sea correcto)
            // Si tu menú se llama "E_Gestion.fxml" o "A_Menu.fxml", cámbialo aquí:
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/B_Escenas/E_Gestion.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            ((Node) event.getSource()).getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}