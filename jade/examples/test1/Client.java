package test1;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;

import java.io.IOException;

import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class Client extends Agent {

    protected void setup() {
        // Search for the ServerAgent in the DF
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("calculator-server"); // Change service type
        //sd.setType("multiples-server");
        //sd.setType("factor-server");
        //sd.setType("ppcm-server");
        //sd.setType("prime-number-server");
        //sd.setType("divisibility-server");
        
        
        template.addServices(sd);

        try {
            DFAgentDescription[] result = DFService.search(this, template);
            if (result.length > 0) {
                // ServerAgent found, send a message to it
                AID serverAID = result[0].getName();
                Object[] args = getArguments(); // Example arguments
                sendRequestToServer(serverAID, args);
            } else {
                System.err.println("ServerAgent not found.");
                doDelete(); // Terminate the agent if the server is not found
            }
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }

    private void sendRequestToServer(AID serverAID, Object[] args) {
        // Send a message to the ServerAgent
        addBehaviour(new RequestOperationBehaviour(serverAID, args));
    }

    private class RequestOperationBehaviour extends OneShotBehaviour {
        private AID serverAID;
        private Object[] arguments;

        RequestOperationBehaviour(AID serverAID, Object[] args) {
            this.serverAID = serverAID;
            this.arguments = args;
        }

        @Override
        public void action() {
            // Send a request message to the ServerAgent
            try {
                myAgent.send(createRequestMessage(serverAID, arguments));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private jade.lang.acl.ACLMessage createRequestMessage(AID receiver, Object[] args) throws IOException {
            jade.lang.acl.ACLMessage message = new jade.lang.acl.ACLMessage(jade.lang.acl.ACLMessage.REQUEST);
            message.addReceiver(receiver);
            message.setContentObject(args);
            return message;
        }
    }
}
