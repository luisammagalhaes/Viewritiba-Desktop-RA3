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

    public Dados() {
        eventos = new ArrayList<Evento>();
        categorias = new ArrayList<Categoria>();
    }

    public ArrayList<Evento> getEventos() {
        return eventos;
    }

    public ArrayList<Categoria> getCategorias() {
        return categorias;
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
            System.out.println(
                    "Erro ao salvar os dados: " + erro.getMessage()
            );
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
            System.out.println(
                    "Erro ao carregar os dados: " + erro.getMessage()
            );

            return new Dados();
        }
    }
}