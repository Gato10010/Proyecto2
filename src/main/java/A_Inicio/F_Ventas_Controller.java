package A_Inicio;

import A_Logica_Y_Metodos.ConexionDB;
import E_Modelos.Producto;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
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
import javafx.scene.control.TableCell; // Importante para el botón en tabla
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback; // Importante para el botón en tabla

public class F_Ventas_Controller implements Initializable {

    // --- ELEMENTOS FXML ---
    @FXML private TableView<Producto> tablaDisponibles;
    @FXML private TableColumn<Producto, String> colProdDisp;
    @FXML private TableColumn<Producto, Double> colPrecioDisp;

    @FXML private TableView<Producto> tablaCarrito;
    @FXML private TableColumn<Producto, String> colProdCar;
    @FXML private TableColumn<Producto, Double> colPrecioCar;
    @FXML private TableColumn<Producto, Void> colAccionCar; // Columna para el botón X

    @FXML private TextField txtBuscar;
    @FXML private TextField txtSubtotal;
    @FXML private TextField txtTotal;
    @FXML private Label lblTicket;
    
    @FXML private Button btnVender;
    @FXML private Button btnCancelar;
    @FXML private Button btnCerrarSesion;
    @FXML private Button btnRegistrar;

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
        // Tabla Izquierda (Disponibles)
        colProdDisp.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        colPrecioDisp.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPrecioVenta()));

        // Tabla Derecha (Carrito)
        colProdCar.setCellValueFactory(cellData -> {
            Producto p = cellData.getValue();
            return new SimpleStringProperty(p.getNombre() + " (x" + p.getCantidadVenta() + ")");
        });
        colPrecioCar.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPrecioVenta()));
        
        // --- AQUÍ LLAMAMOS A LA FUNCIÓN DEL BOTÓN ROJO ---
        agregarBotonEliminar();
        
        tablaCarrito.setItems(listaCarrito);
    }

    // --- MÉTODO PARA CREAR EL BOTÓN "X" EN CADA FILA ---
    private void agregarBotonEliminar() {
        Callback<TableColumn<Producto, Void>, TableCell<Producto, Void>> cellFactory = (final TableColumn<Producto, Void> param) -> {
            return new TableCell<Producto, Void>() {
                private final Button btn = new Button("X");
                {
                    // Estilo rojo para que parezca de borrar
                    btn.setStyle("-fx-background-color: #ffcccc; -fx-text-fill: red; -fx-font-weight: bold; -fx-cursor: hand;");
                    btn.setOnAction((ActionEvent event) -> {
                        Producto producto = getTableView().getItems().get(getIndex());
                        listaCarrito.remove(producto);
                        calcularTotales();
                        actualizarTicketPreview(0, 0, false);
                    });
                }
                @Override
                public void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(btn);
                        setAlignment(javafx.geometry.Pos.CENTER);
                    }
                }
            };
        };
        colAccionCar.setCellFactory(cellFactory);
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
                if (seleccionado.getStock() <= 0) {
                    mostrarAlerta("Sin Stock", "No hay existencias.");
                    return;
                }

                TextInputDialog dialog = new TextInputDialog("1");
                dialog.setTitle("Agregar");
                dialog.setHeaderText("Producto: " + seleccionado.getNombre());
                dialog.setContentText("Cantidad:");

                Optional<String> result = dialog.showAndWait();
                
                if (result.isPresent()) {
                    try {
                        int cantidadSolicitada = Integer.parseInt(result.get());
                        if (cantidadSolicitada <= 0) return;

                        Producto productoEnCarrito = null;
                        for (Producto p : listaCarrito) {
                            if (p.getId() == seleccionado.getId()) {
                                productoEnCarrito = p;
                                break;
                            }
                        }

                        int cantidadActualEnCarrito = (productoEnCarrito != null) ? productoEnCarrito.getCantidadVenta() : 0;
                        
                        if ((cantidadActualEnCarrito + cantidadSolicitada) > seleccionado.getStock()) {
                            mostrarAlerta("Stock Insuficiente", "Solo tienes " + seleccionado.getStock() + " en almacén.");
                            return;
                        }

                        if (productoEnCarrito != null) {
                            productoEnCarrito.setCantidadVenta(cantidadActualEnCarrito + cantidadSolicitada);
                            tablaCarrito.refresh();
                        } else {
                            Producto nuevoEnCarrito = new Producto(
                                seleccionado.getId(), seleccionado.getCodigoBarras(), seleccionado.getNombre(),
                                seleccionado.getPrecioCompra(), seleccionado.getPrecioVenta(), 
                                seleccionado.getStock(), seleccionado.getCategoria()
                            );
                            nuevoEnCarrito.setCantidadVenta(cantidadSolicitada);
                            listaCarrito.add(nuevoEnCarrito);
                        }

                        calcularTotales();
                        actualizarTicketPreview(0, 0, false);

                    } catch (NumberFormatException e) {
                        mostrarAlerta("Error", "Ingresa solo números.");
                    }
                }
            }
        }
    }

    // --- NUEVA LÓGICA DEL BOTÓN CANCELAR ---
    // Ahora borra SOLO el producto seleccionado en la tabla derecha
    private void accionCancelar(ActionEvent event) {
        Producto seleccionado = tablaCarrito.getSelectionModel().getSelectedItem();
        
        if (seleccionado != null) {
            listaCarrito.remove(seleccionado);
            calcularTotales();
            actualizarTicketPreview(0, 0, false);
        } else {
            // Si no seleccionó nada, le avisamos (o podríamos borrar todo si prefieres)
            if (listaCarrito.isEmpty()) {
                mostrarAlerta("Aviso", "El carrito ya está vacío.");
            } else {
                mostrarAlerta("Selección Requerida", "Selecciona un producto de la lista derecha para eliminarlo.\nO usa la 'X' roja.");
            }
        }
    }

    private void abrirVentanaCobro(ActionEvent event) {
        if (listaCarrito.isEmpty()) {
            mostrarAlerta("Carrito Vacío", "Agrega productos antes de vender.");
            return;
        }
        
        try {
            double total = 0;
            for (Producto p : listaCarrito) { total += (p.getPrecioVenta() * p.getCantidadVenta()); }
            
            URL url = getClass().getResource("/B_Escenas/F_Cobro.fxml");
            if (url == null) {
                mostrarAlerta("Error Crítico", "No se encuentra F_Cobro.fxml en /B_Escenas/");
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
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
            mostrarAlerta("Error", "Fallo al abrir ventana: " + e.getMessage());
        }
    }
    
    public void realizarImpresionTicket(double recibido, double cambio) {
        double total = 0;
        for (Producto p : listaCarrito) { total += (p.getPrecioVenta() * p.getCantidadVenta()); }

        boolean guardado = guardarVentaEnBD(total, recibido, cambio);

        if (guardado) {
            actualizarTicketPreview(recibido, cambio, true);
            mostrarAlerta("¡Venta Exitosa!", "Venta registrada.\nCambio: $ " + String.format("%.2f", cambio));
            
            // Limpiar todo al finalizar venta exitosa
            listaCarrito.clear();
            calcularTotales();
            cargarProductosDesdeBD();
        }
    }

    private boolean guardarVentaEnBD(double total, double pago, double cambio) {
        Connection con = ConexionDB.conectar();
        if (con == null) return false;

        try {
            String sqlVenta = "INSERT INTO ventas (total, pago, cambio) VALUES (?, ?, ?)";
            PreparedStatement pstVenta = con.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS);
            pstVenta.setDouble(1, total);
            pstVenta.setDouble(2, pago);
            pstVenta.setDouble(3, cambio);
            pstVenta.executeUpdate();

            ResultSet rsKeys = pstVenta.getGeneratedKeys();
            int idVenta = 0;
            if (rsKeys.next()) idVenta = rsKeys.getInt(1);

            for (Producto p : listaCarrito) {
                String sqlDetalle = "INSERT INTO detalle_ventas (id_venta, id_producto, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pstDetalle = con.prepareStatement(sqlDetalle);
                pstDetalle.setInt(1, idVenta);
                pstDetalle.setInt(2, p.getId());
                pstDetalle.setInt(3, p.getCantidadVenta());
                pstDetalle.setDouble(4, p.getPrecioVenta());
                pstDetalle.setDouble(5, p.getPrecioVenta() * p.getCantidadVenta());
                pstDetalle.executeUpdate();

                String sqlStock = "UPDATE productos SET stock = stock - ? WHERE id = ?";
                PreparedStatement pstStock = con.prepareStatement(sqlStock);
                pstStock.setInt(1, p.getCantidadVenta());
                pstStock.setInt(2, p.getId());
                pstStock.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error BD", e.getMessage());
            return false;
        }
    }

    private void calcularTotales() {
        double total = 0;
        for (Producto p : listaCarrito) {
            total += (p.getPrecioVenta() * p.getCantidadVenta());
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
            
            double importe = p.getPrecioVenta() * p.getCantidadVenta();
            
            ticket.append(String.format(" %-4d %-15s   $ %5.2f\n", 
                    p.getCantidadVenta(), nombreCorto, importe));
            
            total += importe;
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