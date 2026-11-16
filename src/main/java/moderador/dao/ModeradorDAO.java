package moderador.dao;

import moderador.model.Moderador;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ModeradorDAO {
    private static final String ARQUIVO = "moderadores.txt";

    public List<Moderador> listar() {
        List<Moderador> lista = new ArrayList<>();
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
                    String nivelAcesso = partes[3].trim();
                    lista.add(new Moderador(id, nome, email, nivelAcesso));
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void salvarTodos(List<Moderador> lista) {
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(ARQUIVO))) {
            for (Moderador m : lista) {
                escritor.write(m.getId() + ";" + m.getNome() + ";" + m.getEmail() + ";" + m.getNivelAcesso());
                escritor.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean inserir(Moderador moderador) {
        List<Moderador> lista = listar();
        for (Moderador m : lista) {
            if (m.getId() == moderador.getId()) {
                return false;
            }
        }
        lista.add(moderador);
        salvarTodos(lista);
        return true;
    }

    public boolean atualizar(Moderador moderador) {
        List<Moderador> lista = listar();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == moderador.getId()) {
                lista.set(i, moderador);
                salvarTodos(lista);
                return true;
            }
        }
        return false;
    }

    public boolean excluir(int id) {
        List<Moderador> lista = listar();
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
