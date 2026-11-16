
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class TelaDetalhesEvento {

    private Dados dados;

    private ListView<Evento> listaEventos;

    private Label id;
    private Label titulo;
    private Label descricao;
    private Label dataHora;
    private Label local;
    private Label categoria;

    public TelaDetalhesEvento(Dados dados) {
        this.dados = dados;
    }

    public void mostrar() {

        Stage janela = new Stage();

        Label cabecalho = new Label("Detalhes dos Eventos");
        cabecalho.setStyle("-fx-font-size: 25px; -fx-font-weight: bold;");

        listaEventos = new ListView<>();
        listaEventos.setPrefWidth(300);

        atualizarLista();

        id = new Label("-");
        titulo = new Label("-");
        descricao = new Label("-");
        dataHora = new Label("-");
        local = new Label("-");
        categoria = new Label("-");

        GridPane detalhes = new GridPane();

        detalhes.setHgap(15);
        detalhes.setVgap(18);
        detalhes.setPadding(new Insets(20));

        detalhes.add(new Label("ID:"), 0, 0);
        detalhes.add(id, 1, 0);

        detalhes.add(new Label("Título:"), 0, 1);
        detalhes.add(titulo, 1, 1);

        detalhes.add(new Label("Descrição:"), 0, 2);
        detalhes.add(descricao, 1, 2);

        detalhes.add(new Label("Data e hora:"), 0, 3);
        detalhes.add(dataHora, 1, 3);

        detalhes.add(new Label("Local:"), 0, 4);
        detalhes.add(local, 1, 4);

        detalhes.add(new Label("Categoria:"), 0, 5);
        detalhes.add(categoria, 1, 5);

        listaEventos.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, antigo, selecionado) -> {

                    if (selecionado != null) {
                        exibirDetalhes(selecionado);
                    }
                });

        VBox painelLista = new VBox(10, new Label("Eventos cadastrados"), listaEventos);

        VBox painelDetalhes = new VBox(10, new Label("Informações do evento"), detalhes);

        HBox conteudo = new HBox(
                25,
                painelLista,
                painelDetalhes
        );

        conteudo.setAlignment(Pos.CENTER);

        Button fechar = new Button("Fechar");
        fechar.setOnAction(e -> janela.close());

        VBox layout = new VBox(20, cabecalho, conteudo, fechar);

        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(25));

        Scene cena = new Scene(layout, 900, 600);

        janela.setTitle("Viewritiba - Eventos");
        janela.setScene(cena);
        janela.show();
    }

    private void atualizarLista() {

        listaEventos.getItems().clear();

        for (Evento evento : dados.getEventos()) {
            listaEventos.getItems().add(evento);
        }
    }

    private void exibirDetalhes(Evento evento) {

        id.setText(String.valueOf(evento.getId()));

        titulo.setText(evento.getTitulo());

        descricao.setText(evento.getDescricao());

        dataHora.setText(evento.getDataHora());

        local.setText(evento.getLocal());

        if (evento.getCategoria() != null) {
            categoria.setText(evento.getCategoria().getNome());
        }

        else {
            categoria.setText("Não informada");
        }
    }
}
