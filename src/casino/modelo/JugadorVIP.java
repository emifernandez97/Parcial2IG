package casino.modelo;

public class JugadorVIP extends Jugador {

  private boolean rerollDisponible = true;

  public JugadorVIP(String nombre, String apodo, int dineroInicial) {
	super(nombre, apodo, dineroInicial);
  }

  public void setReroll(boolean disponible) {
	this.rerollDisponible = disponible;
  }

  @Override
  public int calcularApuesta() {
	return (int) (getDinero() * 0.30);
  }

  @Override
  public String obtenerTipoJugador() {
	return "VIP";
  }
  @Override
public void resetParaNuevaPartida(int dineroInicial) {
    // 1. Llama al método de la clase padre (Jugador) 
    //    para resetear dinero, victorias y dados.
    super.resetParaNuevaPartida(dineroInicial); 
    
    // 2. Resetea su propia variable
    this.rerollDisponible = true;
}

  public boolean tieneReroll() {
	return rerollDisponible;
  }

  public void usarReroll() {
	this.rerollDisponible = false;
  }
}
