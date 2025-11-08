package casino.modelo;

import java.util.Random;

public class Dado {
    private final Random random;

    public Dado() {
        this.random = new Random();
    }

    public int tirar() {
        return random.nextInt(6) + 1;
    }
}
