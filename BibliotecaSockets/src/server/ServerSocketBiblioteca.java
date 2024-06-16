package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Formatter;
import java.util.List;
import org.json.JSONObject;
import org.json.JSONArray;
import static server.OperacoesLivros.ajustarDataBaseCampos;

public class ServerSocketBiblioteca {
    public static void main(String[] args) {
        int portChoise = 8082; // Define o número da porta para o servidor

        try {
            //Precisa testar se o arquivo está adequado para realizar as operações
            String FILE_PATH = ".//src//server//database//databaseLivros.json";
            ajustarDataBaseCampos(FILE_PATH);
                        
            //Prepara o socket (listener) para a porta especificada:
            ServerSocket server = new ServerSocket(portChoise);
            System.out.println("\n\nServidor pronto na porta -> " + portChoise);
            
            // Aceita uma conexão de cliente
            Socket serverSocket = server.accept();
            System.out.println("Cliente conectado: " + serverSocket.getInetAddress().getHostAddress());

            // Configura streams de entrada e saída para comunicação com o cliente
            InputStreamReader inputServer = new InputStreamReader(serverSocket.getInputStream());
            PrintStream saida = new PrintStream(serverSocket.getOutputStream());            
            BufferedReader readerServer = new BufferedReader(inputServer);
            
            String messageInputClient;
            // Lê mensagens do cliente
            while((messageInputClient = readerServer.readLine()) != null){
                // Trata os diferentes comandos do cliente
                if(messageInputClient.toUpperCase().equals("EXIT")){
                    // Encerra o servidor
                    messageInputClient = "KILL";
                    saida.println(messageInputClient);
                    serverSocket.shutdownOutput();
                }else if(messageInputClient.toUpperCase().equals("AJUDA")){
                    // Envia o menu de ajuda para o cliente
                    messageInputClient = menuAjuda();
                    saida.println(messageInputClient);
                }else if(messageInputClient.toUpperCase().equals("LISTAR")){
                    // Lista todos os livros do JSON
                    messageInputClient = listarLivrosDatabase();
                    saida.println(messageInputClient);
                }else if(messageInputClient.toUpperCase().equals("ALUGAR")){
                    // Solicita o ID do livro para alugar
                    messageInputClient = "IDALUGAR";
                    saida.println(messageInputClient);
                }else if(messageInputClient.toUpperCase().equals("DEVOLVER")){
                    // Solicita o ID do livro para devolver
                    messageInputClient = "IDDEVOLVER";
                    saida.println(messageInputClient);
                }else if(messageInputClient.toUpperCase().equals("CADASTRAR")){
                    // Obtém um novo ID para cadastro de livro e solicita os dados do livro ao cliente
                    int idNovoCadastro = new OperacoesLivros().consultaNovoIdCadastroLivro();
                    messageInputClient = "CADASTRARLIVRO\n" + idNovoCadastro;
                    saida.println(messageInputClient);
                }else if(messageInputClient.toUpperCase().equals("DADOSLIVRO")){
                    // Recebe os dados do livro do cliente e cadastra o livro
                    messageInputClient = readerServer.readLine();    
                    JSONObject jsonLivroNovo = new JSONObject(messageInputClient);
                    Boolean realizaCadastro = new OperacoesLivros().cadastrarLivro(jsonLivroNovo);
                    
                    if(realizaCadastro) messageInputClient = "CADASTROSUCESSO\n__EOF";
                    saida.println(messageInputClient);
                }else if(messageInputClient.toUpperCase().equals("DADOSIDALUGAR")){
                    // Recebe o ID do livro do cliente e processa o aluguel
                    messageInputClient = readerServer.readLine();
                    int idAlugarLivro = 0;
                    try{
                       idAlugarLivro = Integer.parseInt(messageInputClient);
                       Boolean realizaAluguel = realizarLocacaoLivro(idAlugarLivro);
                       
                       if(realizaAluguel) messageInputClient = "ALUGADOSUCESSO\n__EOF";
                       else messageInputClient = "ALUGUELSEMSUCESSO\n__EOF";
                       
                       saida.println(messageInputClient);
                    }catch(Exception e){
                        System.out.println("Erro ao converter ID aluguel");
                    }
                }else if(messageInputClient.toUpperCase().equals("DADOSIDDEVOLVER")){
                    // Recebe o ID do livro do cliente e processa a devolução
                    messageInputClient = readerServer.readLine();
                    int idDevolverLivro = 0;
                    
                    try{
                       idDevolverLivro = Integer.parseInt(messageInputClient);
                       Boolean realizaDevolucao = realizarDevolucaoLivro(idDevolverLivro);
                       
                       if(realizaDevolucao) messageInputClient = "DEVOLVIDOSUCESSO\n__EOF";
                       else messageInputClient = "DEVOLUCAOSEMSUCESSO\n__EOF";
                       saida.println(messageInputClient);
                    }catch(Exception e){
                        System.out.println("Erro ao converter ID aluguel");
                    }
                }else{
                    // Trata comandos desconhecidos
                    messageInputClient = messageInputClient = "DESCONHECIDO\n__EOF";
                    saida.println(messageInputClient);
                }
            }
        }catch(Exception e) {
           System.out.println("Erro: " + e.getMessage());
        }
    }
    
