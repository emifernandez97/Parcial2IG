package casino.modelo;

public class JugadorExperto extends Jugador {
    public JugadorExperto(String nombre, String apodo, int dineroInicial) {
        super(nombre, apodo, dineroInicial);
    }
    @Override
    public int calcularApuesta() { return (int) (getDinero() * 0.20); }
    @Override
    public String obtenerTipoJugador() { return "Experto"; }
}