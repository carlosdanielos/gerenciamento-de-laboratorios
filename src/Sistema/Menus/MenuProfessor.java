package Sistema.Menus;

import Sistema.Lab_Est.Laboratorio;
import Sistema.Lab_Est.StatusEstacao;
import Sistema.Lab_Est.StatusLaboratorio;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class MenuProfessor {

    // O sistema principal vai guardar a lista de TODOS os laboratórios
    // Usamos a ListaEncadeada, pois podemos ter N laboratórios.
    private List<Laboratorio> todosOsLaboratorios;

    public MenuProfessor() {
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
            System.out.println("\n------ PAINEL DE PROFESSOR ------");
            System.out.println("1. Fazer Reserva");
            System.out.println("2. Gerenciar Reservas");
            System.out.println("3. Solicitar Manutenção");
            System.out.println("0. Deslogar");
            System.out.print("Escolha: ");

            opcao = sc.nextInt();
            sc.nextLine(); // Limpar buffer

            switch (opcao) {
                case 1:
                    menuReservas(sc);
                    break;
                case 2:
                    menuGerenciaReservas(sc);
                    break;
                case 3:
                    menuManutencao(sc);
                case 0:
                    System.out.println("Deslogando...");
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    private void menuReservas(Scanner sc){

        System.out.println("\n-- Fazer Reserva --");
        System.out.println("1. Reservar Laboratório");
        System.out.println("2. Reservar Estação");

        System.out.println("Escolha: ");

        int op = sc.nextInt();
        sc.nextLine();

        switch (op){
            case 1:
                DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm");

                System.out.println("Informe o horário a ser reservado.");
                System.out.println("Início: ");

                String input = sc.nextLine();

                LocalTime horarioInicio = LocalTime.parse(input, formato);

                listarLaboratorios();
                System.out.println("Digite o nome do Laboratório (ex: LAB-01): ");
                String nome = sc.nextLine();
                Laboratorio lab = buscarLaboratorio(nome);

                if (lab != null) {
                    StatusLaboratorio novoStatus = StatusLaboratorio.BLOQUEADO;
                    lab.setStatus(novoStatus);
                    System.out.println(lab.getNome() + " agora está " + lab.getStatus());
                } else {
                    System.out.println("Laboratório não encontrado.");
                }
                break;
            default:
                System.out.println("Opção inválida.");
        }

    }

    private void menuGerenciaReservas(Scanner sc){

    }

    private void menuManutencao(Scanner sc){

    }

    // --- Métodos de Ajuda ---

    private void listarLaboratorios() {
        System.out.println("\n-- Lista de Laboratórios --");
        for(int i = 0; i < todosOsLaboratorios.size(); i++) {
            System.out.println(todosOsLaboratorios.get(i));
        }
    }

    private void listarLaboratoriosDisponiveis(){
        System.out.println("\n-- Lista de Laboratórios Disponíveis --");
        for(Laboratorio lab : todosOsLaboratorios){
            if(lab.getStatus().equals(StatusEstacao.DISPONIVEL)){
                System.out.println("Nome = " + lab.getNome() + ", Status = " + lab.getStatus() + ", Estações: " + lab.getCapacidade());
            }
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
}
