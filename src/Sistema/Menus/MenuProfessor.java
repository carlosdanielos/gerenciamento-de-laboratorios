package Sistema.Menus;

import Excecoes.ParametroNaoValidoException;
import Excecoes.ReservaNaoDisponivelException;
import Sistema.Lab_Est.*;
import Sistema.Usuarios.Aluno;
import Sistema.Usuarios.Professor;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class MenuProfessor {

    // O sistema principal vai guardar a lista de TODOS os laboratórios
    // Usamos a ListaEncadeada, pois podemos ter N laboratórios.
    private List<Laboratorio> todosOsLaboratorios;
    private Professor professor;

    public MenuProfessor(Professor professor) {
        this.professor = professor;
        this.todosOsLaboratorios = new LinkedList<>();
        carregarReservasDoArquivo();
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
                try{
                    DateTimeFormatter formatoHorario = DateTimeFormatter.ofPattern("HH:mm");
                    DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");


                    System.out.println("Informe a data de reserva: ");
                    LocalDate data = LocalDate.parse(sc.nextLine(), formatoData);

                    System.out.println("Informe o horário a ser reservado.");

                    System.out.println("Início: ");
                    LocalTime horarioInicio = LocalTime.parse(sc.nextLine(), formatoHorario);
                    LocalDateTime inicioCompleto = LocalDateTime.of(data, horarioInicio);

                    System.out.println("Fim: ");
                    LocalTime horarioFim = LocalTime.parse(sc.nextLine(), formatoHorario);
                    LocalDateTime fimCompleto = LocalDateTime.of(data, horarioFim);

                    if (fimCompleto.isBefore(inicioCompleto) || fimCompleto.isEqual(inicioCompleto)) {
                        throw new ParametroNaoValidoException("Erro: O horário/data de término deve ser posterior ao início");
                    }

                    listarLaboratorios();
                    System.out.println("Digite o nome do Laboratório (ex: LAB-01): ");
                    String nome = sc.nextLine();
                    Laboratorio lab = buscarLaboratorio(nome);

                    if (lab == null) {
                        System.out.println("Laboratório não encontrado");
                        return;
                    }

                    if(lab.getStatus() == StatusLaboratorio.BLOQUEADO){
                        System.out.println("Esse laboratório está momentaneamente bloqueado e não pode ser reservado!");
                        return;
                    }

                    boolean reservou = lab.adicionarReserva(this.professor, inicioCompleto, fimCompleto);

                    if (reservou) {
                        System.out.println("Reserva realizada com sucesso!");
                        Sistema.Lab_Est.Reserva rTemp = new Sistema.Lab_Est.Reserva(this.professor, inicioCompleto, fimCompleto);
                        salvarReservaNoArquivo(lab.getNome(), rTemp);
                    } else {
                        System.out.println("Erro: Já existe uma reserva nesse horário para esta estação");
                    }

                }catch (DateTimeParseException | ParametroNaoValidoException e){
                    boolean entradaValida = false;
                    while (!entradaValida){
                        System.out.println("Horário ou data inválida, tente novamente: ");
                        DateTimeFormatter formatoHorario = DateTimeFormatter.ofPattern("HH:mm");
                        DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("HH:mm");


                        System.out.println("Informe a data de reserva: ");
                        LocalDate data = LocalDate.parse(sc.nextLine(), formatoData);

                        System.out.println("Informe o horário a ser reservado.");

                        System.out.println("Início: ");
                        LocalTime horarioInicio = LocalTime.parse(sc.nextLine(), formatoHorario);
                        LocalDateTime inicioCompleto = LocalDateTime.of(data, horarioInicio);

                        System.out.println("Fim: ");
                        LocalTime horarioFim = LocalTime.parse(sc.nextLine(), formatoHorario);
                        LocalDateTime fimCompleto = LocalDateTime.of(data, horarioFim);

                        entradaValida = true;
                    }
                } catch (ReservaNaoDisponivelException e) {
                    System.out.println("Erro ao reservar: " + e.getMessage());
                }


                break;

            default:
                System.out.println("Opção inválida.");
                return;
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

    private void salvarReservaNoArquivo(String nomeLab, Sistema.Lab_Est.Reserva reserva) {
        try (
                FileWriter fw = new FileWriter("src/Arquivos/ReservasLaboratorio.txt", true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw);
        ) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            String linha = nomeLab + ", " +
                    reserva.getUsuario().getMatricula() + ", " +
                    reserva.getInicio().format(fmt) + ", " +
                    reserva.getFim().format(fmt);

            out.println(linha);

        } catch (IOException e) {
            System.out.println("Erro ao salvar reserva no arquivo: " + e.getMessage());
        }
    }

    private void carregarReservasDoArquivo() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        try (BufferedReader br = new BufferedReader(new FileReader("src/Arquivos/ReservasLaboratorio.txt"))) {
            String linha = br.readLine();

            while (linha != null) {
                String[] campos = linha.split(", ");
                if (campos.length >= 4) {
                    String nomeLab = campos[0].trim();
                    String matriculaNoArquivo = campos[1].trim();
                    LocalDateTime inicio = LocalDateTime.parse(campos[2].trim(), fmt);
                    LocalDateTime fim = LocalDateTime.parse(campos[3].trim(), fmt);

                    Laboratorio lab = buscarLaboratorio(nomeLab);

                    if (lab != null) {
                        if (matriculaNoArquivo.equals(this.professor.getMatricula())) {
                            try {
                                lab.adicionarReserva(this.professor, inicio, fim);
                            } catch (Exception e) {}
                        } else {
                            Professor alunoFantasma = new Professor("Outro Professor", matriculaNoArquivo, "Padrao@123");
                            try {
                                lab.adicionarReserva(alunoFantasma, inicio, fim);
                            } catch (Exception e) {}
                        }
                    }

                }
                linha = br.readLine();
            }
        } catch (IOException e) {
            System.out.println("Nenhuma reserva anterior encontrada");
        }

    }
}
