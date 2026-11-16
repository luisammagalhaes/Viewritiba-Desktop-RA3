import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TelaLocal {

    private Dados dados;

    private TextField id = new TextField();
    private TextField nome = new TextField();
    private TextField endereco = new TextField();
    private TextField capacidade = new TextField();

    private Label mensagem = new Label();
    private GridPane tabela = new GridPane();

    public TelaLocal(Dados dados) {
        this.dados = dados;
    }

    public void mostrar() {
        Stage janela = new Stage();

        Label titulo = new Label("Gerenciamento de locais");
        titulo.getStyleClass().add("titulo-tela");

        id.setPromptText("Informe para consultar, alterar ou excluir");
        nome.setPromptText("Exemplo: Parque Barigui");
        endereco.setPromptText("Exemplo: Av. Cândido Hartmann, s/n");
        capacidade.setPromptText("Exemplo: 500");

        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(10);

        adicionarCampo(formulario, "ID para consultar, alterar ou excluir:", id, 0);
        adicionarCampo(formulario, "Nome do local:", nome, 1);
        adicionarCampo(formulario, "Endereço:", endereco, 2);
        adicionarCampo(formulario, "Capacidade de público:", capacidade, 3);

        Button cadastrar = new Button("Cadastrar");
        Button consultar = new Button("Consultar ID");
        Button alterar = new Button("Alterar");
        Button excluir = new Button("Excluir");
        Button voltar = new Button("Voltar");

        cadastrar.setOnAction(evento -> cadastrar());
        consultar.setOnAction(evento -> consultar());
        alterar.setOnAction(evento -> alterar());
        excluir.setOnAction(evento -> excluir());
        voltar.setOnAction(evento -> janela.close());

        HBox botoes = new HBox(10);
        botoes.setAlignment(Pos.CENTER);
        botoes.getChildren().add(cadastrar);
        botoes.getChildren().add(consultar);
        botoes.getChildren().add(alterar);
        botoes.getChildren().add(excluir);
        botoes.getChildren().add(voltar);

        VBox conteudo = new VBox(12);
        conteudo.setAlignment(Pos.CENTER);
        conteudo.getStyleClass().add("formulario");

        conteudo.getChildren().add(titulo);
        conteudo.getChildren().add(formulario);
        conteudo.getChildren().add(botoes);
        conteudo.getChildren().add(mensagem);
        conteudo.getChildren().add(tabela);

        VBox fundo = new VBox();
        fundo.setAlignment(Pos.CENTER);
        fundo.getChildren().add(conteudo);

        Scene cena = new Scene(fundo, 850, 650);
        cena.getStylesheets().add(
                getClass().getResource("/style.css").toExternalForm()
        );

        janela.setTitle("Viewritiba - Locais");
        janela.setScene(cena);

        atualizarTabela();

        janela.show();
    }

    private void adicionarCampo(GridPane painel, String texto, TextField campo, int linha) {
        painel.add(new Label(texto), 0, linha);
        painel.add(campo, 1, linha);
    }

    private void cadastrar() {
        if (!camposPreenchidos()) {
            return;
        }

        Integer cap = lerCapacidade();
        if (cap == null) {
            return;
        }

        Local local = new Local(
                gerarId(),
                nome.getText().trim(),
                endereco.getText().trim(),
                cap
        );

        dados.getLocais().add(local);
        dados.salvar();

        limparCampos();
        atualizarTabela();

        mensagem.setText("Local cadastrado com sucesso!");
    }

    private void consultar() {
        Local local = buscarPorId();
        if (local == null) {
            return;
        }

        nome.setText(local.getNome());
        endereco.setText(local.getEndereco());
        capacidade.setText(String.valueOf(local.getCapacidade()));

        mensagem.setText("Local encontrado.");
    }

    private void alterar() {
        Local local = buscarPorId();
        if (local == null || !camposPreenchidos()) {
            return;
        }

        Integer cap = lerCapacidade();
        if (cap == null) {
            return;
        }

        local.setNome(nome.getText().trim());
        local.setEndereco(endereco.getText().trim());
        local.setCapacidade(cap);

        dados.salvar();
        atualizarTabela();

        mensagem.setText("Local alterado com sucesso!");
    }

    private void excluir() {
        Local local = buscarPorId();
        if (local == null) {
            return;
        }

        dados.getLocais().remove(local);
        dados.salvar();

        limparCampos();
        atualizarTabela();

        mensagem.setText("Local excluído com sucesso!");
    }

    private Local buscarPorId() {
        try {
            int numero = Integer.parseInt(id.getText().trim());

            for (Local local : dados.getLocais()) {
                if (local.getId() == numero) {
                    return local;
                }
            }
        } catch (NumberFormatException erro) {
            mensagem.setText("Informe um ID numérico.");
            return null;
        }

        mensagem.setText("Local não encontrado.");
        return null;
    }

    private Integer lerCapacidade() {
        try {
            int cap = Integer.parseInt(capacidade.getText().trim());

            if (cap <= 0) {
                mensagem.setText("A capacidade deve ser maior que zero.");
                return null;
            }

            return cap;
        } catch (NumberFormatException erro) {
            mensagem.setText("Informe um número inteiro válido para a capacidade.");
            return null;
        }
    }

    private boolean camposPreenchidos() {
        if (nome.getText().trim().equals("")
                || endereco.getText().trim().equals("")
                || capacidade.getText().trim().equals("")) {
            mensagem.setText("Preencha todos os campos.");
            return false;
        }

        return true;
    }

    private void atualizarTabela() {
        tabela.getChildren().clear();

        String[] cabecalho = {"ID", "Nome", "Endereço", "Capacidade"};

        for (int coluna = 0; coluna < cabecalho.length; coluna++) {
            tabela.add(criarCelula(cabecalho[coluna], true), coluna, 0);
        }

        int linha = 1;

        for (Local local : dados.getLocais()) {
            String[] valores = {
                    String.valueOf(local.getId()),
                    local.getNome(),
                    local.getEndereco(),
                    String.valueOf(local.getCapacidade())
            };

            for (int coluna = 0; coluna < valores.length; coluna++) {
                tabela.add(criarCelula(valores[coluna], false), coluna, linha);
            }

            linha++;
        }

        if (linha == 1) {
            tabela.add(new Label("Nenhum local cadastrado."), 0, 1);
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
        id.clear();
        nome.clear();
        endereco.clear();
        capacidade.clear();
    }

    private int gerarId() {
        int maior = 0;

        for (Local local : dados.getLocais()) {
            if (local.getId() > maior) {
                maior = local.getId();
            }
        }

        return maior + 1;
    }
}
