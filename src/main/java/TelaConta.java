import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// Tela de gerenciamento de contas. So aparece para moderador.
public class TelaConta {
    private Dados dados;
    private TextField id = new TextField();
    private TextField login = new TextField();
    private PasswordField senha = new PasswordField();
    private ComboBox<String> tipo = new ComboBox<String>();
    private TextField motivo = new TextField();
    private TextField pesquisa = new TextField();
    private Label mensagem = new Label();
    private GridPane tabela = new GridPane();

    public TelaConta(Dados dados) {
        this.dados = dados;
    }

    public void mostrar() {
        Stage janela = new Stage();
        Label tituloTela = new Label("Gerenciamento de contas");
        tituloTela.getStyleClass().add("titulo-tela");

        tipo.getItems().addAll("Organizador", "Participante", "Moderador");
        tipo.setPrefWidth(430);

        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(10);
        formulario.setPadding(new Insets(10));
        adicionarCampo(formulario, "ID para consultar, alterar, excluir ou suspender:", id, 0);
        adicionarCampo(formulario, "Login:", login, 1);
        adicionarCampo(formulario, "Senha:", senha, 2);
        formulario.add(new Label("Tipo de usuario:"), 0, 3);
        formulario.add(tipo, 1, 3);
        adicionarCampo(formulario, "Motivo da suspensao:", motivo, 4);

        Button cadastrar = new Button("Cadastrar");
        Button consultar = new Button("Consultar ID");
        Button alterar = new Button("Alterar");
        Button excluir = new Button("Excluir");
        Button suspender = new Button("Suspender");
        Button reativar = new Button("Reativar");
        Button voltar = new Button("Voltar");
        cadastrar.setOnAction(e -> cadastrar());
        consultar.setOnAction(e -> consultar());
        alterar.setOnAction(e -> alterar());
        excluir.setOnAction(e -> excluir());
        suspender.setOnAction(e -> suspender());
        reativar.setOnAction(e -> reativar());
        voltar.setOnAction(e -> janela.close());

        HBox botoes = new HBox(10, cadastrar, consultar, alterar);
        botoes.setAlignment(Pos.CENTER);
        HBox botoes2 = new HBox(10, excluir, suspender, reativar, voltar);
        botoes2.setAlignment(Pos.CENTER);

        Button pesquisar = new Button("Pesquisar");
        Button todos = new Button("Mostrar todos");
        pesquisar.setOnAction(e -> atualizarTabela(pesquisa.getText().trim()));
        todos.setOnAction(e -> {
            pesquisa.clear();
            atualizarTabela("");
        });

        HBox busca = new HBox(10, new Label("Pesquisar:"), pesquisa, pesquisar, todos);
        busca.setAlignment(Pos.CENTER);

        VBox conteudo = new VBox(12, tituloTela, formulario, botoes, botoes2,
                mensagem, busca, tabela);
        conteudo.setAlignment(Pos.CENTER);
        conteudo.getStyleClass().add("formulario");
        conteudo.getStyleClass().add("formulario-evento");

        VBox fundo = new VBox(conteudo);
        fundo.setAlignment(Pos.CENTER);
        Scene cena = new Scene(fundo, 1050, 750);
        cena.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        janela.setTitle("Viewritiba - Contas");
        janela.setScene(cena);
        atualizarTabela("");
        janela.show();
    }

    private void adicionarCampo(GridPane painel, String texto, TextField campo, int linha) {
        painel.add(new Label(texto), 0, linha);
        painel.add(campo, 1, linha);
    }

    private void cadastrar() {
        if (senha.getText().equals("")) {
            mensagem.setText("Preencha todos os campos.");
            return;
        }
        if (!camposValidos(-1)) return;

        Conta conta = new Conta(gerarId(), login.getText().trim(),
                senha.getText(), tipo.getValue());
        dados.getContas().add(conta);
        dados.salvar();
        limparCampos();
        atualizarTabela("");
        mensagem.setText("Conta cadastrada com sucesso!");
    }

