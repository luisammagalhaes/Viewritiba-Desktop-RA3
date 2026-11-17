import java.io.Serializable;
import java.time.LocalDate;

public class ContaSuspensa implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String tipoConta;
    private int idConta;
    private String motivo;
    private LocalDate dataSuspensao;

    public ContaSuspensa(int id, String tipoConta, int idConta,
                         String motivo, LocalDate dataSuspensao) {
        this.id = id;
        this.tipoConta = tipoConta;
        this.idConta = idConta;
        this.motivo = motivo;
        this.dataSuspensao = dataSuspensao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipoConta() {
        return tipoConta;
    }

    public void setTipoConta(String tipoConta) {
        this.tipoConta = tipoConta;
    }

    public int getIdConta() {
        return idConta;
    }

    public void setIdConta(int idConta) {
        this.idConta = idConta;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDate getDataSuspensao() {
        return dataSuspensao;
    }

    public void setDataSuspensao(LocalDate dataSuspensao) {
        this.dataSuspensao = dataSuspensao;
    }
}
