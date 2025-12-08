package Sistema.Menus;

import Sistema.Lab_Est.Laboratorio;
import Sistema.Lab_Est.StatusEstacao;
import Sistema.Lab_Est.StatusLaboratorio;
import Sistema.Lab_Est.Estacao;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;


public class MenuAdministrador {

    // O sistema principal vai guardar a lista de TODOS os laboratórios
    // Usamos a ListaEncadeada, pois podemos ter N laboratórios.
    private List<Laboratorio> todosOsLaboratorios;

    public MenuAdministrador() {
        this.todosOsLaboratorios = new LinkedList<>();
        // Vamos adicionar um lab de exemplo
        try(
            FileReader fr = new FileReader("src/Arquivos/Laboratorios.txt");
            BufferedReader br = new BufferedReader(fr);
        ){
            lerLaboratorios(br);
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }

    public void iniciar(Scanner sc) {
        int opcao = -1;

        while (opcao != 0) {
            System.out.println("\n------ PAINEL DE ADMINISTRADOR ------");
            System.out.println("1. Gerenciar Laboratórios");
            System.out.println("2. Gerenciar Estações");
            System.out.println("0. Deslogar");
            System.out.print("Escolha: ");
            
            opcao = sc.nextInt();
            sc.nextLine(); // Limpar buffer

            switch (opcao) {
                case 1:
                    menuLaboratorios(sc);
                    break;
                case 2:
                    menuEstacoes(sc);
                    break;
                case 0:
                    System.out.println("Deslogando...");
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    // --- Sub-menu para Laboratórios ---
    private void menuLaboratorios(Scanner sc) {
        System.out.println("\n-- Gerenciar Laboratórios --");
        System.out.println("1. Listar todos os Laboratórios");
        System.out.println("2. Bloquear Laboratório");
        System.out.println("3. Desbloquear Laboratório");
        System.out.println("4. Adicionar Laboratorio");
        // ... (você pode adicionar "Cadastrar Novo Laboratório" aqui)
        System.out.print("Escolha: ");
        int op = sc.nextInt();
        sc.nextLine();

        if (op == 1) {
            listarLaboratorios();
        } else if (op == 2 || op == 3) {
            // 3. Implementar funcionalidade de bloqueio
            System.out.print("Digite o nome do Laboratório (ex: LAB-01): ");
            String nome = sc.nextLine();
            Laboratorio lab = buscarLaboratorio(nome);
            
            if (lab != null) {
                StatusLaboratorio novoStatus = (op == 2) ? StatusLaboratorio.BLOQUEADO : StatusLaboratorio.ABERTO;
                lab.setStatus(novoStatus);
                System.out.println(lab.getNome() + " agora está " + lab.getStatus());
            } else {
                System.out.println("Laboratório não encontrado.");
            }
        } else if (op == 4){

            String nome;
            int capacidade;

            System.out.println("Digite o nome do laboratorio: ");
            nome = sc.nextLine();
            System.out.println("Digite a capacidade deste laboratorio: ");
            capacidade = sc.nextInt();

            Laboratorio novLaboratorio = new Laboratorio(nome, capacidade);

            todosOsLaboratorios.add(novLaboratorio);

            try(
                FileWriter fw = new FileWriter("src/Arquivos/Laboratorios.txt", true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw);
                FileReader fr = new FileReader("src/Arquivos/Laboratorios.txt");
                BufferedReader br = new BufferedReader(fr);
            ){
                escreverLaboratorio(br, out, novLaboratorio);
            }catch(IOException e){
                System.out.println(e.getMessage());
            }

        }
    }

    // --- Sub-menu para Estações ---
    private void menuEstacoes(Scanner sc) {
        System.out.println("\n-- Gerenciar Estações --");
        System.out.println("1. Listar Estações de um Laboratório");
        System.out.println("2. Registrar Manutenção (Corretiva/Preventiva)");
        System.out.println("3. Bloquear Estação");
        System.out.println("4. Liberar Estação");
        // ... (você pode adicionar "Cadastrar Nova Estação" aqui)
        System.out.print("Escolha: ");
        int op = sc.nextInt();
        sc.nextLine();

        System.out.print("Digite o nome do Laboratório (ex: LAB-01): ");
        String nomeLab = sc.nextLine();
        Laboratorio lab = buscarLaboratorio(nomeLab);
        if (lab == null) {
            System.out.println("Laboratório não encontrado.");
            return; // Sai do menu
        }

        if (op == 1) {
            // Lista todas as estações e seus status
            for(int i = 0; i < lab.getEstacoes().size(); i++) {
                System.out.println(lab.getEstacoes().get(i));
            }
        } else if (op >= 2 && op <= 4) {
            System.out.print("Digite o ID da Estação (ex: 1): ");
            int idEstacao = sc.nextInt();
            sc.nextLine();
            Estacao est = lab.getEstacaoPorId(idEstacao);
            
            if (est != null) {
                if (op == 2) {
                    // 2. Registrar Manutenção
                    System.out.print("Descreva a manutenção: ");
                    String desc = sc.nextLine();
                    est.registrarManutencao(desc);
                } else if (op == 3) {
                    // 3. Bloquear Estação
                    est.setStatus(StatusEstacao.BLOQUEADA);
                    System.out.println("Estação " + est.getId() + " bloqueada.");
                } else if (op == 4) {
                    // Liberar (volta ao normal)
                    est.setStatus(StatusEstacao.DISPONIVEL);
                    System.out.println("Estação " + est.getId() + " liberada.");
                }
            } else {
                System.out.println("Estação não encontrada.");
            }
        }
    }
    
    // --- Métodos de Ajuda ---
    
    private void listarLaboratorios() {
        System.out.println("\n-- Lista de Laboratórios --");
        for(int i = 0; i < todosOsLaboratorios.size(); i++) {
            System.out.println(todosOsLaboratorios.get(i));
        }
    }

    private Laboratorio buscarLaboratorio(String nome) {
        for(int i = 0; i < todosOsLaboratorios.size(); i++) {
            Laboratorio lab = todosOsLaboratorios.get(i);
            if (lab.getNome().equalsIgnoreCase(nome)) {
                return lab;
            }
        }
        return null;
    }

    private void lerLaboratorios(BufferedReader br) throws IOException{
        String linha = br.readLine();

        while(linha != null){
            String[] campos = linha.split(",");

            String nome = campos[0];
            int capacidade = Integer.parseInt(campos[1]);

            todosOsLaboratorios.add(new Laboratorio(nome, capacidade));
            
            linha = br.readLine();
        }
    }

    private void escreverLaboratorio(BufferedReader br, PrintWriter out, Laboratorio newLab) throws IOException{
        String linha = br.readLine();

        if ( linha != null) {
            out.println();
            out.print(newLab.getNome() + "," + newLab.getCapacidade());
        }else{
            out.println(newLab.getNome() + "," + newLab.getCapacidade());
        } 
    }
}