    // Método para listar todos os livros
    public static String listarLivrosDatabase(){
        List<LivroBiblioteca> listaLivros = new OperacoesLivros().consultaLivrosBibioteca();
        StringBuilder menuLivros = new StringBuilder();
        Formatter fmtLivros = new Formatter();
        menuLivros.append(fmtLivros.format("%150s","======================================================================================================================================================\n").toString());

        fmtLivros = new Formatter();
        menuLivros.append(fmtLivros.format("%85s","LISTA DE LIVROS\n\n").toString());

        fmtLivros = new Formatter();
        fmtLivros.format("%15s %40s %25s %25s %14s %14s\n\n", "ID", "TÍTULO", "GÊNERO", "AUTOR", "QUANTIDADE", "ALUGADOS");

        for (int i = 0; i < listaLivros.size(); i++) {
            fmtLivros.format("%15s %40s %25s %25s %14s %14s\n",
                    listaLivros.get(i).getId(),
                    listaLivros.get(i).getNomeLivro(),
                    listaLivros.get(i).getGeneroLivro(),
                    listaLivros.get(i).getAutorLivro(),
                    listaLivros.get(i).getNumeroExemplaresLivros(),
                    listaLivros.get(i).getQntdAlugados()
            );
        }

        menuLivros.append(fmtLivros.toString());

        fmtLivros = new Formatter();
        menuLivros.append(fmtLivros.format("%150s","======================================================================================================================================================\n").toString());
        menuLivros.append("__EOF");

        fmtLivros.close();
        
        //System.out.println(menuLivros.toString());
        
        return menuLivros.toString();
    }

    // Método para realizar a locação de um livro pelo ID
    public static Boolean realizarLocacaoLivro(int idAlugarLivro){
            List<LivroBiblioteca> listaLivros = new OperacoesLivros().consultaLivrosBibioteca();
            for (int i = 0; i < listaLivros.size(); i++) {
                if(listaLivros.get(i).getId().equals("" + idAlugarLivro)){
                    try{
                        int numExemplares = Integer.parseInt(listaLivros.get(i).getNumeroExemplaresLivros());
                        int numExemplaresAlugados = Integer.parseInt(listaLivros.get(i).getQntdAlugados());
                        if((numExemplares - numExemplaresAlugados) > 0){
                            listaLivros.get(i).setQntdAlugados("" + (numExemplaresAlugados + 1));
                        }else{
                            return false;
                        }
                    }catch(Exception e){
                        System.out.println("Erro ao alugar o livro.");
                    }
                }
            }//Final do for

            // Atualiza o JSON com as informações de locação
            JSONArray baseLivrosAlugados = new JSONArray();
            for (int i = 0; i < listaLivros.size(); i++) {
                JSONObject jsonBaseLivroAlugados = new JSONObject();
                jsonBaseLivroAlugados.put("id", listaLivros.get(i).getId());
                jsonBaseLivroAlugados.put("titulo", listaLivros.get(i).getNomeLivro());
                jsonBaseLivroAlugados.put("autor", listaLivros.get(i).getAutorLivro());
                jsonBaseLivroAlugados.put("genero", listaLivros.get(i).getGeneroLivro());
                jsonBaseLivroAlugados.put("exemplares", listaLivros.get(i).getNumeroExemplaresLivros());
                jsonBaseLivroAlugados.put("alugados", listaLivros.get(i).getQntdAlugados());
                baseLivrosAlugados.put(jsonBaseLivroAlugados);
            }

            Boolean realizaCadastro = new OperacoesLivros().atualizarBaseDadosLivro(baseLivrosAlugados);

            return realizaCadastro;
        
    }

