package casino.controlador;

import casino.modelo.Casino;
import casino.vista.frmJuegoPrincipal; // Importa la NUEVA vista
import casino.modelo.Jugador;
import casino.modelo.JugadorCasino;
import casino.modelo.GestorPersistencia;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.table.DefaultTableModel; // Para manejar la JTable

public class ControladorJuego implements ActionListener {

  private Casino modelo;
  private frmJuegoPrincipal vista;
  private DefaultTableModel tableModel;

  // Constructor: Recibe el modelo y la vista del juego
  public ControladorJuego(Casino modelo, frmJuegoPrincipal vista) {
	this.modelo = modelo;
	this.vista = vista;

	// Configura el modelo de la tabla
	// Usamos el 'getter' que creamos en la vista
	this.tableModel = (DefaultTableModel) this.vista.getTblDatos().getModel();

	// --- Le decimos a la Vista que "escuche" a ESTE controlador ---
	this.vista.getBtnSiguienteRonda().addActionListener(this);
	this.vista.getMniSalir().addActionListener(this);
	this.vista.getMniGuardar().addActionListener(this);
	// (Aquí irán los 'listeners' para los otros botones del menú)

	this.vista.getBtnSiguientePartida().addActionListener(this);
  }

  /**
   * Este método se llamará una vez para cargar los datos iniciales en la
   * ventana del juego.
   */
 public void actualizarVistaInicial() {
    // Limpiamos la tabla
    tableModel.setRowCount(0);

    // Cargamos los jugadores en la tabla
    for (Jugador j : modelo.getJugadores()) {
        if (j instanceof JugadorCasino) {
            continue;
        }

        Object[] fila = new Object[6]; // 6 columnas
        fila[0] = j.getNombre() + " (" + j.getApodo() + ")";
        fila[1] = j.obtenerTipoJugador();
        fila[2] = j.getDinero();
        
        // ¡CORREGIDO! No recalcula, solo muestra
        // (La apuesta se recalculará en la siguiente ronda de todos modos)
        fila[3] = 0; // O j.calcularApuesta(), como prefieras
        
        // ¡CORREGIDO! Lee el dado guardado del modelo
        fila[4] = j.getUltimoResultadoDados(); 
        
        fila[5] = j.getPartidasGanadas();
        tableModel.addRow(fila);
    }
    
    // Lee la Partida del modelo (esto ya lo tenías bien)
    this.vista.getLblPartida().setText("Partida: " + modelo.getPartidasJugadas() + " / " + modelo.getPartidasMaximas());
    
    // ¡CORREGIDO! Lee la Ronda guardada del modelo
    this.vista.getLblRonda().setText("Ronda: " + modelo.getRondasJugadas() + " / " + modelo.getRondasMaximas()); 
    
    // ¡CORREGIDO! Lee el Pozo guardado del modelo
    this.vista.getLblPozo().setText("Pozo Acumulado: $" + modelo.getPozoRondaActual());

    // Añadimos un log inicial
    this.vista.getTxtLog().append("¡El juego ha comenzado!\n");
    
    // --- LÓGICA ADICIONAL ---
    // Si cargamos una partida que ya terminó, deshabilita el botón de ronda.
    if (modelo.seAlcanzaronRondasMaximas()) {
        finDelJuego(); // Llama a finDelJuego para deshabilitar/habilitar botones
    }
}

  // Este método se ejecutará cuando se haga clic en "Siguiente Ronda" o "Salir"
  @Override
  public void actionPerformed(ActionEvent e) {

	if (e.getSource() == this.vista.getBtnSiguienteRonda()) {
	  jugarRonda();
	}

	if (e.getSource() == this.vista.getMniSalir()) {
	  // Aquí podríamos preguntar "¿Seguro que quieres salir?"
	  System.exit(0);
	}

	if (e.getSource() == this.vista.getMniGuardar()) {
	  guardarPartida();
	}
	if (e.getSource() == this.vista.getBtnSiguientePartida()) {
	  siguientePartida();
	}
  }

  private void guardarPartida() {
	try {
	  GestorPersistencia gestor = new GestorPersistencia();
	  gestor.guardarPartida(this.modelo);

	  // Informa al usuario por el JTextArea
	  this.vista.getTxtLog().append("\n--- ¡Partida guardada exitosamente! ---\n");

	} catch (IOException e) {
	  // Informa el error
	  this.vista.getTxtLog().append("\n--- ERROR: No se pudo guardar la partida. "
			  + e.getMessage() + " ---\n");
	}
  }

