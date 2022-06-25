//java -cp "jade.jar:." jade.Boot -gui -agents central:Central

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class Central extends Agent{
	protected void setup() {
		System.out.println("------------------");
		System.out.println("|-->Bem-vindo(a) à Locadora LO-CAR\n"
		+"|-->Maior locadora de carros da América Latina e arredores\n"
		+"|-->Agente Central em execução\n");
		System.out.println("------------------");
		addBehaviour(new mediador(this));
	}
	
	//comportamento cíclico para intermediar locação
	private class mediador extends CyclicBehaviour{
		public mediador(Agent myAgent) {
			this.myAgent = myAgent;
		}
		@Override
		public void action() {
			try {
			//Central espera mensagem
			ACLMessage mensagem = myAgent.receive();
			if(TipoCarros.searchInteger(mensagem.getProtocol()) == 1 || 
					TipoCarros.searchInteger(mensagem.getProtocol()) == 2|| 
					TipoCarros.searchInteger(mensagem.getProtocol()) == 3) 
			{
				ProcurarLocadores(myAgent, mensagem);
			}
			
			
			}catch(Exception e) {
				block();
			}
		}
	}
	//central procura agentes com carros disponíveis para aluguel
	public void ProcurarLocadores(Agent myAgent, ACLMessage mensagem) throws FIPAException, InterruptedException {
		//registro dos tipos de carros disponíveis nas páginas amarelas
		DFAgentDescription pageDFD = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(TipoCarros.PICAPE.getString()); pageDFD.addServices(sd);
		sd.setType(TipoCarros.SEDAN.getString()); pageDFD.addServices(sd);
		sd.setType(TipoCarros.SUV.getString()); pageDFD.addServices(sd);
		
		System.out.println("------------------");
		System.out.println("|-->Solicitação de " + mensagem.getSender().getLocalName()+" recebida.");
		timer(1000);
		
		//envio de mensagem para verificação de carros disponíveis
		DFAgentDescription [] resultado = DFService.search(myAgent, pageDFD);
		ACLMessage buscarCarro = new ACLMessage(ACLMessage.REQUEST);
		//setando mensagem
		buscarCarro.setProtocol(mensagem.getProtocol());
		System.out.println("|-->Procurando locador para " + mensagem.getSender().getLocalName());
		timer(2000);
		for(DFAgentDescription r: resultado) {
			buscarCarro.addReceiver(r.getName());
		}
		//enviando mensagem
		myAgent.send(buscarCarro);
		
		int cont = 0;
		Double valor = 600.0;//valor máximo para aluguel
		ACLMessage respostaLocatario = mensagem.createReply();//resposta que será dada ao Locatario
		ACLMessage respostaLocador = new ACLMessage(ACLMessage.INFORM);//resposta que será dada ao Locatario
		while(cont < resultado.length) {
			//esperando resposta dos locadores
			ACLMessage mensagemLocador = myAgent.receive();
			if(mensagemLocador != null) {
				cont++;
				//verifica-se a cada iteração qual agente tem o menor valor
				if(!mensagemLocador.getProtocol().equals(Mensagens.STATUS_INDISPONIVEL.getMensagem())) {
					if(Double.parseDouble(mensagemLocador.getProtocol()) < valor) {
						valor = Double.parseDouble(mensagemLocador.getProtocol());
						respostaLocatario.setProtocol(valor.toString());
						respostaLocatario.setContent(mensagemLocador.getSender().getLocalName());
						
						respostaLocador.addReceiver(mensagemLocador.getSender());
						respostaLocador.setProtocol(Mensagens.ALUGAR.getMensagem());
						respostaLocador.setContent(mensagem.getSender().getLocalName());
					}
					//replyBarco.setProtocol(ProtocolMessagesPortoAgents.DOCA_PARA_ATRACAGEM.toString());
					//replyBarco.setContent(((AID)docas.get(0)).getLocalName());
				}
			}else {
				respostaLocatario.setProtocol(Mensagens.SEM_CARROS.getMensagem());
			}
		}
		timer(1000);
		if(cont == 0) {
			respostaLocatario.setProtocol(Mensagens.SEM_CARROS.getMensagem());
			System.out.println("|-->Não há carros para alugar");
		}
		if(!respostaLocatario.getProtocol().equals(Mensagens.SEM_CARROS.getMensagem())) {
			System.out.println("|-->Locador "+respostaLocatario.getContent()+" escolhido para Locatário "+
		respostaLocador.getContent());
			
		}
		myAgent.send(respostaLocador);
		myAgent.send(respostaLocatario);
		
		
	}
	private void timer(final Integer t) throws InterruptedException {
		Thread.sleep(t);
	}
	private void block() {
		// TODO Auto-generated method stub
		
	}
}
