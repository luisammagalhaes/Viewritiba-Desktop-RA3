import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TelaCategoria {

    private Dados dados;

    private TextField id = new TextField();
    private TextField nome = new TextField();
    private TextField descricao = new TextField();
    private TextField publicoAlvo = new TextField();

    private Label mensagem = new Label();
    private GridPane tabela = new GridPane();

    public TelaCategoria(Dados dados) {
        this.dados = dados;
    }

    public void mostrar() {
        Stage janela = new Stage();

        Label titulo = new Label("Gerenciamento de categorias");
        titulo.getStyleClass().add("titulo-tela");

        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(10);

        adicionarCampo(
                formulario,
                "ID para consultar, alterar ou excluir:",
                id,
                0
        );

        adicionarCampo(
                formulario,
                "Nome:",
                nome,
                1
        );

        adicionarCampo(
                formulario,
                "Descrição:",
                descricao,
                2
        );

        adicionarCampo(
                formulario,
                "Público-alvo:",
                publicoAlvo,
                3
        );

        Button cadastrar = new Button("Cadastrar");
        Button consultar = new Button("Consultar ID");
        Button alterar = new Button("Alterar");
        Button excluir = new Button("Excluir");
        Button voltar = new Button("Voltar");

        cadastrar.setOnAction(
                evento -> cadastrar()
        );

        consultar.setOnAction(
                evento -> consultar()
        );

        alterar.setOnAction(
                evento -> alterar()
        );

        excluir.setOnAction(
                evento -> excluir()
        );

        voltar.setOnAction(
                evento -> janela.close()
        );

        HBox botoes = new HBox(10);
        botoes.setAlignment(Pos.CENTER);

        botoes.getChildren().add(cadastrar);
        botoes.getChildren().add(consultar);
        botoes.getChildren().add(alterar);
        botoes.getChildren().add(excluir);
        botoes.getChildren().add(voltar);

        VBox conteudo = new VBox(12);
        conteudo.setAlignment(Pos.CENTER);
        conteudo.getStyleClass().add("formulario");

        conteudo.getChildren().add(titulo);
        conteudo.getChildren().add(formulario);
        conteudo.getChildren().add(botoes);
        conteudo.getChildren().add(mensagem);
        conteudo.getChildren().add(tabela);

        VBox fundo = new VBox();
        fundo.setAlignment(Pos.CENTER);
        fundo.getChildren().add(conteudo);

        Scene cena = new Scene(fundo, 850, 650);

        cena.getStylesheets().add(
                getClass()
                        .getResource("/style.css")
                        .toExternalForm()
        );

        janela.setTitle("Viewritiba - Categorias");
        janela.setScene(cena);

        atualizarTabela();

        janela.show();
    }

    private void adicionarCampo(
            GridPane painel,
            String texto,
            TextField campo,
            int linha
    ) {
        painel.add(
                new Label(texto),
                0,
                linha
        );

        painel.add(
                campo,
                1,
                linha
        );
    }

    private void cadastrar() {
        if (!camposPreenchidos()) {
            return;
        }

        Categoria categoria = new Categoria(
                gerarId(),
                nome.getText().trim(),
                descricao.getText().trim(),
                publicoAlvo.getText().trim()
        );

        dados.getCategorias().add(categoria);
        dados.salvar();

        limparCampos();
        atualizarTabela();

        mensagem.setText(
                "Categoria cadastrada com sucesso!"
        );
    }

    private void consultar() {
        Categoria categoria = buscarPorId();

        if (categoria == null) {
            return;
        }

        nome.setText(categoria.getNome());
        descricao.setText(categoria.getDescricao());
        publicoAlvo.setText(categoria.getPublicoAlvo());

        mensagem.setText(
                "Categoria encontrada."
        );
    }

    private void alterar() {
        Categoria categoria = buscarPorId();

        if (
                categoria == null ||
                        !camposPreenchidos()
        ) {
            return;
        }

        categoria.setNome(
                nome.getText().trim()
        );

        categoria.setDescricao(
                descricao.getText().trim()
        );

        categoria.setPublicoAlvo(
                publicoAlvo.getText().trim()
        );

        dados.salvar();
        atualizarTabela();

        mensagem.setText(
                "Categoria alterada com sucesso!"
        );
    }

    private void excluir() {
        Categoria categoria = buscarPorId();

        if (categoria == null) {
            return;
        }

        if (categoriaEmUso(categoria)) {
            mensagem.setText(
                    "Categoria vinculada a um evento."
            );

            return;
        }

        dados.getCategorias().remove(categoria);
        dados.salvar();

        limparCampos();
        atualizarTabela();

        mensagem.setText(
                "Categoria excluída com sucesso!"
        );
    }

    private Categoria buscarPorId() {
        try {
            int numero = Integer.parseInt(
                    id.getText().trim()
            );

            for (Categoria categoria : dados.getCategorias()) {
                if (categoria.getId() == numero) {
                    return categoria;
                }
            }

        } catch (NumberFormatException erro) {
            mensagem.setText(
                    "Informe um ID numérico."
            );

            return null;
        }

        mensagem.setText(
                "Categoria não encontrada."
        );

        return null;
    }

    private boolean categoriaEmUso(Categoria categoria) {
        for (Evento evento : dados.getEventos()) {
            if (
                    evento.getCategoria() != null &&
                            evento.getCategoria().getId() == categoria.getId()
            ) {
                return true;
            }
        }

        return false;
    }

    private boolean camposPreenchidos() {
        if (
                nome.getText().trim().equals("") ||
                        descricao.getText().trim().equals("") ||
                        publicoAlvo.getText().trim().equals("")
        ) {
            mensagem.setText(
                    "Preencha todos os campos."
            );

            return false;
        }

        return true;
    }

    private void atualizarTabela() {
        tabela.getChildren().clear();

        String[] cabecalho = {
                "ID",
                "Nome",
                "Descrição",
                "Público-alvo"
        };

        for (
                int coluna = 0;
                coluna < cabecalho.length;
                coluna++
        ) {
            tabela.add(
                    criarCelula(
                            cabecalho[coluna],
                            true
                    ),
                    coluna,
                    0
            );
        }

        int linha = 1;

        for (Categoria categoria : dados.getCategorias()) {
            String[] valores = {
                    "" + categoria.getId(),
                    categoria.getNome(),
                    categoria.getDescricao(),
                    categoria.getPublicoAlvo()
            };

            for (
                    int coluna = 0;
                    coluna < valores.length;
                    coluna++
            ) {
                tabela.add(
                        criarCelula(
                                valores[coluna],
                                false
                        ),
                        coluna,
                        linha
                );
            }

            linha++;
        }

        if (linha == 1) {
            tabela.add(
                    new Label(
                            "Nenhuma categoria cadastrada."
                    ),
                    0,
                    1
            );
        }
    }

    private Label criarCelula(
            String texto,
            boolean cabecalho
    ) {
        Label celula = new Label(texto);

        if (cabecalho) {
            celula
                    .getStyleClass()
                    .add("cabecalho-tabela");
        } else {
            celula
                    .getStyleClass()
                    .add("celula-tabela");
        }

        return celula;
    }

    private void limparCampos() {
        id.clear();
        nome.clear();
        descricao.clear();
        publicoAlvo.clear();
    }

    private int gerarId() {
        int maior = 0;

        for (Categoria categoria : dados.getCategorias()) {
            if (categoria.getId() > maior) {
                maior = categoria.getId();
            }
        }

        return maior + 1;
    }
}