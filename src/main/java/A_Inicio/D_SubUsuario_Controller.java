package A_Inicio;

import A_Logica_Y_Metodos.ConexionDB; 
import E_Modelos.Personal;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement; 
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

public class D_SubUsuario_Controller implements Initializable {

    @FXML private TextField txtNombre;
    @FXML private TextField txtApellidoPaterno;
    @FXML private TextField txtApellidoMaterno;
    @FXML private TextField txtEdad;
    @FXML private TextField txtSexo;
    @FXML private TextField txtTelefono;
    @FXML private ComboBox<String> cmbCategoria;
    @FXML private Button btnGuardar;

    // Variables de control
    private boolean esEdicion = false;
    private int idPersonalEditar = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Llenamos el combo con tus roles
        cmbCategoria.setItems(FXCollections.observableArrayList("Administrador", "Cajero", "Almacenista", "Vendedor", "Gerente"));
    }    

    // Recibe los datos cuando pulsas "Editar"
    public void initData(Personal p) {
        this.esEdicion = true;
        this.idPersonalEditar = p.getId(); 
        
        txtNombre.setText(p.getNombre());
        txtApellidoPaterno.setText(p.getApellidoPaterno());
        txtApellidoMaterno.setText(p.getApellidoMaterno());
        txtEdad.setText(String.valueOf(p.getEdad()));
        txtSexo.setText(p.getSexo());
        txtTelefono.setText(p.getTelefono());
        cmbCategoria.setValue(p.getCategoria());
        
        btnGuardar.setText("Actualizar");
    }

    @FXML
    private void guardarPersonal(ActionEvent event) {
        String nombre = txtNombre.getText();
        String apPaterno = txtApellidoPaterno.getText();
        String apMaterno = txtApellidoMaterno.getText();
        String edadStr = txtEdad.getText();
        String sexo = txtSexo.getText();
        String telefono = txtTelefono.getText();
        String categoria = cmbCategoria.getValue();

        // Validaciones básicas
        if (nombre.isEmpty() || apPaterno.isEmpty() || edadStr.isEmpty() || categoria == null) {
            mostrarAlerta("Error", "Por favor llena los campos obligatorios.");
            return;
        }

        try {
            Connection con = ConexionDB.conectar(); 
            if (con == null) return;

            int edad = Integer.parseInt(edadStr);

            if (esEdicion) {
                // ==========================================
                // CASO EDITAR
                // ==========================================
                
                // 1. Actualizar datos personales
                String sql = "UPDATE personal SET nombre=?, apellido_paterno=?, apellido_materno=?, edad=?, sexo=?, telefono=?, categoria=? WHERE id=?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, nombre);
                pst.setString(2, apPaterno);
                pst.setString(3, apMaterno);
                pst.setInt(4, edad);
                pst.setString(5, sexo);
                pst.setString(6, telefono);
                pst.setString(7, categoria);
                pst.setInt(8, idPersonalEditar);
                pst.executeUpdate();

                // 2. Actualizar el ROL en la tabla de usuarios
                // USAMOS id_personal PORQUE ES LA COLUMNA QUE ACABAS DE CREAR
                String sqlRol = "UPDATE usuarios SET rol=? WHERE id_personal=?";
                PreparedStatement pstRol = con.prepareStatement(sqlRol);
                pstRol.setString(1, categoria);
                pstRol.setInt(2, idPersonalEditar);
                pstRol.executeUpdate();

                mostrarAlerta("Éxito", "Personal actualizado correctamente.");
                cerrarVentana(event);

            } else {
                // ==========================================
                // CASO NUEVO
                // ==========================================
                
                // 1. Insertar en tabla PERSONAL
                String sql = "INSERT INTO personal (nombre, apellido_paterno, apellido_materno, edad, sexo, telefono, categoria) VALUES (?, ?, ?, ?, ?, ?, ?)";
                
                PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                pst.setString(1, nombre);
                pst.setString(2, apPaterno);
                pst.setString(3, apMaterno);
                pst.setInt(4, edad);
                pst.setString(5, sexo);
                pst.setString(6, telefono);
                pst.setString(7, categoria);

                int filas = pst.executeUpdate();
                
                if (filas > 0) {
                    // 2. Recuperar el ID creado
                    ResultSet rs = pst.getGeneratedKeys();
                    int idNuevo = 0;
                    if(rs.next()){
                        idNuevo = rs.getInt(1);
                    }

                    // 3. Crear el USUARIO para el Login
                    String usuarioGen = nombre.toLowerCase().replaceAll("\\s+","");
                    String passGen = "1234"; 

                    // USAMOS id_personal AQUI TAMBIEN
                    String sqlUser = "INSERT INTO usuarios (id_personal, usuario, password, rol) VALUES (?, ?, ?, ?)";
                    PreparedStatement pstUser = con.prepareStatement(sqlUser);
                    pstUser.setInt(1, idNuevo);
                    pstUser.setString(2, usuarioGen);
                    pstUser.setString(3, passGen);
                    pstUser.setString(4, categoria);
                    pstUser.executeUpdate();

                    mostrarAlerta("Éxito", "Personal registrado.\n\nUsuario generado: " + usuarioGen + "\nContraseña: " + passGen);
                    cerrarVentana(event);
                }
            }
            con.close();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "La edad debe ser un número válido.");
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error BD", "Error al guardar: " + e.getMessage());
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