import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TelaLoginParticipante {

    private Dados dados;

    private TextField campoId = new TextField();
    private TextField campoNome = new TextField();
    private TextField campoEmail = new TextField();
    private PasswordField campoSenha = new PasswordField();

    private Label mensagem = new Label();
    private GridPane tabela = new GridPane();

    public TelaLoginParticipante(Dados dados) {
        this.dados = dados;
    }

    public void mostrar() {
        Stage janela = new Stage();

        Label titulo = new Label("Participantes e Login");
        titulo.getStyleClass().add("titulo-tela");

        campoId.setPromptText("Informe para consultar, alterar ou excluir");
        campoNome.setPromptText("Nome completo do participante");
        campoEmail.setPromptText("E-mail para cadastro ou login");
        campoSenha.setPromptText("Senha do participante");

        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(10);

        adicionarCampo(formulario, "ID para consultar, alterar ou excluir:", campoId, 0);
        adicionarCampo(formulario, "Nome:", campoNome, 1);
        adicionarCampo(formulario, "E-mail:", campoEmail, 2);
        adicionarCampo(formulario, "Senha:", campoSenha, 3);

        Button cadastrar = new Button("Cadastrar");
        Button consultar = new Button("Consultar ID");
        Button alterar = new Button("Alterar");
        Button excluir = new Button("Excluir");
        Button entrar = new Button("Entrar");
        Button voltar = new Button("Voltar");

        cadastrar.setOnAction(evento -> cadastrar());
        consultar.setOnAction(evento -> consultar());
        alterar.setOnAction(evento -> alterar());
        excluir.setOnAction(evento -> excluir());
        entrar.setOnAction(evento -> realizarLogin(janela));
        voltar.setOnAction(evento -> janela.close());

        HBox botoes = new HBox(10, cadastrar, consultar, alterar, excluir, entrar, voltar);
        botoes.setAlignment(Pos.CENTER);

        VBox conteudo = new VBox(12, titulo, formulario, botoes, mensagem, tabela);
        conteudo.setAlignment(Pos.CENTER);
        conteudo.getStyleClass().add("formulario");
        conteudo.getStyleClass().add("formulario-evento");

        VBox fundo = new VBox(conteudo);
        fundo.setAlignment(Pos.CENTER);

        Scene cena = new Scene(fundo, 950, 680);
        cena.getStylesheets().add(
                getClass().getResource("/style.css").toExternalForm()
        );

        janela.setTitle("Viewritiba - Participantes e Login");
        janela.setScene(cena);

        atualizarTabela();
        janela.show();
    }

    private void adicionarCampo(GridPane painel, String texto, TextField campo, int linha) {
        painel.add(new Label(texto), 0, linha);
        painel.add(campo, 1, linha);
    }

    private void cadastrar() {
        if (!camposValidos(0)) {
            return;
        }

        Participante participante = new Participante(
                gerarId(),
                campoNome.getText().trim(),
                campoEmail.getText().trim(),
                campoSenha.getText()
        );

        dados.getParticipantes().add(participante);
        dados.salvar();

        limparCampos();
        atualizarTabela();
        mensagem.setText("Participante cadastrado com sucesso!");
    }

    private void consultar() {
        Participante participante = buscarPorId();
        if (participante == null) {
            return;
        }

        campoNome.setText(participante.getNome());
        campoEmail.setText(participante.getEmail());
        campoSenha.setText(participante.getSenha());
        mensagem.setText("Participante encontrado.");
    }

    private void alterar() {
        Participante participante = buscarPorId();
        if (participante == null || !camposValidos(participante.getId())) {
            return;
        }

        participante.setNome(campoNome.getText().trim());
        participante.setEmail(campoEmail.getText().trim());
        participante.setSenha(campoSenha.getText());

        dados.salvar();
        atualizarTabela();
        mensagem.setText("Participante alterado com sucesso!");
    }

    private void excluir() {
        Participante participante = buscarPorId();
        if (participante == null) {
            return;
        }

        dados.getParticipantes().remove(participante);
        dados.salvar();

        limparCampos();
        atualizarTabela();
        mensagem.setText("Participante excluído com sucesso!");
    }

    private void realizarLogin(Stage janela) {
        String email = campoEmail.getText().trim();
        String senha = campoSenha.getText();

        if (email.equals("") || senha.equals("")) {
            mensagem.setText("Informe e-mail e senha para entrar.");
            return;
        }

        for (Participante participante : dados.getParticipantes()) {
            if (participante.validarLogin(email, senha)) {
                mensagem.setText("Login realizado!");
                new TelaDetalhesEvento(dados).mostrar();
                janela.close();
                return;
            }
        }

        mensagem.setText("E-mail ou senha incorretos.");
        campoSenha.clear();
    }

    private Participante buscarPorId() {
        try {
            int numero = Integer.parseInt(campoId.getText().trim());

            for (Participante participante : dados.getParticipantes()) {
                if (participante.getId() == numero) {
                    return participante;
                }
            }
        } catch (NumberFormatException erro) {
            mensagem.setText("Informe um ID numérico.");
            return null;
        }

        mensagem.setText("Participante não encontrado.");
        return null;
    }

    private boolean camposValidos(int idIgnorado) {
        String nome = campoNome.getText().trim();
        String email = campoEmail.getText().trim();
        String senha = campoSenha.getText();

        if (nome.equals("") || email.equals("") || senha.equals("")) {
            mensagem.setText("Preencha nome, e-mail e senha.");
            return false;
        }

        for (Participante item : dados.getParticipantes()) {
            if (item.getId() != idIgnorado
                    && item.getEmail().equalsIgnoreCase(email)) {
                mensagem.setText("Já existe participante com este e-mail.");
                return false;
            }
        }

        return true;
    }

    private void atualizarTabela() {
        tabela.getChildren().clear();

        String[] cabecalho = {"ID", "Nome", "E-mail"};
        for (int coluna = 0; coluna < cabecalho.length; coluna++) {
            tabela.add(criarCelula(cabecalho[coluna], true), coluna, 0);
        }

        int linha = 1;
        for (Participante participante : dados.getParticipantes()) {
            tabela.add(criarCelula(String.valueOf(participante.getId()), false), 0, linha);
            tabela.add(criarCelula(participante.getNome(), false), 1, linha);
            tabela.add(criarCelula(participante.getEmail(), false), 2, linha);
            linha++;
        }

        if (linha == 1) {
            tabela.add(new Label("Nenhum participante cadastrado."), 0, 1);
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
        campoNome.clear();
        campoEmail.clear();
        campoSenha.clear();
    }

    private int gerarId() {
        int maior = 0;
        for (Participante participante : dados.getParticipantes()) {
            if (participante.getId() > maior) {
                maior = participante.getId();
            }
        }
        return maior + 1;
    }
}
