package A_Inicio;

import A_Logica_Y_Metodos.PersonalDAO;
import E_Modelos.Personal;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controlador para la escena D_Usuarios.fxml (Gestión de Personal).
 */
public class D_Usuarios_Controller implements Initializable {

    @FXML
    private TableView<Personal> tablaPersonal;
    @FXML
    private TableColumn<Personal, Integer> colId;
    @FXML
    private TableColumn<Personal, String> colNombre;
    @FXML
    private TableColumn<Personal, String> colApPaterno;
    @FXML
    private TableColumn<Personal, String> colApMaterno;
    @FXML
    private TableColumn<Personal, String> colTelefono;
    @FXML
    private TableColumn<Personal, String> colCategoria;
    @FXML
    private TableColumn<Personal, Void> colEditar;
    @FXML
    private TableColumn<Personal, Void> colEliminar;
    @FXML
    private TextField txtBuscar;
    @FXML
    private Button btnAgregar;

    private PersonalDAO personalDAO;
    private ObservableList<Personal> listaPersonal; // Lista principal de todos los empleados

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.personalDAO = new PersonalDAO();
        configurarColumnasTabla();
        cargarDatosTabla();
        
        // Lógica del buscador
        // Escucha cambios en el texto de txtBuscar
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            buscarPersonal(newValue);
        });
    }

    /**
     * Configura las celdas de la tabla para mostrar los datos del modelo Personal.
     */
    private void configurarColumnasTabla() {
        // Vincula las columnas con las propiedades del modelo Personal
        colId.setCellValueFactory(new PropertyValueFactory<>("id_personal"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApPaterno.setCellValueFactory(new PropertyValueFactory<>("apellidoPaterno"));
        colApMaterno.setCellValueFactory(new PropertyValueFactory<>("apellidoMaterno"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));

        // --- Botones de Acción en la Tabla ---
        
        // Configurar botón "Editar"
        colEditar.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Editar");
            {
                btn.setOnAction(event -> {
                    // Obtiene el objeto 'Personal' de la fila en la que se hizo clic
                    Personal personal = getTableView().getItems().get(getIndex());
                    handleEditar(personal);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                // Muestra el botón solo si la fila no está vacía
                setGraphic(empty ? null : btn);
            }
        });

        // Configurar botón "Eliminar"
        colEliminar.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Eliminar");
            {
                btn.setOnAction(event -> {
                    Personal personal = getTableView().getItems().get(getIndex());
                    handleEliminar(personal);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }


    /**
     * Filtra la tabla basado en el texto del buscador.
     */
    private void buscarPersonal(String consulta) {
        // Si la barra de búsqueda está vacía, muestra la lista completa
        if (consulta == null || consulta.isEmpty()) {
            tablaPersonal.setItems(this.listaPersonal);
            return;
        }
        
        // Crea una nueva lista para los resultados filtrados
        ObservableList<Personal> listaFiltrada = FXCollections.observableArrayList();
        for (Personal p : this.listaPersonal) {
            // Comprueba si el nombre o el apellido contienen el texto de búsqueda (ignora mayúsculas)
            if (p.getNombre().toLowerCase().contains(consulta.toLowerCase()) || 
                p.getApellidoPaterno().toLowerCase().contains(consulta.toLowerCase())) {
                listaFiltrada.add(p);
            }
        }
        // Muestra solo la lista filtrada
        tablaPersonal.setItems(listaFiltrada);
    }

    /**
     * Maneja el clic en el botón "Agregar Nuevo".
     * Llama a abrirFormularioPersonal() en modo "Crear" (pasando null).
     */
    @FXML
    private void handleAgregar(ActionEvent event) {
        abrirFormularioPersonal(null); // 'null' significa que es un registro nuevo
    }

    /**
     * Maneja el clic en el botón "Editar" de la tabla.
     * Llama a abrirFormularioPersonal() en modo "Editar" (pasando el objeto).
     */
    private void handleEditar(Personal personal) {
        abrirFormularioPersonal(personal); // Pasa el objeto para editar
    }

    @FXML
    private void agrePersonal()throws IOException{
        App.setRoot("A_Inicio.D_SubUsuarios.fxml");
    }
    
    /**
     * Abre el formulario (D_SubUsuario.fxml) como una ventana emergente.
     */
    private void abrirFormularioPersonal(Personal personal) {
        try {
            // 1. Carga el FXML de la ventana emergente
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/C_SubEscenas/D_SubUsuario.fxml"));
            Parent root = loader.load();

            // 2. Obtiene el controlador del formulario
            D_SubUsuario_Controller formularioController = loader.getController();

            

            // 4. Configura la nueva ventana (Stage)
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana principal
            popupStage.setTitle(personal == null ? "Agregar Nuevo Personal" : "Editar Personal");
            popupStage.setScene(new Scene(root));
            popupStage.setResizable(false);
            
            // 5. Muestra la ventana y espera a que se cierre
            popupStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo cargar el formulario de registro.");
        }
    }

    /**
     * Maneja el clic en el botón "Eliminar" de la tabla.
     */
    private void handleEliminar(Personal personal) {
        // Pide confirmación al usuario
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Está seguro de que desea eliminar a " + personal.getNombre() + "?");
        confirmacion.setContentText("Esta acción no se puede deshacer.");

     }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cargarDatosTabla() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}