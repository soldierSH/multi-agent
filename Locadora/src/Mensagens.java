
public enum Mensagens {
	CARRO_DISPONIVEL("Carro disponível para alugar?",1),
	STATUS_INDISPONIVEL("Carro não está disponível",2),
	CARROS_DISPONIVEIS("Segue a lista de carros disponíveis",3),
	SEM_CARROS("Sem carros disponíveis para este modelo no momento",4),
	ALUGAR("Carro selecionado para aluguel",5),
	DEVOLVENDO_CARRO("Tempo do aluguel terminou, carro foi devolvido",6);
	
	protected final String mensagem;
	protected final Integer numero;
	
	Mensagens(String mensagem, Integer numero) {
		this.mensagem = mensagem;
		this.numero = numero;
	}
	
	public static Integer search(String mensagem) {
		for(Mensagens m : Mensagens.values()) {
			if (m.getMensagem().contentEquals(mensagem)) {
				return m.getNumero();
			}
		}
		return 0;
	}
	public String getMensagem() {
		return mensagem;
	}
	public Integer getNumero() {
		return numero;
	}
}
