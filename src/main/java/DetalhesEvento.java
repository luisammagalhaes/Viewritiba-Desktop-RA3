import java.io.Serializable;
import java.util.ArrayList;

public class DetalhesEvento implements Serializable{

    private static final long serialVersionUID = 1L;

    private Evento evento;
    private String organizador;
    private ArrayList<String> categorias;
    private ArrayList<String> comentarios;

    public DetalhesEvento(){
        categorias = new ArrayList<String>();
        comentarios = new ArrayList<String>();
    }

    public DetalhesEvento(Evento evento, String organizador, ArrayList<String> categorias, ArrayList<String> comentarios){

        this.evento = evento;
        this.organizador = organizador;
        this.categorias = categorias;
        this.comentarios = comentarios;
    }

    public Evento getEvento(){
        return evento;
    }

    public void setEvento(Evento evento){
        this.evento = evento;
    }

    public String getOrganizador() {
        return organizador;
    }

    public void setOrganizador(String organizador) {
        this.organizador = organizador;
    }

    public ArrayList<String> getCategorias() {
        return categorias;
    }

    public void setCategorias(ArrayList<String> categorias) {
        this.categorias = categorias;
    }

    public ArrayList<String> getComentarios() {
        return comentarios;
    }

    public void setComentarios(ArrayList<String> comentarios) {
        this.comentarios = comentarios;
    }

    public String getTitulo() {
        return evento.getTitulo();
    }

    public String getDescricao() {
        return evento.getDescricao();
    }

    public String getDataHora() {
        return evento.getDataHora();
    }

    public String getLocal() {
        return evento.getLocal();
    }
}