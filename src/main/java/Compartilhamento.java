
import java.io.Serializable;
import java.time.LocalDate;

public class Compartilhamento implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private int idEvento;
    private int idRemetente;
    private int idDestinatario;
    private String mensagem;
    private LocalDate dataCompartilhamento;

    public Compartilhamento(int id, int idEvento,
                            int idRemetente, int idDestinatario,
                            String mensagem, LocalDate dataCompartilhamento) {
        this.id = id;
        this.idEvento = idEvento;
        this.idRemetente = idRemetente;
        this.idDestinatario = idDestinatario;
        this.mensagem = mensagem;
        this.dataCompartilhamento = dataCompartilhamento;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(int idEvento) {
        this.idEvento = idEvento;
    }

    public int getIdRemetente() {
        return idRemetente;
    }

    public void setIdRemetente(int idRemetente) {
        this.idRemetente = idRemetente;
    }

    public int getIdDestinatario() {
        return idDestinatario;
    }

    public void setIdDestinatario(int idDestinatario) {
        this.idDestinatario = idDestinatario;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public LocalDate getDataCompartilhamento() {
        return dataCompartilhamento;
    }

    public void setDataCompartilhamento(LocalDate dataCompartilhamento) {
        this.dataCompartilhamento = dataCompartilhamento;
    }
}
