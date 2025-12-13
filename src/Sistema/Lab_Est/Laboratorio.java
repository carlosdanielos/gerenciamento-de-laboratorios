package Sistema.Lab_Est;


import Excecoes.ReservaNaoDisponivelException;
import Sistema.Usuarios.Usuario;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class Laboratorio {
    private String nome;
    private StatusLaboratorio status;
    private List<Estacao> estacoes;
    private int capacidade;
    private List<Reserva> listaDeReservas;


    public Laboratorio(String nome, int capacidadeMaxima) {
        this.nome = nome;
        capacidade = capacidadeMaxima;
        status = StatusLaboratorio.ABERTO;
        estacoes = new LinkedList<>();
        this.listaDeReservas = new LinkedList<>();

        for (int id = 1; id <= capacidadeMaxima; id++) {
            Estacao novaEstacao = new Estacao(id); 
            this.estacoes.add(novaEstacao);     
        }
    }

    // Método principal que o Menu chama para tentar reservar
    public boolean adicionarReserva(Usuario usuario, LocalDateTime inicio, LocalDateTime fim) throws ReservaNaoDisponivelException {

        // Verifica se a estação está em manutenção (Bloqueada)
        if (this.status != StatusLaboratorio.ABERTO) {
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

    public void adicionarEstacao(Estacao estacao) {
        if (!isFull()) {
            estacoes.add(estacao);
            System.out.println("Estação " + estacao.getId() + " adicionada ao " + this.nome);
        } else {
            System.out.println("Laboratório " + this.nome + " está com a capacidade máxima.");
        }
    }


    private boolean isFull(){
        if(estacoes == null){
            return false;
        } else if (estacoes.size() >= capacidade) {
            return true;
        }else{
            return false;
        }
    }

    public String getNome(){
        return nome; 
    }

    public StatusLaboratorio getStatus(){ 
        return status; 
    }

    public int getCapacidade(){
        return capacidade;
    }

    public List<Estacao> getEstacoes(){
        return estacoes; 
    }

    public void setStatus(StatusLaboratorio status) {
        this.status = status;
    }

    
    public Estacao getEstacaoPorId(int id) {
        for(int i = 0; i < estacoes.size(); i++) {
            Estacao e = estacoes.get(i);
            if(e.getId() == id) {
                return e;
            }
        }
        return null; 
    }

    @Override
    public String toString() {
        return "Laboratório [nome = " + nome + ", status = " + status + ", estações = " + estacoes.size() + "/" + isFull() + "]";
    }

}
