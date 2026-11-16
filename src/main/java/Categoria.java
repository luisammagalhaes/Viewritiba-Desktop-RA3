import java.io.Serializable;

public class Categoria implements Serializable {
    private int id;
    private String nome;
    private String descricao;
    private String publicoAlvo;

    public Categoria() {
    }

    public Categoria(int id, String nome, String descricao, String publicoAlvo) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.publicoAlvo = publicoAlvo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getPublicoAlvo() {
        return publicoAlvo;
    }

    public void setPublicoAlvo(String publicoAlvo) {
        this.publicoAlvo = publicoAlvo;
    }
}