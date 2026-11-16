
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public class TelaFavorito {

    private final Dados dados;

    private final TextField campoUsuario = new TextField();
    private final TextField campoEvento = new TextField();
    private final TextField campoObservacao = new TextField();
    private final TextField campoData = new TextField();

    private final TableView<Favorito> tabela = new TableView<>();
    private final ObservableList<Favorito> lista =
            FXCollections.observableArrayList();

    private final DateTimeFormatter formatoData =
            DateTimeFormatter.ofPattern("dd/MM/uuuu")
                    .withResolverStyle(ResolverStyle.STRICT);

    private Favorito selecionado;

    public TelaFavorito(Dados dados) {
        this.dados = dados;
    }

    public void mostrar() {
        Stage janela = new Stage();
        janela.setTitle("Viewritiba - Favoritos");

        Label titulo = new Label("Gerenciamento de Favoritos");
        titulo.getStyleClass().add("titulo-tela");

        campoUsuario.setPromptText("ID do usuário");
        campoEvento.setPromptText("ID do evento");
        campoObservacao.setPromptText("Observação");
        campoData.setPromptText("dd/MM/aaaa");

        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(10);

        formulario.add(new Label("ID do usuário:"), 0, 0);
        formulario.add(campoUsuario, 1, 0);

        formulario.add(new Label("ID do evento:"), 0, 1);
        formulario.add(campoEvento, 1, 1);

        formulario.add(new Label("Observação:"), 0, 2);
        formulario.add(campoObservacao, 1, 2);

        formulario.add(new Label("Data:"), 0, 3);
        formulario.add(campoData, 1, 3);

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
        TableColumn<Favorito, String> colunaId =
                new TableColumn<>("ID");

        colunaId.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(d.getValue().getId())
                )
        );

        TableColumn<Favorito, String> colunaUsuario =
                new TableColumn<>("Usuário");

        colunaUsuario.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(d.getValue().getIdUsuario())
                )
        );

        TableColumn<Favorito, String> colunaEvento =
                new TableColumn<>("Evento");

        colunaEvento.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(d.getValue().getIdEvento())
                )
        );

        TableColumn<Favorito, String> colunaObservacao =
                new TableColumn<>("Observação");

        colunaObservacao.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getObservacao()
                )
        );

        TableColumn<Favorito, String> colunaData =
                new TableColumn<>("Data");

        colunaData.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getDataSalvamento()
                                .format(formatoData)
                )
        );

        tabela.getColumns().addAll(
                colunaId,
                colunaUsuario,
                colunaEvento,
                colunaObservacao,
                colunaData
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
                        campoUsuario.setText(
                                String.valueOf(atual.getIdUsuario())
                        );

                        campoEvento.setText(
                                String.valueOf(atual.getIdEvento())
                        );

                        campoObservacao.setText(
                                atual.getObservacao()
                        );

                        campoData.setText(
                                atual.getDataSalvamento()
                                        .format(formatoData)
                        );
                    }
                });
    }

    private void cadastrar() {
        Integer idUsuario = lerId(campoUsuario, "usuário");
        Integer idEvento = lerId(campoEvento, "evento");
        LocalDate data = lerData();

        if (idUsuario == null || idEvento == null || data == null) {
            return;
        }

        // Verifica se o evento realmente existe
        if (!eventoExiste(idEvento)) {
            mostrarErro("O evento informado não existe.");
            return;
        }

        // Impede favoritos duplicados
        if (favoritoExiste(idUsuario, idEvento, null)) {
            mostrarErro(
                    "Este usuário já favoritou esse evento."
            );
            return;
        }

        Favorito favorito = new Favorito(
                gerarId(),
                idUsuario,
                idEvento,
                campoObservacao.getText().trim(),
                data
        );

        dados.getFavoritos().add(favorito);
        dados.salvar();

        atualizarTabela();
        limparCampos();

        mostrarInformacao("Favorito cadastrado com sucesso!");
    }

    private void atualizar() {
        if (selecionado == null) {
            mostrarErro("Selecione um favorito na tabela.");
            return;
        }

        Integer idUsuario = lerId(campoUsuario, "usuário");
        Integer idEvento = lerId(campoEvento, "evento");
        LocalDate data = lerData();

        if (idUsuario == null || idEvento == null || data == null) {
            return;
        }

        // Validação também na atualização
        if (!eventoExiste(idEvento)) {
            mostrarErro("O evento informado não existe.");
            return;
        }

        if (favoritoExiste(
                idUsuario, idEvento, selecionado
        )) {
            mostrarErro(
                    "Já existe outro favorito para esse usuário e evento."
            );
            return;
        }

        selecionado.setIdUsuario(idUsuario);
        selecionado.setIdEvento(idEvento);
        selecionado.setObservacao(
                campoObservacao.getText().trim()
        );
        selecionado.setDataSalvamento(data);

        dados.salvar();

        atualizarTabela();
        limparCampos();

        mostrarInformacao("Favorito atualizado com sucesso!");
    }

    private void excluir() {
        if (selecionado == null) {
            mostrarErro("Selecione um favorito na tabela.");
            return;
        }

        Alert confirmacao = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Deseja excluir o favorito selecionado?",
                ButtonType.YES,
                ButtonType.NO
        );

        confirmacao.setHeaderText(null);

        if (confirmacao.showAndWait()
                .orElse(ButtonType.NO) != ButtonType.YES) {
            return;
        }

        dados.getFavoritos().remove(selecionado);
        dados.salvar();

        atualizarTabela();
        limparCampos();

        mostrarInformacao("Favorito excluído com sucesso!");
    }

    private boolean eventoExiste(int idEvento) {
        for (Evento evento : dados.getEventos()) {
            if (evento.getId() == idEvento) {
                return true;
            }
        }

        return false;
    }

    private boolean favoritoExiste(
            int idUsuario,
            int idEvento,
            Favorito ignorar
    ) {
        for (Favorito favorito : dados.getFavoritos()) {
            if (favorito != ignorar
                    && favorito.getIdUsuario() == idUsuario
                    && favorito.getIdEvento() == idEvento) {
                return true;
            }
        }

        return false;
    }

    private Integer lerId(TextField campo, String nome) {
        try {
            int valor = Integer.parseInt(
                    campo.getText().trim()
            );

            if (valor <= 0) {
                mostrarErro(
                        "O ID de " + nome + " deve ser positivo."
                );
                return null;
            }

            return valor;

        } catch (NumberFormatException erro) {
            mostrarErro(
                    "Informe um ID numérico válido para " + nome + "."
            );
            return null;
        }
    }

    private LocalDate lerData() {
        try {
            return LocalDate.parse(
                    campoData.getText().trim(),
                    formatoData
            );

        } catch (DateTimeParseException erro) {
            mostrarErro(
                    "Informe uma data válida no formato dd/MM/aaaa."
            );
            return null;
        }
    }

    private int gerarId() {
        int maior = 0;

        for (Favorito favorito : dados.getFavoritos()) {
            if (favorito.getId() > maior) {
                maior = favorito.getId();
            }
        }

        return maior + 1;
    }

    private void atualizarTabela() {
        lista.setAll(dados.getFavoritos());
        tabela.refresh();
    }

    private void limparCampos() {
        selecionado = null;
        tabela.getSelectionModel().clearSelection();

        campoUsuario.clear();
        campoEvento.clear();
        campoObservacao.clear();
        campoData.clear();
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

    private boolean contaExiste(int idConta) {
        for (Conta conta : dados.getContas()) {
            if (conta.getId() == idConta) {
                return true;
            }
        }

        return false;
    }
}
