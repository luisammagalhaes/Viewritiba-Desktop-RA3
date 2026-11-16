import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import moderador.view.ModeradorView;
import organizador.view.OrganizadorView;

public class Main extends Application {
    private BorderPane painelPrincipal;
    private final OrganizadorView organizadorView = new OrganizadorView();
    private final ModeradorView moderadorView = new ModeradorView();

    @Override
    public void start(Stage palco) {
        painelPrincipal = new BorderPane();

        HBox menu = new HBox(15);
        menu.setPadding(new Insets(15));
        menu.setAlignment(Pos.CENTER);

        Button btnInicio = new Button("Início");
        Button btnOrganizadores = new Button("Organizadores");
        Button btnModeradores = new Button("Moderadores");

        btnInicio.setOnAction(e -> mostrarInicio());
        btnOrganizadores.setOnAction(e -> painelPrincipal.setCenter(organizadorView.criarTela()));
        btnModeradores.setOnAction(e -> painelPrincipal.setCenter(moderadorView.criarTela()));

        menu.getChildren().addAll(btnInicio, btnOrganizadores, btnModeradores);
        painelPrincipal.setTop(menu);

        mostrarInicio();

        Scene cena = new Scene(painelPrincipal, 820, 680);
        try {
            cena.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        } catch (Exception ignored) {
        }

        palco.setTitle("Viewritiba - Módulos Nyckson");
        palco.setScene(cena);
        palco.show();
    }

    private void mostrarInicio() {
        VBox cartao = new VBox(15);
        cartao.setAlignment(Pos.CENTER);
        cartao.getStyleClass().add("cartao");

        Label titulo = new Label("Viewritiba");
        titulo.setId("titulo");

        Label subtitulo = new Label("Módulos: Organizadores e Moderadores");
        subtitulo.setId("subtitulo");

        cartao.getChildren().addAll(titulo, subtitulo);

        BorderPane centro = new BorderPane();
        centro.setCenter(cartao);
        painelPrincipal.setCenter(centro);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
