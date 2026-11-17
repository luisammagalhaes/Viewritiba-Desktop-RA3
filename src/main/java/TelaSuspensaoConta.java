
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import moderador.dao.ModeradorDAO;
import moderador.model.Moderador;
import organizador.dao.OrganizadorDAO;
import organizador.model.Organizador;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TelaSuspensaoConta {

    private final Dados dados;

    private final ComboBox<String> campoTipo = new ComboBox<>();
    private final TextField campoIdConta = new TextField();
    private final TextField campoMotivo = new TextField();

    private final TableView<ContaSuspensa> tabela = new TableView<>();
    private final ObservableList<ContaSuspensa> lista =
            FXCollections.observableArrayList();

    private final DateTimeFormatter formatoData =
            DateTimeFormatter.ofPattern("dd/MM/uuuu");

    private ContaSuspensa selecionada;

    public TelaSuspensaoConta(Dados dados) {
        this.dados = dados;
    }

    public void mostrar() {
        Stage janela = new Stage();
        janela.setTitle("Suspender contas");

        Label titulo = new Label("Suspender contas");
        titulo.getStyleClass().add("titulo-tela");

        campoTipo.getItems().addAll("Participante", "Organizador", "Moderador");
        campoTipo.setPromptText("Tipo da conta");
        campoIdConta.setPromptText("ID da conta");
        campoMotivo.setPromptText("Motivo da suspensão");

        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(10);

        formulario.add(new Label("Tipo da conta:"), 0, 0);
        formulario.add(campoTipo, 1, 0);

        formulario.add(new Label("ID da conta:"), 0, 1);
        formulario.add(campoIdConta, 1, 1);

        formulario.add(new Label("Motivo:"), 0, 2);
        formulario.add(campoMotivo, 1, 2);

        configurarTabela();

        Button suspender = new Button("Suspender");
        Button atualizar = new Button("Atualizar");
        Button reativar = new Button("Reativar");
        Button limpar = new Button("Limpar");

        suspender.setOnAction(e -> suspender());
        atualizar.setOnAction(e -> atualizar());
        reativar.setOnAction(e -> reativar());
        limpar.setOnAction(e -> limparCampos());

        HBox botoes = new HBox(
                10, suspender, atualizar, reativar, limpar
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
        TableColumn<ContaSuspensa, String> colunaId =
                new TableColumn<>("ID");

        colunaId.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(d.getValue().getId())
                )
        );

        TableColumn<ContaSuspensa, String> colunaTipo =
                new TableColumn<>("Tipo da conta");

        colunaTipo.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getTipoConta()
                )
        );

        TableColumn<ContaSuspensa, String> colunaIdConta =
                new TableColumn<>("ID da conta");

        colunaIdConta.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(d.getValue().getIdConta())
                )
        );

        TableColumn<ContaSuspensa, String> colunaMotivo =
                new TableColumn<>("Motivo");

        colunaMotivo.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getMotivo()
                )
        );

        TableColumn<ContaSuspensa, String> colunaData =
                new TableColumn<>("Data");

        colunaData.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getDataSuspensao()
                                .format(formatoData)
                )
        );

        tabela.getColumns().addAll(
                colunaId,
                colunaTipo,
                colunaIdConta,
                colunaMotivo,
                colunaData
        );

        tabela.setItems(lista);
        tabela.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        tabela.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, anterior, atual) -> {
                    selecionada = atual;

                    if (atual != null) {
                        campoTipo.setValue(atual.getTipoConta());

                        campoIdConta.setText(
                                String.valueOf(atual.getIdConta())
                        );

                        campoMotivo.setText(atual.getMotivo());
                    }
                });
    }

    private void suspender() {
        String tipo = campoTipo.getValue();
        Integer idConta = lerId(campoIdConta);
        String motivo = campoMotivo.getText().trim();

        if (tipo == null || idConta == null || motivo.isEmpty()) {
            mostrarErro("Preencha o tipo, o ID da conta e o motivo.");
            return;
        }

        if (!contaExiste(tipo, idConta)) {
            mostrarErro("Não existe " + tipo + " com esse ID.");
            return;
        }

        if (suspensaoExiste(tipo, idConta, null)) {
            mostrarErro("Esta conta já está suspensa.");
            return;
        }

        ContaSuspensa suspensao = new ContaSuspensa(
                gerarId(),
                tipo,
                idConta,
                motivo,
                LocalDate.now()
        );

        dados.getContasSuspensas().add(suspensao);
        dados.salvar();

        atualizarTabela();
        limparCampos();

        mostrarInformacao("Conta suspensa com sucesso!");
    }

    private void atualizar() {
        if (selecionada == null) {
            mostrarErro("Selecione uma suspensão na tabela.");
            return;
        }

        String tipo = campoTipo.getValue();
        Integer idConta = lerId(campoIdConta);
        String motivo = campoMotivo.getText().trim();

        if (tipo == null || idConta == null || motivo.isEmpty()) {
            mostrarErro("Preencha o tipo, o ID da conta e o motivo.");
            return;
        }

        if (!contaExiste(tipo, idConta)) {
            mostrarErro("Não existe " + tipo + " com esse ID.");
            return;
        }

        if (suspensaoExiste(tipo, idConta, selecionada)) {
            mostrarErro("Já existe outra suspensão para essa conta.");
            return;
        }

        selecionada.setTipoConta(tipo);
        selecionada.setIdConta(idConta);
        selecionada.setMotivo(motivo);

        dados.salvar();

        atualizarTabela();
        limparCampos();

        mostrarInformacao("Suspensão atualizada com sucesso!");
    }

    private void reativar() {
        if (selecionada == null) {
            mostrarErro("Selecione uma suspensão na tabela.");
            return;
        }

        Alert confirmacao = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Deseja reativar a conta selecionada?",
                ButtonType.YES,
                ButtonType.NO
        );

        confirmacao.setHeaderText(null);

        if (confirmacao.showAndWait()
                .orElse(ButtonType.NO) != ButtonType.YES) {
            return;
        }

        dados.getContasSuspensas().remove(selecionada);
        dados.salvar();

        atualizarTabela();
        limparCampos();

        mostrarInformacao("Conta reativada com sucesso!");
    }

    private boolean contaExiste(String tipo, int idConta) {
        if (tipo.equals("Participante")) {
            for (Participante participante : dados.getParticipantes()) {
                if (participante.getId() == idConta) {
                    return true;
                }
            }
        }

        if (tipo.equals("Organizador")) {
            OrganizadorDAO organizadorDAO = new OrganizadorDAO();

            for (Organizador organizador : organizadorDAO.listar()) {
                if (organizador.getId() == idConta) {
                    return true;
                }
            }
        }

        if (tipo.equals("Moderador")) {
            ModeradorDAO moderadorDAO = new ModeradorDAO();

            for (Moderador moderador : moderadorDAO.listar()) {
                if (moderador.getId() == idConta) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean suspensaoExiste(
            String tipo,
            int idConta,
            ContaSuspensa ignorar
    ) {
        for (ContaSuspensa suspensao : dados.getContasSuspensas()) {
            if (suspensao != ignorar
                    && suspensao.getTipoConta().equals(tipo)
                    && suspensao.getIdConta() == idConta) {
                return true;
            }
        }

        return false;
    }

    private Integer lerId(TextField campo) {
        try {
            int valor = Integer.parseInt(
                    campo.getText().trim()
            );

            if (valor <= 0) {
                return null;
            }

            return valor;

        } catch (NumberFormatException erro) {
            return null;
        }
    }

    private int gerarId() {
        int maior = 0;

        for (ContaSuspensa suspensao : dados.getContasSuspensas()) {
            if (suspensao.getId() > maior) {
                maior = suspensao.getId();
            }
        }

        return maior + 1;
    }

    private void atualizarTabela() {
        lista.setAll(dados.getContasSuspensas());
        tabela.refresh();
    }

    private void limparCampos() {
        selecionada = null;
        tabela.getSelectionModel().clearSelection();

        campoTipo.setValue(null);
        campoIdConta.clear();
        campoMotivo.clear();
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
