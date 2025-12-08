package Sistema.Menus;

import java.util.Scanner;
import Excecoes.FormatacaoMatriculaInvalidaException;
import Excecoes.FormatacaoSenhaInvalidaException;
import Sistema.Usuarios.*;

import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;

public class Main {
    
    public static void main(String[] args){

        Scanner sc = new Scanner(System.in);
        Login lg = new Login();
        int opcao = 0;

        try(
            FileReader fr = new FileReader("src/Arquivos/Login.txt");
            BufferedReader br = new BufferedReader(fr);
        ){
            lg.lerArquivos(br);

        }catch(IOException e){
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

            if(opcao == 0){
                break;

            }else if(opcao == 1){
                String matricula = null;
                String senha = null;
                String tipo = null;
                TipoUsuario tipoLimpo = null;
                boolean entradaValida = false;
                Usuario usuarioLogado = null;

                do {
                    System.out.print("\nTipo da sua conta: (Aluno/Professor/Administrador) ");
                    tipo = sc.nextLine().trim().toUpperCase();

                    try{
                        tipoLimpo = TipoUsuario.valueOf(tipo);
                        entradaValida = true;

                    }catch(IllegalArgumentException e){
                        System.out.println("Tipo Inválido. ");
                    }
                } while (!entradaValida);
 
                entradaValida = false;

                do{
                    System.out.print("Sua Matrícula: ");
                    matricula = sc.nextLine();
                    System.out.print("Sua Senha: ");
                    senha = sc.nextLine();

                    try{
                        usuarioLogado = lg.logar(tipoLimpo,matricula, senha);
                        entradaValida = true;

                    }catch(FormatacaoMatriculaInvalidaException e){
                        System.out.println(e.getMessage());
                        System.err.println();

                    }catch(FormatacaoSenhaInvalidaException e){
                        System.out.println(e.getMessage());
                        System.err.println();
                    }
                }while(!entradaValida);

                if(usuarioLogado != null){
                    if(usuarioLogado.getTipo().equals(TipoUsuario.ADMINISTRADOR)){
                        MenuAdministrador menuAdmin = new MenuAdministrador();
                        menuAdmin.iniciar(sc);
                    }else if(usuarioLogado.getTipo().equals(TipoUsuario.PROFESSOR)){
                        MenuProfessor menuProfessor = new MenuProfessor();
                        menuProfessor.iniciar(sc);
                    }else{
                        System.out.println("Funcionalidades para " + usuarioLogado.getTipo() + " ainda não implementadas.");
                    }
                }

            }else if(opcao == 2){
                String matricula = null;
                String senha = null;
                String tipo = null;
                TipoUsuario tipoLimpo = null;
                boolean entradaValida = false;

                do {
                    System.out.print("\nTipo da conta: (Aluno/Professor/Administrador) ");
                    tipo = sc.nextLine().trim().toUpperCase();

                    try{
                        tipoLimpo = TipoUsuario.valueOf(tipo);
                        entradaValida = true;

                    }catch(IllegalArgumentException e){
                        System.out.println("Tipo Inválido. ");
                    }
                } while (!entradaValida);
               
                entradaValida = false;

                do{
                    System.out.print("Sua Matrícula: ");
                    matricula = sc.nextLine();
                    System.out.print("\nCrie uma senha: (Deve conter: Letra Maiúscula; Número; Caracter especial(!@#$%&*); Pelo menos 8 caracteres.) ");
                    senha = sc.nextLine();

                    try{
                        lg.cadastrar(tipoLimpo, matricula, senha);
                        entradaValida = true;
                    }catch(FormatacaoMatriculaInvalidaException e){
                        System.out.println(e.getMessage());
                        System.err.println();

                    }catch(FormatacaoSenhaInvalidaException e){
                        System.out.println(e.getMessage());
                        System.err.println();
                    }  
                }while(!entradaValida);
            }
        }

        sc.close();
    }
}
