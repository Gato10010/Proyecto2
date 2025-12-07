package E_Modelos;

public class Producto {

    private int id;
    private String codigoBarras;
    private String nombre;
    private double precioCompra;
    private double precioVenta;
    private int stock;
    private String categoria;
    
    // --- NUEVO: CAMPO PARA LLEVAR LA CUENTA EN EL CARRITO ---
    private int cantidadVenta = 1; 

    public Producto() {
    }

    public Producto(int id, String codigoBarras, String nombre, double precioCompra, double precioVenta, int stock, String categoria) {
        this.id = id;
        this.codigoBarras = codigoBarras;
        this.nombre = nombre;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.stock = stock;
        this.categoria = categoria;
    }

    // --- GETTERS Y SETTERS ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(double precioCompra) { this.precioCompra = precioCompra; }

    public double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(double precioVenta) { this.precioVenta = precioVenta; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    
    // --- NUEVOS MÉTODOS PARA LA CANTIDAD DE VENTA ---
    public int getCantidadVenta() { return cantidadVenta; }
    public void setCantidadVenta(int cantidadVenta) { this.cantidadVenta = cantidadVenta; }
    
    @Override
    public String toString() { return nombre; }
}