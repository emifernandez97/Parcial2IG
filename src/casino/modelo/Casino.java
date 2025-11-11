package casino.modelo;

import java.util.ArrayList;
import java.util.List;

public class Casino {

  private final List<Jugador> jugadores;
  private final JuegoDados juego;
  private int rondasJugadas;

  // Guardamos la configuración de la partida
  private int rondasMaximas = 3;
  private int dineroInicial = 500;
  private int pozoRondaActual; // <-- ¡NUEVO!
  private int partidasJugadas;
  private int partidasMaximas;

  public Casino() {
	this.jugadores = new ArrayList<>();
	this.juego = new JuegoDados(); // El juego ahora se crea aquí
	this.rondasJugadas = 0;

	this.partidasJugadas = 1; // Empezamos en la partida 1
	this.partidasMaximas = 3; // Valor por defecto

	Jugador laCasa = new JugadorCasino("La Casa", "Casino", 99999);
	this.jugadores.add(laCasa);
	//System.out.println("-> La Casa se ha unido a la mesa.");
  }

  public List<Jugador> getJugadores() {
	return jugadores;
  }

  public int getPozoRondaActual() {
	return pozoRondaActual;
  }

  public void setPartidasJugadas(int partidasJugadas) {
	this.partidasJugadas = partidasJugadas;
  }

  public void setPozoRondaActual(int pozo) {
	this.pozoRondaActual = pozo;
  }

  public void setRondasJugadas(int rondasJugadas) {
	this.rondasJugadas = rondasJugadas;
  }

  public int getRondasJugadas() {
	return rondasJugadas;
  }

  // Métodos para que el Controlador configure el juego
  public void setRondasMaximas(int rondas) {
	this.rondasMaximas = rondas;
  }

  public int getRondasMaximas() {
	return this.rondasMaximas;
  }

  public void setDineroInicial(int dinero) {
	this.dineroInicial = dinero;
  }

  public int getDineroInicial() {
	return this.dineroInicial;
  }

  public void agregarJugador(Jugador jugador) {
	jugadores.add(jugador);
  }

  public Jugador crearJugador(String nombre, String apodo, int tipo) {

	return switch (tipo) {
	  case 1 ->
		new JugadorNovato(nombre, apodo, dineroInicial);
	  case 2 ->
		new JugadorExperto(nombre, apodo, dineroInicial);
	  case 3 ->
		new JugadorVIP(nombre, apodo, dineroInicial);
	  default -> {
		//System.out.println("Tipo inválido. Se asignará como Novato.");
		yield new JugadorNovato(nombre, apodo, dineroInicial);
	  }
	};
  }

  public java.util.List<String> jugarUnaRonda() {
	// Le pasamos 'this' para que JuegoDados pueda actualizarnos el pozo
	java.util.List<String> logDeLaRonda = juego.jugarRonda(jugadores, this);
	this.rondasJugadas++;
	return logDeLaRonda;
  }

  /**
   * Elimina a los jugadores que se quedaron sin dinero. Devuelve true si el
   * juego debe terminar (quedan menos de 2 jugadores).
   */
  public boolean actualizarEstadoJugadores() {
	jugadores.removeIf(j -> {
	  if (j.getDinero() <= 0 && !(j instanceof JugadorCasino)) {
		// System.out.println(...); // <-- ¡ELIMINADO!
		// ¿Cómo informamos esto? El Controlador lo revisará.
		return true;
	  }
	  return false;
	});

	if (jugadores.size() < 2) {
	  return true; // Termina el juego
	}
	return false; // El juego continúa
  }

  /**
   * Verifica si se alcanzó el límite de rondas.
   */
  public boolean seAlcanzaronRondasMaximas() {
	return this.rondasJugadas >= this.rondasMaximas;
  }

  public Jugador obtenerGanadorFinal() {
	if (jugadores.isEmpty()) {
	  return null;
	}

	Jugador ganadorFinal = null;
	int maxDinero = -1;
	for (Jugador j : jugadores) {
	  if (j.getDinero() > maxDinero) {
		maxDinero = j.getDinero();
		ganadorFinal = j;
	  }
	}
	return ganadorFinal;
  }

  // --- Getters y Setters para PARTIDAS ---
  public int getPartidasJugadas() {
	return partidasJugadas;
  }

  public int getPartidasMaximas() {
	return partidasMaximas;
  }

  public void setPartidasMaximas(int partidasMaximas) {
	this.partidasMaximas = partidasMaximas;
  }

  /**
   * Resetea el estado del juego para una nueva partida.
   */
  public void iniciarSiguientePartida() {
	this.rondasJugadas = 0;
	this.pozoRondaActual = 0;

	// Incrementa el contador de partidas
	this.partidasJugadas++;

	for (Jugador j : jugadores) {
	  if (j instanceof JugadorCasino) {
		continue;
	  }
	  // Llama al método de reseteo del jugador
	  // (Asumiendo que ya lo creaste en Jugador.java y JugadorVIP.java)
	  j.resetParaNuevaPartida(this.dineroInicial);
	}
  }

  /**
   * Verifica si se alcanzó el límite de partidas.
   *
   * @return true si se jugaron todas las partidas.
   */
  public boolean seAlcanzaronPartidasMaximas() {
	// Comparamos si las jugadas son MAYORES O IGUALES a las máximas
	return this.partidasJugadas >= this.partidasMaximas;
  }

}
