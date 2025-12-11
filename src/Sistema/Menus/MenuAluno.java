package Sistema.Menus;

import Sistema.Lab_Est.Laboratorio;
import Sistema.Lab_Est.Estacao;
import Sistema.Lab_Est.StatusEstacao;
import Sistema.Usuarios.Aluno; 

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import Excecoes.ParametroNaoValidoException;
import Excecoes.ReservaNaoDisponivelException;

public class MenuAluno {

    private List<Laboratorio> todosOsLaboratorios;
    private Aluno alunoLogado; 

    public MenuAluno(Aluno aluno) {
        this.alunoLogado = aluno;
        this.todosOsLaboratorios = new LinkedList<>();
        carregarLaboratorios();
    }

    public void iniciar(Scanner sc) {
        int opcao = -1;

        while (opcao != 0) {
            System.out.println("\n------ PAINEL DO ALUNO ------");
            System.out.println("1. Visualizar Laboratórios e Estações");
            System.out.println("2. Fazer Reserva de Estação");
            System.out.println("0. Deslogar");
            System.out.print("Escolha: ");

            try {
                opcao = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                opcao = -1;
            }

            switch (opcao) {
                case 1:
                    listarLaboratoriosDisponiveis();
                    break;
                case 2:
                    menuReservas(sc);
                    break;
                case 0:
                    System.out.println("Deslogando...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    // Fazer Reservas
    private void menuReservas(Scanner sc) {
        System.out.println("\n-- Fazer Reserva de Estação --");

        // Formatadores separados
        DateTimeFormatter formatoHorario = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try {
            System.out.println("Informe a Data de Início (dd/MM/yyyy): ");
            LocalDate dataInicio = LocalDate.parse(sc.nextLine(), formatoData);

            System.out.println("Informe a Hora de Início (HH:mm): ");
            LocalTime horaInicio = LocalTime.parse(sc.nextLine(), formatoHorario);

            LocalDateTime inicioCompleto = LocalDateTime.of(dataInicio, horaInicio);

            System.out.println("Informe a Data de Término (dd/MM/yyyy): ");
            LocalDate dataFim = LocalDate.parse(sc.nextLine(), formatoData);

            System.out.println("Informe a Hora de Término (HH:mm): ");
            LocalTime horaFim = LocalTime.parse(sc.nextLine(), formatoHorario);

            LocalDateTime fimCompleto = LocalDateTime.of(dataFim, horaFim);

            if (fimCompleto.isBefore(inicioCompleto) || fimCompleto.isEqual(inicioCompleto)) {
                throw new ParametroNaoValidoException("Erro: O horário de término deve ser posterior ao início");
            }

            listarLaboratorios();
            System.out.print("\nDigite o nome do Laboratório (ex: LAB-01): ");
            String nomeLab = sc.nextLine();
            Laboratorio lab = buscarLaboratorio(nomeLab);

            if (lab == null) {
                System.out.println("Laboratório não encontrado");
                return;
            }

            System.out.print("Digite o ID da Estação que deseja reservar: ");
            int idEstacao = Integer.parseInt(sc.nextLine());
            Estacao estacao = lab.getEstacaoPorId(idEstacao);

            if (estacao != null) {
                if (estacao.getStatus() == StatusEstacao.BLOQUEADA) {
                    System.out.println("Esta estação está em manutenção e não pode ser reservada");
                    return;
                }

                boolean reservou = estacao.adicionarReserva(this.alunoLogado, inicioCompleto, fimCompleto);

                if (reservou) {
                    System.out.println("Reserva realizada com sucesso!");
                } else {
                    System.out.println("Erro: Já existe uma reserva nesse horário para esta estação");
                }

            } else {
                System.out.println("Estação inválida.");
            }

        } catch (ReservaNaoDisponivelException e) {
            System.out.println("Erro ao reservar: " + e.getMessage());
        }
    }

    // Métodos Auxiliares
    private void listarLaboratorios() {
        System.out.println("\n-- Lista de Laboratórios --");
        for (Laboratorio lab : todosOsLaboratorios) {
            System.out.println("Lab: " + lab.getNome() + " | Total Estações: " + lab.getCapacidade());
        }
    }

    private void listarLaboratoriosDisponiveis() {
        System.out.println("\n-- Detalhes dos Laboratórios --");
        if (todosOsLaboratorios.isEmpty()) {
            System.out.println("Nenhum laboratório carregado");
        }
        for (Laboratorio lab : todosOsLaboratorios) {
            System.out.println("------------------------------");
            System.out.println("Laboratório: " + lab.getNome());
            for (Estacao est : lab.getEstacoes()) {
                System.out.println("   -> Estação " + est.getId() + " [" + est.getStatus() + "]");
            }
        }
    }

    private Laboratorio buscarLaboratorio(String nome) {
        for (Laboratorio lab : todosOsLaboratorios) {
            if (lab.getNome().equalsIgnoreCase(nome)) {
                return lab;
            }
        }
        return null;
    }

    private void carregarLaboratorios() {
        try (BufferedReader br = new BufferedReader(new FileReader("src/Arquivos/Laboratorios.txt"))) {
            String linha = br.readLine();
            while (linha != null) {
                String[] campos = linha.split(",");
                if (campos.length >= 2) {
                    String nome = campos[0];
                    int capacidade = Integer.parseInt(campos[1]);
                    todosOsLaboratorios.add(new Laboratorio(nome, capacidade));
                }
                linha = br.readLine();
            }
        } catch (IOException e) {
            System.out.println("Erro ao carregar laboratórios: " + e.getMessage());
        }
    }
}