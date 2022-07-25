# Projeto Multiagentes

Projeto feito para a avaliação da disciplina optativa de Tópicos Especiais em IA. UFPA - Ciência da Computação.
## Descrição
O projeto consiste em um sistema para alugar carros. Três agentes fazem parte do sistema, são eles:

- ***Central***: agente responsável pela comunicação entre os outros dois agentes, ele determina qual carro do modelo pedido pelo locador será alugado, isso feito com base no valor do aluguel, o agente central sempre escolhe o carro com menor preço. Uma vez instaciado, o agente central permanece em execução.
- ***Locatario***: agente que solicita o aluguel de um carro, ele envia o modelo do carro que quer alugar e aguarda a resposta com o valor do aluguel. Após ser instanciado, o agente locador permanece tentando alugar o carro fazendo solicitação passado um certo período, após esse evento acontecer e o aluguel terminar o agente é encerrado.
- ***Locador***: agente que aluga o carro, ele recebe a solicitação do agente central e verifica se o modelo do carro pedido é correspondente ao seu. Caso corresponda, ele responde com o valor do aluguel e aguarda enquanto não é escolhido ou solicitado. O agente locador, assim com o central, permanece em atividade.
