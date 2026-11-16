import java.io.Serializable;

public class Comentario implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int eventoId;
    private int nota;
    private String titulo;
    private String descricao;

    public Comentario() {
    }

    public Comentario(int id, int eventoId, int nota, String titulo, String descricao) {
        this.id = id;
        this.eventoId = eventoId;
        this.nota = nota;
        this.titulo = titulo;
        this.descricao = descricao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEventoId() {
        return eventoId;
    }

    public void setEventoId(int eventoId) {
        this.eventoId = eventoId;
    }

    public int getNota() {
        return nota;
    }

    public void setNota(int nota) {
        this.nota = nota;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
