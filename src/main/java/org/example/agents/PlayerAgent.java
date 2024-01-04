package org.example.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Arrays;

public class PlayerAgent extends Agent {
    protected void setup() {
        addBehaviour(new SequentialBehaviour());
        // Assign a unique name to the player agent
        String playerName = "PlayerAgent"; // You can generate a unique name here

        // Get the reference to the WorldAgent
        Object[] args = getArguments();
        if (args != null && args.length > 0 && args[0] instanceof WorldAgent) {
            WorldAgent world = (WorldAgent) args[0];
            // Set the player's position in the world
            world.setPlayerPosition(playerName, 0, 3);
            // Print player's position
        } else {
            System.out.println("No reference to WorldAgent found.");
            doDelete();
        }
    }

    private class SequentialBehaviour extends CyclicBehaviour {
        private int state = 0;
        private boolean receivedMoveUp = false;


        public void action() {
            switch (state) {
                case 0:
                    // Behavior to ask for position
                    ACLMessage inquirePosition = new ACLMessage(ACLMessage.REQUEST);
                    inquirePosition.addReceiver(new AID("WorldAgent", AID.ISLOCALNAME));
                    inquirePosition.setContent("Where am I?");
                    send(inquirePosition);
                    state++;
                    break;
                case 1:
                    // Behavior to handle received surroundings and move
                    MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
                    ACLMessage msg = receive(mt);
                    if (msg != null) {
                        String[] surroundings = msg.getContent().split(",");
                        System.out.println(getLocalName() + " received surroundings: " + Arrays.toString(surroundings));

                        // Send the received surroundings to the navigator agent
                        ACLMessage navigateMsg = new ACLMessage(ACLMessage.CFP);
                        navigateMsg.addReceiver(new AID("NavigatorAgent", AID.ISLOCALNAME));
                        navigateMsg.setContent(msg.getContent());
                        send(navigateMsg);
                        state++;
                    } else {
                        block();
                    }
                    break;
                case 2:
                    // Behavior to handle the response from Navigator
                    MessageTemplate mt2 = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
                    ACLMessage msg2 = receive(mt2);
                    if (msg2 != null && msg2.getContent().equals("Move up")) {
                        ACLMessage moveMsg = new ACLMessage(ACLMessage.CFP);
                        moveMsg.addReceiver(new AID("WorldAgent", AID.ISLOCALNAME));
                        moveMsg.setContent("Move up"); // Informing the world about moving up
                        send(moveMsg);
                        state++;
                        break;

                    }
                    if (msg2 != null && msg2.getContent().equals("Move down")) {
                        ACLMessage moveMsg = new ACLMessage(ACLMessage.CFP);
                        moveMsg.addReceiver(new AID("WorldAgent", AID.ISLOCALNAME));
                        moveMsg.setContent("Move down"); // Informing the world about moving up
                        send(moveMsg);
                        state++;
                        break;

                    }
                    if (msg2 != null && msg2.getContent().equals("Move right")) {
                        ACLMessage moveMsg = new ACLMessage(ACLMessage.CFP);
                        moveMsg.addReceiver(new AID("WorldAgent", AID.ISLOCALNAME));
                        moveMsg.setContent("Move right"); // Informing the world about moving up
                        send(moveMsg);
                        state++;
                        break;

                    }
                    if (msg2 != null && msg2.getContent().equals("Move left")) {
                        ACLMessage moveMsg = new ACLMessage(ACLMessage.CFP);
                        moveMsg.addReceiver(new AID("WorldAgent", AID.ISLOCALNAME));
                        moveMsg.setContent("Move left"); // Informing the world about moving up
                        send(moveMsg);
                        state++;
                        break;
                    }
                    if (msg2 != null && msg2.getContent().equals("Move randomly")) {
                        ACLMessage moveMsg = new ACLMessage(ACLMessage.CFP);
                        moveMsg.addReceiver(new AID("WorldAgent", AID.ISLOCALNAME));
                        moveMsg.setContent("Move randomly"); // Informing the world about moving up
                        send(moveMsg);
                        state++;
                        break;

                    } else {
                        block();
                    }

                case 3:
                    // Behavior to inform the world about intended movement direction
                    MessageTemplate mt3 = MessageTemplate.MatchPerformative(ACLMessage.CONFIRM);
                    ACLMessage msg3 = receive(mt3);
                    if (msg3 != null && msg3.getContent().equals("OK")) {
                        state = 0;
                        break;
                    } else {
                        block();
                    }
            }
        }
    }
}
