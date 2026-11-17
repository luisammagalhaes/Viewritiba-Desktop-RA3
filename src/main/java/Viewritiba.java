import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import moderador.view.ModeradorView;
import organizador.view.OrganizadorView;

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
        Button botaoEventos = criarBotao("Gerenciar eventos");
        Button botaoCategorias = criarBotao("Gerenciar categorias");
        Button botaoLocais = criarBotao("Gerenciar locais");
        Button botaoPatrocinadores = criarBotao("Gerenciar patrocinadores");
        Button botaoSuspensao = criarBotao("Suspensão de contas");
        Button botaoOrganizadores = criarBotao("Gerenciar organizadores");
        Button botaoModeradores = criarBotao("Gerenciar moderadores");
        Button botaoFavoritos = criarBotao("Gerenciar favoritos");
        Button botaoCompartilhamentos = criarBotao("Gerenciar compartilhamentos");
        Button botaoParticipantes = criarBotao("Participantes e login");
        Button botaoDetalhes = criarBotao("Detalhes de eventos");

        // Ações dos botões
        botaoEventos.setOnAction(evento -> new TelaEvento(dados).mostrar());
        botaoCategorias.setOnAction(evento -> new TelaCategoria(dados).mostrar());
        botaoLocais.setOnAction(evento -> new TelaLocal(dados).mostrar());
        botaoPatrocinadores.setOnAction(evento -> new TelaPatrocinador(dados).mostrar());
        botaoSuspensao.setOnAction(evento -> new TelaSuspensaoConta(dados).mostrar());
        botaoOrganizadores.setOnAction(evento -> new OrganizadorView().mostrar());
        botaoModeradores.setOnAction(evento -> new ModeradorView().mostrar());
        botaoFavoritos.setOnAction(evento -> new TelaFavorito(dados).mostrar());
        botaoCompartilhamentos.setOnAction(evento -> new TelaCompartilhamento(dados).mostrar());
        botaoParticipantes.setOnAction(evento -> new TelaLoginParticipante(dados).mostrar());
        botaoDetalhes.setOnAction(evento -> new TelaDetalhesEvento(dados).mostrar());

        GridPane gradeBotoes = new GridPane();
        gradeBotoes.setHgap(14);
        gradeBotoes.setVgap(12);
        gradeBotoes.setAlignment(Pos.CENTER);

        gradeBotoes.add(botaoEventos, 0, 0);
        gradeBotoes.add(botaoCategorias, 1, 0);
        gradeBotoes.add(botaoLocais, 0, 1);
        gradeBotoes.add(botaoPatrocinadores, 1, 1);
        gradeBotoes.add(botaoSuspensao, 0, 2);
        gradeBotoes.add(botaoOrganizadores, 1, 2);
        gradeBotoes.add(botaoModeradores, 0, 3);
        gradeBotoes.add(botaoFavoritos, 1, 3);
        gradeBotoes.add(botaoCompartilhamentos, 0, 4);
        gradeBotoes.add(botaoParticipantes, 1, 4);
        gradeBotoes.add(botaoDetalhes, 0, 5, 2, 1);

        // Painel que organiza os elementos
        VBox painel = new VBox(15);
        painel.setAlignment(Pos.CENTER);
        painel.getStyleClass().add("cartao");

        painel.getChildren().addAll(
                titulo,
                subtitulo,
                gradeBotoes
        );

        // Fundo da tela
        BorderPane fundo = new BorderPane();
        fundo.setCenter(painel);

        // Criação da cena
        Scene cena = new Scene(fundo, 860, 620);

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

    private Button criarBotao(String texto) {
        Button botao = new Button(texto);
        botao.setPrefWidth(250);
        return botao;
    }

    public static void main(String[] args) {
        launch();
    }
}
