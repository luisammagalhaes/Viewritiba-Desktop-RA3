package organizador.dao;

import organizador.model.Organizador;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OrganizadorDAO {
    private static final String ARQUIVO = "organizadores.txt";

    public List<Organizador> listar() {
        List<Organizador> lista = new ArrayList<>();
        File arquivo = new File(ARQUIVO);
        if (!arquivo.exists()) {
            return lista;
        }

        try (BufferedReader leitor = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = leitor.readLine()) != null) {
                if (linha.trim().isEmpty()) {
                    continue;
                }
                String[] partes = linha.split(";");
                if (partes.length >= 4) {
                    int id = Integer.parseInt(partes[0].trim());
                    String nome = partes[1].trim();
                    String email = partes[2].trim();
                    String telefone = partes[3].trim();
                    lista.add(new Organizador(id, nome, email, telefone));
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void salvarTodos(List<Organizador> lista) {
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(ARQUIVO))) {
            for (Organizador o : lista) {
                escritor.write(o.getId() + ";" + o.getNome() + ";" + o.getEmail() + ";" + o.getTelefone());
                escritor.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean inserir(Organizador organizador) {
        List<Organizador> lista = listar();
        for (Organizador o : lista) {
            if (o.getId() == organizador.getId()) {
                return false;
            }
        }
        lista.add(organizador);
        salvarTodos(lista);
        return true;
    }

    public boolean atualizar(Organizador organizador) {
        List<Organizador> lista = listar();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == organizador.getId()) {
                lista.set(i, organizador);
                salvarTodos(lista);
                return true;
            }
        }
        return false;
    }

    public boolean excluir(int id) {
        List<Organizador> lista = listar();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == id) {
                lista.remove(i);
                salvarTodos(lista);
                return true;
            }
        }
        return false;
    }
}
