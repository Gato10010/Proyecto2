package E_Modelos;

public class Personal {
    
    // Variables
    private int id;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private int edad;
    private String sexo;
    private String telefono;
    private String categoria;

    // Constructor COMPLETO (Con ID) - Para leer de la BD
    public Personal(int id, String nombre, String apellidoPaterno, String apellidoMaterno, int edad, String sexo, String telefono, String categoria) {
        this.id = id;
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.edad = edad;
        this.sexo = sexo;
        this.telefono = telefono;
        this.categoria = categoria;
    }

    // Constructor SIMPLE (Sin ID) - Para crear nuevos
    public Personal(String nombre, String apellidoPaterno, String apellidoMaterno, int edad, String sexo, String telefono, String categoria) {
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.edad = edad;
        this.sexo = sexo;
        this.telefono = telefono;
        this.categoria = categoria;
    }

    // --- GETTERS Y SETTERS (Indispensables) ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidoPaterno() { return apellidoPaterno; }
    public void setApellidoPaterno(String apellidoPaterno) { this.apellidoPaterno = apellidoPaterno; }

    public String getApellidoMaterno() { return apellidoMaterno; }
    public void setApellidoMaterno(String apellidoMaterno) { this.apellidoMaterno = apellidoMaterno; }

    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
}