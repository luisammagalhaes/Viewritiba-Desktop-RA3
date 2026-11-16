
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TelaFavorito {

    private final Dados dados;
    private final TableView<Favorito> tabela = new TableView<>();

    private final TextField campoUsuario = new TextField();
    private final TextField campoEvento = new TextField();
    private final TextField campoData = new TextField();
    private final TextArea campoObservacao = new TextArea();

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/uuuu")
                    .withResolverStyle(
                            java.time.format.ResolverStyle.STRICT
                    );

    public TelaFavorito(Dados dados) {
        this.dados = dados;
    }

    public void mostrar() {
        Stage janela = new Stage();

        TableColumn<Favorito, String> colunaId =
                new TableColumn<>("ID");
        colunaId.setCellValueFactory(c ->
                new SimpleStringProperty(
                        String.valueOf(c.getValue().getId())
                ));

        TableColumn<Favorito, String> colunaUsuario =
                new TableColumn<>("Usuário");
        colunaUsuario.setCellValueFactory(c ->
                new SimpleStringProperty(
                        String.valueOf(c.getValue().getIdUsuario())
                ));

        TableColumn<Favorito, String> colunaEvento =
                new TableColumn<>("Evento");
        colunaEvento.setCellValueFactory(c ->
                new SimpleStringProperty(
                        String.valueOf(c.getValue().getIdEvento())
                ));

        TableColumn<Favorito, String> colunaObservacao =
                new TableColumn<>("Observação");
        colunaObservacao.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getObservacao()
                ));

        TableColumn<Favorito, String> colunaData =
                new TableColumn<>("Data");
        colunaData.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getDataSalvamento().format(FORMATO)
                ));

        tabela.getColumns().addAll(
                colunaId, colunaUsuario, colunaEvento,
                colunaObservacao, colunaData
        );

        tabela.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        atualizarTabela();

        campoUsuario.setPromptText("ID do usuário");
        campoEvento.setPromptText("ID do evento");
        campoData.setPromptText("DD/MM/AAAA");
        campoData.setText(LocalDate.now().format(FORMATO));

        campoObservacao.setPromptText("Observação opcional");
        campoObservacao.setPrefRowCount(2);

        tabela.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, anterior, atual) -> {
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
                                atual.getDataSalvamento().format(FORMATO)
                        );
                    }
                });

        Button cadastrar = new Button("Cadastrar");
        Button atualizar = new Button("Atualizar");
        Button excluir = new Button("Excluir");
        Button limpar = new Button("Limpar");

        cadastrar.setOnAction(e -> cadastrarFavorito());
        atualizar.setOnAction(e -> atualizarFavorito());
        excluir.setOnAction(e -> excluirFavorito());
        limpar.setOnAction(e -> limparCampos());

        HBox botoes = new HBox(
                10, cadastrar, atualizar, excluir, limpar
        );

        VBox painel = new VBox(
                10,
                new Label("Gerenciar favoritos"),
                tabela,
                new Label("ID do usuário"),
                campoUsuario,
                new Label("ID do evento"),
                campoEvento,
                new Label("Data de salvamento"),
                campoData,
                new Label("Observação"),
                campoObservacao,
                botoes
        );

        painel.setPadding(new Insets(20));

        janela.setTitle("Viewritiba - Favoritos");
        janela.setScene(new Scene(painel, 800, 650));
        janela.show();
    }

    private void cadastrarFavorito() {
        try {
            int usuario = lerId(campoUsuario.getText());
            int evento = lerId(campoEvento.getText());
            LocalDate data = lerData();

            for (Favorito f : dados.getFavoritos()) {
                if (f.getIdUsuario() == usuario &&
                        f.getIdEvento() == evento) {
                    throw new IllegalArgumentException(
                            "Este usuário já favoritou esse evento."
                    );
                }
            }

            int novoId = 1;
            for (Favorito f : dados.getFavoritos()) {
                if (f.getId() >= novoId) {
                    novoId = f.getId() + 1;
                }
            }

            Favorito favorito = new Favorito(
                    novoId,
                    usuario,
                    evento,
                    campoObservacao.getText(),
                    data
            );

            dados.getFavoritos().add(favorito);
            dados.salvar();
            atualizarTabela();
            limparCampos();

        } catch (IllegalArgumentException ex) {
            mostrarErro(ex.getMessage());
        }
    }

    private void atualizarFavorito() {
        Favorito selecionado =
                tabela.getSelectionModel().getSelectedItem();

        if (selecionado == null) {
            mostrarErro("Selecione um favorito.");
            return;
        }

        try {
            int usuario = lerId(campoUsuario.getText());
            int evento = lerId(campoEvento.getText());
            LocalDate data = lerData();

            for (Favorito f : dados.getFavoritos()) {
                if (f != selecionado &&
                        f.getIdUsuario() == usuario &&
                        f.getIdEvento() == evento) {
                    throw new IllegalArgumentException(
                            "Este usuário já favoritou esse evento."
                    );
                }
            }

            selecionado.setIdUsuario(usuario);
            selecionado.setIdEvento(evento);
            selecionado.setObservacao(campoObservacao.getText());
            selecionado.setDataSalvamento(data);
            dados.salvar(); // ADICIONAR AQUI

            atualizarTabela();

        } catch (IllegalArgumentException ex) {
            mostrarErro(ex.getMessage());
        }
    }

    private void excluirFavorito() {
        Favorito selecionado =
                tabela.getSelectionModel().getSelectedItem();

        if (selecionado == null) {
            mostrarErro("Selecione um favorito.");
            return;
        }

        dados.getFavoritos().remove(selecionado);
        dados.salvar(); // ADICIONAR AQUI
        atualizarTabela();
        limparCampos();
    }

    private void atualizarTabela() {
        tabela.getItems().setAll(dados.getFavoritos());
        tabela.refresh();
    }

    private int lerId(String texto) {
        try {
            int id = Integer.parseInt(texto.trim());

            if (id <= 0) {
                throw new NumberFormatException();
            }

            return id;

        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                    "Informe um ID inteiro positivo."
            );
        }
    }

    private LocalDate lerData() {
        try {
            return LocalDate.parse(
                    campoData.getText().trim(), FORMATO
            );
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(
                    "Informe uma data válida em DD/MM/AAAA."
            );
        }
    }

    private void limparCampos() {
        tabela.getSelectionModel().clearSelection();
        campoUsuario.clear();
        campoEvento.clear();
        campoObservacao.clear();
        campoData.setText(LocalDate.now().format(FORMATO));
    }

    private void mostrarErro(String mensagem) {
        Alert alerta = new Alert(
                Alert.AlertType.ERROR, mensagem
        );
        alerta.setHeaderText("Erro");
        alerta.showAndWait();
    }
}

