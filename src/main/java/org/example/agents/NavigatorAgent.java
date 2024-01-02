package org.example.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class NavigatorAgent extends Agent {
    protected void setup() {
        addBehaviour(new GuidePlayerBehaviour());
        System.out.println("AGENT SMITH");
    }

    private class GuidePlayerBehaviour extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = receive();
            if (msg != null && msg.getSender().getLocalName().equals("PlayerAgent")) {
                String[] surroundings = msg.getContent().split(",");
                // Logic to decide the player movement based on surroundings
                // For now, let's assume the navigator tells the player to move up
                ACLMessage moveMsg = new ACLMessage(ACLMessage.INFORM);
                moveMsg.addReceiver(new AID("PlayerAgent", AID.ISLOCALNAME));
                moveMsg.setContent("Move up");
                send(moveMsg);
            } else {
                block();
            }
        }
    }
}