  // Lógica principal del juego
  private void jugarRonda() {
	// 1. Ejecutamos la lógica del Modelo
	java.util.List<String> logEventos = modelo.jugarUnaRonda(); // Nota: necesitamos 'jugadores'
	// Pequeño ajuste: pasar la lista de jugadores al modelo
	// O mejor, modificar 'jugarRonda' en Casino.java

	// ------ (Vayamos al Casino.java un momento) ------
	// En Casino.java, tu método 'jugarUnaRonda' es:
	// public void jugarUnaRonda() {
	//     juego.jugarRonda(jugadores); // ¡Perfecto! 'jugadores' ya es un atributo de Casino
	//     this.rondasJugadas++;
	// }
	// ¡Pero este método no nos devuelve el LOG!
	// *** MODIFICACIÓN NECESARIA EN Casino.java ***
	// Cambia 'jugarUnaRonda' por esto:
	/*
        public java.util.List<String> jugarUnaRonda() {
            java.util.List<String> log = juego.jugarRonda(jugadores); 
            this.rondasJugadas++;
            return log; // Devuelve el log
        }
	 */
	// ------ (Fin de la modificación) ------
	// (Asumiendo que hiciste el cambio en Casino.java...)
	// 1. Ejecutamos la lógica del Modelo y obtenemos el log
	// 2. Actualizamos el Log en la Vista
	for (String linea : logEventos) {
	  this.vista.getTxtLog().append(linea + "\n");
	}

	// 3. Actualizamos la Tabla con los nuevos datos
	actualizarDatosTabla();

	// 4. Actualizamos los JLabels superiores
	this.vista.getLblRonda().setText("Ronda: " + modelo.getRondasJugadas() + " / " + modelo.getRondasMaximas());
	// (El pozo se resetea cada ronda, el log ya dice quién ganó)
	this.vista.getLblPozo().setText("Pozo Acumulado: $" + modelo.getPozoRondaActual());
	// 5. Verificamos si el juego terminó
	boolean sinJugadores = modelo.actualizarEstadoJugadores();
	boolean sinRondas = modelo.seAlcanzaronRondasMaximas();

	if (sinJugadores || sinRondas) {
	  finDelJuego();
	}
  }

  private void actualizarDatosTabla() {
	// Actualiza la JTable con el dinero y victorias
	int fila = 0;
	for (Jugador j : modelo.getJugadores()) {
	  if (j instanceof JugadorCasino) {
		continue;
	  }

	  tableModel.setValueAt(j.getDinero(), fila, 2); // Columna Dinero
	  tableModel.setValueAt(j.calcularApuesta(), fila, 3);
	  tableModel.setValueAt(j.getUltimoResultadoDados(), fila, 4);
	  tableModel.setValueAt(j.getPartidasGanadas(), fila, 5); // Columna Victorias

	  // (La apuesta y los dados se podrían actualizar, pero es más complejo)
	  // (Por ahora, el log lo informa)
	  fila++;
	}
  }

  private void finDelJuego() {
	// Informa el fin de la PARTIDA actual
	this.vista.getTxtLog().append("====== FIN DE LA PARTIDA " + modelo.getPartidasJugadas() + " ======\n");

	// Deshabilitamos el botón de ronda
	this.vista.getBtnSiguienteRonda().setEnabled(false);

	// --- ¡NUEVA LÓGICA DE DECISIÓN! ---
	if (modelo.seAlcanzaronPartidasMaximas()) {
	  // --- SÍ se jugaron todas las partidas ---
	  this.vista.getTxtLog().append("====== FIN DEL JUEGO COMPLETO ======\n");

	  Jugador ganador = modelo.obtenerGanadorFinal();
	  if (ganador != null) {
		this.vista.getTxtLog().append("El ganador final es: " + ganador.getNombre() + "\n");
	  } else {
		this.vista.getTxtLog().append("No hay ganador.\n");
	  }

	  // Deshabilitamos "Siguiente Partida" porque no hay más
	  this.vista.getBtnSiguientePartida().setEnabled(false);

	} else {
	  // --- NO, AÚN QUEDAN PARTIDAS ---
	  this.vista.getTxtLog().append("¡Preparense para la siguiente partida!\n");

	  // Habilitamos el botón de "Siguiente Partida"
	  this.vista.getBtnSiguientePartida().setEnabled(true);
	}

  }

  /**
   * Contiene la lógica del botón "Siguiente Partida"
   */
  private void siguientePartida() {
	// 1. Le dice al Modelo que se resetee (esto incrementa partidasJugadas)
	modelo.iniciarSiguientePartida();

	// 2. Actualiza la Vista (la JTable Y los JLabels)
	//    ¡Reutilizamos el método que ya tenías!
	actualizarVistaInicial();

	// 3. Limpia el log de la partida anterior
	//    (y muestra el número de la nueva partida)
	this.vista.getTxtLog().setText("¡Comienza la Partida " + modelo.getPartidasJugadas() + "!\n");

	// 4. Invierte los botones
	this.vista.getBtnSiguienteRonda().setEnabled(true);
	this.vista.getBtnSiguientePartida().setEnabled(false);
  }
}