    // Método para realizar a devolução de um livro pelo ID
    public static Boolean realizarDevolucaoLivro(int idDevolverLivro){
        List<LivroBiblioteca> listaLivros = new OperacoesLivros().consultaLivrosBibioteca();
            for (int i = 0; i < listaLivros.size(); i++) {
                if(listaLivros.get(i).getId().equals("" + idDevolverLivro)){
                    try{
                        int numExemplaresAlugados = Integer.parseInt(listaLivros.get(i).getQntdAlugados());
                        if(numExemplaresAlugados > 0){
                            listaLivros.get(i).setQntdAlugados("" + (numExemplaresAlugados - 1));
                        }else{
                            return false;
                        }
                    }catch(Exception e){
                        System.out.println("Erro ao devolver o livro.");
                    }
                }
            }//Final do for

            // Atualiza o JSON com as informações de devolução
            JSONArray baseLivrosDevolucao = new JSONArray();
            for (int i = 0; i < listaLivros.size(); i++) {
                JSONObject jsonBaseLivroAlugados = new JSONObject();
                jsonBaseLivroAlugados.put("id", listaLivros.get(i).getId());
                jsonBaseLivroAlugados.put("titulo", listaLivros.get(i).getNomeLivro());
                jsonBaseLivroAlugados.put("autor", listaLivros.get(i).getAutorLivro());
                jsonBaseLivroAlugados.put("genero", listaLivros.get(i).getGeneroLivro());
                jsonBaseLivroAlugados.put("exemplares", listaLivros.get(i).getNumeroExemplaresLivros());
                jsonBaseLivroAlugados.put("alugados", listaLivros.get(i).getQntdAlugados());
                baseLivrosDevolucao.put(jsonBaseLivroAlugados);
            }

            Boolean realizaDevolucao = new OperacoesLivros().atualizarBaseDadosLivro(baseLivrosDevolucao);

            return realizaDevolucao;
    }
    
    public static String menuAjuda(){
        StringBuilder menuAjuda = new StringBuilder();
        int i = 0;
        
        // Cria um formatador para adicionar a linha inicial do menu
        Formatter fmtMenuAjuda = new Formatter();
        menuAjuda.append(fmtMenuAjuda.format("%150s","======================================================================================================================================================\n").toString());

        // Cria um formatador para adicionar o título do menu de ajuda
        fmtMenuAjuda = new Formatter();
        menuAjuda.append(fmtMenuAjuda.format("%80s","MENU AJUDA - LISTA DE COMANDOS\n\n").toString());

        // Cria um formatador para adicionar os comandos e suas descrições
        fmtMenuAjuda = new Formatter();
        fmtMenuAjuda.format("%5s %15s %110s\n\n", "NR", "COMANDO", "DESCRIÇÃO");
        fmtMenuAjuda.format("%5s %15s %110s\n",++i,"AJUDA","Chama o menu de AJUDA com os comandos possíveis.");
        fmtMenuAjuda.format("%5s %15s %110s\n",++i,"ALUGAR","Aluga um livro desejado. Vai pedir o ID do livro escolhido.");
        fmtMenuAjuda.format("%5s %15s %110s\n",++i,"CADASTRAR","Cadastra um novo livro da base de dados. Pedirá dados do livro para cadastro.");
        fmtMenuAjuda.format("%5s %15s %110s\n",++i,"DEVOLVER","Devolve um livro específico. Vai pedir o ID do livro alugado.");
        fmtMenuAjuda.format("%5s %15s %110s\n",++i,"LISTAR","Para obter os livros do sistema");
        fmtMenuAjuda.format("%5s %15s %110s\n",++i,"EXIT","Sai do sistema. Irá finalizar o cliente e o servidor! Cuidado.");
        menuAjuda.append(fmtMenuAjuda.toString());

        // Adiciona a linha final do menu
        fmtMenuAjuda = new Formatter();
        menuAjuda.append(fmtMenuAjuda.format("%150s","======================================================================================================================================================\n").toString());
        menuAjuda.append("__EOF");

        // Fecha o formatador
        fmtMenuAjuda.close();
        
        return menuAjuda.toString();
    }
}