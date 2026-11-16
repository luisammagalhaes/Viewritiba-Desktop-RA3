package organizador;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import organizador.dao.OrganizadorDAO;
import organizador.model.Organizador;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrganizadorDAOTest {
    private final OrganizadorDAO dao = new OrganizadorDAO();
    private final File arquivo = new File("organizadores.txt");

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
        Organizador o = new Organizador(1, "Carlos", "carlos@teste.com", "41999998888");
        boolean inserido = dao.inserir(o);
        assertTrue(inserido);

        List<Organizador> lista = dao.listar();
        assertEquals(1, lista.size());
        assertEquals("Carlos", lista.get(0).getNome());
    }

    @Test
    public void testNaoPermitirIdDuplicado() {
        Organizador o1 = new Organizador(1, "Carlos", "carlos@teste.com", "41999998888");
        Organizador o2 = new Organizador(1, "Outro", "outro@teste.com", "41999997777");

        assertTrue(dao.inserir(o1));
        assertFalse(dao.inserir(o2));
        assertEquals(1, dao.listar().size());
    }

    @Test
    public void testAtualizar() {
        Organizador o = new Organizador(1, "Carlos", "carlos@teste.com", "41999998888");
        dao.inserir(o);

        Organizador atualizado = new Organizador(1, "Carlos Silva", "carlos.silva@teste.com", "41988887777");
        boolean sucesso = dao.atualizar(atualizado);
        assertTrue(sucesso);

        List<Organizador> lista = dao.listar();
        assertEquals("Carlos Silva", lista.get(0).getNome());
        assertEquals("carlos.silva@teste.com", lista.get(0).getEmail());
    }

    @Test
    public void testExcluir() {
        Organizador o = new Organizador(1, "Carlos", "carlos@teste.com", "41999998888");
        dao.inserir(o);

        boolean removido = dao.excluir(1);
        assertTrue(removido);
        assertEquals(0, dao.listar().size());
    }
}
