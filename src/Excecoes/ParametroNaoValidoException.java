package Excecoes;

public class ParametroNaoValidoException extends IllegalArgumentException{
    
    public ParametroNaoValidoException(String mensagem){
        super(mensagem);
    }
}
