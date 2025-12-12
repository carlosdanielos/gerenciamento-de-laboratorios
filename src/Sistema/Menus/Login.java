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

            if(campos.length >= 4) {
                String nome = campos[0];
                String matricula = campos[1];
                String senha = campos[2];
                try {
                    TipoUsuario tipo = TipoUsuario.valueOf(campos[3]);
                    if(tipo == TipoUsuario.ALUNO) listaLogin.add(new Aluno(nome, matricula, senha));
                    else if(tipo == TipoUsuario.PROFESSOR) listaLogin.add(new Professor(nome, matricula, senha));
                    else if(tipo == TipoUsuario.ADMINISTRADOR) listaLogin.add(new Administrador(nome, matricula, senha));
                } catch (IllegalArgumentException e) {}
            }
            linha = br.readLine();
        }
    }

    public boolean verificarEspaco(Usuario novoUsuario){
        for(Usuario u : listaLogin){
            if(novoUsuario.getMatricula().equals(u.getMatricula())){
                return false; 
            }
        } 
        return true; 
    }

    public Usuario buscarPorMatricula(String matricula) {
        for (Usuario u : listaLogin) {
            if (u.getMatricula().equals(matricula)) {
                return u;
            }
        }
        return null; 
    }

    public Usuario logar(String matricula, String senha){        
        if(listaLogin.isEmpty()){
            System.out.println("Nenhum usuário cadastrado");
            return null;
        }
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
                usuario = new Aluno(nome, matricula, senha);
                break;
        
            case PROFESSOR: 
                usuario = new Professor(nome, matricula, senha);
                break;
                
            case ADMINISTRADOR:
                usuario = new Administrador(nome, matricula, senha);
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
                out.println(usuario.getNome() + "," + usuario.getMatricula() + "," + usuario.getSenha() + "," + usuario.getTipo());

            }catch(IOException e){
                System.out.println(e.getMessage());
            }

        }else{
            System.out.println("Usuário já existe");
        }
    }
}
