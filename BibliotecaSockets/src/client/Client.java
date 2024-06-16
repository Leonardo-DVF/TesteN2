package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        // Define a porta e o host para a conexão com o servidor
        int portConnect = 8082;
        String host = "localhost";
        
        // Cria um objeto ObjectInputStream para ler as mensagens do servidor
        ObjectInputStream messageServer = null;
        
        try {
            // Realiza a conexão com o socket server
            Socket clienteSocket = new Socket(host, portConnect);
            
            // Cria um Scanner para ler as entradas do usuário
            Scanner scanner = new Scanner(System.in);
            
            // Cria um objeto ClienteThread para gerenciar a comunicação com o servidor
            ClienteThread cThread = new ClienteThread(clienteSocket);
            cThread.start();
            
            // Cria um PrintStream para enviar mensagens para o servidor
            PrintStream saida = new PrintStream(clienteSocket.getOutputStream());
            
            // Exibe uma mensagem inicial e solicita um comando ao usuário
            System.out.print("Digite 'ajuda' para obter os comandos\nDigite o comando >>>");
            saida.println(scanner.nextLine());
            
        } catch (Exception e) {
            // Trata exceções que possam ocorrer durante a execução do método main
            System.out.println("Erro: " + e);
        } finally {
            // Bloco finally é executado sempre, independentemente de uma exceção ser lançada ou não
        }
    }
}