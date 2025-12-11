package Sistema.Lab_Est;

import Sistema.Usuarios.Usuario;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Reserva {
    private Usuario usuario;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;

    public Reserva(Usuario usuario, LocalDateTime dataInicio, LocalDateTime dataFim) {
        this.usuario = usuario;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
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

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM HH:mm");
        return "Reserva: " + usuario.getNome() + 
               " | Início: " + dataInicio.format(fmt) + 
               " | Fim: " + dataFim.format(fmt);
    }
}