package Sistema.Lab_Est;

import Sistema.Usuarios.Usuario;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Reserva {
    private Usuario usuario;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private boolean checkInRealizado;

    public Reserva(Usuario usuario, LocalDateTime dataInicio, LocalDateTime dataFim) {
        this.usuario = usuario;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.checkInRealizado = false;
    }

    // Getters
    public LocalDateTime getInicio() {
        return dataInicio;
    }

    public LocalDateTime getFim() {
        return dataFim;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public boolean isCheckInRealizado() {
        return checkInRealizado;
    }

    public void setCheckInRealizado(boolean checkInRealizado) {
        this.checkInRealizado = checkInRealizado;
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM HH:mm");
        String status = checkInRealizado ? " [EM USO/CONCLUÍDA]" : " [PENDENTE]";
        return "Reserva: " + usuario.getNome() + 
               " | Início: " + dataInicio.format(fmt) + 
               " | Fim: " + dataFim.format(fmt) + status;
    }
}