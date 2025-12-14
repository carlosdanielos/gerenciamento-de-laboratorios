package Sistema.Menus;

import Sistema.Lab_Est.Laboratorio;
import Sistema.Lab_Est.Software;
import Sistema.Lab_Est.StatusEstacao;
import Sistema.Lab_Est.StatusLaboratorio;
import Sistema.Lab_Est.TipoLicenca;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MenuAdministrador {
    private List<Laboratorio> todosOsLaboratorios;

    public MenuAdministrador() {
        this.todosOsLaboratorios = new LinkedList<>();
        try (
                FileReader fr = new FileReader("src/Arquivos/Laboratorios.txt");
                BufferedReader br = new BufferedReader(fr);) {
            lerLaboratorios(br);
            carregarSoftwares();
            carregarManutencoes();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void iniciar(Scanner sc) {
        int opcao = -1;

        while (opcao != 0) {
            System.out.println("\n------ PAINEL DE ADMINISTRADOR ------");
            System.out.println("1. Gerenciar Laboratórios");
            System.out.println("2. Gerenciar Estações");
            System.out.println("3. Relatórios e Estatísticas");
            System.out.println("0. Deslogar");
            System.out.print("Escolha: ");

            opcao = sc.nextInt();
            sc.nextLine();

            switch (opcao) {
                case 1:
                    menuLaboratorios(sc);
                    break;
                case 2:
                    menuEstacoes(sc);
                    break;
                case 3:
                    menuRelatorios(sc); 
                    break;
                case 0:
                    System.out.println("Deslogando...");
                    break;
                default:
                    System.out.println("Opção inválida");
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
        System.out.print("Escolha: ");
        int op = sc.nextInt();
        sc.nextLine();

        if (op == 1) {
            listarLaboratorios();
        } else if (op == 2 || op == 3) {
            System.out.print("Digite o nome do Laboratório (ex: H401): ");
            String nome = sc.nextLine();
            Laboratorio lab = buscarLaboratorio(nome);

            if (lab != null) {
                StatusLaboratorio novoStatus = (op == 2) ? StatusLaboratorio.BLOQUEADO : StatusLaboratorio.ABERTO;
                lab.setStatus(novoStatus);
                System.out.println(lab.getNome() + " agora está " + lab.getStatus());
            } else {
                System.out.println("Laboratório não encontrado");
            }
        } else if (op == 4) {

            String nome;
            int capacidade;

            System.out.println("Digite o nome do laboratorio: ");
            nome = sc.nextLine();
            System.out.println("Digite a capacidade deste laboratorio: ");
            capacidade = sc.nextInt();

            Laboratorio novLaboratorio = new Laboratorio(nome, capacidade);

            todosOsLaboratorios.add(novLaboratorio);

            try (
                    FileWriter fw = new FileWriter("src/Arquivos/Laboratorios.txt", true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    PrintWriter out = new PrintWriter(bw);) {
                out.println(novLaboratorio.getNome() + ", " + novLaboratorio.getCapacidade());
                System.out.println("Laboratório salvo com sucesso!");
            } catch (IOException e) {
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
        System.out.println("5. Instalar Software na Estação");
        System.out.print("Escolha: ");
        int op = sc.nextInt();
        sc.nextLine();

        listarLaboratorios();
        System.out.print("Digite o nome do Laboratório (ex: H401): ");
        String nomeLab = sc.nextLine();
        Laboratorio lab = buscarLaboratorio(nomeLab);
        if (lab == null) {
            System.out.println("Laboratório não encontrado.");
            return;
        }

        if (op == 1) {
            for (int i = 0; i < lab.getEstacoes().size(); i++) {
                System.out.println(lab.getEstacoes().get(i));
            }
        } else if (op >= 2 && op <= 5) {
            System.out.print("Digite o ID da Estação (ex: 1): ");
            int idEstacao = sc.nextInt();
            sc.nextLine();
            Estacao est = lab.getEstacaoPorId(idEstacao);

            if (est != null) {
                if (op == 2) {
                    // Registrar Manutenção
                    System.out.print("Descreva a manutenção: ");
                    String desc = sc.nextLine();

                    est.registrarManutencao(desc);
                    salvarManutencaoNoArquivo(lab.getNome(), est.getId(), desc);
                } else if (op == 3) {
                    // Bloquear Estação
                    est.setStatus(StatusEstacao.BLOQUEADA);
                    System.out.println("Estação " + est.getId() + " bloqueada");
                } else if (op == 4) {
                    // Liberar
                    est.setStatus(StatusEstacao.DISPONIVEL);
                    System.out.println("Estação " + est.getId() + " liberada");
                } else if (op == 5) {
                    System.out.print("Nome do Software: ");
                    String nomeSw = sc.nextLine();

                    System.out.print("Versão: ");
                    String versaoSw = sc.nextLine();

                    System.out.println("Tipo de Licença: (1) Gratuita ou (2) Paga");
                    int tipoLic = sc.nextInt();
                    sc.nextLine();

                    TipoLicenca licenca = (tipoLic == 2) ? TipoLicenca.PAGA : TipoLicenca.GRATUITA;

                    Software novoSoftware = new Software(nomeSw, versaoSw, licenca);
                    est.cadastrarSoftware(novoSoftware);

                    salvarSoftwareNoArquivo(lab.getNome(), est.getId(), novoSoftware);
                }
            } else {
                System.out.println("Estação não encontrada");
            }
        }
    }

    // --- Sub-menu para Relatórios e Estatísticas ---
    private void menuRelatorios(Scanner sc) {
        System.out.println("\n-- Relatórios e Estatísticas --");
        System.out.println("1. Tempo de Uso por Estação");
        System.out.println("2. Histórico de Manutenção");
        System.out.println("3. Taxa de Ocupação (visão geral)");
        System.out.print("Escolha: ");
        int op = sc.nextInt();
        sc.nextLine();

        System.out.println("\nSelecione o Laboratório:");
        listarLaboratorios();
        System.out.print("Nome do Lab: ");
        String nomeLab = sc.nextLine();
        
        Laboratorio lab = buscarLaboratorio(nomeLab);
        if (lab == null) {
            System.out.println("Laboratório não encontrado");
            return;
        }

        if (op == 1) {
            System.out.println("\n--- Tempo Total de Uso ---");
            for (Estacao est : lab.getEstacoes()) {
                long horas = est.calcularTotalHorasUso();
                System.out.println("Estação ID " + est.getId() + ": " + horas + " horas de uso acumulado");
            }

        } else if (op == 2) {
            System.out.print("Digite o ID da Estação para ver o histórico: ");
            int idEst = sc.nextInt();
            sc.nextLine();
            Estacao est = lab.getEstacaoPorId(idEst);

            if (est != null) {
                System.out.println("\n--- Histórico de Manutenção da Estação " + est.getId() + " ---");
                List<String> historico = est.getHistoricoManutencao();
                
                if (historico.isEmpty()) {
                    System.out.println("Nenhuma manutenção registrada");
                } else {
                    for (String registro : historico) {
                        System.out.println(" " + registro);
                    }
                }
            } else {
                System.out.println("Estação não encontrada");
            }

        } else if (op == 3) {
            System.out.println("\n--- Taxa de Ocupação ---");
            for (Estacao est : lab.getEstacoes()) {
                double taxa = est.calcularTaxaOcupacao();
                System.out.printf("Estação ID %d: %.2f%% de ocupação\n", est.getId(), taxa);
            }
        }
    }

    private void listarLaboratorios() {
        System.out.println("\n-- Lista de Laboratórios --");
        for (int i = 0; i < todosOsLaboratorios.size(); i++) {
            System.out.println(todosOsLaboratorios.get(i));
        }
    }

    private Laboratorio buscarLaboratorio(String nome) {
        for (int i = 0; i < todosOsLaboratorios.size(); i++) {
            Laboratorio lab = todosOsLaboratorios.get(i);
            if (lab.getNome().equalsIgnoreCase(nome)) {
                return lab;
            }
        }
        return null;
    }

    private void lerLaboratorios(BufferedReader br) throws IOException {
        String linha = br.readLine();

        while (linha != null) {
            if (linha.trim().isEmpty()) {
                linha = br.readLine();
                continue;
            }

            String[] campos = linha.split(",");

            if (campos.length >= 2) {
                String nome = campos[0].trim();
                try {
                    int capacidade = Integer.parseInt(campos[1].trim());
                    todosOsLaboratorios.add(new Laboratorio(nome, capacidade));
                } catch (NumberFormatException e) {
                    System.out.println("Ignorando linha com número inválido: " + linha);
                }
            }

            linha = br.readLine();
        }
    }

    private void salvarSoftwareNoArquivo(String nomeLab, int idEstacao, Software sw) {
        try (
            FileWriter fw = new FileWriter("src/Arquivos/Softwares.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw);
        ) {
            String linha = nomeLab + "," + idEstacao + "," + sw.getNome() + "," + sw.getVersao() + "," + sw.getTipoLicenca();
            out.println(linha);
            
        } catch (IOException e) {
            System.out.println("Erro ao salvar software: " + e.getMessage());
        }
    }

    private void carregarSoftwares() {
        try (BufferedReader br = new BufferedReader(new FileReader("src/Arquivos/Softwares.txt"))) {
            String linha = br.readLine();

            while (linha != null) {
                if (linha.trim().isEmpty()) {
                    linha = br.readLine();
                    continue;
                }

                String[] campos = linha.split(",");
                if (campos.length >= 5) {
                    String nomeLab = campos[0].trim();
                    int idEstacao = Integer.parseInt(campos[1].trim());
                    String nomeSw = campos[2].trim();
                    String versaoSw = campos[3].trim();
                    String tipoStr = campos[4].trim();

                    Laboratorio lab = buscarLaboratorio(nomeLab);
                    if (lab != null) {
                        Estacao est = lab.getEstacaoPorId(idEstacao);
                        if (est != null) {
                            TipoLicenca tipo = TipoLicenca.valueOf(tipoStr);
                            Software sw = new Software(nomeSw, versaoSw, tipo);
                            
                            est.getSoftwares().add(sw); 
                        }
                    }
                }
                linha = br.readLine();
            }
        } catch (IOException e) {
        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao ler tipo de licença no arquivo");
        }
    }

    private void salvarManutencaoNoArquivo(String nomeLab, int idEstacao, String descricao) {
        try (
            FileWriter fw = new FileWriter("src/Arquivos/Manutencoes.txt", true); 
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw);
        ) {
            String dataHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            String linha = nomeLab + "," + idEstacao + "," + dataHora + "," + descricao;
            out.println(linha);
            
        } catch (IOException e) {
            System.out.println("Erro ao salvar manutenção: " + e.getMessage());
        }
    }

    private void carregarManutencoes() {
        try (BufferedReader br = new BufferedReader(new FileReader("src/Arquivos/Manutencoes.txt"))) {
            String linha = br.readLine();

            while (linha != null) {
                if (!linha.trim().isEmpty()) {
                    String[] campos = linha.split(",");
                    if (campos.length >= 4) {
                        String nomeLab = campos[0].trim();
                        int idEstacao = Integer.parseInt(campos[1].trim());
                        String dataHora = campos[2].trim();
                        String descricao = campos[3].trim();

                        Laboratorio lab = buscarLaboratorio(nomeLab);
                        if (lab != null) {
                            Estacao est = lab.getEstacaoPorId(idEstacao);
                            if (est != null) {
                                est.getHistoricoManutencao().add("Data: " + dataHora + " - " + descricao);
                            }
                        }
                    }
                }
                linha = br.readLine();
            }
        } catch (IOException e) {}
    }

}
