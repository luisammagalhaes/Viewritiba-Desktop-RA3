import java.io.Serializable;

public class Participante implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String nome;
    private String email;
    private String senha;

    public Participante(){
    }

    public Participante(int id, String nome, String email, String senha){
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getNome(){
        return nome;
    }

    public void setNome(String nome){
        this.nome = nome;
    }

    public String getEmail(){
        return email;
    }

    public void settEmail(String email){
        this.email = email;
    }

    public String getSenha(){
        return senha;
    }

    public void setSenha(String senha){
        this.senha = senha;
    }

    public  boolean validarLogin(String identificador, String senhaInformada){
        return identificador != null
                && senhaInformada != null
                && (email.equalsIgnoreCase(identificador)
                || String.valueOf(id).equals(identificador))
                && senha.equals(senhaInformada);
    }
}