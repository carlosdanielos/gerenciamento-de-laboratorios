package Sistema.Usuarios;

public class Administrador extends Usuario{
    
    public Administrador(String matricula, String senha){
        super(TipoUsuario.ADMINISTRADOR, matricula, senha);
    }
}
