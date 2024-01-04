package org.example.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Arrays;
import java.util.Random;

public class PlayerAgent extends Agent {
    Random random = new Random();
    String[] responsesBreeze = {
            "Ah, I feel a breeze here.",
            "It's a breeze here."
    };


    String[] responsesStench = {
            "Ugh, it stinks in this area.",
            "Meh, something stinks down there."
    };

    String[] responsesGlitter = {
            "Wow, I think I've found gold!",
            "Shiny! There might be something valuable here. Gold?"
    };

    String[] responsesDefault = {
            "It's so good to be safe in here.",
            "Seems quiet and safe around."
    };
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
                    inquirePosition.setContent("Hey, I need some help!");
                    send(inquirePosition);
                    state++;
                    System.out.println(getLocalName() + ": " + inquirePosition.getContent());
                    break;
                case 1:
                    // Behavior to handle received surroundings and move
                    MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
                    ACLMessage msg = receive(mt);
                    if (msg != null) {
                        String[] surroundings = msg.getContent().split(",");
                        String response = "";

                        if (Arrays.asList(surroundings).contains("breeze") && Arrays.asList(surroundings).contains("stench")) {
                            response = "Whoa, I feel a breeze and it stinks!";
                        } else if (Arrays.asList(surroundings).contains("breeze") && Arrays.asList(surroundings).contains("glitter")) {
                            response = "Ah, I feel a breeze and I think I've found gold nearby.";
                        } else if (Arrays.asList(surroundings).contains("stench") && Arrays.asList(surroundings).contains("glitter")) {
                            response = "Ugh, it stinks here, but I think I've found gold!";
                        } else if (Arrays.asList(surroundings).contains("breeze") && Arrays.asList(surroundings).contains("stench") && Arrays.asList(surroundings).contains("glitter")) {
                            response = "It's breeze, it stinks, and I've found gold!";
                        } else if (Arrays.asList(surroundings).contains("breeze")) {
                            response = responsesBreeze[random.nextInt(responsesBreeze.length)];
                        } else if (Arrays.asList(surroundings).contains("stench")) {
                            response = responsesStench[random.nextInt(responsesStench.length)];
                        } else if (Arrays.asList(surroundings).contains("glitter")) {
                            response = responsesGlitter[random.nextInt(responsesGlitter.length)];
                        } else {
                            response = responsesDefault[random.nextInt(responsesDefault.length)];
                        }

                        System.out.println(getLocalName() + ": " + response);

                        // Send the received surroundings to the navigator agent
                        ACLMessage navigateMsg = new ACLMessage(ACLMessage.CFP);
                        navigateMsg.addReceiver(new AID("NavigatorAgent", AID.ISLOCALNAME));
                        navigateMsg.setContent(response);
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

                    if (msg2!=null) {
                        String action = msg2.getContent();
                        if (action.matches("(?i)(.*\\bup\\b.*|.*\\bdown\\b.*|.*\\bleft\\b.*|.*\\bright\\b.*|.*\\brandomly\\b.*)")) {
                            ACLMessage moveMsg = new ACLMessage(ACLMessage.CFP);
                            moveMsg.addReceiver(new AID("WorldAgent", AID.ISLOCALNAME));
                            moveMsg.setContent(action);
                            send(moveMsg);
                            System.out.println(msg2.getSender().getLocalName() + ": " + action);
                            state++;
                        } else if (action.matches("(?i)(.*\\bgold\\b.*|)")) {
                            ACLMessage moveMsg = new ACLMessage(ACLMessage.CFP);
                            moveMsg.addReceiver(new AID("WorldAgent", AID.ISLOCALNAME));
                            moveMsg.setContent(action); // Informing the world about the action
                            send(moveMsg);
                            state++;
                        } }
                    else {
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
