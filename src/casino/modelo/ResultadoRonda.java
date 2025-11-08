package casino.modelo;

public class ResultadoRonda {
    private final Jugador jugador;
    private final int suma;
    
    public ResultadoRonda(Jugador j, int s) { 
        this.jugador = j; 
        this.suma = s; 
    }
    
    public Jugador getJugador() { return jugador; }
    public int getSuma() { return suma; }
}