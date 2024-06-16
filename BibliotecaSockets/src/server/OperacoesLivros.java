package server;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class OperacoesLivros {
    
    // Consulta todos os livros da biblioteca
    public List<LivroBiblioteca> consultaLivrosBibioteca() {
        List<LivroBiblioteca> livrosBiblioteca = new ArrayList<>();
        try {
            // Carrega o arquivo JSON com os livros
            String FILE_PATH = ".//src//server//database//databaseLivros.json";
            JSONArray livrosArray = retornaJSON(FILE_PATH);
            
            // Carrega cada livro do array em uma instância da classe LivroBiblioteca
            for (int i = 0; i < livrosArray.length(); i++) {
                JSONObject jsonLivro = livrosArray.getJSONObject(i);
                LivroBiblioteca livroB = new LivroBiblioteca();
                livroB.setId("" + jsonLivro.getInt("id"));                
                livroB.setAutorLivro(jsonLivro.getString("autor"));
                livroB.setGeneroLivro(jsonLivro.getString("genero"));
                livroB.setNomeLivro(jsonLivro.getString("titulo"));
                livroB.setNumeroExemplaresLivros("" + jsonLivro.getInt("exemplares"));
                livroB.setQntdAlugados(jsonLivro.getString("alugados"));
                livrosBiblioteca.add(livroB);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return livrosBiblioteca;
    }
    
    // Cadastra um novo livro na biblioteca
    public boolean cadastrarLivro(JSONObject novoLivro) {
        String FILE_PATH = ".//src//server//database//databaseLivros.json";
        
        try {
            // Lê o arquivo JSON existente
            FileReader reader = new FileReader(FILE_PATH);
            JSONTokener tokener = new JSONTokener(reader);
            JSONObject jsonObject = new JSONObject(tokener);
            reader.close();
            JSONArray objArrayJson = jsonObject.getJSONArray("livros");
            
            // Adiciona o novo livro ao array
            objArrayJson.put(novoLivro);
            jsonObject.put("livros", objArrayJson);
            
            // Grava o arquivo JSON com o novo livro
            try (FileWriter writeFile = new FileWriter(FILE_PATH)) {
                writeFile.write(jsonObject.toString(4));
            }
            
            return true;
        } catch (IOException e) {
            System.out.println("Erro ao gravar o arquivo JSON");
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Atualiza a base de dados com os livros
    public boolean atualizarBaseDadosLivro(JSONArray baseLivros){
        String filePath = ".//src//server//database//databaseLivros.json";
        
        try {
            // Lê o arquivo JSON existente
            FileReader reader = new FileReader(filePath);
            JSONTokener tokener = new JSONTokener(reader);
            JSONObject jsonObject = new JSONObject(tokener);
            reader.close();
            
            // Atualiza o array de livros
            jsonObject.put("livros", baseLivros);
                       
            // Grava o arquivo JSON com os livros atualizados
            try (FileWriter writeFile = new FileWriter(filePath)) {
                writeFile.write(jsonObject.toString(4));
            }
            
            return true;
        } catch (IOException e) {
            System.out.println("Erro ao gravar o arquivo JSON");
            e.printStackTrace();
        }
       
        return false;
    }
    
    // Ajusta a base de dados para adicionar campos ausentes
    public static void ajustarDataBaseCampos(String filePath) throws IOException{
        JSONArray livrosArray = retornaJSON(filePath);
        
        JSONArray livrosArrayCompleto = new JSONArray();
        
        for (int i = 0; i < livrosArray.length(); i++) {
            JSONObject jsonLivro = livrosArray.getJSONObject(i);
            if(!jsonLivro.has("id")) jsonLivro.put("id", (i + 1));
            if(!jsonLivro.has("alugados")) jsonLivro.put("alugados","0");
            
            livrosArrayCompleto.put(jsonLivro);
        }
        
        JSONObject jsonObjectCompleto = new JSONObject();
        jsonObjectCompleto.put("livros", livrosArrayCompleto);
            
        try (FileWriter writeFile = new FileWriter(filePath)) {
            writeFile.write(jsonObjectCompleto.toString(6));
        }
    }

    // Retorna um array de livros a partir do arquivo JSON
    public static JSONArray retornaJSON(String FILE_PATH){
        JSONArray livrosArray = null;
        try{
            FileReader reader = new FileReader(FILE_PATH);
            JSONTokener tokener = new JSONTokener(reader);
            JSONObject jsonObject = new JSONObject(tokener);
            livrosArray = jsonObject.getJSONArray("livros");
        }catch(Exception e){
            System.out.println("Não foi possível ler o arquivo JSON");
        }
        
        return livrosArray;
    }
    
    // Consulta o próximo ID para um novo livro
    public static Integer consultaNovoIdCadastroLivro(){
        int ultimoID = 0;
        try {
            String FILE_PATH = ".//src//server//database//databaseLivros.json";
            //Recarrega com os campos corretos do arquivo.
            JSONArray livrosArray = retornaJSON(FILE_PATH);
            
            for(int i = 0; i < livrosArray.length();i++){
                JSONObject jsonLivro = livrosArray.getJSONObject(i);
                if(jsonLivro.getInt("id") > ultimoID) ultimoID = jsonLivro.getInt("id");
            }

            ultimoID = ultimoID + 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ultimoID;
    }
}