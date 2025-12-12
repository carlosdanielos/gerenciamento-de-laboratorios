package Sistema.Menus;

import java.util.Scanner;
import Excecoes.FormatacaoMatriculaInvalidaException;
import Excecoes.FormatacaoSenhaInvalidaException;
import Sistema.Usuarios.*;

import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        Login lg = new Login();
        int opcao = 0;

        try (
                FileReader fr = new FileReader("src/Arquivos/Login.txt");
                BufferedReader br = new BufferedReader(fr);) {
            lg.lerArquivos(br);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        while (true) {
            System.out.println();
            System.out.println("------ SISTEMA LABORATÓRIO (LOGIN/CADASTRO) ------");
            System.out.println("--  1) Fazer Login                              --");
            System.out.println("--  2) Fazer Cadastro                           --");
            System.out.println("--                                              --");
            System.out.println("--  0) Encerrar Programa                        --");
            System.out.println("--------------------------------------------------");
            System.out.println();

            opcao = sc.nextInt();
            sc.nextLine();

            if (opcao == 0) {
                break;

            } else if (opcao == 1) {
                String matricula;
                String senha;
                Usuario usuarioLogado = null;
                boolean cancelarLogin = false;
                
                while (usuarioLogado == null) {
                    System.out.print("Sua Matrícula: ");
                    matricula = sc.nextLine();

                    if (matricula.equals("0")) {
                        cancelarLogin = true;
                        break; 
                    }

                    usuarioLogado = lg.buscarPorMatricula(matricula);

                    if (usuarioLogado == null) {
                        System.out.println("Matrícula não encontrada. Tente novamente ou digite '0' para voltar");
                    }
                }

                if (!cancelarLogin && usuarioLogado != null) {
                    while (true) {
                        System.out.print("Sua Senha: ");
                        senha = sc.nextLine();

                        if (senha.equals("0")) {
                            System.out.println("Login cancelado");
                            break;
                        }

                        if (senha.equals(usuarioLogado.getSenha())) {
                            System.out.println("Logado com sucesso. Bem-vindo, " + usuarioLogado.getNome() + "!");

                            if (usuarioLogado.getTipo() == TipoUsuario.ADMINISTRADOR) {
                                MenuAdministrador menuAdmin = new MenuAdministrador();
                                menuAdmin.iniciar(sc);
                            } else if (usuarioLogado.getTipo() == TipoUsuario.PROFESSOR) {
                                MenuProfessor menuProfessor = new MenuProfessor();
                                menuProfessor.iniciar(sc);
                            } else if (usuarioLogado.getTipo() == TipoUsuario.ALUNO) {
                                MenuAluno menuAluno = new MenuAluno((Aluno) usuarioLogado);
                                menuAluno.iniciar(sc);
                            }
                            break;
                        } else {
                            System.out.println("Senha incorreta. Tente novamente ou digite '0' para voltar");
                        }
                    }
                }

            } else if (opcao == 2) {
                String nome = null;
                String matricula = null;
                String senha = null;
                String tipo = null;
                TipoUsuario tipoLimpo = null;
                boolean entradaValida = false;

                do {
                    System.out.print("\nTipo da conta: (Aluno/Professor/Administrador) ");
                    tipo = sc.nextLine().trim().toUpperCase();

                    try {
                        tipoLimpo = TipoUsuario.valueOf(tipo);
                        entradaValida = true;

                    } catch (IllegalArgumentException e) {
                        System.out.println("Tipo Inválido");
                    }
                } while (!entradaValida);

                entradaValida = false;

                do {
                    System.out.print("Seu nome completo: ");
                    nome = sc.nextLine();
                    System.out.print("Sua Matrícula: ");
                    matricula = sc.nextLine();
                    System.out.print(
                            "\nCrie uma senha: (Deve conter: Letra Maiúscula; Número; Caracter especial(!@#$%&*); Pelo menos 8 caracteres.) ");
                    senha = sc.nextLine();

                    try {
                        lg.cadastrar(tipoLimpo, nome, matricula, senha);
                        entradaValida = true;

                    } catch (FormatacaoSenhaInvalidaException | FormatacaoMatriculaInvalidaException e) {
                        System.out.println(e.getMessage());
                        System.err.println("Tente novamente");
                    } catch (RuntimeException e) {
                        System.out.println("Erro: " + e.getMessage());
                    }

                } while (!entradaValida);
            }
        }

        sc.close();
    }
}