    private void consultar() {
        Conta conta = buscarPorId();
        if (conta == null) return;
        login.setText(conta.getLogin());
        senha.clear();
        tipo.setValue(conta.getTipo());
        motivo.setText(conta.getMotivoSuspensao());
        mensagem.setText("Conta encontrada.");
    }

    private void alterar() {
        Conta conta = buscarPorId();
        if (conta == null || !camposValidos(conta.getId())) return;

        conta.setLogin(login.getText().trim());
        conta.setTipo(tipo.getValue());
        if (!senha.getText().equals("")) {
            conta.setSenha(senha.getText());
        }
        dados.salvar();
        atualizarTabela("");
        mensagem.setText("Conta alterada com sucesso!");
    }

    private void excluir() {
        Conta conta = buscarPorId();
        if (conta == null) return;
        dados.getContas().remove(conta);
        dados.salvar();
        limparCampos();
        atualizarTabela("");
        mensagem.setText("Conta excluida com sucesso!");
    }

    private void suspender() {
        Conta conta = buscarPorId();
        if (conta == null) return;
        if (motivo.getText().trim().equals("")) {
            mensagem.setText("Informe o motivo da suspensao.");
            return;
        }
        conta.setSuspensa(true);
        conta.setMotivoSuspensao(motivo.getText().trim());
        dados.salvar();
        atualizarTabela("");
        mensagem.setText("Conta suspensa com sucesso!");
    }

    private void reativar() {
        Conta conta = buscarPorId();
        if (conta == null) return;
        conta.setSuspensa(false);
        conta.setMotivoSuspensao("");
        motivo.clear();
        dados.salvar();
        atualizarTabela("");
        mensagem.setText("Conta reativada com sucesso!");
    }

    private Conta buscarPorId() {
        try {
            int numero = Integer.parseInt(id.getText().trim());
            for (Conta conta : dados.getContas()) {
                if (conta.getId() == numero) return conta;
            }
        } catch (NumberFormatException erro) {
            mensagem.setText("Informe um ID numerico.");
            return null;
        }
        mensagem.setText("Conta nao encontrada.");
        return null;
    }

    // idIgnorado permite alterar a propria conta sem acusar login repetido
    private boolean camposValidos(int idIgnorado) {
        if (login.getText().trim().equals("") || tipo.getValue() == null) {
            mensagem.setText("Preencha todos os campos.");
            return false;
        }
        for (Conta conta : dados.getContas()) {
            if (conta.getId() != idIgnorado
                    && conta.getLogin().equalsIgnoreCase(login.getText().trim())) {
                mensagem.setText("Ja existe uma conta com este login.");
                return false;
            }
        }
        return true;
    }

    private void atualizarTabela(String busca) {
        tabela.getChildren().clear();
        String[] cabecalho = {"ID", "Login", "Tipo", "Situacao"};
        for (int coluna = 0; coluna < cabecalho.length; coluna++)
            tabela.add(criarCelula(cabecalho[coluna], true), coluna, 0);

        int linha = 1;
        String termo = busca.toLowerCase();
        for (Conta conta : dados.getContas()) {
            if (termo.equals("") || conta.getLogin().toLowerCase().contains(termo)
                    || conta.getTipo().toLowerCase().contains(termo)) {
                String situacao = conta.isSuspensa()
                        ? "Suspensa: " + conta.getMotivoSuspensao() : "Ativa";
                tabela.add(criarCelula("" + conta.getId(), false), 0, linha);
                tabela.add(criarCelula(conta.getLogin(), false), 1, linha);
                tabela.add(criarCelula(conta.getTipo(), false), 2, linha);
                tabela.add(criarCelula(situacao, false), 3, linha);
                linha++;
            }
        }
        if (linha == 1) tabela.add(new Label("Nenhuma conta encontrada."), 0, 1, 4, 1);
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
        id.clear();
        login.clear();
        senha.clear();
        tipo.setValue(null);
        motivo.clear();
    }

    private int gerarId() {
        int maior = 0;
        for (Conta conta : dados.getContas())
            if (conta.getId() > maior) maior = conta.getId();
        return maior + 1;
    }
}
