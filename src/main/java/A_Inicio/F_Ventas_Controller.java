package A_Inicio;

import A_Logica_Y_Metodos.ConexionDB;
import E_Modelos.Producto;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement; // Importante para guardar
import java.sql.ResultSet;
import java.sql.SQLException;      // Importante para errores de BD
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class F_Ventas_Controller implements Initializable {

    // --- ELEMENTOS FXML ---
    @FXML private TableView<Producto> tablaDisponibles;
    @FXML private TableColumn<Producto, String> colProdDisp;
    @FXML private TableColumn<Producto, Double> colPrecioDisp;

    @FXML private TableView<Producto> tablaCarrito;
    @FXML private TableColumn<Producto, String> colProdCar;
    @FXML private TableColumn<Producto, Double> colPrecioCar;
    @FXML private TableColumn<Producto, Void> colAccionCar; 

    @FXML private TextField txtBuscar;
    @FXML private TextField txtSubtotal;
    @FXML private TextField txtTotal;
    @FXML private Label lblTicket;
    
    @FXML private Button btnVender;
    @FXML private Button btnCancelar;
    @FXML private Button btnCerrarSesion;

    // --- LISTAS DE DATOS ---
    ObservableList<Producto> listaMaster = FXCollections.observableArrayList();
    ObservableList<Producto> listaCarrito = FXCollections.observableArrayList();
    FilteredList<Producto> listaFiltrada;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTablas();
        cargarProductosDesdeBD();
        
        listaFiltrada = new FilteredList<>(listaMaster, p -> true);
        tablaDisponibles.setItems(listaFiltrada);
        
        if(btnCerrarSesion != null) btnCerrarSesion.setOnAction(this::cerrarSesion);
        if(btnCancelar != null) btnCancelar.setOnAction(this::accionCancelar);
        if(btnVender != null) btnVender.setOnAction(this::abrirVentanaCobro);
        
        actualizarTicketPreview(0, 0, false);
    }

    private void configurarTablas() {
        colProdDisp.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        colPrecioDisp.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPrecioVenta()));

        colProdCar.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        colPrecioCar.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPrecioVenta()));
        
        tablaCarrito.setItems(listaCarrito);
    }

    private void cargarProductosDesdeBD() {
        listaMaster.clear();
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
                    listaMaster.add(p);
                }
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    @FXML
    private void filtrarProductos(KeyEvent event) {
        String texto = txtBuscar.getText().toLowerCase();
        listaFiltrada.setPredicate(producto -> {
            if (texto == null || texto.isEmpty()) return true;
            return producto.getNombre().toLowerCase().contains(texto) 
                || producto.getCodigoBarras().toLowerCase().contains(texto);
        });
    }

    @FXML
    private void agregarAlCarrito(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Producto seleccionado = tablaDisponibles.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                listaCarrito.add(seleccionado);
                calcularTotales();
                actualizarTicketPreview(0, 0, false);
            }
        }
    }

    private void accionCancelar(ActionEvent event) {
        listaCarrito.clear();
        txtSubtotal.setText("$ 0.00");
        txtTotal.setText("$ 0.00");
        txtBuscar.setText("");
        listaFiltrada.setPredicate(p -> true);
        actualizarTicketPreview(0, 0, false);
    }

    private void abrirVentanaCobro(ActionEvent event) {
        if (listaCarrito.isEmpty()) {
            mostrarAlerta("Carrito Vacío", "Agrega productos antes de vender.");
            return;
        }
        
        try {
            double total = 0;
            for (Producto p : listaCarrito) { total += p.getPrecioVenta(); }
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/B_Escenas/F_Cobro.fxml"));
            Parent root = loader.load();
            
            F_Cobro_Controller cobroCtrl = loader.getController();
            cobroCtrl.initData(total, this); 
            
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Cobrar");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se encontró el archivo /B_Escenas/F_Cobro.fxml");
        }
    }
    
    // --- ESTE ES EL MÉTODO QUE CONECTA TODO ---
    public void realizarImpresionTicket(double recibido, double cambio) {
        // 1. Calcular total
        double total = 0;
        for (Producto p : listaCarrito) { total += p.getPrecioVenta(); }

        // 2. GUARDAR EN LA BASE DE DATOS Y DESCONTAR STOCK
        boolean exito = guardarVentaEnBD(total, recibido, cambio);

        if (exito) {
            actualizarTicketPreview(recibido, cambio, true);
            mostrarAlerta("¡Venta Exitosa!", "Venta guardada y stock actualizado.\nCambio: $ " + String.format("%.2f", cambio));
            
            // 3. LIMPIAR Y RECARGAR EL INVENTARIO (Para ver el nuevo stock)
            listaCarrito.clear();
            calcularTotales();
            cargarProductosDesdeBD(); 
        }
    }

    // --- LÓGICA DE BASE DE DATOS ---
    private boolean guardarVentaEnBD(double total, double pago, double cambio) {
        Connection con = ConexionDB.conectar();
        if (con == null) return false;

        try {
            // A) Registrar Venta General
            String sqlVenta = "INSERT INTO ventas (total, pago, cambio) VALUES (?, ?, ?)";
            PreparedStatement pstVenta = con.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS);
            pstVenta.setDouble(1, total);
            pstVenta.setDouble(2, pago);
            pstVenta.setDouble(3, cambio);
            pstVenta.executeUpdate();

            // Obtener el ID de la venta generada
            ResultSet rsKeys = pstVenta.getGeneratedKeys();
            int idVenta = 0;
            if (rsKeys.next()) {
                idVenta = rsKeys.getInt(1);
            }

            // B) Registrar Detalles y Descontar Stock
            for (Producto p : listaCarrito) {
                // Detalle
                String sqlDetalle = "INSERT INTO detalle_ventas (id_venta, id_producto, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pstDetalle = con.prepareStatement(sqlDetalle);
                pstDetalle.setInt(1, idVenta);
                pstDetalle.setInt(2, p.getId());
                pstDetalle.setInt(3, 1); // 1 pieza por renglón
                pstDetalle.setDouble(4, p.getPrecioVenta());
                pstDetalle.setDouble(5, p.getPrecioVenta());
                pstDetalle.executeUpdate();

                // Stock (La resta)
                String sqlStock = "UPDATE productos SET stock = stock - 1 WHERE id = ?";
                PreparedStatement pstStock = con.prepareStatement(sqlStock);
                pstStock.setInt(1, p.getId());
                pstStock.executeUpdate();
            }
            
            System.out.println("✅ Venta #" + idVenta + " registrada exitosamente.");
            con.close();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error de BD", "No se pudo guardar la venta: " + e.getMessage());
            return false;
        }
    }

    private void calcularTotales() {
        double total = 0;
        for (Producto p : listaCarrito) {
            total += p.getPrecioVenta();
        }
        txtSubtotal.setText(String.format("$ %.2f", total));
        txtTotal.setText(String.format("$ %.2f", total));
    }
    
    private void actualizarTicketPreview(double recibido, double cambio, boolean esFinal) {
        StringBuilder ticket = new StringBuilder();
        ticket.append("********************************\n");
        ticket.append("       TIENDA DE ABARROTES      \n");
        ticket.append("********************************\n");
        
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        ticket.append("Fecha: ").append(ahora.format(formatter)).append("\n\n");
        
        ticket.append("CANT  DESCRIPCION       IMPORTE\n");
        ticket.append("--------------------------------\n");
        
        double total = 0;
        for (Producto p : listaCarrito) {
            String nombreCorto = p.getNombre();
            if (nombreCorto.length() > 15) nombreCorto = nombreCorto.substring(0, 15);
            ticket.append(String.format(" 1    %-15s   $ %5.2f\n", nombreCorto, p.getPrecioVenta()));
            total += p.getPrecioVenta();
        }
        
        ticket.append("--------------------------------\n");
        ticket.append(String.format("TOTAL:                  $ %5.2f\n", total));
        
        if (esFinal) {
            ticket.append(String.format("EFECTIVO:               $ %5.2f\n", recibido));
            ticket.append(String.format("CAMBIO:                 $ %5.2f\n", cambio));
        }
        
        ticket.append("\n");
        ticket.append("      ¡GRACIAS POR SU COMPRA!   \n");
        ticket.append("********************************");

        if (lblTicket != null) lblTicket.setText(ticket.toString());
    }
    
    private void cerrarSesion(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/B_Escenas/B_Login.fxml"));
            Parent root = loader.load();
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