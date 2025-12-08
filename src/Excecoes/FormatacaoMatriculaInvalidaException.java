package Excecoes;

public class FormatacaoMatriculaInvalidaException extends IllegalArgumentException{
    
    public FormatacaoMatriculaInvalidaException(String mensagem){
        super(mensagem);
    }
}
