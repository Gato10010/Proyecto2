package E_Modelos;

/**
 * Modelo que representa la entidad 'personal' de la base de datos.
 * Esta es la versión REVERTIDA (simple) que solo se usa para CREAR.
 */
public class Personal {

    // No se incluye el 'id_personal' porque solo estamos creando
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private int edad;
    private String sexo;
    private String telefono;
    private String categoria;

    /**
     * Constructor para CREAR nuevo personal (el ID es autogenerado por la BD)
     */
    public Personal(String nombre, String apellidoPaterno, String apellidoMaterno, int edad, String sexo, String telefono, String categoria) {
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.edad = edad;
        this.sexo = sexo;
        this.telefono = telefono;
        this.categoria = categoria;
    }

    // --- Getters (Para que el DAO pueda leer los datos y guardarlos) ---
    
    public String getNombre() {
        return nombre;
    }
    public String getApellidoPaterno() {
        return apellidoPaterno;
    }
    public String getApellidoMaterno() {
        return apellidoMaterno;
    }
    public int getEdad() {
        return edad;
    }
    public String getSexo() {
        return sexo;
    }
    public String getTelefono() {
        return telefono;
    }
    public String getCategoria() {
        return categoria;
    }
    
    // No se necesitan Setters ni constructores con ID para esta lógica simple
}

