package Sistema.Usuarios;

public class Aluno extends Usuario{
    
    public Aluno(String matricula, String senha){
        super(TipoUsuario.ALUNO, matricula, senha);
    }
}
