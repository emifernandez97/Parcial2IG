package casino.modelo;

public class JugadorVIP extends Jugador {
    private boolean rerollDisponible = true;
    public JugadorVIP(String nombre, String apodo, int dineroInicial) {
        super(nombre, apodo, dineroInicial);
    }
    @Override
    public int calcularApuesta() { return (int) (getDinero() * 0.30); }
    @Override
    public String obtenerTipoJugador() { return "VIP"; }
    public boolean tieneReroll() { return rerollDisponible; }
    public void usarReroll() { this.rerollDisponible = false; }
}