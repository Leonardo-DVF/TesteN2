package server;

public class LivroBiblioteca {
    // Atributos da classe LivroBiblioteca
    private String id, autorLivro, nomeLivro, generoLivro, numeroExemplaresLivros, qntdAlugados;

    // Métodos getters e setters para acessar e alterar os atributos
    public String getAutorLivro() {
        return autorLivro;
    }

    public void setAutorLivro(String autorLivro) {
        this.autorLivro = autorLivro;
    }

    public String getNomeLivro() {
        return nomeLivro;
    }

    public void setNomeLivro(String nomeLivro) {
        this.nomeLivro = nomeLivro;
    }

    public String getGeneroLivro() {
        return generoLivro;
    }

    public void setGeneroLivro(String generoLivro) {
        this.generoLivro = generoLivro;
    }

    public String getNumeroExemplaresLivros() {
        return numeroExemplaresLivros;
    }

    public void setNumeroExemplaresLivros(String numeroExemplaresLivros) {
        this.numeroExemplaresLivros = numeroExemplaresLivros;
    }    

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQntdAlugados() {
        return qntdAlugados;
    }

    public void setQntdAlugados(String qntdAlugados) {
        this.qntdAlugados = qntdAlugados;
    }
}