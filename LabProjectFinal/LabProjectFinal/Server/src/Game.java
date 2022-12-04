
import java.io.*;
import java.net.*;
import java.util.HashSet;

public class Game {

    Player currentPlayer;
    int count = 0;
    int player1, player2;
    
    private HashSet<String> names = new HashSet<String>();
    private HashSet<PrintWriter> writers = new HashSet<PrintWriter>();
    
    public String result(int player, int choice) {
	
        count += 1;
	
        if(count == 1) player1 = choice;
	else if (count == 2) {
	    player2 = choice;
	    count = 0;

	    String score = ifPlayer1Win() ? "Player1 WINS" : ifPlayer2Win() ? "Player2 WINS":
			ifDraw() ? "Draw" : "SOMEONE DIDN'T PLAY";
	    
            return score;
	}
	return null;
    }
    
    public boolean ifDraw() {
	if(player1 == player2)  return true;
	
        return false;
    }
    
    public boolean ifPlayer1Win() {
        if(player1==1 && player2 ==3 || player1==2 && player2==1 || player1==3 && player2==2)   return true;
        
        return false;
    }

    public boolean ifPlayer2Win() {
        if(player1==1 && player2==2 || player1==2 && player2==3 || player1==3 && player2==1)    return true;
        
	return false;
    }
    
    public synchronized boolean legalMove(int hand, Player player) {
        if (player == currentPlayer && hand >= 0 &&  hand <= 3) {
            currentPlayer = currentPlayer.opponentPlayer;
            currentPlayer.otherPlayerPlayed(hand);
            return true;
        }
        return false;
    }

    public class Player extends Thread {
        
	char number;
        String nameString;
        Player opponentPlayer;
        Socket socket;
        BufferedReader bufferedReader;
        PrintWriter printWriter;

        public Player(Socket socket, char number) {
            
            this.socket = socket;
            this.number = number;
            
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                printWriter = new PrintWriter(socket.getOutputStream(), true);
                
                while (true) {
                    printWriter.println("SUBMITNAME");
                    nameString = bufferedReader.readLine();
                    if (nameString == null) return;
                    
                    synchronized (names) {
                        if (!names.contains(nameString)) {
                            names.add(nameString);
                            break;
                        }
                        else {
                            nameString = nameString + "1";
                            names.add(nameString);
                            break;
                        }
                    }
                }
                
                printWriter.println("NAMEACCEPTED");
                writers.add(printWriter);
                
                printWriter.println("RULE Rock breaks Scissors, Paper covers Rock, Scissors cuts Paper");
                printWriter.println("WELCOME Welcome Player " + number);
                printWriter.println("MESSAGE Waiting for opponentPlayer to connect");
            } catch (IOException e) {
                System.out.println("Player has left the game.");
        	for (PrintWriter writer : writers)  writer.println("MESSAGE " + nameString + "has left the game.\nCreate a new game.");
            }
        }

        public void run() {
            
            try {
        	printWriter.println("MESSAGE All players connected.");
                if (number == '1') {
                    printWriter.println("MESSAGE You choose first\n");
                    printWriter.println("PLAYER1");
                }
                if (number == '2')  printWriter.println("MESSAGE Select Your Hand After Opponent.\n");

                while (true) {
                    String command = bufferedReader.readLine();
                    
                    if (bufferedReader == null) return;
                    if (command.startsWith("MOVE")) {
                        int hand = Integer.parseInt(command.substring(4));
                        if (legalMove(hand, this)) {
                            if(number == '1')   result(number,hand);
                            else if (number == '2') {
                        	String tempString = result(number, hand); 
                        	for (PrintWriter writer : writers)  writer.println("RESULT"+ tempString);
                            }
                        }
                    }
                    else    for (PrintWriter writer : writers)  writer.println("MESSAGE " + nameString + ": " + command);
                      
                }
            } catch (IOException e) {
        	System.err.println("Player has left the game.");
        	for (PrintWriter writer : writers)  writer.println("MESSAGE Player 1 has left the game.\n");
              
            } catch (NullPointerException e) {
        	System.err.println("Player has left the game.");
        	for (PrintWriter writer : writers)  writer.println("MESSAGE " + nameString + " has left the game.\n");
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {}
            }
        }
  
        public void setOpponent(Player opponentPlayer) {
            this.opponentPlayer = opponentPlayer;
        }

        public void otherPlayerPlayed(int hand) {
            printWriter.println("OPPONENT_PLAYED" + hand);
        }
    }
}