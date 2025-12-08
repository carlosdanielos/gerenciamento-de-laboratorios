package Sistema.Usuarios;

public class Professor extends Usuario{
    
    public Professor(String matricula, String senha){
        super(TipoUsuario.PROFESSOR, matricula, senha);
    }
}
