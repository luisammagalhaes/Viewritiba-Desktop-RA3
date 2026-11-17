
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
import java.time.format.ResolverStyle;

public class TelaCompartilhamento {

    private final Dados dados;

    private final TableView<Compartilhamento> tabela =
            new TableView<>();

    private final TextField campoEvento = new TextField();
    private final TextField campoRemetente = new TextField();
    private final TextField campoDestinatario = new TextField();
    private final TextField campoData = new TextField();
    private final TextArea campoMensagem = new TextArea();

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/uuuu")
                    .withResolverStyle(ResolverStyle.STRICT);

    public TelaCompartilhamento(Dados dados) {
        this.dados = dados;
    }

    public void mostrar() {
        Stage janela = new Stage();

        // Colunas da tabela
        TableColumn<Compartilhamento, String> colunaId =
                new TableColumn<>("ID");

        colunaId.setCellValueFactory(c ->
                new SimpleStringProperty(
                        String.valueOf(c.getValue().getId())
                ));

        TableColumn<Compartilhamento, String> colunaEvento =
                new TableColumn<>("Evento");

        colunaEvento.setCellValueFactory(c ->
                new SimpleStringProperty(
                        String.valueOf(c.getValue().getIdEvento())
                ));

        TableColumn<Compartilhamento, String> colunaRemetente =
                new TableColumn<>("Remetente");

        colunaRemetente.setCellValueFactory(c ->
                new SimpleStringProperty(
                        String.valueOf(c.getValue().getIdRemetente())
                ));

        TableColumn<Compartilhamento, String> colunaDestinatario =
                new TableColumn<>("Destinatário");

        colunaDestinatario.setCellValueFactory(c ->
                new SimpleStringProperty(
                        String.valueOf(c.getValue().getIdDestinatario())
                ));

        TableColumn<Compartilhamento, String> colunaMensagem =
                new TableColumn<>("Mensagem");

        colunaMensagem.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getMensagem()
                ));

        TableColumn<Compartilhamento, String> colunaData =
                new TableColumn<>("Data");

        colunaData.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getDataCompartilhamento()
                                .format(FORMATO)
                ));

        tabela.getColumns().addAll(
                colunaId,
                colunaEvento,
                colunaRemetente,
                colunaDestinatario,
                colunaMensagem,
                colunaData
        );

        tabela.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        atualizarTabela();

        // Campos do formulário
        campoEvento.setPromptText("ID do evento");
        campoRemetente.setPromptText("ID do remetente");
        campoDestinatario.setPromptText("ID do destinatário");
        campoData.setPromptText("DD/MM/AAAA");
        campoData.setText(LocalDate.now().format(FORMATO));

        campoMensagem.setPromptText(
                "Mensagem opcional (máximo 200 caracteres)"
        );
        campoMensagem.setPrefRowCount(2);

        // Preencher campos ao selecionar um compartilhamento
        tabela.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, anterior, atual) -> {
                    if (atual != null) {
                        campoEvento.setText(
                                String.valueOf(atual.getIdEvento())
                        );

                        campoRemetente.setText(
                                String.valueOf(atual.getIdRemetente())
                        );

                        campoDestinatario.setText(
                                String.valueOf(atual.getIdDestinatario())
                        );

                        campoMensagem.setText(
                                atual.getMensagem()
                        );

                        campoData.setText(
                                atual.getDataCompartilhamento()
                                        .format(FORMATO)
                        );
                    }
                });

        // Botões
        Button cadastrar = new Button("Cadastrar");
        Button atualizar = new Button("Atualizar");
        Button excluir = new Button("Excluir");
        Button limpar = new Button("Limpar");

        cadastrar.setOnAction(e -> cadastrarCompartilhamento());
        atualizar.setOnAction(e -> atualizarCompartilhamento());
        excluir.setOnAction(e -> excluirCompartilhamento());
        limpar.setOnAction(e -> limparCampos());

        HBox botoes = new HBox(
                10, cadastrar, atualizar, excluir, limpar
        );

        VBox painel = new VBox(
                10,
                new Label("Gerenciar compartilhamentos"),
                tabela,
                new Label("ID do evento"),
                campoEvento,
                new Label("ID do remetente"),
                campoRemetente,
                new Label("ID do destinatário"),
                campoDestinatario,
                new Label("Data do compartilhamento"),
                campoData,
                new Label("Mensagem"),
                campoMensagem,
                botoes
        );

        painel.setPadding(new Insets(20));

        ScrollPane rolagem = new ScrollPane(painel);
        rolagem.setFitToWidth(true);

        janela.setTitle("Viewritiba - Compartilhamentos");
        janela.setScene(new Scene(rolagem, 900, 700));
        janela.show();
    }

    // CREATE - Cadastrar
    private void cadastrarCompartilhamento() {
        try {
            int evento = lerId(campoEvento.getText());
            int remetente = lerId(campoRemetente.getText());
            int destinatario = lerId(campoDestinatario.getText());
            LocalDate data = lerData();
            String mensagem = campoMensagem.getText().trim();

            validarCompartilhamento(
                    remetente, destinatario, mensagem
            );

            if (!eventoExiste(evento)) {
                throw new IllegalArgumentException(
                        "O evento informado não existe."
                );
            }

            int novoId = 1;

            for (Compartilhamento c :
                    dados.getCompartilhamentos()) {
                if (c.getId() >= novoId) {
                    novoId = c.getId() + 1;
                }
            }

            Compartilhamento compartilhamento =
                    new Compartilhamento(
                            novoId,
                            evento,
                            remetente,
                            destinatario,
                            mensagem,
                            data
                    );

            dados.getCompartilhamentos().add(compartilhamento);
            dados.salvar();

            atualizarTabela();
            limparCampos();

        } catch (IllegalArgumentException ex) {
            mostrarErro(ex.getMessage());
        }
    }

    // READ - Consultar
    private void atualizarTabela() {
        tabela.getItems().setAll(
                dados.getCompartilhamentos()
        );

        tabela.refresh();
    }

    // UPDATE - Atualizar
    private void atualizarCompartilhamento() {
        Compartilhamento selecionado =
                tabela.getSelectionModel().getSelectedItem();

        if (selecionado == null) {
            mostrarErro("Selecione um compartilhamento.");
            return;
        }

        try {
            int evento = lerId(campoEvento.getText());
            int remetente = lerId(campoRemetente.getText());
            int destinatario = lerId(campoDestinatario.getText());
            LocalDate data = lerData();
            String mensagem = campoMensagem.getText().trim();

            validarCompartilhamento(
                    remetente, destinatario, mensagem
            );

            if (!eventoExiste(evento)) {
                throw new IllegalArgumentException(
                        "O evento informado não existe."
                );
            }

            selecionado.setIdEvento(evento);
            selecionado.setIdRemetente(remetente);
            selecionado.setIdDestinatario(destinatario);
            selecionado.setMensagem(mensagem);
            selecionado.setDataCompartilhamento(data);

            dados.salvar();
            atualizarTabela();
            limparCampos();

            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Viewritiba");
            alerta.setHeaderText(null);
            alerta.setContentText(
                    "Compartilhamento atualizado com sucesso!"
            );
            alerta.showAndWait();

        } catch (IllegalArgumentException ex) {
            mostrarErro(ex.getMessage());
        }
    }

    // DELETE - Excluir
    private void excluirCompartilhamento() {
        Compartilhamento selecionado =
                tabela.getSelectionModel().getSelectedItem();

        if (selecionado == null) {
            mostrarErro("Selecione um compartilhamento.");
            return;
        }

        // Confirma antes de excluir
        Alert confirmacao = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        confirmacao.setTitle("Viewritiba");
        confirmacao.setHeaderText(null);
        confirmacao.setContentText(
                "Deseja excluir o compartilhamento selecionado?"
        );

        java.util.Optional<ButtonType> resposta =
                confirmacao.showAndWait();

        if (resposta.isEmpty()
                || resposta.get() != ButtonType.OK) {
            return;
        }

        dados.getCompartilhamentos().remove(selecionado);
        dados.salvar();

        atualizarTabela();
        limparCampos();

        // Mensagem de sucesso após excluir
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Viewritiba");
        alerta.setHeaderText(null);
        alerta.setContentText(
                "Compartilhamento excluído com sucesso!"
        );
        alerta.showAndWait();
    }

    // Validação dos IDs
    private int lerId(String texto) {
        try {
            int id = Integer.parseInt(texto.trim());

            if (id <= 0) {
                throw new NumberFormatException();
            }

            return id;

        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                    "Informe IDs inteiros e positivos."
            );
        }
    }

    // Validação da data
    private LocalDate lerData() {
        try {
            return LocalDate.parse(
                    campoData.getText().trim(),
                    FORMATO
            );

        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(
                    "Informe uma data válida em DD/MM/AAAA."
            );
        }
    }

    // Regras de compartilhamento
    private void validarCompartilhamento(
            int remetente,
            int destinatario,
            String mensagem
    ) {
        if (!contaExiste(remetente)) {
            throw new IllegalArgumentException(
                    "O remetente informado não está cadastrado."
            );
        }

        if (!contaExiste(destinatario)) {
            throw new IllegalArgumentException(
                    "O destinatário informado não está cadastrado."
            );
        }

        if (remetente == destinatario) {
            throw new IllegalArgumentException(
                    "O remetente não pode compartilhar consigo mesmo."
            );
        }

        if (mensagem.length() > 200) {
            throw new IllegalArgumentException(
                    "A mensagem deve ter no máximo 200 caracteres."
            );
        }
    }

    // Limpar formulário
    private void limparCampos() {
        tabela.getSelectionModel().clearSelection();

        campoEvento.clear();
        campoRemetente.clear();
        campoDestinatario.clear();
        campoMensagem.clear();

        campoData.setText(
                LocalDate.now().format(FORMATO)
        );
    }

    // Mostrar mensagens de erro
    private void mostrarErro(String mensagem) {
        Alert alerta = new Alert(
                Alert.AlertType.ERROR,
                mensagem
        );

        alerta.setHeaderText("Erro");
        alerta.showAndWait();
    }

    private boolean eventoExiste(int idEvento) {
        for (Evento evento : dados.getEventos()) {
            if (evento.getId() == idEvento) {
                return true;
            }
        }

        return false;
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
