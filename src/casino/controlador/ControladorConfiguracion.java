package casino.controlador;

import casino.modelo.Casino;
import casino.modelo.Jugador;
import casino.vista.frmInicio; 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultListModel; // Importante para manejar la JList

public class ControladorConfiguracion implements ActionListener {

    private Casino modelo;
    private frmInicio vista;
    
    // Este "modelo" no es el del MVC, sino el de la JList
    // Sirve para agregar o quitar items de la lista dinámicamente.
    private DefaultListModel<String> listModel;

    public ControladorConfiguracion(Casino modelo, frmInicio vista) {
        this.modelo = modelo;
        this.vista = vista;
        
        // Obtenemos el "modelo" de la JList desde la vista
        // (Asumo que tu JList 'lstJugadoresRegistrados' usa un DefaultListModel)
        // Si no lo hace, dímelo y lo ajustamos.
        // Por ahora, creamos uno nuevo y se lo asignamos.
        this.listModel = new DefaultListModel<>();
        this.vista.getLstJugadoresRegistrados().setModel(this.listModel);

        // --- Le decimos a la Vista que "escuche" a ESTE controlador ---
        
        // Usamos los nombres de TUS variables:
        this.vista.getBtnAgregarJugador().addActionListener(this);
        this.vista.getBtnLimpiar().addActionListener(this);
        this.vista.getBtnEliminarJugador().addActionListener(this);
        this.vista.getBtnIniciarJuego().addActionListener(this);
        this.vista.getBtnCargarJuego().addActionListener(this);
        this.vista.getBtnSalir().addActionListener(this);
        // this.vista.getBtnModificarJugador().addActionListener(this); // (Lo dejamos para después)
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        // --- Lógica del Botón AGREGAR JUGADOR ---
        if (e.getSource() == this.vista.getBtnAgregarJugador()) {
            // ¡Llamamos a nuestro nuevo método con lógica real!
            agregarJugador();
        }
        
        // --- Lógica del Botón LIMPIAR ---
        if (e.getSource() == this.vista.getBtnLimpiar()) {
            // ¡Llamamos a nuestro nuevo método con lógica real!
            limpiarCampos();
        }
        
        // --- Lógica del Botón ELIMINAR JUGADOR ---
        if (e.getSource() == this.vista.getBtnEliminarJugador()) {
            eliminarJugador();
        }
        
        // --- Lógica del Botón SALIR ---
        if (e.getSource() == this.vista.getBtnSalir()) {
            System.exit(0); // Cierra la aplicación
        }
        
        // --- Lógica del Botón INICIAR JUEGO ---
        if (e.getSource() == this.vista.getBtnIniciarJuego()) {
            iniciarJuego();
        }
    }

    // --- MÉTODOS DE LÓGICA ---

