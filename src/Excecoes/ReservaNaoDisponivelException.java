package Excecoes;

public class ReservaNaoDisponivelException extends Exception {
    public ReservaNaoDisponivelException(String mensagem) {
        super(mensagem);
    }
}
