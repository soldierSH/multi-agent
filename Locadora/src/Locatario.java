import java.util.Random;
import java.util.Scanner;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage; 

//java -cp "jade.jar:." jade.Boot -gui -container locatario1:Locatario
//Agente solicitante do carro
public class Locatario extends Agent{
	Random gerador = new Random();
	private Integer timer= gerador.nextInt(5000)+3000;
	protected void setup() {
		//1 para gerar dados aleatorios
		int op = 1;
		if(op == 1) {
			Random gerador = new Random();
			addBehaviour(new procurarCarro(this, timer, gerador.nextInt(3)+1));
		}
		else {
			System.out.print("|-->Escolha modelo do carro: ");
			Scanner scan = new Scanner(System.in);
			addBehaviour(new procurarCarro(this, timer,scan.nextInt()));
		}
		
	}
	
	private class procurarCarro extends TickerBehaviour{

		private Integer TipoCarro;
		private Integer etapa = 1;

		public procurarCarro(Agent agent, long tempo, Integer TipoCarro) {
			super(agent, tempo);
			this.TipoCarro = TipoCarro;
		}

		@Override
		protected void onTick() {
			
			ACLMessage mensagem = new ACLMessage(ACLMessage.REQUEST);
			if(TipoCarros.searchString(TipoCarro) == "null") {
				System.out.println("|-->Operação será encerrada");
				doDelete();
			}
			else {
				switch(etapa) {
				case 1:
					//envia solicitação para alugar carro
					mensagem.setProtocol(TipoCarros.searchString(TipoCarro));
					System.out.println("|-->"+myAgent.getLocalName()+ " solicitando "
					+TipoCarros.searchString(TipoCarro)+" a cada "+timer+" ms");
					mensagem.addReceiver(new AID("central", AID.ISLOCALNAME));
					myAgent.send(mensagem);
					etapa = 2;
					break;
				case 2:
					//recebe resposta de central
					ACLMessage respostaCentral = myAgent.receive();
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if(respostaCentral != null) {
						if(!respostaCentral.getProtocol().equals(Mensagens.SEM_CARROS.getMensagem())) {
							System.out.println("|-->"+myAgent.getLocalName() 
							+ " alugou "+TipoCarros.searchString(TipoCarro)+" de "
							+ respostaCentral.getContent()+" por R$"+respostaCentral.getProtocol());
							//após o tempo de aluguel do carro, o agente encerra
							try {
								timer(30000);
								System.out.println("|-->Carro devolvido");
								//encerrando agente
								doDelete();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}
						else {
							System.out.println("|-->Sem carros - aguardando...");
							etapa = 1;
						}
					}
					else {
						System.out.println("|-->Aguardando...");
						etapa = 1;
					}
				}
			}
		}
		
	}
	private void timer(final Integer t) throws InterruptedException {
		Thread.sleep(t);
	}
	protected void takeDown() {
	    System.out.println("Agent "+getAID().getName()+" Encerrando...");
	}

}
