package parcial2ig; // <-- ¡Importante! Porque está en ese paquete

// 1. Importamos las 3 partes del MVC
import casino.modelo.Casino;
import casino.vista.frmInicio;
import casino.vista.frmJuegoPrincipal;
import casino.controlador.ControladorConfiguracion;
import javax.swing.SwingUtilities;

public class Parcial2IG {

    public static void main(String[] args) {
        
        // Este código asegura que la interfaz se inicie de forma segura
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                
                // 1. Creamos el Modelo (La cocina)
                Casino modelo = new Casino();
                
                // 2. Creamos la Vista (El salón)
                frmInicio vista = new frmInicio();
                frmJuegoPrincipal vistaDos = new frmJuegoPrincipal();
                
                // 3. Creamos el Controlador (El camarero)
                //    y le damos acceso al Modelo y la Vista
                ControladorConfiguracion controlador = new ControladorConfiguracion(modelo, vista);
                
                // 4. Hacemos visible la Vista
                vista.setVisible(true);
            }
        });
    }
}