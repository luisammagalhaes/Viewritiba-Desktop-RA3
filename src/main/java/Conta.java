import java.io.Serializable;

public class Conta implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String login;
    private String senha;
    private String tipo;
    private boolean suspensa;
    private String motivoSuspensao;

    public Conta() {
    }

    public Conta(int id, String login, String senha, String tipo) {
        this.id = id;
        this.login = login;
        setSenha(senha);
        this.tipo = tipo;
        this.suspensa = false;
        this.motivoSuspensao = "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public boolean isSuspensa() {
        return suspensa;
    }

    public void setSuspensa(boolean suspensa) {
        this.suspensa = suspensa;
    }

    public String getMotivoSuspensao() {
        return motivoSuspensao;
    }

    public void setMotivoSuspensao(String motivoSuspensao) {
        this.motivoSuspensao = motivoSuspensao;
    }
}