    /**
     * Contiene la lógica del botón "Agregar Jugador"
     */
    private void agregarJugador() {
        // 1. Limpiamos cualquier error anterior
        this.vista.getLblManejoDeError().setText(" ");

        // 2. Obtenemos los datos de la Vista
        String nombre = this.vista.getTxtNombre().getText();
        String apodo = this.vista.getTxtApodo().getText();
        String dineroStr = this.vista.getTxtDineroInicial().getText();
        
        // (Obtenemos el item seleccionado, ej: "Novato")
        String tipoStr = (String) this.vista.getCmbTipoJugador().getSelectedItem(); 
        
        int dineroInicial;
        
        //[cite_start]// 3. Validaciones (¡Aquí usamos el PDF!) [cite: 53]
        
        // Validación de Apodo (la teníamos en CasinoDados.java)
        if (apodo.length() < 3 || apodo.length() > 10 || !apodo.matches("[a-zA-Z ]+")) {
            this.vista.getLblManejoDeError().setText("Error: Apodo inválido (3-10 letras/espacios).");
            return; // Detenemos la ejecución
        }
        
        // Validación de Dinero (¡Nueva!)
        try {
            dineroInicial = Integer.parseInt(dineroStr);
            if (dineroInicial <= 0) {
                this.vista.getLblManejoDeError().setText("Error: El dinero debe ser positivo.");
                return;
            }
        } catch (NumberFormatException ex) {
            this.vista.getLblManejoDeError().setText("Error: Dinero inicial debe ser un número.");
            return;
        }
        
        // Validación de Nombre (simple)
        if (nombre.trim().isEmpty()) {
            this.vista.getLblManejoDeError().setText("Error: El nombre no puede estar vacío.");
            return;
        }

        // 4. Mapeo de Tipo de Jugador (Convertir String a int)
        int tipoInt;
        switch (tipoStr) {
            case "Experto": tipoInt = 2; break;
            case "VIP": tipoInt = 3; break;
            default: tipoInt = 1; // Novato
        }

        // 5. Si todo está OK, le damos la orden al Modelo
        
        // Le pasamos el dinero al modelo (lo usará si creamos otro jugador)
        this.modelo.setDineroInicial(dineroInicial); 
        
        Jugador nuevoJugador = this.modelo.crearJugador(nombre, apodo, tipoInt);
        this.modelo.agregarJugador(nuevoJugador);

        // 6. Actualizamos la Vista
        // Añadimos al jugador al "modelo" de la JList
        String infoJugador = nuevoJugador.getNombre() + " (" + nuevoJugador.getApodo() + ") - $" + nuevoJugador.getDinero();
        this.listModel.addElement(infoJugador);
        
        // 7. Limpiamos los campos
        limpiarCampos();
    }

    /**
     * Limpia los campos de texto y errores
     */
    private void limpiarCampos() {
        this.vista.getTxtNombre().setText("");
        this.vista.getTxtApodo().setText("");
        this.vista.getTxtDineroInicial().setText(""); // O poner el default, ej: "500"
        this.vista.getCmbTipoJugador().setSelectedIndex(0); // Vuelve a "Novato"
        this.vista.getLblManejoDeError().setText(" ");
        this.vista.getTxtNombre().requestFocus(); // Pone el cursor en "Nombre"
    }

    /**
     * Contiene la lógica del botón "Eliminar Jugador"
     */
    private void eliminarJugador() {
        // 1. Vemos qué item está seleccionado en la lista
        int indiceSeleccionado = this.vista.getLstJugadoresRegistrados().getSelectedIndex();

        if (indiceSeleccionado == -1) {
            // No hay nada seleccionado
            this.vista.getLblManejoDeError().setText("Error: Selecciona un jugador de la lista para eliminar.");
            return;
        }

        // 2. Quitamos al jugador del Modelo
        // ¡OJO! La Casa está en el índice 0 del Modelo, pero no en la Vista.
        // Así que el índice de la vista + 1 es el índice del modelo.
        this.modelo.getJugadores().remove(indiceSeleccionado + 1);

        // 3. Quitamos al jugador de la Vista (del listModel)
        this.listModel.remove(indiceSeleccionado);
        
        this.vista.getLblManejoDeError().setText(" ");
    }
    
    /**
     * Contiene la lógica del botón "Iniciar Juego"
     */
    private void iniciarJuego() {
        //[cite_start]// Validación de jugadores (del PDF) [cite: 63]
        // (modelo.getJugadores() incluye a "La Casa", por eso comparamos con 3 y 5)
        int totalJugadores = modelo.getJugadores().size();
        
        if (totalJugadores < 3 || totalJugadores > 5) { // 1 Casa + (2 a 4) Humanos
            this.vista.getLblManejoDeError().setText("Error: Se necesita un total de 2 a 4 jugadores humanos.");
            return;
        }
        
        // ¡Todo listo para empezar!
        this.vista.getLblManejoDeError().setText("¡Iniciando juego!");
        System.out.println("¡Iniciando el juego!");
        
        // Aquí es donde ocultaríamos esta ventana
        this.vista.setVisible(false);
        
        //[cite_start]// Y crearíamos la VentanaPrincipalJuego (Paso 3 del PDF) [cite: 66]
        // (Eso lo haremos después)
    }
}