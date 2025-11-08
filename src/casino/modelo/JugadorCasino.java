package casino.modelo;

public class JugadorCasino extends Jugador {

    private static final double PROBABILIDAD_DADOS_CARGADOS = 0.40; // 40%
    private static final double PROBABILIDAD_CONFUNDIR = 0.30; // 30%

    public JugadorCasino(String nombre, String apodo, int dineroInicial) {
        super(nombre, apodo, dineroInicial);
    }

    @Override
    public int calcularApuesta() {
        return 0; // La Casa no apuesta
    }

    @Override
    public String obtenerTipoJugador() {
        return "La Casa";
    }

    /**
     * Lanza los dados.
     * @param dadoNormal El dado a usar.
     * @return Un array de Object: 
     * - Indice 0 (Integer): El resultado del dado.
     * - Indice 1 (String): El mensaje de log (o null si no hay).
     */
    public Object[] lanzarDados(Dado dadoNormal) {
        if (Math.random() < PROBABILIDAD_DADOS_CARGADOS) {
            // System.out.println("-> ¡La Casa usa sus dados cargados!"); // <-- ELIMINADO
            // CasinoDados.registrarTrampa(...); // <-- ELIMINADO

            // En vez de imprimir, DEVOLVEMOS los datos.
            return new Object[]{ 6, "¡La Casa usa sus dados cargados!" };
        } else {
            return new Object[]{ dadoNormal.tirar(), null };
        }
    }

    /**
     * Intenta confundir a un jugador.
     * @param objetivo El jugador a confundir.
     * @return Un String con el log si la trampa funciona, o null si falla.
     */
    public String intentarConfundir(Jugador objetivo) {
        if (Math.random() < PROBABILIDAD_CONFUNDIR) {
            // System.out.println(...); // <-- ELIMINADO
            // CasinoDados.registrarTrampa(...); // <-- ELIMINADO
            
            // DEVOLVEMOS el log
            return "¡La Casa intenta confundir a " + objetivo.getNombre() + "!";
        }
        return null; // No hubo trampa
    }
}