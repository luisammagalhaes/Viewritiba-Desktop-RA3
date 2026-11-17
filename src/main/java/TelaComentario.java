
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class TelaComentario {

    private final Dados dados;
    private final Evento evento;

    private final TextField campoNota = new TextField();
    private final TextField campoTitulo = new TextField();
    private final TextField campoDescricao = new TextField();

    private final TableView<Comentario> tabela = new TableView<>();
    private final ObservableList<Comentario> lista =
            FXCollections.observableArrayList();

    private Comentario selecionado;

    public TelaComentario(Dados dados, Evento evento) {
        this.dados = dados;
        this.evento = evento;
    }

    public void mostrar() {
        Stage janela = new Stage();
        janela.setTitle("Viewritiba - Comentários");

        Label titulo = new Label("Comentários - " + evento.getTitulo());
        titulo.getStyleClass().add("titulo-tela");

        campoNota.setPromptText("Nota de 1 a 5");
        campoTitulo.setPromptText("Título");
        campoDescricao.setPromptText("Descrição");

        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(10);

        formulario.add(new Label("Nota:"), 0, 0);
        formulario.add(campoNota, 1, 0);

        formulario.add(new Label("Título:"), 0, 1);
        formulario.add(campoTitulo, 1, 1);

        formulario.add(new Label("Descrição:"), 0, 2);
        formulario.add(campoDescricao, 1, 2);

        configurarTabela();

        Button cadastrar = new Button("Cadastrar");
        Button atualizar = new Button("Atualizar");
        Button excluir = new Button("Excluir");
        Button limpar = new Button("Limpar");

        cadastrar.setOnAction(e -> cadastrar());
        atualizar.setOnAction(e -> atualizar());
        excluir.setOnAction(e -> excluir());
        limpar.setOnAction(e -> limparCampos());

        HBox botoes = new HBox(
                10, cadastrar, atualizar, excluir, limpar
        );
        botoes.setAlignment(Pos.CENTER);

        VBox conteudo = new VBox(
                15, titulo, formulario, botoes, tabela
        );

        conteudo.setPadding(new Insets(20));
        conteudo.setAlignment(Pos.CENTER);

        Scene cena = new Scene(conteudo, 900, 650);

        var css = getClass().getResource("/style.css");
        if (css != null) {
            cena.getStylesheets().add(css.toExternalForm());
        }

        janela.setScene(cena);

        atualizarTabela();
        janela.show();
    }

    private void configurarTabela() {
        TableColumn<Comentario, String> colunaId =
                new TableColumn<>("ID");

        colunaId.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(d.getValue().getId())
                )
        );

        TableColumn<Comentario, String> colunaNota =
                new TableColumn<>("Nota");

        colunaNota.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(d.getValue().getNota())
                )
        );

        TableColumn<Comentario, String> colunaTitulo =
                new TableColumn<>("Título");

        colunaTitulo.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getTitulo()
                )
        );

        TableColumn<Comentario, String> colunaDescricao =
                new TableColumn<>("Descrição");

        colunaDescricao.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getDescricao()
                )
        );

        tabela.getColumns().addAll(
                colunaId,
                colunaNota,
                colunaTitulo,
                colunaDescricao
        );

        tabela.setItems(lista);
        tabela.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        tabela.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, anterior, atual) -> {
                    selecionado = atual;

                    if (atual != null) {
                        campoNota.setText(
                                String.valueOf(atual.getNota())
                        );

                        campoTitulo.setText(
                                atual.getTitulo()
                        );

                        campoDescricao.setText(
                                atual.getDescricao()
                        );
                    }
                });
    }

    private void cadastrar() {
        Integer nota = lerNota();

        if (nota == null || !camposPreenchidos()) {
            return;
        }

        Comentario comentario = new Comentario(
                gerarId(),
                evento.getId(),
                nota,
                campoTitulo.getText().trim(),
                campoDescricao.getText().trim()
        );

        dados.getComentarios().add(comentario);
        dados.salvar();

        atualizarTabela();
        limparCampos();

        mostrarInformacao("Comentário cadastrado com sucesso!");
    }

    private void atualizar() {
        if (selecionado == null) {
            mostrarErro("Selecione um comentário na tabela.");
            return;
        }

        Integer nota = lerNota();

        if (nota == null || !camposPreenchidos()) {
            return;
        }

        selecionado.setNota(nota);
        selecionado.setTitulo(
                campoTitulo.getText().trim()
        );
        selecionado.setDescricao(
                campoDescricao.getText().trim()
        );

        dados.salvar();

        atualizarTabela();
        limparCampos();

        mostrarInformacao("Comentário atualizado com sucesso!");
    }

    private void excluir() {
        if (selecionado == null) {
            mostrarErro("Selecione um comentário na tabela.");
            return;
        }

        Alert confirmacao = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Deseja excluir o comentário selecionado?",
                ButtonType.YES,
                ButtonType.NO
        );

        confirmacao.setHeaderText(null);

        if (confirmacao.showAndWait()
                .orElse(ButtonType.NO) != ButtonType.YES) {
            return;
        }

        dados.getComentarios().remove(selecionado);
        dados.salvar();

        atualizarTabela();
        limparCampos();

        mostrarInformacao("Comentário excluído com sucesso!");
    }

    private boolean camposPreenchidos() {
        if (campoTitulo.getText().trim().isEmpty()
                || campoDescricao.getText().trim().isEmpty()) {
            mostrarErro("Preencha o título e a descrição.");
            return false;
        }

        return true;
    }

    private Integer lerNota() {
        try {
            int valor = Integer.parseInt(
                    campoNota.getText().trim()
            );

            if (valor < 1 || valor > 5) {
                mostrarErro("A nota deve ser de 1 a 5.");
                return null;
            }

            return valor;

        } catch (NumberFormatException erro) {
            mostrarErro("Informe uma nota numérica de 1 a 5.");
            return null;
        }
    }

    private int gerarId() {
        int maior = 0;

        for (Comentario comentario : dados.getComentarios()) {
            if (comentario.getId() > maior) {
                maior = comentario.getId();
            }
        }

        return maior + 1;
    }

    // Mostra só os comentários do evento aberto
    private void atualizarTabela() {
        lista.clear();

        for (Comentario comentario : dados.getComentarios()) {
            if (comentario.getEventoId() == evento.getId()) {
                lista.add(comentario);
            }
        }

        tabela.refresh();
    }

    private void limparCampos() {
        selecionado = null;
        tabela.getSelectionModel().clearSelection();

        campoNota.clear();
        campoTitulo.clear();
        campoDescricao.clear();
    }

    private void mostrarErro(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Erro");
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    private void mostrarInformacao(String mensagem) {
        Alert alerta = new Alert(
                Alert.AlertType.INFORMATION
        );

        alerta.setTitle("Viewritiba");
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}
