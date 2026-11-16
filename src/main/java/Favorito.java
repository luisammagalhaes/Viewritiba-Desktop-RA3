
import java.io.Serializable;
import java.time.LocalDate;

public class Favorito implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private int idUsuario;
    private int idEvento;
    private String observacao;
    private LocalDate dataSalvamento;

    public Favorito(int id, int idUsuario, int idEvento,
                    String observacao, LocalDate dataSalvamento) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.idEvento = idEvento;
        this.observacao = observacao;
        this.dataSalvamento = dataSalvamento;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(int idEvento) {
        this.idEvento = idEvento;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public LocalDate getDataSalvamento() {
        return dataSalvamento;
    }

    public void setDataSalvamento(LocalDate dataSalvamento) {
        this.dataSalvamento = dataSalvamento;
    }
}
