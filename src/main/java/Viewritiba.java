import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Viewritiba extends Application {

    private Dados dados;

    @Override
    public void start(Stage janelaPrincipal) {
        dados = Dados.carregar();

        // Título
        Label titulo = new Label("Viewritiba");
        titulo.setId("titulo");

        // Subtítulo
        Label subtitulo = new Label(
                "Gerenciamento de eventos de Curitiba"
        );
        subtitulo.setId("subtitulo");

        // Botões
        Button botaoEventos =
                new Button("Gerenciar eventos");

        Button botaoCategorias =
                new Button("Gerenciar categorias");

        Button botaoContas =
                new Button("Gerenciar contas");

        botaoContas.setOnAction(evento -> {
            TelaConta telaConta =
                    new TelaConta(dados);

            telaConta.mostrar();
        });

        botaoCategorias.setOnAction(evento -> {
            TelaCategoria telaCategoria =
                    new TelaCategoria(dados);

            telaCategoria.mostrar();
        });

        botaoEventos.setOnAction(evento -> {
            TelaEvento telaEvento =
                    new TelaEvento(dados);

            telaEvento.mostrar();
        });

        // Painel que organiza os elementos
        VBox painel = new VBox(15);
        painel.setAlignment(Pos.CENTER);
        painel.getStyleClass().add("cartao");

        painel.getChildren().add(titulo);
        painel.getChildren().add(subtitulo);
        painel.getChildren().add(botaoEventos);
        painel.getChildren().add(botaoCategorias);
        painel.getChildren().add(botaoContas);

        // Fundo da tela
        BorderPane fundo = new BorderPane();
        fundo.setCenter(painel);

        // Criação da cena
        Scene cena = new Scene(fundo, 800, 550);

        // Ligação com o arquivo CSS
        cena.getStylesheets().add(
                getClass()
                        .getResource("/style.css")
                        .toExternalForm()
        );

        // Configuração da janela
        janelaPrincipal.setTitle("Viewritiba");
        janelaPrincipal.setScene(cena);
        janelaPrincipal.show();
    }

    public static void main(String[] args) {
        launch();
    }
}