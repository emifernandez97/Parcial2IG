package casino.modelo;

import java.io.*; // Importamos todas las herramientas de Input/Output
import java.util.List;

public class GestorPersistencia {

  // El nombre del archivo donde guardaremos todo.
  private static final String NOMBRE_ARCHIVO = "partida_guardada.txt";
  // El separador que usaremos (CSV: Comma Separated Values)
  private static final String DELIMITADOR = ",";

  /**
   * Guarda el estado actual del Casino en un archivo de texto.
   *
   * @param casino El objeto Casino que contiene el estado del juego.
   * @throws IOException Si ocurre un error al escribir en el archivo.
   */
  public void guardarPartida(Casino casino) throws IOException {

	try (BufferedWriter writer = new BufferedWriter(new FileWriter(NOMBRE_ARCHIVO))) {

	  // --- 1. GUARDAR CONFIGURACIÓN GENERAL ---
	  // Formato: RondasJugadas,RondasMaximas,PozoActual,PartidasJugadas,PartidasMaximas
	  writer.write(casino.getRondasJugadas() + DELIMITADOR
			  + casino.getRondasMaximas() + DELIMITADOR
			  + casino.getPozoRondaActual() + DELIMITADOR
			  + // <-- NUEVO
			  casino.getPartidasJugadas() + DELIMITADOR
			  + // <-- NUEVO
			  casino.getPartidasMaximas()); // <-- NUEVO
	  writer.newLine();

	  // --- 2. GUARDAR CADA JUGADOR ---
	  for (Jugador j : casino.getJugadores()) {
		if (j instanceof JugadorCasino) {
		  continue;
		}

		// Preparamos los datos
		String tipo = j.obtenerTipoJugador();
		String nombre = j.getNombre();
		String apodo = j.getApodo();
		int dinero = j.getDinero();
		int victorias = j.getPartidasGanadas();
		int dados = j.getUltimoResultadoDados(); // <-- NUEVO

		// Formato: Tipo,Nombre,Apodo,Dinero,Victorias,UltimoResultadoDados
		String linea = tipo + DELIMITADOR + nombre + DELIMITADOR + apodo
				+ DELIMITADOR + dinero + DELIMITADOR + victorias
				+ DELIMITADOR + dados; // <-- NUEVO

		// 3. Guardar datos específicos (si es VIP)
		if (j instanceof JugadorVIP vip) {
		  linea += DELIMITADOR + vip.tieneReroll();
		}

		writer.write(linea);
		writer.newLine();
	  }

	  writer.flush();
	  System.out.println("Partida guardada exitosamente en " + NOMBRE_ARCHIVO);

	} catch (IOException e) {
	  System.err.println("Error al guardar la partida: " + e.getMessage());
	  throw e;
	}
  }

  /**
   * Carga el estado del juego desde el archivo de guardado.
   *
   * @return Un NUEVO objeto Casino con el estado cargado.
   * @throws IOException Si el archivo no se encuentra o no se puede leer.
   */
  // Dentro de GestorPersistencia.java
  /**
   * Carga el estado del juego desde el archivo de guardado.
   *
   * @return Un NUEVO objeto Casino con el estado cargado.
   * @throws IOException Si el archivo no se encuentra o no se puede leer.
   */
  public Casino cargarPartida() throws IOException {

	File archivo = new File(NOMBRE_ARCHIVO);
	if (!archivo.exists()) {
	  throw new FileNotFoundException("No se encontró el archivo de guardado: " + NOMBRE_ARCHIVO);
	}

	// --- CAMBIO CLAVE AQUÍ ---
	// En lugar de crear un Casino vacío y que su constructor pise los valores,
	// creamos un Casino que sabremos configurar.
	// O, más simple: DEJAMOS QUE EL CONSTRUCTOR LO INICIALICE, y luego PISAMOS con los valores cargados.
	Casino casinoCargado = new Casino();
	// Ahora, el constructor ya añadió "La Casa". Debemos limpiar los otros jugadores.
	// Esto es importante si el constructor de Casino agrega jugadores por defecto además de La Casa.
	// Si tu constructor de Casino SOLO añade La Casa, este paso no es estrictamente necesario,
	// pero es una buena práctica para asegurar que solo tengamos los cargados.
	casinoCargado.getJugadores().removeIf(j -> !(j instanceof JugadorCasino)); // Elimina todo excepto La Casa

	try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {

	  // --- 1. LEER LA CONFIGURACIÓN GENERAL ---
	  String lineaConfig = reader.readLine();
	  String[] config = lineaConfig.split(DELIMITADOR);

	  int rondasJugadas = Integer.parseInt(config[0]);
	  int rondasMaximas = Integer.parseInt(config[1]);
	  int pozo = Integer.parseInt(config[2]);
	  int partidasJugadas = Integer.parseInt(config[3]);
	  int partidasMaximas = Integer.parseInt(config[4]);

	  // --- ASIGNACIÓN CORRECTA DE VALORES CARGADOS ---
	  casinoCargado.setRondasJugadas(rondasJugadas);
	  casinoCargado.setRondasMaximas(rondasMaximas);
	  casinoCargado.setPozoRondaActual(pozo);
	  casinoCargado.setPartidasJugadas(partidasJugadas);
	  casinoCargado.setPartidasMaximas(partidasMaximas);
	  // --- FIN ASIGNACIÓN ---

	  // --- 2. LEER Y "RE-CONSTRUIR" CADA JUGADOR ---
	  String lineaJugador;
	  while ((lineaJugador = reader.readLine()) != null) {
		String[] datos = lineaJugador.split(DELIMITADOR);

		String tipo = datos[0];
		String nombre = datos[1];
		String apodo = datos[2];
		int dinero = Integer.parseInt(datos[3]);
		int victorias = Integer.parseInt(datos[4]);
		int dados = Integer.parseInt(datos[5]);

		Jugador j = null;

		switch (tipo) {
		  case "VIP":
			j = new JugadorVIP(nombre, apodo, dinero);
			boolean reroll = Boolean.parseBoolean(datos[6]);
			((JugadorVIP) j).setReroll(reroll);
			break;
		  case "Experto":
			j = new JugadorExperto(nombre, apodo, dinero);
			break;
		  case "Novato":
		  default:
			j = new JugadorNovato(nombre, apodo, dinero);
			break;
		}

		j.setPartidasGanadas(victorias);
		j.setUltimoResultadoDados(dados); // <--- ESTO YA LO TENÍAS Y ESTÁ BIEN

		casinoCargado.agregarJugador(j);
	  }

	  System.out.println("Partida cargada exitosamente.");
	  return casinoCargado;

	} catch (IOException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
	  System.err.println("Error al cargar la partida (archivo corrupto?): " + e.getMessage());
	  throw new IOException("Error al leer el archivo de guardado.", e);
	}
  }
}
