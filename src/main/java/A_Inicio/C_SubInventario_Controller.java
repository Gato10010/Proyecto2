package A_Inicio;

import A_Logica_Y_Metodos.ConexionDB;
import E_Modelos.Producto; // <--- Importante: Necesario para recibir el producto
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class C_SubInventario_Controller implements Initializable {

    @FXML private TextField txtProveedor;
    @FXML private TextField txtProducto;
    @FXML private TextField txtPresentacion;
    @FXML private ComboBox<String> cmbUnidad;
    @FXML private TextField txtClave;
    @FXML private TextField txtStock;
    @FXML private TextField txtPrecio;
    @FXML private ComboBox<String> cmbCategoria;
    @FXML private Button btnGuardar;

    // --- VARIABLES DE CONTROL ---
    private boolean esEdicion = false; // Bandera para saber si estamos editando
    private int idProductoEditar = 0;  // Para guardar el ID del producto a editar

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Opciones para productos
        cmbCategoria.setItems(FXCollections.observableArrayList("Bebidas", "Botanas", "Lacteos", "Limpieza", "Otros"));
        cmbUnidad.setItems(FXCollections.observableArrayList("Pieza", "Kg", "Litro", "Paquete"));
    }    

    // --- MÉTODO NUEVO: Recibe los datos cuando presionas "Editar" ---
    public void initData(Producto p) {
        this.esEdicion = true; // Activamos el modo edición
        this.idProductoEditar = p.getId(); // Guardamos el ID para el WHERE de SQL
        
        // Rellenamos los campos con la info que viene de la tabla
        txtProducto.setText(p.getNombre());
        txtClave.setText(p.getCodigoBarras());
        txtStock.setText(String.valueOf(p.getStock()));
        txtPrecio.setText(String.valueOf(p.getPrecioVenta()));
        cmbCategoria.setValue(p.getCategoria());
        
        // Cambiamos el texto del botón para que el usuario sepa que va a actualizar
        btnGuardar.setText("Actualizar"); 
    }

    @FXML
    private void guardarProducto(ActionEvent event) {
        String nombre = txtProducto.getText();
        String codigo = txtClave.getText();
        String categoria = cmbCategoria.getValue();
        String stockStr = txtStock.getText();
        String precioStr = txtPrecio.getText();

        if (nombre == null || nombre.isEmpty() || codigo.isEmpty() || stockStr.isEmpty() || precioStr.isEmpty()) {
            mostrarAlerta("Error", "Llena Producto, Clave, Stock y Precio.");
            return;
        }

        try {
            int stock = Integer.parseInt(stockStr);
            double precio = Double.parseDouble(precioStr);
            
            Connection con = ConexionDB.conectar();
            if (con != null) {
                
                if (esEdicion) {
                    // --- CASO 1: EDITAR (UPDATE) ---
                    String sql = "UPDATE productos SET codigo_barras=?, nombre=?, precio_venta=?, stock=?, categoria=? WHERE id=?";
                    PreparedStatement pst = con.prepareStatement(sql);
                    pst.setString(1, codigo);
                    pst.setString(2, nombre);
                    pst.setDouble(3, precio);
                    pst.setInt(4, stock);
                    pst.setString(5, categoria != null ? categoria : "General");
                    pst.setInt(6, idProductoEditar); // Usamos el ID guardado
                    
                    int filas = pst.executeUpdate();
                    if (filas > 0) {
                        mostrarAlerta("Éxito", "Producto actualizado correctamente.");
                        cerrarVentana(event);
                    }
                    
                } else {
                    // --- CASO 2: NUEVO (INSERT) ---
                    String sql = "INSERT INTO productos (codigo_barras, nombre, precio_compra, precio_venta, stock, categoria) VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement pst = con.prepareStatement(sql);
                    pst.setString(1, codigo);
                    pst.setString(2, nombre);
                    pst.setDouble(3, 0.0); // Precio compra por defecto
                    pst.setDouble(4, precio);
                    pst.setInt(5, stock);
                    pst.setString(6, categoria != null ? categoria : "General");

                    int filas = pst.executeUpdate();
                    if (filas > 0) {
                        mostrarAlerta("Éxito", "Producto guardado correctamente.");
                        cerrarVentana(event);
                    }
                }
                con.close();
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error de Formato", "El stock debe ser entero y el precio un número válido.");
        } catch (Exception e) {
            mostrarAlerta("Error Base de Datos", "Ocurrió un error al guardar: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void cerrarVentana(ActionEvent event) {
        ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}