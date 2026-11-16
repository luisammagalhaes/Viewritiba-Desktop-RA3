import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TelaCategoria {

    private Dados dados;

    private TextField campoNome;
    private TextField campoDescricao;
    private TextField campoPublicoAlvo;

    private Label mensagem;
    private ListView<String> listaCategorias;

    private Categoria categoriaEmAlteracao;

    public TelaCategoria(Dados dados) {
        this.dados = dados;
    }

    public void mostrar() {
        Stage janelaCategoria = new Stage();

        Label titulo =
                new Label("Gerenciamento de categorias");

        titulo.getStyleClass().add("titulo-tela");

        Label nome = new Label("Nome:");

        campoNome = new TextField();
        campoNome.setPromptText("Exemplo: Gastronomia");

        Label descricao = new Label("Descrição:");

        campoDescricao = new TextField();
        campoDescricao.setPromptText(
                "Digite uma descrição para a categoria"
        );

        Label publicoAlvo = new Label("Público-alvo:");

        campoPublicoAlvo = new TextField();
        campoPublicoAlvo.setPromptText(
                "Exemplo: Jovens e adultos"
        );

        Button botaoCadastrar =
                new Button("Cadastrar categoria");

        mensagem = new Label();
        mensagem.getStyleClass().add("mensagem");

        Label tituloLista =
                new Label("Categorias cadastradas");

        tituloLista.getStyleClass().add("subtitulo-tela");

        listaCategorias = new ListView<String>();
        listaCategorias.setPrefHeight(190);

        Button botaoCarregar =
                new Button("Carregar dados");

        Button botaoAlterar =
                new Button("Alterar");

        Button botaoExcluir =
                new Button("Excluir");

        botaoCarregar.getStyleClass().add("botao-acao");
        botaoCarregar.getStyleClass().add("botao-carregar");

        botaoAlterar.getStyleClass().add("botao-acao");
        botaoAlterar.getStyleClass().add("botao-alterar");

        botaoExcluir.getStyleClass().add("botao-acao");
        botaoExcluir.getStyleClass().add("botao-excluir");

        botaoCadastrar.setOnAction(
                evento -> cadastrar()
        );

        botaoCarregar.setOnAction(
                evento -> carregarSelecionada()
        );

        botaoAlterar.setOnAction(
                evento -> alterar()
        );

        botaoExcluir.setOnAction(
                evento -> excluir()
        );

        HBox painelAcoes = new HBox(10);
        painelAcoes.setAlignment(Pos.CENTER);

        painelAcoes.getChildren().add(botaoCarregar);
        painelAcoes.getChildren().add(botaoAlterar);
        painelAcoes.getChildren().add(botaoExcluir);

        VBox formulario = new VBox(10);
        formulario.setAlignment(Pos.CENTER_LEFT);
        formulario.getStyleClass().add("formulario");

        formulario.getChildren().add(titulo);
        formulario.getChildren().add(nome);
        formulario.getChildren().add(campoNome);
        formulario.getChildren().add(descricao);
        formulario.getChildren().add(campoDescricao);
        formulario.getChildren().add(publicoAlvo);
        formulario.getChildren().add(campoPublicoAlvo);
        formulario.getChildren().add(botaoCadastrar);
        formulario.getChildren().add(mensagem);
        formulario.getChildren().add(tituloLista);
        formulario.getChildren().add(listaCategorias);
        formulario.getChildren().add(painelAcoes);

        VBox fundo = new VBox();
        fundo.setAlignment(Pos.CENTER);
        fundo.getChildren().add(formulario);

        Scene cena = new Scene(fundo, 760, 760);

        cena.getStylesheets().add(
                getClass()
                        .getResource("/style.css")
                        .toExternalForm()
        );

        janelaCategoria.setTitle(
                "Viewritiba - Categorias"
        );

        janelaCategoria.setScene(cena);

        atualizarLista();

        janelaCategoria.show();
    }

    private void cadastrar() {
        if (!camposPreenchidos()) {
            return;
        }

        int id = gerarId();

        Categoria categoria = new Categoria(
                id,
                campoNome.getText().trim(),
                campoDescricao.getText().trim(),
                campoPublicoAlvo.getText().trim()
        );

        dados.getCategorias().add(categoria);
        dados.salvar();

        limparCampos();
        atualizarLista();

        mensagem.setText(
                "Categoria cadastrada com sucesso!"
        );
    }

    private void carregarSelecionada() {
        Categoria categoria = obterCategoriaSelecionada();

        if (categoria == null) {
            return;
        }

        categoriaEmAlteracao = categoria;

        campoNome.setText(categoria.getNome());
        campoDescricao.setText(categoria.getDescricao());
        campoPublicoAlvo.setText(
                categoria.getPublicoAlvo()
        );

        mensagem.setText(
                "Dados carregados. Altere os campos desejados."
        );
    }

    private void alterar() {
        if (categoriaEmAlteracao == null) {
            mensagem.setText(
                    "Selecione e carregue uma categoria."
            );

            return;
        }

        if (!camposPreenchidos()) {
            return;
        }

        categoriaEmAlteracao.setNome(
                campoNome.getText().trim()
        );

        categoriaEmAlteracao.setDescricao(
                campoDescricao.getText().trim()
        );

        categoriaEmAlteracao.setPublicoAlvo(
                campoPublicoAlvo.getText().trim()
        );

        dados.salvar();

        categoriaEmAlteracao = null;

        limparCampos();
        atualizarLista();

        mensagem.setText(
                "Categoria alterada com sucesso!"
        );
    }

    private void excluir() {
        Categoria categoria = obterCategoriaSelecionada();

        if (categoria == null) {
            return;
        }

        dados.getCategorias().remove(categoria);
        dados.salvar();

        if (categoria == categoriaEmAlteracao) {
            categoriaEmAlteracao = null;
        }

        limparCampos();
        atualizarLista();

        mensagem.setText(
                "Categoria excluída com sucesso!"
        );
    }

    private Categoria obterCategoriaSelecionada() {
        int indice =
                listaCategorias
                        .getSelectionModel()
                        .getSelectedIndex();

        if (
                indice < 0 ||
                        indice >= dados.getCategorias().size()
        ) {
            mensagem.setText(
                    "Selecione uma categoria na lista."
            );

            return null;
        }

        return dados.getCategorias().get(indice);
    }

    private boolean camposPreenchidos() {
        String nome = campoNome.getText().trim();
        String descricao = campoDescricao.getText().trim();
        String publicoAlvo =
                campoPublicoAlvo.getText().trim();

        if (
                nome.equals("") ||
                        descricao.equals("") ||
                        publicoAlvo.equals("")
        ) {
            mensagem.setText(
                    "Preencha todos os campos obrigatórios."
            );

            return false;
        }

        return true;
    }

    private void limparCampos() {
        campoNome.clear();
        campoDescricao.clear();
        campoPublicoAlvo.clear();

        listaCategorias
                .getSelectionModel()
                .clearSelection();
    }

    private void atualizarLista() {
        listaCategorias.getItems().clear();

        for (Categoria categoria : dados.getCategorias()) {
            String item =
                    categoria.getId() + " | " +
                            categoria.getNome() + " | " +
                            categoria.getDescricao() + " | " +
                            categoria.getPublicoAlvo();

            listaCategorias.getItems().add(item);
        }

        if (dados.getCategorias().size() == 0) {
            listaCategorias.getItems().add(
                    "Nenhuma categoria cadastrada."
            );
        }
    }

    private int gerarId() {
        int maiorId = 0;

        for (Categoria categoria : dados.getCategorias()) {
            if (categoria.getId() > maiorId) {
                maiorId = categoria.getId();
            }
        }

        return maiorId + 1;
    }
}