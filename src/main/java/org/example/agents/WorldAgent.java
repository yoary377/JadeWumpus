package org.example.agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import java.util.Arrays;

public class WorldAgent extends Agent {
    private int[][] cave;
    private int playerX = 0;
    private int playerY = 3;


    protected void setup() {
        initializeCave();
        printCave();
        addBehaviour(new CheckPositionBehaviour());
        addBehaviour(new HandlePlayerMovements());

        // Start the player agent
        Object[] args = {this}; // Pass reference to this WorldAgent
        try {
            AgentController player = getContainerController().createNewAgent("PlayerAgent", PlayerAgent.class.getName(), args);
            player.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }

    private class CheckPositionBehaviour extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = receive();
            if (msg != null) {
                System.out.println(getLocalName() + " received: " + msg.getContent());

                String content = msg.getContent();
                if (content.equals("Where am I?")) {
                    String[] surroundings = checkSurroundings();
                    ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
                    reply.addReceiver(msg.getSender());
                    reply.setContent(String.join(",", surroundings));

                    // Display the sent message
                    System.out.println(getLocalName() + " sent: " + reply.getContent());
                    send(reply);
                }
            } else {
                block();
            }
        }
    }

    // Method to check surroundings and return information
    private String[] checkSurroundings() {
        // Check player's surroundings (for example, based on your cave structure)
        // For this example, assuming the player is at position (0,0)
        String[] surroundings = new String[5];
        Arrays.fill(surroundings, "none"); // Default: no indication

        // Logic to check surroundings and update the array
        // For instance, if the player is near a pit, update surroundings[0] to "breeze"
        // Implement your specific logic here based on the game rules
        // This is a sample representation:
        if (playerIsNearPit()) {
            surroundings[0] = "breeze";
        }
        if (playerIsNearWumpus()) {
            surroundings[1] = "smell";
        }
        if (playerIsNearGold()) {
            surroundings[2] = "glitter";
        }
        return surroundings;
    }

    // Sample methods for proximity checks (replace with your actual logic)
    private boolean playerIsNearPit() {
        // Sample logic to check if the player is near a pit
        // For example, if the player is at (0,0) and a pit is at (0,1)
        return getPlayerX() == 0 && getPlayerY() == 0;
    }

    private boolean playerIsNearWumpus() {
        // Sample logic to check if the player is near the wumpus
        // For example, if the player is at (1,1) and the wumpus is at (2,1)
        return getPlayerX() == 1 && getPlayerY() == 1;
    }

    private boolean playerIsNearGold() {
        // Sample logic to check if the player is near the gold
        // For example, if the player is at (1,1) and the gold is at (2,2)
        return getPlayerX() == 1 && getPlayerY() == 1;
    }

    // Sample methods to get player position (replace with your actual player tracking logic)
    protected int getPlayerX() {
        // Return player's X position
        // Implement your actual player position tracking logic here
        return playerX; // Sample: player X position is 0
    }

    protected int getPlayerY() {
        // Return player's Y position
        // Implement your actual player position tracking logic here
        return playerY; // Sample: player Y position is 0
    }

    // Set player's position in the cave
    public void setPlayerPosition(String playerName, int x, int y) {
        // Logic to set player's position in the cave
        // For instance, update cave[x][y] to represent the player
        // Here, you could use a specific value (e.g., 3) to represent the player
        playerY = y;
        playerX = x;
    }

    // Initialize the 4x4 cave with default values
    private void initializeCave() {
        cave = new int[4][4];
        // Fill the cave with default values
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                cave[i][j] = 0; // Empty cell by default
            }
        }
        // Add pits, gold, wumpus, and player
        cave[0][3] = 1; // Pit at position (0,3)
        cave[1][3] = 1; // Pit at position (1,3)
        cave[2][0] = 1; // Pit at position (2,0)
        cave[3][2] = 1; // Pit at position (3,2)
        cave[0][0] = 2; // Wumpus at position (0,0)
        cave[playerY][playerX] = 3; // Player at position (0,1)
        cave[2][1] = 4; // Gold at position (2,1)
    }

    // Print the cave structure (for demonstration purposes)
    private void printCave() {
        System.out.println("Cave Structure:");
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (i == playerY && j == playerX) {
                    System.out.print("A "); // Player
                } else if (cave[i][j] == 1) {
                    System.out.print("P "); // Pit
                } else if (cave[i][j] == 2) {
                    System.out.print("W "); // Wumpus
                } else if (cave[i][j] == 4) {
                    System.out.print("G "); // Gold
                } else {
                    System.out.print("- "); // Empty cell
                }
            }
            System.out.println();
        }
    }


    private class HandlePlayerMovements extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = receive(mt);

            if (msg != null && msg.getContent().equals("Move up")) {
                System.out.println(playerY);
                // Move the player coordinates up if it's within the bounds of the cave
                if (playerY > 0) {
                    playerY--;
                    System.out.println("Player moved up to coordinates: (" + playerX + "," + playerY + ")");
                } else {
                    System.out.println("Player cannot move up. Already at the top.");
                }
                printCave();
            } else {
                // Handle other instructions or situations as needed
            }
        }
    }
}