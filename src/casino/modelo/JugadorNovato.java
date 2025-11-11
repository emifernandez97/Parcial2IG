package casino.modelo;

public class JugadorNovato extends Jugador {
    public JugadorNovato(String nombre, String apodo, int dineroInicial) {
        super(nombre, apodo, dineroInicial);
    }
    @Override
    public int calcularApuesta() { return (int) (getDinero() * 0.10); }
    @Override
    public String obtenerTipoJugador() { return "Novato"; }
}