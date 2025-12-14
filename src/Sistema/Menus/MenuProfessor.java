package Sistema.Menus;

import Excecoes.ParametroNaoValidoException;
import Excecoes.ReservaNaoDisponivelException;
import Sistema.Lab_Est.*;
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

    private List<Laboratorio> todosOsLaboratorios;
    private Professor professorLogado;

    public MenuProfessor(Professor professor) {
        this.professorLogado = professor;
        this.todosOsLaboratorios = new LinkedList<>();
        carregarLaboratorios();
        carregarReservasDoArquivo();
    }

    public void iniciar(Scanner sc) {
        int opcao = -1;

        while (opcao != 0) {
            System.out.println("\n------ PAINEL DO PROFESSOR ------");
            System.out.println("1. Visualizar Laboratórios e Estações");
            System.out.println("2. Fazer Reserva de Estação");
            System.out.println("3. Visualizar Minhas Reservas");
            System.out.println("4. Realizar Check-in (iniciar uso)");
            System.out.println("5. Realizar Checkout (encerrar uso)");
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
                case 3:
                    listarMinhasReservas();
                    break;
                case 4:
                    realizarCheckIn(sc);
                    break;
                case 5:
                    realizarCheckOut(sc);
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

        DateTimeFormatter formatoHorario = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        LocalDateTime inicioCompleto = null;
        LocalDateTime fimCompleto = null;
        boolean datasValidas = false;

        while (!datasValidas) {
            try {
                System.out.print("Informe a data de início (dd/MM/yyyy): ");
                LocalDate data = LocalDate.parse(sc.nextLine(), formatoData);

                System.out.print("Informe a hora de início (HH:mm): ");
                LocalTime horaInicio = LocalTime.parse(sc.nextLine(), formatoHorario);

                inicioCompleto = LocalDateTime.of(data, horaInicio);

                System.out.print("Informe a hora de término (HH:mm): ");
                LocalTime horaFim = LocalTime.parse(sc.nextLine(), formatoHorario);

                fimCompleto = LocalDateTime.of(data, horaFim);

                if (fimCompleto.isBefore(inicioCompleto) || fimCompleto.isEqual(inicioCompleto)) {
                    System.out.println("Erro: O término deve ser depois do início");
                    System.out.println("Por favor, preencha novamente.\n");
                } else {
                    datasValidas = true;
                }

            } catch (java.time.format.DateTimeParseException e) {
                System.out.println("Formato inválido! Use dd/MM/yyyy para data e HH:mm para hora");
                System.out.println("Tente novamente\n");
            }
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

        try {
            System.out.println("Informe a Data de Início (dd/MM/yyyy): ");
            LocalDate data = LocalDate.parse(sc.nextLine(), formatoData);

            System.out.println("Informe a Hora de Início (HH:mm): ");
            LocalTime horaInicio = LocalTime.parse(sc.nextLine(), formatoHorario);

            inicioCompleto = LocalDateTime.of(data, horaInicio);

            System.out.println("Informe a Hora de Término (HH:mm): ");
            LocalTime horaFim = LocalTime.parse(sc.nextLine(), formatoHorario);

            fimCompleto = LocalDateTime.of(data, horaFim);

            if (fimCompleto.isBefore(inicioCompleto) || fimCompleto.isEqual(inicioCompleto)) {
                throw new ParametroNaoValidoException("Erro: O horário/data de término deve ser posterior ao início");
            }

            listarLaboratorios();
            System.out.print("\nDigite o nome do Laboratório (ex: H401): ");
            nomeLab = sc.nextLine();
            lab = buscarLaboratorio(nomeLab);

            if (lab == null) {
                System.out.println("Laboratório não encontrado");
                return;
            }

            System.out.print("Digite o ID da Estação que deseja reservar: ");
            idEstacao = Integer.parseInt(sc.nextLine());
            estacao = lab.getEstacaoPorId(idEstacao);

            if (estacao != null) {
                if (estacao.getStatus() == StatusEstacao.BLOQUEADA) {
                    System.out.println("Esta estação está em manutenção e não pode ser reservada");
                    return;
                }

                boolean reservou = estacao.adicionarReserva(this.professorLogado, inicioCompleto, fimCompleto);

                if (reservou) {
                    System.out.println("Reserva realizada com sucesso!");
                    Sistema.Lab_Est.Reserva rTemp = new Sistema.Lab_Est.Reserva(this.professorLogado, inicioCompleto,
                            fimCompleto);
                    salvarReservaNoArquivo(lab.getNome(), estacao.getId(), rTemp);
                } else {
                    System.out.println("Erro: Já existe uma reserva nesse horário para esta estação");
                }

            } else {
                System.out.println("Estação inválida");
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

    private void listarMinhasReservas() {
        System.out.println("\n------ MINHAS RESERVAS ------");
        boolean encontrou = false;

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Laboratorio lab : todosOsLaboratorios) {
            for (Estacao est : lab.getEstacoes()) {
                for (Sistema.Lab_Est.Reserva r : est.getReservas()) {
                    if (r.getUsuario().getMatricula().equals(this.professorLogado.getMatricula())) {
                        System.out.println("-----------------------------------------");
                        System.out.println("Laboratório: " + lab.getNome());
                        System.out.println("Estação ID:  " + est.getId());
                        System.out.println("Início:      " + r.getInicio().format(fmt));
                        System.out.println("Término:     " + r.getFim().format(fmt));

                        encontrou = true;
                    }
                }
            }
        }

        if (!encontrou) {
            System.out.println("Você não possui reservas ativas no momento");
        }
        System.out.println("-----------------------------------------");
    }

    private void carregarReservasDoArquivo() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        try (BufferedReader br = new BufferedReader(new FileReader("src/Arquivos/Reservas.txt"))) {
            String linha = br.readLine();

            while (linha != null) {
                String[] campos = linha.split(", ");
                if (campos.length >= 5) {
                    String nomeLab = campos[0].trim();
                    int idEstacao = Integer.parseInt(campos[1].trim());
                    String matriculaNoArquivo = campos[2].trim();
                    LocalDateTime inicio = LocalDateTime.parse(campos[3].trim(), fmt);
                    LocalDateTime fim = LocalDateTime.parse(campos[4].trim(), fmt);

                    Laboratorio lab = buscarLaboratorio(nomeLab);
                    if (lab != null) {
                        Estacao est = lab.getEstacaoPorId(idEstacao);
                        if (est != null) {
                            if (matriculaNoArquivo.equals(this.professorLogado.getMatricula())) {
                                try {
                                    est.adicionarReserva(this.professorLogado, inicio, fim);
                                } catch (Exception e) {
                                }
                            } else {
                                Professor professorFantasma = new Professor("Outro Professor", matriculaNoArquivo, "Padrao@123");
                                try {
                                    est.adicionarReserva(professorFantasma, inicio, fim);
                                } catch (Exception e) {
                                }
                            }
                        }
                    }
                }
                linha = br.readLine();
            }
        } catch (IOException e) {
            System.out.println("Nenhuma reserva anterior encontrada");
        }

    }
    private void salvarReservaNoArquivo(String nomeLab, int idEstacao, Sistema.Lab_Est.Reserva reserva) {
        try (
                FileWriter fw = new FileWriter("src/Arquivos/Reservas.txt", true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw);
        ) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            String linha = nomeLab + ", " +
                    idEstacao + ", " +
                    reserva.getUsuario().getMatricula() + ", " +
                    reserva.getInicio().format(fmt) + ", " +
                    reserva.getFim().format(fmt);

            out.println(linha);

        } catch (IOException e) {
            System.out.println("Erro ao salvar reserva no arquivo: " + e.getMessage());
        }
    }

    private void realizarCheckIn(Scanner sc) {
        System.out.println("\n-- Realizar Check-in --");
        System.out.println("Selecione uma reserva pendente para iniciar o uso:");

        List<Sistema.Lab_Est.Reserva> reservasPendentes = new LinkedList<>();
        List<Estacao> estacoesDasReservas = new LinkedList<>();
        List<Laboratorio> labsDasReservas = new LinkedList<>();

        int contador = 1;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Laboratorio lab : todosOsLaboratorios) {
            for (Estacao est : lab.getEstacoes()) {
                for (Sistema.Lab_Est.Reserva r : est.getReservas()) {
                    if (r.getUsuario().getMatricula().equals(this.professorLogado.getMatricula())
                            && !r.isCheckInRealizado()) {

                        System.out.println(contador + ". " + lab.getNome() + " - Estação " + est.getId() +
                                " (" + r.getInicio().format(fmt) + " as " + r.getFim().format(fmt) + ")");

                        reservasPendentes.add(r);
                        estacoesDasReservas.add(est);
                        labsDasReservas.add(lab);
                        contador++;
                    }
                }
            }
        }

        if (reservasPendentes.isEmpty()) {
            System.out.println("Você não possui reservas pendentes para check-in");
            return;
        }

        System.out.print("Digite o número da reserva para confirmar presença: ");
        try {
            int escolha = Integer.parseInt(sc.nextLine());

            if (escolha >= 1 && escolha <= reservasPendentes.size()) {
                int index = escolha - 1;
                Sistema.Lab_Est.Reserva reservaEscolhida = reservasPendentes.get(index);
                Estacao estacaoCorrespondente = estacoesDasReservas.get(index);
                Laboratorio labCorrespondente = labsDasReservas.get(index);

                reservaEscolhida.setCheckInRealizado(true);

                System.out.println("Check-in realizado com sucesso!");
                System.out.println("Sessão iniciada em: " + LocalDateTime.now().format(fmt));

                salvarSessaoNoArquivo(labCorrespondente.getNome(), estacaoCorrespondente.getId(), reservaEscolhida);
            } else {
                System.out.println("Opção inválida");
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida");
        }
    }

    private void realizarCheckOut(Scanner sc) {
        System.out.println("\n-- Realizar Checkout --");

        List<String> linhasDoArquivo = new LinkedList<>();
        boolean encontrouSessaoAberta = false;
        String linhaAtualizada = "";

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        try (BufferedReader br = new BufferedReader(new FileReader("src/Arquivos/Sessoes.txt"))) {
            String linha = br.readLine();

            while (linha != null) {
                if (!linha.trim().isEmpty()) {
                    String[] campos = linha.split(",");

                    if (campos.length == 6 && campos[2].trim().equals(this.professorLogado.getMatricula())) {

                        if (!encontrouSessaoAberta) {
                            System.out.println("Encerrando sessão na " + campos[0] + " - Estação " + campos[1]);

                            String dataCheckOut = LocalDateTime.now().format(fmt);
                            linhaAtualizada = linha + "," + dataCheckOut;

                            linhasDoArquivo.add(linhaAtualizada);
                            encontrouSessaoAberta = true;

                            System.out.println("Checkout realizado às: " + dataCheckOut);
                        } else {
                            linhasDoArquivo.add(linha);
                        }
                    } else {
                        linhasDoArquivo.add(linha);
                    }
                }
                linha = br.readLine();
            }

        } catch (IOException e) {
            System.out.println("Erro ao ler sessões: " + e.getMessage());
            return;
        }

        if (encontrouSessaoAberta) {
            reescreverArquivoSessoes(linhasDoArquivo);
        } else {
            System.out.println("Você não possui nenhuma sessão aberta para fazer checkout");
        }
    }

    private void salvarSessaoNoArquivo(String nomeLab, int idEstacao, Sistema.Lab_Est.Reserva r) {
        try (
                FileWriter fw = new FileWriter("src/Arquivos/Sessoes.txt", true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw);) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String linha = nomeLab + ", " +
                    idEstacao + ", " +
                    r.getUsuario().getMatricula() + ", " +
                    r.getInicio().format(fmt) + ", " +
                    r.getFim().format(fmt) + ", " +
                    LocalDateTime.now().format(fmt);

            out.println(linha);

        } catch (IOException e) {
            System.out.println("Erro ao salvar sessão: " + e.getMessage());
        }
    }

    private void reescreverArquivoSessoes(List<String> todasAsLinhas) {
        try (
                FileWriter fw = new FileWriter("src/Arquivos/Sessoes.txt", false);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw);) {
            for (String linha : todasAsLinhas) {
                out.println(linha);
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar checkout: " + e.getMessage());
        }
    }
}
