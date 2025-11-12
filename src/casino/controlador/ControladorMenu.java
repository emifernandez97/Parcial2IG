/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package casino.controlador;

import casino.vista.frmJuegoPrincipal;
import casino.vista.frmRanking;
import casino.vista.frmEstadisticas;
import casino.vista.frmHistorial;
import report.ReporteData;
import report.ReporteServiceAdapterFromModelo;


public class ControladorMenu {

    private final frmJuegoPrincipal vista;
    private final ControladorJuego ctrl;
    
    
    public ControladorMenu(frmJuegoPrincipal vista, ControladorJuego ctrl) {
        this.vista = vista;
        this.ctrl = ctrl;
        wire();
    }

    private void wire() {
        vista.getMniRanking().addActionListener(e -> abrirRanking());
        vista.getMniEstadisticas().addActionListener(e -> abrirEstadisticas());
        vista.getMniHistorial().addActionListener(e -> abrirHistorial());
    }

    private ReporteData buildData() {
        // Tomamos los datos directamente del modelo del ControladorJuego
        var jugadores = ctrl.modelo.getJugadores();
        var rondas = new java.util.ArrayList<casino.modelo.ResultadoRonda>();

        // Creamos resultados "instantáneos" usando el último tiro de cada jugador
        for (var j : jugadores) {
            if (j instanceof casino.modelo.JugadorCasino) continue;
            rondas.add(new casino.modelo.ResultadoRonda(j, j.getUltimoResultadoDados()));
        }

        int rondasTotales = ctrl.modelo.getRondasMaximas();
        return ReporteServiceAdapterFromModelo.build(jugadores, rondas, rondasTotales);
    }

    private void abrirRanking() {
        ReporteData data = buildData();
        frmRanking v = new frmRanking();
        v.setRanking(data.ranking());
        v.setLocationRelativeTo(vista);
        v.setVisible(true);
    }

    private void abrirEstadisticas() {
        ReporteData data = buildData();
        frmEstadisticas v = new frmEstadisticas();
        v.setEstadisticas(data);
        v.setLocationRelativeTo(vista);
        v.setVisible(true);
    }

    private void abrirHistorial() {
        ReporteData data = buildData();
        frmHistorial v = new frmHistorial();
        v.setHistorial(data.historialUltimas3());
        v.setLocationRelativeTo(vista);
        v.setVisible(true);
    }
}
