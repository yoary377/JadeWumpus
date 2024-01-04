package org.example.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class NavigatorAgent extends Agent {

    Player player = new Player();
    Tile[][] worldGrid = new Tile[4][4];
    WorldAgent worldAgent = new WorldAgent();
    protected void setup() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                worldGrid[i][j] = new Tile();
            }
        }
        addBehaviour(new GuidePlayerBehaviour());
        // Initialize a grid representing the world (4x4 in this example)

    }

    private void updateNearbyTiles(int currentX, int currentY, String[] surroundings) {
        // Update the current tile as visited
        worldGrid[currentX][currentY].setVisited(true);

        // Update nearby tiles based on the perceived surroundings
        // Assuming the agent perceives surroundings in North, East, South, West directions
        // Check for breeze perception (only in the 0th element)
        if (surroundings[0].equals("breeze")||surroundings[1].equals("stench")) {
            // Mark nearby tiles as unsafe (potential pits)
            if (currentY > 0) {
                worldGrid[currentX][currentY-1].setSafe(false);
                System.out.println("SET UNSAFE");// North
            }
            if (currentY < 3) {
                System.out.println(worldGrid[currentX][currentY]);
                worldGrid[currentX][currentY+1].setSafe(false); // South
            }
            if (currentX < 3) {
                System.out.println(currentY);
                worldGrid[currentX+1][currentY].setSafe(false); // East
            }
            if (currentX > 0) {
                System.out.println(worldGrid[currentX][currentY]);
                worldGrid[currentX-1][currentY].setSafe(false); // West
            }
        }
    }
    private class GuidePlayerBehaviour extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = receive();
            if (msg != null && msg.getSender().getLocalName().equals("PlayerAgent") && msg.getContent().length()>2) {
                String[] surroundings = msg.getContent().split(",");
                updateNearbyTiles(worldAgent.getPlayerX(), worldAgent.getPlayerY(), surroundings);

                String nextMove = decideNextMove(worldAgent.getPlayerX(), worldAgent.getPlayerY());
                ACLMessage moveMsg = new ACLMessage(ACLMessage.PROPOSE);
                moveMsg.addReceiver(new AID("PlayerAgent", AID.ISLOCALNAME));
                moveMsg.setContent(nextMove);
                send(moveMsg);

            } else {
                block();
            }
        }

        private String decideNextMove(int currentX, int currentY) {
            // Logic to check nearby safe and unvisited tiles
            if (currentY > 0 && !worldGrid[currentX][currentY - 1].isVisited() && worldGrid[currentX][currentY - 1].isSafe()) {
                player.storeMovement("Move up"); // Store the movement
                return "Move up";
            }
            if (currentY < 3 && !worldGrid[currentX][currentY + 1].isVisited() && worldGrid[currentX][currentY + 1].isSafe()) {
                player.storeMovement("Move down"); // Store the movement
                return "Move down";
            }
            if (currentX < 3 && !worldGrid[currentX + 1][currentY].isVisited() && worldGrid[currentX + 1][currentY].isSafe()) {
                player.storeMovement("Move right"); // Store the movement
                return "Move right";
            }
            if (currentX > 0 && !worldGrid[currentX - 1][currentY].isVisited() && worldGrid[currentX - 1][currentY].isSafe()) {
                player.storeMovement("Move left"); // Store the movement
                return "Move left";
            }

            // Backtracking logic if no safe unvisited tiles
            String lastMove = player.getLastMovement(); // Retrieve the last stored movement
            if (lastMove != null) {
                player.backtrack(); // Backtrack by removing the last movement

                // Determine the opposite movement for backtracking
                switch (lastMove) {
                    case "Move up":
                        return "Move down";
                    case "Move down":
                        return "Move up";
                    case "Move right":
                        return "Move left";
                    case "Move left":
                        return "Move right"; default: return "Move randomly";
                }
            }

            // Default action if no specific move is determined
            return "Move randomly";
        }
    }
}