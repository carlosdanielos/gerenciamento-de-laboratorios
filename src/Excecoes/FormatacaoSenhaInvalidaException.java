package Excecoes;

public class FormatacaoSenhaInvalidaException extends IllegalArgumentException{
    
    public FormatacaoSenhaInvalidaException(String mensagem){
        super(mensagem);
    }
}
