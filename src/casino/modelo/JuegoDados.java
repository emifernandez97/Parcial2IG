package casino.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
// import java.util.Scanner; // <-- ELIMINADO
import java.util.stream.Collectors;

public class JuegoDados {

    private final Dado dado1;
    private final Dado dado2;

    public JuegoDados() {
        this.dado1 = new Dado();
        this.dado2 = new Dado();
    }

    /**
     * Juega una ronda completa y devuelve el log de eventos.
     * @param jugadores La lista de jugadores.
     * @return Una Lista de Strings que representa el log de la ronda.
     */
    public List<String> jugarRonda(List<Jugador> jugadores) {
        int pozo = 0;
        List<ResultadoRonda> resultados = new ArrayList<>();
        List<String> logEventos = new ArrayList<>(); // <-- ¡NUEVO! Aquí guardamos los eventos.

        logEventos.add("--- Fase de Apuestas ---");
        for (Jugador j : jugadores) {
            int apuesta = j.calcularApuesta();
            if (apuesta > j.getDinero()) {
                apuesta = j.getDinero();
            }
            j.perder(apuesta);
            pozo += apuesta;
            logEventos.add(j.getNombre() + " (" + j.obtenerTipoJugador() + ") apuesta $" + apuesta);
            
            // Las referencias a CasinoDados.mayorApuesta se ELIMINARON
            // El controlador se encargará de esto si es necesario.
        }
        logEventos.add("\nEl pozo total es de: $" + pozo);

        logEventos.add("\n--- Fase de Lanzamientos ---");
        JugadorCasino laCasa = null;
        Jugador jugadorConfundido = null;

        //... (lógica para encontrar a La Casa, igual que antes)
        for (Jugador j : jugadores) {
            if (j instanceof JugadorCasino) {
                laCasa = (JugadorCasino) j;
                break;
            }
        }

        if (laCasa != null) {
            List<Jugador> posiblesVictimas = jugadores.stream()
                    .filter(j -> !(j instanceof JugadorCasino))
                    .collect(Collectors.toList());
            
            if (!posiblesVictimas.isEmpty()) {
                Jugador victima = posiblesVictimas.get(new Random().nextInt(posiblesVictimas.size()));
                
                // Usamos el nuevo método de JugadorCasino
                String logTrampa = laCasa.intentarConfundir(victima);
                if (logTrampa != null) {
                    jugadorConfundido = victima;
                    logEventos.add(logTrampa); // Añadimos el log de la trampa
                }
            }
        }

        for (Jugador j : jugadores) {
            int res1, res2;
            if (j instanceof JugadorCasino casa) {
                // Usamos el nuevo método de JugadorCasino
                Object[] resultadoCasa = casa.lanzarDados(dado1);
                res1 = (Integer) resultadoCasa[0];
                if (resultadoCasa[1] != null) {
                    logEventos.add((String) resultadoCasa[1]); // Añadimos log de dados cargados
                }
                
                resultadoCasa = casa.lanzarDados(dado2);
                res2 = (Integer) resultadoCasa[0];
                if (resultadoCasa[1] != null) {
                    logEventos.add((String) resultadoCasa[1]);
                }
                
            } else {
                res1 = dado1.tirar();
                res2 = dado2.tirar();
                if (j == jugadorConfundido) {
                    logEventos.add("-> ¡" + j.getNombre() + " está confundido! Sus dados se reducen.");
                    res1 = Math.max(1, res1 - 1);
                    res2 = Math.max(1, res2 - 1);
                }
            }

            int suma = res1 + res2;

            if (j instanceof JugadorVIP vip && vip.tieneReroll()) {
                logEventos.add(j.getNombre() + " (VIP) sacó " + res1 + " + " + res2 + " = " + suma);
                
                // ¡Lógica de re-roll modificada!
                // Ya no usamos Scanner. Asumimos que la IA del VIP siempre usa el re-roll.
                logEventos.add("¡" + j.getNombre() + " (VIP) decide usar su re-roll!");
                res1 = dado1.tirar();
                res2 = dado2.tirar();
                suma = res1 + res2;
                vip.usarReroll();
                logEventos.add("¡Nuevo lanzamiento!");
            }
            
            logEventos.add(j.getNombre() + " sacó " + res1 + " + " + res2 + ". Suma total: " + suma);
            
            // Las referencias a CasinoDados.mejorPuntajeDados se ELIMINARON
            
            resultados.add(new ResultadoRonda(j, suma));
        }

        // ... (lógica para encontrar al ganador, igual que antes)
        int maxSuma = resultados.stream().mapToInt(ResultadoRonda::getSuma).max().orElse(0);
        List<Jugador> ganadores = resultados.stream()
                .filter(r -> r.getSuma() == maxSuma)
                .map(ResultadoRonda::getJugador)
                .toList();

        logEventos.add("\n--- Resultados de la Ronda ---");
        if (ganadores.size() == 1) {
            Jugador ganador = ganadores.get(0);
            ganador.ganar(pozo);
            logEventos.add("¡El ganador es " + ganador.getNombre() + " y se lleva $" + pozo + "!");
        } else {
            logEventos.add("¡Hubo un empate entre " + ganadores.size() + " jugadores!");
            int premioDividido = pozo / ganadores.size();
            for (Jugador g : ganadores) {
                g.ganar(premioDividido);
                logEventos.add(g.getNombre() + " gana $" + premioDividido);
            }
        }
        
        return logEventos; // <-- ¡DEVOLVEMOS EL LOG!
    }
}