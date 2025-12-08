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

            String matricula = campos[0];
            String senha = campos[1];
            TipoUsuario tipo = TipoUsuario.valueOf(campos[2]);

            listaLogin.add(new Usuario(tipo, matricula, senha));
            linha = br.readLine();
        }
    }

    public boolean verificarEspaco(Usuario a){
        int aux = 0;
        
        for(Usuario i : listaLogin){
            if(a.getMatricula().equals(i.getMatricula())){
                if(a.getTipo() == i.getTipo()){
                    aux++;
                }  
            }
        } 

        return aux == 0;
    }

    public Usuario logar(TipoUsuario tipo, String matricula, String senha){
        Usuario usuario = null;
        int aux = 0;

        if(listaLogin.isEmpty()){
            System.out.println("Senha ou Usuário inválido.");
        }else{
            for(Usuario a : listaLogin){
                if(matricula.equals(a.getMatricula()) && tipo.equals(a.getTipo())){
                    if(senha.equals(a.getSenha())){
                        aux++;
                        usuario = a;
                        System.out.println("Logado com sucesso.");
                        break;
                    }else{
                        aux++;
                        System.out.println("Senha ou Usuário inválido.");
                        break;
                    }
                }
            }
            
            if(aux == 0){
                System.out.println("Senha ou Usuário inválido.");
            }
        }

        return usuario;
    }
    

    public void cadastrar(TipoUsuario tipo, String matricula, String senha){
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
