import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class TelaDetalhesEvento {

    private Dados dados;

    private ListView<Evento> listaEventos;

    private Label lblEventoId;
    private Label lblTitulo;
    private Label lblDescricao;
    private Label lblDataHora;
    private Label lblLocal;
    private Label lblCategoria;

    private TextField campoId = new TextField();
    private TextField campoIdEvento = new TextField();
    private TextField campoOrganizador = new TextField();
    private TextField campoCategorias = new TextField();
    private TextField campoComentario = new TextField();
    private Label mensagem = new Label();
    private GridPane tabela = new GridPane();

    public TelaDetalhesEvento(Dados dados) {
        this.dados = dados;
    }

    public void mostrar() {
        Stage janela = new Stage();

        Label cabecalho = new Label("Detalhes dos Eventos");
        cabecalho.getStyleClass().add("titulo-tela");

        listaEventos = new ListView<Evento>();
        listaEventos.setPrefWidth(260);
        listaEventos.setPrefHeight(180);
        atualizarListaEventos();

        lblEventoId = new Label("-");
        lblTitulo = new Label("-");
        lblDescricao = new Label("-");
        lblDataHora = new Label("-");
        lblLocal = new Label("-");
        lblCategoria = new Label("-");

        GridPane detalhes = new GridPane();
        detalhes.setHgap(15);
        detalhes.setVgap(10);
        detalhes.setPadding(new Insets(10));

        detalhes.add(new Label("ID:"), 0, 0);
        detalhes.add(lblEventoId, 1, 0);
        detalhes.add(new Label("Título:"), 0, 1);
        detalhes.add(lblTitulo, 1, 1);
        detalhes.add(new Label("Descrição:"), 0, 2);
        detalhes.add(lblDescricao, 1, 2);
        detalhes.add(new Label("Data e hora:"), 0, 3);
        detalhes.add(lblDataHora, 1, 3);
        detalhes.add(new Label("Local:"), 0, 4);
        detalhes.add(lblLocal, 1, 4);
        detalhes.add(new Label("Categoria:"), 0, 5);
        detalhes.add(lblCategoria, 1, 5);

        listaEventos.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, antigo, selecionado) -> {
                    if (selecionado != null) {
                        exibirDetalhes(selecionado);
                        campoIdEvento.setText(String.valueOf(selecionado.getId()));
                    }
                });

        VBox painelLista = new VBox(8, new Label("Eventos cadastrados"), listaEventos);
        VBox painelDetalhes = new VBox(8, new Label("Informações do evento"), detalhes);
        HBox blocoVisualizacao = new HBox(25, painelLista, painelDetalhes);
        blocoVisualizacao.setAlignment(Pos.CENTER);

        campoId.setPromptText("Informe para consultar, alterar ou excluir");
        campoIdEvento.setPromptText("ID numérico do evento");
        campoOrganizador.setPromptText("Nome do organizador");
        campoCategorias.setPromptText("Ex.: Música, Cultura");
        campoComentario.setPromptText("Observação ou comentário inicial");

        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(10);
        adicionarCampo(formulario, "ID do detalhe:", campoId, 0);
        adicionarCampo(formulario, "ID do evento:", campoIdEvento, 1);
        adicionarCampo(formulario, "Organizador:", campoOrganizador, 2);
        adicionarCampo(formulario, "Categorias:", campoCategorias, 3);
        adicionarCampo(formulario, "Comentário:", campoComentario, 4);

        Button cadastrar = new Button("Cadastrar");
        Button consultar = new Button("Consultar ID");
        Button alterar = new Button("Alterar");
        Button excluir = new Button("Excluir");
        Button voltar = new Button("Voltar");

        cadastrar.setOnAction(e -> cadastrar());
        consultar.setOnAction(e -> consultar());
        alterar.setOnAction(e -> alterar());
        excluir.setOnAction(e -> excluir());
        voltar.setOnAction(e -> janela.close());

        HBox botoes = new HBox(10, cadastrar, consultar, alterar, excluir, voltar);
        botoes.setAlignment(Pos.CENTER);

        VBox conteudo = new VBox(12, cabecalho, blocoVisualizacao, formulario, botoes, mensagem, tabela);
        conteudo.setAlignment(Pos.CENTER);
        conteudo.getStyleClass().add("formulario");
        conteudo.getStyleClass().add("formulario-evento");

        VBox fundo = new VBox(conteudo);
        fundo.setAlignment(Pos.CENTER);
        fundo.setPadding(new Insets(20));

        ScrollPane rolagem = new ScrollPane(fundo);
        rolagem.setFitToWidth(true);

        Scene cena = new Scene(rolagem, 1000, 720);
        cena.getStylesheets().add(
                getClass().getResource("/style.css").toExternalForm()
        );

        janela.setTitle("Viewritiba - Detalhes dos Eventos");
        janela.setScene(cena);

        atualizarTabela();
        janela.show();
    }

    private void adicionarCampo(GridPane painel, String rotulo, TextField campo, int linha) {
        painel.add(new Label(rotulo), 0, linha);
        painel.add(campo, 1, linha);
    }

    private void cadastrar() {
        Evento evento = buscarEventoInformado();
        if (evento == null || !camposTextoValidos()) {
            return;
        }

        DetalhesEvento detalhe = new DetalhesEvento(
                gerarId(),
                evento,
                campoOrganizador.getText().trim(),
                converterLista(campoCategorias.getText()),
                converterLista(campoComentario.getText())
        );

        dados.getDetalhesEventos().add(detalhe);
        dados.salvar();

        limparCampos();
        atualizarTabela();
        mensagem.setText("Detalhes do evento cadastrados com sucesso!");
    }

    private void consultar() {
        DetalhesEvento detalhe = buscarPorId();
        if (detalhe == null) {
            return;
        }

        campoIdEvento.setText(detalhe.getEvento() != null
                ? String.valueOf(detalhe.getEvento().getId()) : "");
        campoOrganizador.setText(detalhe.getOrganizador());
        campoCategorias.setText(String.join(", ", detalhe.getCategorias()));
        campoComentario.setText(String.join(", ", detalhe.getComentarios()));
        mensagem.setText("Detalhes encontrados.");
    }

    private void alterar() {
        DetalhesEvento detalhe = buscarPorId();
        if (detalhe == null) {
            return;
        }

        Evento evento = buscarEventoInformado();
        if (evento == null || !camposTextoValidos()) {
            return;
        }

        detalhe.setEvento(evento);
        detalhe.setOrganizador(campoOrganizador.getText().trim());
        detalhe.setCategorias(converterLista(campoCategorias.getText()));
        detalhe.setComentarios(converterLista(campoComentario.getText()));

        dados.salvar();
        atualizarTabela();
        mensagem.setText("Detalhes alterados com sucesso!");
    }

    private void excluir() {
        DetalhesEvento detalhe = buscarPorId();
        if (detalhe == null) {
            return;
        }

        dados.getDetalhesEventos().remove(detalhe);
        dados.salvar();

        limparCampos();
        atualizarTabela();
        mensagem.setText("Detalhes excluídos com sucesso!");
    }

    private DetalhesEvento buscarPorId() {
        try {
            int numero = Integer.parseInt(campoId.getText().trim());

            for (DetalhesEvento item : dados.getDetalhesEventos()) {
                if (item.getId() == numero) {
                    return item;
                }
            }
        } catch (NumberFormatException erro) {
            mensagem.setText("Informe um ID numérico de detalhe.");
            return null;
        }

        mensagem.setText("Detalhe não encontrado.");
        return null;
    }

    private Evento buscarEventoInformado() {
        try {
            int idEvento = Integer.parseInt(campoIdEvento.getText().trim());

            for (Evento evento : dados.getEventos()) {
                if (evento.getId() == idEvento) {
                    return evento;
                }
            }
        } catch (NumberFormatException erro) {
            mensagem.setText("Informe um ID numérico de evento.");
            return null;
        }

        mensagem.setText("Evento não encontrado.");
        return null;
    }

    private boolean camposTextoValidos() {
        if (campoOrganizador.getText().trim().equals("")
                || campoCategorias.getText().trim().equals("")
                || campoComentario.getText().trim().equals("")) {
            mensagem.setText("Preencha organizador, categorias e comentário.");
            return false;
        }
        return true;
    }

    private ArrayList<String> converterLista(String texto) {
        ArrayList<String> lista = new ArrayList<String>();
        String[] partes = texto.split(",");
        for (String parte : partes) {
            String item = parte.trim();
            if (!item.equals("")) {
                lista.add(item);
            }
        }
        return lista;
    }

    private void atualizarListaEventos() {
        listaEventos.getItems().clear();
        for (Evento evento : dados.getEventos()) {
            listaEventos.getItems().add(evento);
        }
    }

    private void exibirDetalhes(Evento evento) {
        lblEventoId.setText(String.valueOf(evento.getId()));
        lblTitulo.setText(evento.getTitulo());
        lblDescricao.setText(evento.getDescricao());
        lblDataHora.setText(evento.getDataHora());
        lblLocal.setText(evento.getLocal());

        if (evento.getCategoria() != null) {
            lblCategoria.setText(evento.getCategoria().getNome());
        } else {
            lblCategoria.setText("Não informada");
        }
    }

    private void atualizarTabela() {
        tabela.getChildren().clear();

        String[] cabecalho = {"ID", "Evento", "Organizador", "Categorias", "Comentários"};
        for (int coluna = 0; coluna < cabecalho.length; coluna++) {
            tabela.add(criarCelula(cabecalho[coluna], true), coluna, 0);
        }

        int linha = 1;
        for (DetalhesEvento item : dados.getDetalhesEventos()) {
            tabela.add(criarCelula(String.valueOf(item.getId()), false), 0, linha);
            tabela.add(criarCelula(item.getTitulo(), false), 1, linha);
            tabela.add(criarCelula(item.getOrganizador(), false), 2, linha);
            tabela.add(criarCelula(String.join(", ", item.getCategorias()), false), 3, linha);
            tabela.add(criarCelula(String.join(", ", item.getComentarios()), false), 4, linha);
            linha++;
        }

        if (linha == 1) {
            tabela.add(new Label("Nenhum detalhe cadastrado."), 0, 1);
        }
    }

    private Label criarCelula(String texto, boolean cabecalho) {
        Label celula = new Label(texto);
        if (cabecalho) {
            celula.getStyleClass().add("cabecalho-tabela");
        } else {
            celula.getStyleClass().add("celula-tabela");
        }
        return celula;
    }

    private void limparCampos() {
        campoId.clear();
        campoIdEvento.clear();
        campoOrganizador.clear();
        campoCategorias.clear();
        campoComentario.clear();
    }

    private int gerarId() {
        int maior = 0;
        for (DetalhesEvento item : dados.getDetalhesEventos()) {
            if (item.getId() > maior) {
                maior = item.getId();
            }
        }
        return maior + 1;
    }
}
