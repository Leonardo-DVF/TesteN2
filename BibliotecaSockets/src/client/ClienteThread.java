package client;



import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ClienteThread extends Thread{

    private Socket socketClient;

    ClienteThread(Socket clienteSocket) {
        this.socketClient = clienteSocket;
    }
    
    @Override
    public void run(){
        try{
            // Cria um InputStreamReader e um BufferedReader para ler as mensagens do servidor
            InputStreamReader inputServer = new InputStreamReader(socketClient.getInputStream());
            BufferedReader readerServer = new BufferedReader(inputServer);
            
            // Cria um Scanner para ler as entradas do usuário
            String messageInputClient = null;
            Scanner scanner = new Scanner(System.in);
            
            // Cria uma lista de comandos
            List<String> comandos = new ArrayList<String>();
            comandos.add("KILL");
            comandos.add("__EOF");
           
            // Loop principal para processar as mensagens do servidor
            while((messageInputClient = readerServer.readLine())!=null){
                
                // Se a mensagem for "KILL", exibe uma mensagem de conexão encerrada
                if(messageInputClient.equals("KILL")){
                    messageInputClient = "Conexão encerrada!\n <<< Pressione qualquer tecla para continuar >>>";
                    
                // Se a mensagem não contiver "KILL" ou "__EOF" e não estiver na lista de comandos, exibe a mensagem
                }else if(!messageInputClient.contains("__EOF")) {
                    if(!comandos.contains(messageInputClient)) System.out.println(messageInputClient);
                }
                
                // Se a mensagem for "CADASTRARLIVRO", solicita os dados do novo livro e envia para o servidor                
                if(messageInputClient.equals("CADASTRARLIVRO")){
                    PrintStream saida = new PrintStream(socketClient.getOutputStream());

                    messageInputClient = readerServer.readLine();
                    String idNovoCadastro = messageInputClient;
                    
                    String jsonDadosLivros = "";
                    System.out.println("\nDigite os dados do livro\n");
                    jsonDadosLivros = jsonDadosLivros + "{";
                    jsonDadosLivros = jsonDadosLivros + "\"id\":" + idNovoCadastro + ",";
                    jsonDadosLivros = jsonDadosLivros + "\"alugados\":\"0\",";
                    System.out.print("Título:");
                    jsonDadosLivros = jsonDadosLivros + "\"titulo\":\"" + scanner.nextLine() + "\",";
                    System.out.print("Autor:");
                    jsonDadosLivros = jsonDadosLivros + "\"autor\":\"" + scanner.nextLine() + "\",";
                    System.out.print("Gênero:");
                    jsonDadosLivros = jsonDadosLivros + "\"genero\":\"" + scanner.nextLine() + "\",";
                    System.out.print("Quantidade de Exemplares:");
                    jsonDadosLivros = jsonDadosLivros + "\"exemplares\":" + scanner.nextLine();
                    jsonDadosLivros = jsonDadosLivros + "}";
                    
                    saida.println("DADOSLIVRO");
                    saida.println(jsonDadosLivros);
                }
                
                // Se a mensagem for "IDALUGAR", solicita o ID do livro a ser alugado e envia para o servidor                
                if(messageInputClient.equals("IDALUGAR")){
                    PrintStream saida = new PrintStream(socketClient.getOutputStream());
                    saida.println("DADOSIDALUGAR");
                    System.out.print("\nQual livro deseja alugar  (Digite o ID):");
                    saida.println(scanner.nextLine());
                }
                
                // Se a mensagem for "IDDEVOLVER", solicita o ID do livro a ser devolvido e envia para o servidor        
                if(messageInputClient.equals("IDDEVOLVER")){
                    System.out.print("\nQual livro deseja devolver (Digite o ID):");
                    PrintStream saida = new PrintStream(socketClient.getOutputStream());
                    saida.println("DADOSIDDEVOLVER");
                    saida.println(scanner.nextLine());
                }
                
                // Exibe mensagens de sucesso ou erro, dependendo da resposta do servidor
                if(messageInputClient.equals("CADASTROSUCESSO")) System.out.println("\nLivro cadastrado!");                
                if(messageInputClient.equals("ALUGADOSUCESSO")) System.out.println("\nLivro alugado!");
                if(messageInputClient.equals("ALUGUELSEMSUCESSO")) System.out.println("\nLivro não foi alugado. Sem exemplar disponível. Tente outro livro!");
                if(messageInputClient.equals("DEVOLVIDOSUCESSO")) System.out.println("\nLivro devolvido!");
                if(messageInputClient.equals("DEVOLUCAOSEMSUCESSO")) System.out.println("\nLivro não foi devolvido. Verifique se o ID do livro foi digitado corretamente!");
                if(messageInputClient.equals("DESCONHECIDO")) System.out.println("\nComando desconhecido. \nDigite \"AJUDA\" para informação dos comandos.\n\n");

                // Se a mensagem contiver "__EOF", solicita um novo comando ao usuário e envia para o servidor                
                if(messageInputClient.contains("__EOF")){
                    PrintStream saida = new PrintStream(socketClient.getOutputStream());
                    System.out.print("Digite o comando >>> ");
                    saida.println(scanner.nextLine());
                }
            }
        }catch(Exception e){
            System.out.println("Erro no client thread + " + e);
        }
    }
    
}
