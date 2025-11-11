package casino.controlador;

import casino.modelo.Casino;
import casino.vista.frmJuegoPrincipal; // Importa la NUEVA vista
import casino.modelo.Jugador;
import casino.modelo.JugadorCasino;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
        // (Aquí irán los 'listeners' para los otros botones del menú)
    }
    
    /**
     * Este método se llamará una vez para cargar los datos iniciales
     * en la ventana del juego.
     */
    public void actualizarVistaInicial() {
        // Limpiamos la tabla por si acaso
        tableModel.setRowCount(0); 
        
        // Cargamos los jugadores en la tabla
        for (Jugador j : modelo.getJugadores()) {
            if (j instanceof JugadorCasino) continue; // No mostramos a "La Casa" en la tabla

            Object[] fila = new Object[6]; // 6 columnas
            fila[0] = j.getNombre() + " (" + j.getApodo() + ")";
            fila[1] = j.obtenerTipoJugador();
            fila[2] = j.getDinero();
            fila[3] = j.calcularApuesta(); // Apuesta (aún no han apostado)
            fila[4] = 
            fila[5] = j.getPartidasGanadas();
            tableModel.addRow(fila);
        }
        
        // Actualizamos los JLabels superiores
        this.vista.getLblPartida().setText("Partida: 1 / " + modelo.getRondasMaximas()); // O "Partidas"
        this.vista.getLblRonda().setText("Ronda: 0 / " + modelo.getRondasMaximas()); // O "Rondas"
        this.vista.getLblPozo().setText("Pozo Acumulado: $0");
        
        // Añadimos un log inicial
        this.vista.getTxtLog().append("¡El juego ha comenzado!\n");
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
            if (j instanceof JugadorCasino) continue; 
            
            tableModel.setValueAt(j.getDinero(), fila, 2); // Columna Dinero
            tableModel.setValueAt(j.calcularApuesta(), fila, 3);
            tableModel.setValueAt(j.calcularApuesta(), fila, 4);
            tableModel.setValueAt(j.getPartidasGanadas(), fila, 5); // Columna Victorias
            
            // (La apuesta y los dados se podrían actualizar, pero es más complejo)
            // (Por ahora, el log lo informa)
            
            fila++;
        }
    }
    
    private void finDelJuego() {
        this.vista.getTxtLog().append("====== FIN DEL JUEGO ======\n");
        
        Jugador ganador = modelo.obtenerGanadorFinal();
        if (ganador != null) {
            this.vista.getTxtLog().append("El ganador final es: " + ganador.getNombre() + "\n");
        } else {
            this.vista.getTxtLog().append("No hay ganador.\n");
        }
        
        // Deshabilitamos el botón para que no se juegue más
        this.vista.getBtnSiguienteRonda().setEnabled(false);
        
        // TODO: (Punto 4) Aquí abriríamos la ventana de Reporte Final
    }
}