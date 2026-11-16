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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public class TelaEvento {
    private Dados dados;
    private TextField id = new TextField();
    private TextField titulo = new TextField();
    private TextField descricao = new TextField();
    private TextField dataHora = new TextField();
    private TextField local = new TextField();
    private TextField categoria = new TextField();
    private TextField pesquisa = new TextField();
    private Label mensagem = new Label();
    private GridPane tabela = new GridPane();

    public TelaEvento(Dados dados) {
        this.dados = dados;
    }

    public void mostrar() {
        Stage janela = new Stage();
        Label tituloTela = new Label("Gerenciamento de eventos");
        tituloTela.getStyleClass().add("titulo-tela");

        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(10);
        formulario.setPadding(new Insets(10));
        adicionarCampo(formulario, "ID para consultar, alterar ou excluir:", id, 0);
        adicionarCampo(formulario, "Titulo:", titulo, 1);
        adicionarCampo(formulario, "Descricao:", descricao, 2);
        adicionarCampo(formulario, "Data e hora:", dataHora, 3);
        adicionarCampo(formulario, "Local:", local, 4);
        adicionarCampo(formulario, "Categoria:", categoria, 5);

        Button cadastrar = new Button("Cadastrar");
        Button consultar = new Button("Consultar ID");
        Button alterar = new Button("Alterar");
        Button excluir = new Button("Excluir");
        Button voltar = new Button("Voltar");
        cadastrar.setOnAction(e -> cadastrar());
        consultar.setOnAction(e -> consultar());
        alterar.setOnAction(e -> alterar());
        excluir.setOnAction(e -> excluir());

        voltar.setOnAction(
                evento -> janela.close()
        );

        HBox botoes = new HBox(10, cadastrar, consultar, alterar, excluir, voltar);
        botoes.setAlignment(Pos.CENTER);

        Button pesquisar = new Button("Pesquisar");
        Button todos = new Button("Mostrar todos");
        pesquisar.setOnAction(e -> atualizarTabela(pesquisa.getText().trim()));
        todos.setOnAction(e -> {
            pesquisa.clear();
            atualizarTabela("");
        });

        HBox busca = new HBox(10, new Label("Pesquisar:"), pesquisa, pesquisar, todos);
        busca.setAlignment(Pos.CENTER);

        VBox conteudo = new VBox(12, tituloTela, formulario,
                new Label("Categorias: " + nomesCategorias()), botoes,
                mensagem, busca, tabela);
        conteudo.setAlignment(Pos.CENTER);
        conteudo.getStyleClass().add("formulario");
        conteudo.getStyleClass().add("formulario-evento");

        VBox fundo = new VBox(conteudo);
        fundo.setAlignment(Pos.CENTER);
        Scene cena = new Scene(fundo, 1050, 750);
        cena.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        janela.setTitle("Viewritiba - Eventos");
        janela.setScene(cena);
        atualizarTabela("");
        janela.show();
    }

    private void adicionarCampo(GridPane painel, String texto, TextField campo, int linha) {
        painel.add(new Label(texto), 0, linha);
        painel.add(campo, 1, linha);
    }

    private void cadastrar() {
        if (!camposValidos()) return;
        Categoria categoriaEncontrada = buscarCategoria();
        if (categoriaEncontrada == null) return;

        Evento evento = new Evento(gerarId(), titulo.getText().trim(),
                descricao.getText().trim(), dataHora.getText().trim(),
                local.getText().trim(), categoriaEncontrada);
        dados.getEventos().add(evento);
        dados.salvar();
        limparCampos();
        atualizarTabela("");
        mensagem.setText("Evento cadastrado com sucesso!");
    }

    private void consultar() {
        Evento evento = buscarEventoPorId();
        if (evento == null) return;
        titulo.setText(evento.getTitulo());
        descricao.setText(evento.getDescricao());
        dataHora.setText(evento.getDataHora());
        local.setText(evento.getLocal());
        categoria.setText(evento.getCategoria().getNome());
        mensagem.setText("Evento encontrado.");
    }

    private void alterar() {
        Evento evento = buscarEventoPorId();
        if (evento == null || !camposValidos()) return;
        Categoria categoriaEncontrada = buscarCategoria();
        if (categoriaEncontrada == null) return;

        evento.setTitulo(titulo.getText().trim());
        evento.setDescricao(descricao.getText().trim());
        evento.setDataHora(dataHora.getText().trim());
        evento.setLocal(local.getText().trim());
        evento.setCategoria(categoriaEncontrada);
        dados.salvar();
        atualizarTabela("");
        mensagem.setText("Evento alterado com sucesso!");
    }

    private void excluir() {
        Evento evento = buscarEventoPorId();
        if (evento == null) return;
        dados.getEventos().remove(evento);
        dados.getComentarios().removeIf(c -> c.getEventoId() == evento.getId());
        dados.salvar();
        limparCampos();
        atualizarTabela("");
        mensagem.setText("Evento excluido com sucesso!");
    }

    private Evento buscarEventoPorId() {
        try {
            int numero = Integer.parseInt(id.getText().trim());
            for (Evento evento : dados.getEventos()) {
                if (evento.getId() == numero) return evento;
            }
        } catch (NumberFormatException erro) {
            mensagem.setText("Informe um ID numerico.");
            return null;
        }
        mensagem.setText("Evento nao encontrado.");
        return null;
    }

    private Categoria buscarCategoria() {
        for (Categoria item : dados.getCategorias()) {
            if (item.getNome().equalsIgnoreCase(categoria.getText().trim())) return item;
        }
        mensagem.setText("Categoria nao encontrada.");
        return null;
    }

    private boolean camposValidos() {
        if (titulo.getText().trim().equals("") || descricao.getText().trim().equals("")
                || dataHora.getText().trim().equals("") || local.getText().trim().equals("")
                || categoria.getText().trim().equals("")) {
            mensagem.setText("Preencha todos os campos.");
            return false;
        }
        if (!dataHoraValida()) {
            mensagem.setText(
                    "Informe uma data e hora futuras."
            );
            return false;
        }
        return true;
    }

    private boolean dataHoraValida() {
        try {
            DateTimeFormatter formato = DateTimeFormatter
                    .ofPattern("dd/MM/uuuu HH:mm")
                    .withResolverStyle(ResolverStyle.STRICT);

            LocalDateTime dataEvento = LocalDateTime.parse(
                    dataHora.getText().trim(),
                    formato
            );

            return dataEvento.isAfter(LocalDateTime.now());

        } catch (DateTimeParseException erro) {
            return false;
        }
    }

    private void atualizarTabela(String texto) {
        tabela.getChildren().clear();
        String[] cabecalho = {"ID", "Titulo", "Descricao", "Data", "Local", "Categoria", "Ações"};
        for (int coluna = 0; coluna < cabecalho.length; coluna++)
            tabela.add(criarCelula(cabecalho[coluna], true), coluna, 0);

        int linha = 1;
        String termo = texto.toLowerCase();
        for (Evento evento : dados.getEventos()) {
            String nomeCategoria = evento.getCategoria().getNome();
            if (termo.equals("") || evento.getTitulo().toLowerCase().contains(termo)
                    || evento.getDescricao().toLowerCase().contains(termo)
                    || evento.getLocal().toLowerCase().contains(termo)
                    || nomeCategoria.toLowerCase().contains(termo)) {
                tabela.add(criarCelula("" + evento.getId(), false), 0, linha);
                tabela.add(criarCelula(evento.getTitulo(), false), 1, linha);
                tabela.add(criarCelula(evento.getDescricao(), false), 2, linha);
                tabela.add(criarCelula(evento.getDataHora(), false), 3, linha);
                tabela.add(criarCelula(evento.getLocal(), false), 4, linha);
                tabela.add(criarCelula(nomeCategoria, false), 5, linha);
                Button botaoComentarios = new Button("Comentarios");
                botaoComentarios.setStyle("-fx-min-width: 145; -fx-padding: 13 10;");
                botaoComentarios.setOnAction(e -> new TelaComentario(dados, evento).mostrar());
                tabela.add(botaoComentarios, 6, linha);
                linha++;
            }
        }
        if (linha == 1) tabela.add(new Label("Nenhum evento encontrado."), 0, 1, 7, 1);
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

    private String nomesCategorias() {
        String nomes = "";
        for (Categoria item : dados.getCategorias()) nomes += item.getNome() + "  ";
        return nomes;
    }

    private void limparCampos() {
        id.clear();
        titulo.clear();
        descricao.clear();
        dataHora.clear();
        local.clear();
        categoria.clear();
    }

    private int gerarId() {
        int maior = 0;
        for (Evento evento : dados.getEventos())
            if (evento.getId() > maior) maior = evento.getId();
        return maior + 1;
    }
}
