package Sistema.Lab_Est;

public class Software {
    private String nome;
    private String versao;
    private TipoLicenca tipoLicenca;

    public Software(String nome, String versao, TipoLicenca tipoLicenca) {
        this.nome = nome;
        this.versao = versao;
        this.tipoLicenca = tipoLicenca;
    }

    public String getNome() {
        return nome;
    }

    public String getVersao() {
        return versao;
    }

    public TipoLicenca getTipoLicenca() {
        return tipoLicenca;
    }

    @Override
    public String toString() {
        return "Software: " + nome + " (v." + versao + ") - Licença: " + tipoLicenca;
    }
}
