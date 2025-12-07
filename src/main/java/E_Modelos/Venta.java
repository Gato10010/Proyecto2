package E_Modelos;

public class Venta {
    private int id;
    private String fecha;
    private double total;
    private double pago;
    private double cambio;

    public Venta(int id, String fecha, double total, double pago, double cambio) {
        this.id = id;
        this.fecha = fecha;
        this.total = total;
        this.pago = pago;
        this.cambio = cambio;
    }

    // Estos Getters son OBLIGATORIOS para que la tabla funcione
    public int getId() { return id; }
    public String getFecha() { return fecha; }
    public double getTotal() { return total; }
    public double getPago() { return pago; }
    public double getCambio() { return cambio; }
}