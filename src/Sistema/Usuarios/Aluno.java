package Sistema.Usuarios;

public class Aluno extends Usuario{
    
    public Aluno(String nome, String matricula, String senha){
        super(TipoUsuario.ALUNO, nome, matricula, senha);
    }

    public Aluno(String matricula, String senha){
        super(TipoUsuario.ALUNO, "Aluno", matricula, senha);
    }
}
