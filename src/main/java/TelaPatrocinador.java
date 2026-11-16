import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TelaPatrocinador {

    private Dados dados;

    private TextField id = new TextField();
    private TextField nome = new TextField();
    private TextField contato = new TextField();
    private TextField valor = new TextField();

    private Label mensagem = new Label();
    private GridPane tabela = new GridPane();

    public TelaPatrocinador(Dados dados) {
        this.dados = dados;
    }

    public void mostrar() {
        Stage janela = new Stage();

        Label titulo = new Label("Gerenciamento de patrocinadores");
        titulo.getStyleClass().add("titulo-tela");

        id.setPromptText("Informe para consultar, alterar ou excluir");
        nome.setPromptText("Exemplo: Prefeitura de Curitiba");
        contato.setPromptText("Exemplo: contato@empresa.com");
        valor.setPromptText("Exemplo: 5000.00");

        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(10);

        adicionarCampo(formulario, "ID para consultar, alterar ou excluir:", id, 0);
        adicionarCampo(formulario, "Nome:", nome, 1);
        adicionarCampo(formulario, "Contato:", contato, 2);
        adicionarCampo(formulario, "Valor do patrocínio (R$):", valor, 3);

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

        janela.setTitle("Viewritiba - Patrocinadores");
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

        Double valorPatrocinio = lerValor();
        if (valorPatrocinio == null) {
            return;
        }

        Patrocinador patrocinador = new Patrocinador(
                gerarId(),
                nome.getText().trim(),
                contato.getText().trim(),
                valorPatrocinio
        );

        dados.getPatrocinadores().add(patrocinador);
        dados.salvar();

        limparCampos();
        atualizarTabela();

        mensagem.setText("Patrocinador cadastrado com sucesso!");
    }

    private void consultar() {
        Patrocinador patrocinador = buscarPorId();
        if (patrocinador == null) {
            return;
        }

        nome.setText(patrocinador.getNome());
        contato.setText(patrocinador.getContato());
        valor.setText(String.valueOf(patrocinador.getValor()));

        mensagem.setText("Patrocinador encontrado.");
    }

    private void alterar() {
        Patrocinador patrocinador = buscarPorId();
        if (patrocinador == null || !camposPreenchidos()) {
            return;
        }

        Double valorPatrocinio = lerValor();
        if (valorPatrocinio == null) {
            return;
        }

        patrocinador.setNome(nome.getText().trim());
        patrocinador.setContato(contato.getText().trim());
        patrocinador.setValor(valorPatrocinio);

        dados.salvar();
        atualizarTabela();

        mensagem.setText("Patrocinador alterado com sucesso!");
    }

    private void excluir() {
        Patrocinador patrocinador = buscarPorId();
        if (patrocinador == null) {
            return;
        }

        dados.getPatrocinadores().remove(patrocinador);
        dados.salvar();

        limparCampos();
        atualizarTabela();

        mensagem.setText("Patrocinador excluído com sucesso!");
    }

    private Patrocinador buscarPorId() {
        try {
            int numero = Integer.parseInt(id.getText().trim());

            for (Patrocinador patrocinador : dados.getPatrocinadores()) {
                if (patrocinador.getId() == numero) {
                    return patrocinador;
                }
            }
        } catch (NumberFormatException erro) {
            mensagem.setText("Informe um ID numérico.");
            return null;
        }

        mensagem.setText("Patrocinador não encontrado.");
        return null;
    }

    private Double lerValor() {
        try {
            String textoValor = valor.getText().trim().replace(",", ".");
            double valorNumero = Double.parseDouble(textoValor);

            if (valorNumero <= 0) {
                mensagem.setText("O valor do patrocínio deve ser maior que zero.");
                return null;
            }

            return valorNumero;
        } catch (NumberFormatException erro) {
            mensagem.setText("Informe um valor numérico válido.");
            return null;
        }
    }

    private boolean camposPreenchidos() {
        if (nome.getText().trim().equals("")
                || contato.getText().trim().equals("")
                || valor.getText().trim().equals("")) {
            mensagem.setText("Preencha todos os campos.");
            return false;
        }

        return true;
    }

    private void atualizarTabela() {
        tabela.getChildren().clear();

        String[] cabecalho = {"ID", "Nome", "Contato", "Valor (R$)"};

        for (int coluna = 0; coluna < cabecalho.length; coluna++) {
            tabela.add(criarCelula(cabecalho[coluna], true), coluna, 0);
        }

        int linha = 1;

        for (Patrocinador patrocinador : dados.getPatrocinadores()) {
            String[] valores = {
                    String.valueOf(patrocinador.getId()),
                    patrocinador.getNome(),
                    patrocinador.getContato(),
                    String.format("R$ %.2f", patrocinador.getValor())
            };

            for (int coluna = 0; coluna < valores.length; coluna++) {
                tabela.add(criarCelula(valores[coluna], false), coluna, linha);
            }

            linha++;
        }

        if (linha == 1) {
            tabela.add(new Label("Nenhum patrocinador cadastrado."), 0, 1);
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
        contato.clear();
        valor.clear();
    }

    private int gerarId() {
        int maior = 0;

        for (Patrocinador patrocinador : dados.getPatrocinadores()) {
            if (patrocinador.getId() > maior) {
                maior = patrocinador.getId();
            }
        }

        return maior + 1;
    }
}
