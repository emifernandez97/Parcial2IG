package casino.modelo;

public abstract class Jugador {
    private String nombre;
    private String apodo;
    private int dinero;
    private int partidasGanadas; // Rondas ganadas

    public Jugador(String nombre, String apodo, int dineroInicial) {
        this.nombre = nombre;
        this.apodo = apodo;
        this.dinero = dineroInicial;
        this.partidasGanadas = 0;
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public String getApodo() { return apodo; }
    public int getDinero() { return dinero; }
    public int getPartidasGanadas() { return partidasGanadas; }

    // Métodos comunes
    public void ganar(int cantidad) {
        this.dinero += cantidad;
        this.partidasGanadas++;
    }

    public void perder(int cantidad) {
        this.dinero -= cantidad;
        if (this.dinero < 0) {
            this.dinero = 0;
        }
    }

    // Métodos Abstractos
    public abstract int calcularApuesta();
    public abstract String obtenerTipoJugador();
}