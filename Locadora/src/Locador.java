import java.util.Random;
import java.util.Scanner;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

//java -cp "jade.jar:." jade.Boot -gui -container locador1:Locador

//Agent que aluga o carro
public class Locador extends Agent{
	private Boolean disponivel = true;
	Random gerador = new Random();
	public Float valor = 1 + gerador.nextFloat()*(600-1);
	protected void setup() {
		int op = 0;
		int type = 0;
		//1 para gerar dados aleatorios
		if(op == 1) {
			Random gerador = new Random();
			type = gerador.nextInt(3)+1;
		}
		else {
			System.out.print("|-->Modelo do carro: ");
			Scanner scan = new Scanner(System.in);
			type = scan.nextInt();
			if(TipoCarros.searchString(type) == "null") {
				System.out.println("|-->Operação será encerrada");
				doDelete();
			}
		}
		System.out.println("|-->"+this.getAID().getLocalName()
		+" disponibiliza "+TipoCarros.searchString(type)+" para alugar "+"no valor de R$"+valor);
		ServiceDescription sd = new ServiceDescription();
		sd.setType(TipoCarros.PICAPE.getString()); sd.setName(TipoCarros.PICAPE.getString());
		sd.setType(TipoCarros.SEDAN.getString()); sd.setName(TipoCarros.SEDAN.getString());
		sd.setType(TipoCarros.SUV.getString()); sd.setName(TipoCarros.SUV.getString());
		
		DFAgentDescription pageDFD = new DFAgentDescription();
		pageDFD.setName(getAID());
		pageDFD.addServices(sd);
		
		try {
			DFService.register(this, pageDFD);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		
		addBehaviour(new Alugar(this,type));
		
		
	}
	
	private class Alugar extends CyclicBehaviour{
		
		private Integer TipoCarro;
		public Alugar(Agent myAgent, Integer TipoCarro) {
			myAgent = myAgent;
			this.TipoCarro = TipoCarro;
		}

		@Override
		public void action() {
			try {
				//locador recebe mensagem de central e verifica as opções válidas
				ACLMessage mensagem = myAgent.receive();
				ACLMessage resposta = mensagem.createReply();
				if(mensagem.getProtocol().equals(TipoCarros.searchString(TipoCarro))) {
					if(disponivel == true) {
						//se o carro estiver disponível o agente locador envia o preço do aluguel para o agente central
						System.out.println("|-->Carro de "+myAgent.getAID().getLocalName()+" está disponível");
						resposta.setProtocol(valor.toString());
					}
				}
				//caso receba mensagem para solicitação de aluguel o status dele muda e ele fica indisponivel para aluguel por 20s
				else if(mensagem.getProtocol().equals(Mensagens.ALUGAR.getMensagem())){
					System.out.println("|-->"+mensagem.getContent()+" alugou "
				+TipoCarros.searchString(TipoCarro)+" de "+myAgent.getLocalName()+" por R$"+valor);
					disponivel = false;
					timer(30000);
					System.out.println("|-->Carro devolvido");
					disponivel = true;
				}
				/*se a mensagem corresponder a um carro 
				 * que não seja do modelo deste agente, 
				 * ele envia uma resposta de que está indisponivel
				*/
				else if((!mensagem.getProtocol().equals(TipoCarros.searchString(TipoCarro)))){
					System.out.println("|-->Carro de "+myAgent.getAID().getLocalName()+" está indisponível");
					resposta.setProtocol(Mensagens.STATUS_INDISPONIVEL.getMensagem());
				}
				myAgent.send(resposta);
			}
			catch(Exception e) {
				
			}
		}
	}
	private void timer(final Integer t) throws InterruptedException {
		Thread.sleep(t);
	}
	public Boolean getDisponivel() {
		return disponivel;
	}

	public void setDisponivel(Boolean disponivel) {
		this.disponivel = disponivel;
	}
	public Float getValor() {
		return valor;
	}

	public void setDisponivel(Float valor) {
		this.valor = valor;
	}
	protected void takeDown() {
	    // Printout a dismissal message
	    System.out.println("|-->Agent "+getAID().getName()+" terminando..");
	}
}
