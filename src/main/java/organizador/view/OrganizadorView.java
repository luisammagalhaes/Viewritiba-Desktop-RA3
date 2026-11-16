package organizador.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import organizador.dao.OrganizadorDAO;
import organizador.model.Organizador;

public class OrganizadorView {
    private final OrganizadorDAO dao = new OrganizadorDAO();

    private final TextField campoId = new TextField();
    private final TextField campoNome = new TextField();
    private final TextField campoEmail = new TextField();
    private final TextField campoTelefone = new TextField();

    private final TableView<Organizador> tabela = new TableView<>();
    private final ObservableList<Organizador> dadosTabela = FXCollections.observableArrayList();
    private final Label mensagem = new Label();

    public VBox criarTela() {
        VBox raiz = new VBox(15);
        raiz.setPadding(new Insets(20));
        raiz.setAlignment(Pos.TOP_CENTER);

        Label titulo = new Label("Gerenciamento de Organizadores");
        titulo.getStyleClass().add("titulo-tela");

        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(10);
        formulario.setAlignment(Pos.CENTER);

        formulario.add(new Label("ID:"), 0, 0);
        formulario.add(campoId, 1, 0);
        formulario.add(new Label("Nome:"), 0, 1);
        formulario.add(campoNome, 1, 1);
        formulario.add(new Label("E-mail:"), 0, 2);
        formulario.add(campoEmail, 1, 2);
        formulario.add(new Label("Telefone:"), 0, 3);
        formulario.add(campoTelefone, 1, 3);

        Button botaoCadastrar = new Button("Cadastrar");
        Button botaoAlterar = new Button("Alterar");
        Button botaoExcluir = new Button("Excluir");
        Button botaoLimpar = new Button("Limpar");

        botaoCadastrar.setOnAction(e -> cadastrar());
        botaoAlterar.setOnAction(e -> alterar());
        botaoExcluir.setOnAction(e -> excluir());
        botaoLimpar.setOnAction(e -> limparCampos());

        HBox painelBotoes = new HBox(10, botaoCadastrar, botaoAlterar, botaoExcluir, botaoLimpar);
        painelBotoes.setAlignment(Pos.CENTER);

        configurarTabela();

        raiz.getChildren().addAll(titulo, formulario, painelBotoes, mensagem, tabela);
        atualizarTabela();

        return raiz;
    }

    public void mostrar() {
        Stage stage = new Stage();
        VBox tela = criarTela();
        Scene scene = new Scene(tela, 780, 680);
        try {
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        } catch (Exception ignored) {
        }
        stage.setTitle("Viewritiba - Organizadores");
        stage.setScene(scene);
        stage.show();
    }

    private void configurarTabela() {
        TableColumn<Organizador, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setMinWidth(60);

        TableColumn<Organizador, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colNome.setMinWidth(160);

        TableColumn<Organizador, String> colEmail = new TableColumn<>("E-mail");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEmail.setMinWidth(180);

        TableColumn<Organizador, String> colTelefone = new TableColumn<>("Telefone");
        colTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));
        colTelefone.setMinWidth(120);

        tabela.getColumns().setAll(colId, colNome, colEmail, colTelefone);
        tabela.setItems(dadosTabela);
        tabela.setPrefHeight(250);

        tabela.getSelectionModel().selectedItemProperty().addListener((obs, antigo, selecionado) -> {
            if (selecionado != null) {
                campoId.setText(String.valueOf(selecionado.getId()));
                campoNome.setText(selecionado.getNome());
                campoEmail.setText(selecionado.getEmail());
                campoTelefone.setText(selecionado.getTelefone());
            }
        });
    }

    private void cadastrar() {
        try {
            int id = Integer.parseInt(campoId.getText().trim());
            String nome = campoNome.getText().trim();
            String email = campoEmail.getText().trim();
            String telefone = campoTelefone.getText().trim();

            if (nome.isEmpty() || email.isEmpty() || telefone.isEmpty()) {
                definirMensagem("Preencha todos os campos.", true);
                return;
            }

            Organizador organizador = new Organizador(id, nome, email, telefone);
            if (dao.inserir(organizador)) {
                definirMensagem("Organizador cadastrado com sucesso!", false);
                limparCampos();
                atualizarTabela();
            } else {
                definirMensagem("Erro: ID já existente.", true);
            }
        } catch (NumberFormatException e) {
            definirMensagem("Erro: ID deve ser um número inteiro.", true);
        }
    }

    private void alterar() {
        try {
            int id = Integer.parseInt(campoId.getText().trim());
            String nome = campoNome.getText().trim();
            String email = campoEmail.getText().trim();
            String telefone = campoTelefone.getText().trim();

            if (nome.isEmpty() || email.isEmpty() || telefone.isEmpty()) {
                definirMensagem("Preencha todos os campos.", true);
                return;
            }

            Organizador organizador = new Organizador(id, nome, email, telefone);
            if (dao.atualizar(organizador)) {
                definirMensagem("Organizador alterado com sucesso!", false);
                limparCampos();
                atualizarTabela();
            } else {
                definirMensagem("Erro: Organizador não encontrado.", true);
            }
        } catch (NumberFormatException e) {
            definirMensagem("Erro: ID deve ser um número inteiro.", true);
        }
    }

    private void excluir() {
        try {
            int id = Integer.parseInt(campoId.getText().trim());
            if (dao.excluir(id)) {
                definirMensagem("Organizador excluído com sucesso!", false);
                limparCampos();
                atualizarTabela();
            } else {
                definirMensagem("Erro: Organizador não encontrado.", true);
            }
        } catch (NumberFormatException e) {
            definirMensagem("Erro: Selecione ou informe um ID válido.", true);
        }
    }

    private void limparCampos() {
        campoId.clear();
        campoNome.clear();
        campoEmail.clear();
        campoTelefone.clear();
        tabela.getSelectionModel().clearSelection();
    }

    private void atualizarTabela() {
        dadosTabela.setAll(dao.listar());
    }

    private void definirMensagem(String texto, boolean erro) {
        mensagem.setText(texto);
        if (erro) {
            mensagem.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
        } else {
            mensagem.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold;");
        }
    }
}
