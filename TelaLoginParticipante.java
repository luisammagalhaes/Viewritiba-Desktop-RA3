
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TelaLoginParticipante {

    private Dados dados;

    private TextField campoEmail;
    private PasswordField campoSenha;
    private Label mensagem;

    public TelaLoginParticipante(Dados dados) {
        this.dados = dados;
    }

    public void mostrar() {

        Stage janela = new Stage();

        Label titulo = new Label("Login do Participante");
        titulo.setStyle("-fx-font-size: 25px; -fx-font-weight: bold;");

        Label texto = new Label("Entre na sua conta Viewritiba");

        Label labelEmail = new Label("E-mail");

        campoEmail = new TextField();
        campoEmail.setPromptText("Digite seu e-mail");

        Label labelSenha = new Label("Senha");

        campoSenha = new PasswordField();
        campoSenha.setPromptText("Digite sua senha");

        Button entrar = new Button("Entrar");
        entrar.setMaxWidth(Double.MAX_VALUE);

        mensagem = new Label();

        entrar.setOnAction(e -> realizarLogin(janela));

        VBox layout = new VBox(12);

        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));

        layout.getChildren().addAll(titulo, texto, labelEmail, campoEmail, labelSenha, campoSenha, entrar, mensagem);

        Scene cena = new Scene(layout, 400, 450);

        janela.setTitle("Viewritiba - Login");
        janela.setScene(cena);
        janela.setResizable(false);
        janela.show();
    }

    private void realizarLogin(Stage janela) {

        String email = campoEmail.getText().trim();
        String senha = campoSenha.getText();

        if (email.isEmpty() || senha.isEmpty()) {
            mensagem.setText("Preencha todos os campos.");
            return;
        }

        for (Participante p : dados.getParticipantes()) {

            if (p.validarLogin(email, senha)) {

                mensagem.setText("Login realizado!");

                TelaDetalhesEvento tela =
                        new TelaDetalhesEvento(dados);

                tela.mostrar();

                janela.close();
                return;
            }
        }

        mensagem.setText("E-mail ou senha incorretos.");
        campoSenha.clear();
    }
}
