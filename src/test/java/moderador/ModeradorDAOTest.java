package moderador;

import moderador.dao.ModeradorDAO;
import moderador.model.Moderador;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ModeradorDAOTest {
    private final ModeradorDAO dao = new ModeradorDAO();
    private final File arquivo = new File("moderadores.txt");

    @BeforeEach
    public void setup() {
        if (arquivo.exists()) {
            arquivo.delete();
        }
    }

    @AfterEach
    public void tearDown() {
        if (arquivo.exists()) {
            arquivo.delete();
        }
    }

    @Test
    public void testInserirEListar() {
        Moderador m = new Moderador(1, "Ana", "ana@moderacao.com", "ADMIN");
        boolean inserido = dao.inserir(m);
        assertTrue(inserido);

        List<Moderador> lista = dao.listar();
        assertEquals(1, lista.size());
        assertEquals("Ana", lista.get(0).getNome());
        assertEquals("ADMIN", lista.get(0).getNivelAcesso());
    }

    @Test
    public void testNaoPermitirIdDuplicado() {
        Moderador m1 = new Moderador(1, "Ana", "ana@moderacao.com", "ADMIN");
        Moderador m2 = new Moderador(1, "Beatriz", "beatriz@moderacao.com", "MODERADOR");

        assertTrue(dao.inserir(m1));
        assertFalse(dao.inserir(m2));
        assertEquals(1, dao.listar().size());
    }

    @Test
    public void testAtualizar() {
        Moderador m = new Moderador(1, "Ana", "ana@moderacao.com", "ADMIN");
        dao.inserir(m);

        Moderador atualizado = new Moderador(1, "Ana Paula", "ana.paula@moderacao.com", "SUPERVISOR");
        boolean sucesso = dao.atualizar(atualizado);
        assertTrue(sucesso);

        List<Moderador> lista = dao.listar();
        assertEquals("Ana Paula", lista.get(0).getNome());
        assertEquals("SUPERVISOR", lista.get(0).getNivelAcesso());
    }

    @Test
    public void testExcluir() {
        Moderador m = new Moderador(1, "Ana", "ana@moderacao.com", "ADMIN");
        dao.inserir(m);

        boolean removido = dao.excluir(1);
        assertTrue(removido);
        assertEquals(0, dao.listar().size());
    }
}
