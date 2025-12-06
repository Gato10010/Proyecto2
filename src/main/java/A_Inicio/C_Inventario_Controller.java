package A_Inicio;

import A_Logica_Y_Metodos.ConexionDB;
import E_Modelos.Producto;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class C_Inventario_Controller implements Initializable {

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

    // Listas para manejar los datos
    ObservableList<Producto> listaProductos = FXCollections.observableArrayList();
    FilteredList<Producto> listaFiltrada;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        
        // Inicializamos el filtro para el buscador
        listaFiltrada = new FilteredList<>(listaProductos, p -> true);
        tablaProductos.setItems(listaFiltrada);
        
        cargarDatos();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId()));
        colCodigo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCodigoBarras()));
        colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        colStock.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getStock()));
        colPrecio.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPrecioVenta()));
        colCategoria.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategoria()));
    }

    // --- BUSCADOR EN TIEMPO REAL ---
    @FXML
    private void filtrarInventario(KeyEvent event) {
        String filtro = txtBuscar.getText().toLowerCase();
        listaFiltrada.setPredicate(producto -> {
            if (filtro == null || filtro.isEmpty()) return true;
            if (producto.getNombre().toLowerCase().contains(filtro)) return true;
            if (producto.getCodigoBarras().toLowerCase().contains(filtro)) return true;
            if (producto.getCategoria().toLowerCase().contains(filtro)) return true;
            return false;
        });
    }

    // --- BOTÓN AGREGAR ---
    @FXML
    private void addProduct(ActionEvent event) {
        abrirFormulario(null); // null = Modo Agregar
    }

    // --- BOTÓN EDITAR ---
    @FXML
    private void editarProducto(ActionEvent event) {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Atención", "Primero selecciona un producto de la tabla (clic azul).");
            return;
        }
        abrirFormulario(seleccionado); // Pasamos el producto = Modo Editar
    }

    // --- MÉTODO COMÚN PARA ABRIR LA VENTANA ---
    private void abrirFormulario(Producto productoEditar) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/C_SubEscenas/C_SubInventario.fxml"));
            Parent root = loader.load();
            
            // Si es edición, pasamos los datos al controlador hijo
            if (productoEditar != null) {
                C_SubInventario_Controller subController = loader.getController();
                subController.initData(productoEditar);
            }
            
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); 
            stage.showAndWait();
            
            cargarDatos(); // Recargar tabla al regresar
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la ventana: " + e.getMessage());
        }
    }

    // --- BOTÓN BORRAR ---
    @FXML
    private void borrarProducto(ActionEvent event) {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Atención", "Selecciona un producto para borrar.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Estás seguro de eliminar: " + seleccionado.getNombre() + "?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                Connection con = ConexionDB.conectar();
                String sql = "DELETE FROM productos WHERE id = " + seleccionado.getId();
                Statement st = con.createStatement();
                st.executeUpdate(sql);
                con.close();
                cargarDatos(); // Actualizar tabla
                mostrarAlerta("Éxito", "Producto eliminado.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void cargarDatos() {
        listaProductos.clear();
        Connection con = ConexionDB.conectar();
        if (con != null) {
            try {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM productos");
                while (rs.next()) {
                    listaProductos.add(new Producto(
                        rs.getInt("id"), rs.getString("codigo_barras"), rs.getString("nombre"),
                        rs.getDouble("precio_compra"), rs.getDouble("precio_venta"),
                        rs.getInt("stock"), rs.getString("categoria")
                    ));
                }
                con.close();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    @FXML private void cerrarSesion(ActionEvent event) { cambiarPantalla(event, "/B_Escenas/B_Login.fxml"); }
    @FXML private void regresarMenu(ActionEvent event) { cambiarPantalla(event, "/B_Escenas/E_Gestion.fxml"); }

    private void cambiarPantalla(ActionEvent event, String ruta) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(ruta));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            ((Node) event.getSource()).getScene().getWindow().hide();
        } catch (IOException e) { e.printStackTrace(); }
    }
    
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}