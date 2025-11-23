package E_Modelos;

/**
 * Modelo que representa la entidad 'Usuario' de la base de datos.
 * Esta clase se usa para pasar la información del usuario que ha iniciado sesión.
 */
public class Usuario {
    
    private int id;
    private String usuario; // Corresponde a la columna 'Usuario'
    private String categoria;

    /**
     * Constructor para crear el objeto Usuario después de un login exitoso.
     */
    public Usuario(int id, String usuario, String categoria) {
        this.id = id;
        this.usuario = usuario;
        this.categoria = categoria;
    }

    // --- Getters (Para que el controlador pueda leer los datos) ---
    
    public int getId() {
        return id;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getCategoria() {
        return categoria;
    }
    
    // Esta clase no necesita 'setters' para esta lógica,
    // ya que solo transporta datos leídos de la BD.
}
