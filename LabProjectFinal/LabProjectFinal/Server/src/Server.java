
import java.io.*;
import java.net.*;

public class Server {

    ServerSocket listenerServerSocket = null;
  
    public Server(int port) throws IOException {
	
        try {
	    listenerServerSocket = new ServerSocket(port);
	    System.out.println("Server is Running");	        
	} catch (IOException e) {
	    System.err.printf("Server: could not listen on port: %d.", port);
            System.exit(-1);
	}
	
        ServerLoop();
    }
    
    public void ServerLoop() throws IOException {
        
        try {
            while (true) {
                Game game = new Game();
                
                Game.Player player1 = game.new Player(listenerServerSocket.accept(), '1');
                Game.Player player2 = game.new Player(listenerServerSocket.accept(), '2');
                
                player1.setOpponent(player2);
                player2.setOpponent(player1);
                
                game.currentPlayer = player1;
                
                player1.start();
                player2.start();
            }
        } finally {    
            listenerServerSocket.close();
        }
    }
    
    public static void main(String[] args) throws Exception {
      new Server(1235);
    }
}