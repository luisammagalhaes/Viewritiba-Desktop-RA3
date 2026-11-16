
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Dados implements Serializable {

    private static final long serialVersionUID = 1L;

    private ArrayList<Evento> eventos;
    private ArrayList<Categoria> categorias;
    private ArrayList<Comentario> comentarios;
    private ArrayList<Conta> contas;

    // CRUDs de Favoritos e Compartilhamentos
    private ArrayList<Favorito> favoritos;
    private ArrayList<Compartilhamento> compartilhamentos;

    // CRUDs de Locais e Patrocinadores
    private ArrayList<Local> locais;
    private ArrayList<Patrocinador> patrocinadores;

    // CRUDs de Participantes e Detalhes de Eventos
    private ArrayList<Participante> participantes;
    private ArrayList<DetalhesEvento> detalhesEventos;

    public Dados() {
        eventos = new ArrayList<Evento>();
        categorias = new ArrayList<Categoria>();
        comentarios = new ArrayList<Comentario>();
        contas = new ArrayList<Conta>();

        favoritos = new ArrayList<Favorito>();
        compartilhamentos = new ArrayList<Compartilhamento>();

        locais = new ArrayList<Local>();
        patrocinadores = new ArrayList<Patrocinador>();

        participantes = new ArrayList<Participante>();
        detalhesEventos = new ArrayList<DetalhesEvento>();
    }

    public ArrayList<Evento> getEventos() {
        if (eventos == null) {
            eventos = new ArrayList<Evento>();
        }
        return eventos;
    }

    public ArrayList<Categoria> getCategorias() {
        if (categorias == null) {
            categorias = new ArrayList<Categoria>();
        }
        return categorias;
    }

    public ArrayList<Comentario> getComentarios() {
        if (comentarios == null) {
            comentarios = new ArrayList<Comentario>();
        }
        return comentarios;
    }

    public ArrayList<Conta> getContas() {
        if (contas == null) {
            contas = new ArrayList<Conta>();
        }
        return contas;
    }

    public ArrayList<Favorito> getFavoritos() {
        if (favoritos == null) {
            favoritos = new ArrayList<Favorito>();
        }
        return favoritos;
    }

    public ArrayList<Compartilhamento> getCompartilhamentos() {
        if (compartilhamentos == null) {
            compartilhamentos = new ArrayList<Compartilhamento>();
        }
        return compartilhamentos;
    }

    public ArrayList<Local> getLocais() {
        if (locais == null) {
            locais = new ArrayList<Local>();
        }
        return locais;
    }

    public ArrayList<Patrocinador> getPatrocinadores() {
        if (patrocinadores == null) {
            patrocinadores = new ArrayList<Patrocinador>();
        }
        return patrocinadores;
    }

    public ArrayList<Participante> getParticipantes() {
        if (participantes == null) {
            participantes = new ArrayList<Participante>();
        }
        return participantes;
    }

    public ArrayList<DetalhesEvento> getDetalhesEventos() {
        if (detalhesEventos == null) {
            detalhesEventos = new ArrayList<DetalhesEvento>();
        }
        return detalhesEventos;
    }

    public void salvar() {
        try {
            FileOutputStream arquivo =
                    new FileOutputStream("viewritiba.dat");

            ObjectOutputStream gravador =
                    new ObjectOutputStream(arquivo);

            gravador.writeObject(this);
            gravador.close();

        } catch (Exception erro) {
            System.out.println("Erro ao salvar os dados.");
        }
    }

    public static Dados carregar() {
        try {
            FileInputStream arquivo =
                    new FileInputStream("viewritiba.dat");

            ObjectInputStream leitor =
                    new ObjectInputStream(arquivo);

            Dados dadosSalvos = (Dados) leitor.readObject();
            leitor.close();

            return dadosSalvos;

        } catch (FileNotFoundException erro) {
            return new Dados();

        } catch (Exception erro) {
            System.out.println("Erro ao carregar os dados.");
            return new Dados();
        }
    }
}
