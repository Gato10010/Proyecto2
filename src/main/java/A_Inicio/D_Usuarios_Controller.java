package A_Inicio;

import A_Logica_Y_Metodos.ConexionDB;
import E_Modelos.Personal;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class D_Usuarios_Controller implements Initializable {

    @FXML private TableView<Personal> tablaPersonal;
    @FXML private TableColumn<Personal, Integer> colId;
    @FXML private TableColumn<Personal, String> colNombre;
    @FXML private TableColumn<Personal, String> colApPaterno;
    @FXML private TableColumn<Personal, String> colApMaterno;
    @FXML private TableColumn<Personal, Integer> colEdad;
    @FXML private TableColumn<Personal, String> colSexo;
    @FXML private TableColumn<Personal, String> colTelefono;
    @FXML private TableColumn<Personal, String> colCategoria;
    
    @FXML private TextField txtBuscar;

    // Listas para manejar los datos
    ObservableList<Personal> listaPersonal = FXCollections.observableArrayList();
    FilteredList<Personal> listaFiltrada;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        
        // Inicializamos el filtro
        listaFiltrada = new FilteredList<>(listaPersonal, p -> true);
        tablaPersonal.setItems(listaFiltrada);
        
        cargarDatos();
    }

    private void configurarTabla() {
        // Enlaza las columnas con los datos del Modelo
        colId.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId()));
        colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        colApPaterno.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getApellidoPaterno()));
        colApMaterno.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getApellidoMaterno()));
        colEdad.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getEdad()));
        colSexo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSexo()));
        colTelefono.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTelefono()));
        colCategoria.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategoria()));
    }

    // --- BUSCADOR ---
    @FXML
    private void filtrarPersonal(KeyEvent event) {
        String filtro = txtBuscar.getText().toLowerCase();
        listaFiltrada.setPredicate(p -> {
            if (filtro == null || filtro.isEmpty()) return true;
            // Busca por nombre, apellido o categoría
            if (p.getNombre().toLowerCase().contains(filtro)) return true;
            if (p.getApellidoPaterno().toLowerCase().contains(filtro)) return true;
            if (p.getCategoria().toLowerCase().contains(filtro)) return true;
            return false;
        });
    }

    // --- AGREGAR ---
    @FXML
    private void agregarPersonal(ActionEvent event) {
        abrirFormulario(null); // null = Modo Nuevo
    }

    // --- EDITAR ---
    @FXML
    private void editarPersonal(ActionEvent event) {
        Personal seleccionado = tablaPersonal.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Atención", "Selecciona un empleado de la tabla para editar.");
            return;
        }
        abrirFormulario(seleccionado); // Pasamos el empleado = Modo Editar
    }

    // --- ABRIR VENTANA (Compartido) ---
    private void abrirFormulario(Personal personalEditar) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/C_SubEscenas/D_SubUsuario.fxml"));
            Parent root = loader.load();
            
            // Si vamos a editar, pasamos los datos al controlador de la ventanita
            if (personalEditar != null) {
                D_SubUsuario_Controller subController = loader.getController();
                subController.initData(personalEditar);
            }
            
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            cargarDatos(); // Recargar tabla al cerrar la ventana
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la ventana: " + e.getMessage());
        }
    }

    // --- BORRAR ---
    @FXML
    private void borrarPersonal(ActionEvent event) {
        Personal seleccionado = tablaPersonal.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Atención", "Selecciona un empleado para borrar.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar");
        confirm.setContentText("¿Eliminar a " + seleccionado.getNombre() + "?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                Connection con = ConexionDB.conectar();
                String sql = "DELETE FROM personal WHERE id = " + seleccionado.getId();
                Statement st = con.createStatement();
                st.executeUpdate(sql);
                con.close();
                cargarDatos();
                mostrarAlerta("Éxito", "Empleado eliminado.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void cargarDatos() {
        listaPersonal.clear();
        Connection con = ConexionDB.conectar();
        if (con != null) {
            try {
                String sql = "SELECT * FROM personal";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql);
                while (rs.next()) {
                    // IMPORTANTE: Aquí se usa el constructor con ID
                    listaPersonal.add(new Personal(
                        rs.getInt("id"), 
                        rs.getString("nombre"), 
                        rs.getString("apellido_paterno"), 
                        rs.getString("apellido_materno"),
                        rs.getInt("edad"),
                        rs.getString("sexo"),
                        rs.getString("telefono"),
                        rs.getString("categoria")
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
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}