package Sistema.Menus;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import Sistema.Usuarios.*;


public class Login {
    private List<Usuario> listaLogin;

    public Login(){
        listaLogin = new LinkedList<>();
    }

    public void lerArquivos(BufferedReader br) throws IOException{

        String linha = br.readLine();

        while (linha != null) {
            String[] campos = linha.split(",");

            String nome = campos[0];
            String matricula = campos[1];
            String senha = campos[2];
            TipoUsuario tipo = TipoUsuario.valueOf(campos[3]);

            listaLogin.add(new Usuario(tipo,nome, matricula, senha));
            linha = br.readLine();
        }
    }

    // Verifica se a matrícula já existe
    public boolean verificarEspaco(Usuario novoUsuario){
        for(Usuario u : listaLogin){
            if(novoUsuario.getMatricula().equals(u.getMatricula())){
                return false; 
            }
        } 
        return true; 
    }

    public Usuario logar(String matricula, String senha){        
        if(listaLogin.isEmpty()){
            System.out.println("Nenhum usuário cadastrado");
            return null;
        }

        // Percorre a lista procurando apenas pela matrícula
        for(Usuario u : listaLogin){
            if(matricula.equals(u.getMatricula())){
                if(senha.equals(u.getSenha())){
                    System.out.println("Logado com sucesso. Bem-vindo, " + u.getNome() + "!");
                    return u; 
                } else {
                    System.out.println("Senha incorreta");
                    return null;
                }
            }
        }
        
        System.out.println("Usuário não encontrado");
        return null;
    }
    

    public void cadastrar(TipoUsuario tipo,String nome, String matricula, String senha){
        Usuario usuario = null;

        switch (tipo) {
            case ALUNO:
                usuario = new Aluno(matricula, senha);
                break;
        
            case PROFESSOR: 
                usuario = new Professor(matricula, senha);
                break;
                
            case ADMINISTRADOR:

                usuario = new Administrador(matricula, senha);
                break;

            default:
                System.out.println("Tipo inválido");
        }

        if (verificarEspaco(usuario)) {
            listaLogin.add(usuario);

            try(
                FileWriter fw = new FileWriter("src/Arquivos/Login.txt", true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw);
            ){
                out.println(usuario.getMatricula() + "," + usuario.getSenha() + "," + usuario.getTipo());

            }catch(IOException e){
                System.out.println(e.getMessage());
            }

        }else{
            System.out.println("Usuário ja existe.");
        }
    }
}
