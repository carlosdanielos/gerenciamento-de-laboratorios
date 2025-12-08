package Sistema.Lab_Est;


import java.util.LinkedList;
import java.util.List;

public class Estacao {
    private int id; 
    private StatusEstacao status;
    private List<String> historicoManutencao;

    public Estacao(int id) {
        this.id = id;
        status = StatusEstacao.DISPONIVEL;
        historicoManutencao = new LinkedList<>();
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
    
    public void setStatus(StatusEstacao status){
        this.status = status;
    }

    public void registrarManutencao(String descricao) {

        status = StatusEstacao.EM_MANUTENCAO;
        historicoManutencao.add("Data: [HOJE] - " + descricao);
        System.out.println("Manutenção registrada para Estação " + id);
    }
    
    @Override
    public String toString() {
        return "Estacao [id=" + id + ", status=" + status + "]";
    }
}
