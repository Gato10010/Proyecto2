package A_Inicio;

import A_Logica_Y_Metodos.ConexionDB;
import E_Modelos.Producto;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javax.swing.JOptionPane;

public class C_Inventario_Controller implements Initializable {

    // 1. Elementos visuales (Coinciden con los fx:id del FXML)
    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, Integer> colId;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, Integer> colStock;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, String> colCategoria;

    @FXML private Button btncerrarsesion;
    @FXML private Button GotoAgre;
    @FXML private TextField txtBuscar;

    // 2. Lista para guardar los productos de la BD
    ObservableList<Producto> listaProductos = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarDatos();
    }

    // Configura qué columna muestra qué dato
    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoBarras"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
    }

    // Descarga los datos de MySQL
    private void cargarDatos() {
        listaProductos.clear(); // Limpiar lista anterior
        Connection con = ConexionDB.conectar();
        
        if (con != null) {
            String sql = "SELECT * FROM productos";
            try {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql);

                while (rs.next()) {
                    Producto p = new Producto(
                        rs.getInt("id"),
                        rs.getString("codigo_barras"),
                        rs.getString("nombre"),
                        rs.getDouble("precio_compra"),
                        rs.getDouble("precio_venta"),
                        rs.getInt("stock"),
                        rs.getString("categoria")
                    );
                    listaProductos.add(p);
                }
                
                // Poner la lista en la tabla visual
                tablaProductos.setItems(listaProductos);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void cerrarSesion(ActionEvent event) {
        cambiarPantalla(event, "/B_Escenas/B_Login.fxml", "Login");
    }

    @FXML
    private void regresarMenu(ActionEvent event) {
        cambiarPantalla(event, "/B_Escenas/E_Gestion.fxml", "Menú Principal");
    }
    
    @FXML
    private void addProduct(ActionEvent event) {
        JOptionPane.showMessageDialog(null, "¡Aquí abriremos la ventana para agregar productos!");
        // Aquí programaremos después la pantalla de "Nuevo Producto"
    }

    // Método para navegar entre ventanas
    private void cambiarPantalla(ActionEvent event, String ruta, String titulo) {
        try {
            URL url = getClass().getResource(ruta);
            if (url == null) return;
            Parent root = FXMLLoader.load(url);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.show();
            ((Node) event.getSource()).getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}