package Sistema.Lab_Est;


import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import Excecoes.ReservaNaoDisponivelException;
import Sistema.Usuarios.Usuario;

public class Estacao {
    private int id; 
    private StatusEstacao status;
    private List<String> historicoManutencao;
    private List<Reserva> listaDeReservas;

    public Estacao(int id) {
        this.id = id;
        status = StatusEstacao.DISPONIVEL;
        historicoManutencao = new LinkedList<>();
        this.listaDeReservas = new LinkedList<>();
    }

    // Método principal que o Menu chama para tentar reservar
    public boolean adicionarReserva(Usuario usuario, LocalDateTime inicio, LocalDateTime fim) throws ReservaNaoDisponivelException {
        
        // Verifica se a estação está em manutenção (Bloqueada)
        if (this.status != StatusEstacao.DISPONIVEL) {
            throw new ReservaNaoDisponivelException("A estação está " + this.status);
        }

        // Verifica conflito de horário com reservas existentes
        for (Reserva r : listaDeReservas) {
            if (inicio.isBefore(r.getFim()) && fim.isAfter(r.getInicio())) {
                throw new ReservaNaoDisponivelException("Já existe uma reserva neste horário (" 
                    + r.getInicio().toString() + " até " + r.getFim().toString() + ")");
            }
        }

        Reserva novaReserva = new Reserva(usuario, inicio, fim);
        listaDeReservas.add(novaReserva);
        return true;
    }

    public int getId(){ 
        return id; 
    }

    public StatusEstacao getStatus(){ 
        return status; 
    }

    public List<String> getHistoricoManutencao(){
        return historicoManutencao; 
    }

    public List<Reserva> getReservas() {
        return listaDeReservas;
    }
    
    public void setStatus(StatusEstacao status){
        this.status = status;
    }

    public void registrarManutencao(String descricao) {
        this.status = StatusEstacao.EM_MANUTENCAO;
        historicoManutencao.add("Data: " + LocalDateTime.now().toString() + " - " + descricao);
        System.out.println("Manutenção registrada para Estação " + id);
    }
    
    @Override
    public String toString() {
        return "Estacao [id=" + id + ", status=" + status + "]";
    }
}
