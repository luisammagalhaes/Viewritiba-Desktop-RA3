import java.io.Serializable;

public class Patrocinador implements Serializable {
    private int id;
    private String nome;
    private String contato;
    private double valor;

    public Patrocinador() {
    }

    public Patrocinador(int id, String nome, String contato, double valor) {
        this.id = id;
        this.nome = nome;
        this.contato = contato;
        this.valor = valor;
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

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
