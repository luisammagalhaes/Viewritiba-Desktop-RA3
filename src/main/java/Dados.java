import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Dados implements Serializable {

    private ArrayList<Evento> eventos;
    private ArrayList<Categoria> categorias;
    private ArrayList<Comentario> comentarios;
    private ArrayList<Conta> contas;

    public Dados() {
        eventos = new ArrayList<Evento>();
        categorias = new ArrayList<Categoria>();
        comentarios = new ArrayList<Comentario>();
        contas = new ArrayList<Conta>();
    }

    public ArrayList<Evento> getEventos() {
        return eventos;
    }

    public ArrayList<Categoria> getCategorias() {
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