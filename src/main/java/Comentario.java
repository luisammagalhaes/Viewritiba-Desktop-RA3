import java.io.Serializable;

public class Comentario implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int eventoId;
    private int nota;
    private String texto;

    public Comentario() {
    }

    public Comentario(int id, int eventoId, int nota, String texto) {
        this.id = id;
        this.eventoId = eventoId;
        this.nota = nota;
        this.texto = texto;
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

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
}
