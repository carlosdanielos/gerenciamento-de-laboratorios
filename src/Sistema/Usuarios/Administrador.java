package Sistema.Usuarios;

public class Administrador extends Usuario{
    
    public Administrador(String nome, String matricula, String senha){
        super(TipoUsuario.ADMINISTRADOR, nome, matricula, senha);
    }

    public Administrador(String matricula, String senha){
        super(TipoUsuario.ADMINISTRADOR, matricula, senha);
    }
}
