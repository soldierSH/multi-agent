
public enum TipoCarros {
	SEDAN("Sedan",1),
	SUV("SUV",2),
	PICAPE("Picape",3);
		
		protected final String tipocarro;
		protected final Integer cod;
		
		TipoCarros(String tipocarro, Integer cod) {
			this.tipocarro = tipocarro;
			this.cod = cod;
		}
		
		public static String searchString(Integer cod) {
			for(TipoCarros t: TipoCarros.values()) {
				if(t.getCodigo() == cod) {
					return t.getString();
				}
			}
			return "null";
		}
		public static Integer searchInteger(String mensagem) {
			for(TipoCarros type: TipoCarros.values()) {
				if (type.getString().contentEquals(mensagem)) {
					return type.getCodigo();
				}
			}
			return 0;
		}
		
		//get
		public String getString() {
			return tipocarro;
		}
		public Integer getCodigo() {
			return cod;
		}
}
