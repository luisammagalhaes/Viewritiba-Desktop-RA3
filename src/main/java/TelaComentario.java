import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TelaComentario {
    private Dados dados;
    private Evento evento;
    private TextField id = new TextField();
    private TextField nota = new TextField();
    private TextField texto = new TextField();
    private TextField pesquisa = new TextField();
    private Label mensagem = new Label();
    private GridPane tabela = new GridPane();

    public TelaComentario(Dados dados, Evento evento) {
        this.dados = dados;
        this.evento = evento;
    }

    public void mostrar() {
        Stage janela = new Stage();
        Label tituloTela = new Label("Comentarios - " + evento.getTitulo());
        tituloTela.getStyleClass().add("titulo-tela");

        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(10);
        formulario.setPadding(new Insets(10));
        adicionarCampo(formulario, "ID:", id, 0);
        adicionarCampo(formulario, "Nota (1 a 5):", nota, 1);
        adicionarCampo(formulario, "Comentario:", texto, 2);

        id.setPromptText("Informe para consultar, alterar ou excluir");

        Button cadastrar = new Button("Cadastrar");
        Button consultar = new Button("Consultar ID");
        Button alterar = new Button("Alterar");
        Button excluir = new Button("Excluir");
        cadastrar.setOnAction(e -> cadastrar());
        consultar.setOnAction(e -> consultar());
        alterar.setOnAction(e -> alterar());
        excluir.setOnAction(e -> excluir());

        HBox botoes = new HBox(10, cadastrar, consultar, alterar, excluir);
        botoes.setAlignment(Pos.CENTER);

        pesquisa.setPromptText("Pesquisar no comentario");
        Button pesquisar = new Button("Pesquisar");
        Button todos = new Button("Mostrar todos");
        pesquisar.setOnAction(e -> atualizarTabela(pesquisa.getText().trim()));
        todos.setOnAction(e -> {
            pesquisa.clear();
            atualizarTabela("");
        });

        HBox busca = new HBox(10, pesquisa, pesquisar, todos);
        busca.setAlignment(Pos.CENTER);

        VBox conteudo = new VBox(12, tituloTela, formulario, botoes,
                mensagem, busca, tabela);
        conteudo.setAlignment(Pos.CENTER);
        conteudo.getStyleClass().add("formulario");
        conteudo.getStyleClass().add("formulario-evento");

        VBox fundo = new VBox(conteudo);
        fundo.setAlignment(Pos.CENTER);
        Scene cena = new Scene(fundo, 1050, 750);
        cena.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        janela.setTitle("Viewritiba - Comentarios");
        janela.setScene(cena);
        atualizarTabela("");
        janela.show();
    }

    private void adicionarCampo(GridPane painel, String rotulo, TextField campo, int linha) {
        painel.add(new Label(rotulo), 0, linha);
        painel.add(campo, 1, linha);
        campo.setPrefWidth(430);
    }

    private void cadastrar() {
        if (!camposValidos()) return;
        Comentario comentario = new Comentario(gerarId(), evento.getId(),
                Integer.parseInt(nota.getText().trim()), texto.getText().trim());
        dados.getComentarios().add(comentario);
        dados.salvar();
        limparCampos();
        atualizarTabela("");
        mensagem.setText("Comentario cadastrado com sucesso!");
    }

    private void consultar() {
        Comentario comentario = buscarPorId();
        if (comentario == null) return;
        nota.setText(String.valueOf(comentario.getNota()));
        texto.setText(comentario.getTexto());
        mensagem.setText("Comentario encontrado.");
    }

    private void alterar() {
        Comentario comentario = buscarPorId();
        if (comentario == null || !camposValidos()) return;
        comentario.setNota(Integer.parseInt(nota.getText().trim()));
        comentario.setTexto(texto.getText().trim());
        dados.salvar();
        atualizarTabela("");
        mensagem.setText("Comentario alterado com sucesso!");
    }

    private void excluir() {
        Comentario comentario = buscarPorId();
        if (comentario == null) return;
        dados.getComentarios().remove(comentario);
        dados.salvar();
        limparCampos();
        atualizarTabela("");
        mensagem.setText("Comentario excluido com sucesso!");
    }

    private Comentario buscarPorId() {
        try {
            int numero = Integer.parseInt(id.getText().trim());
            for (Comentario item : dados.getComentarios()) {
                if (item.getId() == numero && item.getEventoId() == evento.getId()) return item;
            }
        } catch (NumberFormatException erro) {
            mensagem.setText("Informe um ID numerico.");
            return null;
        }
        mensagem.setText("Comentario nao encontrado neste evento.");
        return null;
    }

    private boolean camposValidos() {
        if (nota.getText().trim().equals("") || texto.getText().trim().equals("")) {
            mensagem.setText("Preencha todos os campos.");
            return false;
        }
        try {
            int valor = Integer.parseInt(nota.getText().trim());
            if (valor < 1 || valor > 5) {
                mensagem.setText("A nota deve ser de 1 a 5.");
                return false;
            }
        } catch (NumberFormatException erro) {
            mensagem.setText("A nota deve ser um numero de 1 a 5.");
            return false;
        }
        return true;
    }

    private void atualizarTabela(String busca) {
        tabela.getChildren().clear();
        String[] cabecalho = {"ID", "Nota", "Comentario"};
        for (int coluna = 0; coluna < cabecalho.length; coluna++)
            tabela.add(criarCelula(cabecalho[coluna], true, coluna == 2), coluna, 0);

        int linha = 1;
        String termo = busca.toLowerCase();
        for (Comentario item : dados.getComentarios()) {
            if (item.getEventoId() != evento.getId()) continue;
            if (termo.equals("") || item.getTexto().toLowerCase().contains(termo)) {
                tabela.add(criarCelula(String.valueOf(item.getId()), false, false), 0, linha);
                tabela.add(criarCelula(String.valueOf(item.getNota()), false, false), 1, linha);
                tabela.add(criarCelula(item.getTexto(), false, true), 2, linha);
                linha++;
            }
        }
        if (linha == 1) tabela.add(new Label("Nenhum comentario encontrado."), 0, 1, 3, 1);
    }

    private Label criarCelula(String conteudo, boolean cabecalho, boolean larga) {
        Label celula = new Label(conteudo);
        celula.setWrapText(true);
        celula.setPrefWidth(larga ? 500 : 100);
        celula.getStyleClass().add(cabecalho ? "cabecalho-tabela" : "celula-tabela");
        return celula;
    }

    private void limparCampos() {
        id.clear();
        nota.clear();
        texto.clear();
    }

    private int gerarId() {
        int maior = 0;
        for (Comentario item : dados.getComentarios())
            if (item.getId() > maior) maior = item.getId();
        return maior + 1;
    }
}
