# Declaração de uso de inteligência artificial

UNIVALI
Disciplina Grafos T2, professora Fernanda Cunha
Trabalho T2, algoritmos DSATUR e A*

Kauan Rocha Dalfovo
Gustavo Odilon

Data: 19/10/2026

## 1. Ferramenta utilizada

Claude (Anthropic), utilizado por meio do Claude Code.

## 2. Finalidade, o que a ferramenta ajudou a fazer

- Correção e reestruturação da fórmula de Haversine que havíamos escrito na
  classe `Vertice`, incluindo a passagem da coordenada para atributo do
  vértice, no lugar da leitura a partir do identificador.
- Reestruturação do algoritmo A*, com a lista aberta em fila de prioridade
  por f(n) igual a g(n) mais h(n), a lista fechada e a reconstrução do
  caminho.
- Visualização: destaque das arestas do caminho mínimo, coloração dos
  vértices após o DSATUR, paleta de cores e posicionamento dos rótulos.
- Baterias de teste usadas para validar os algoritmos, entre elas a
  comparação do A* contra uma implementação independente de Dijkstra em
  todos os pares de cidades do mapa, e a conferência do DSATUR contra
  grafos de número cromático conhecido.
- Comentários do código e documentação de apoio do projeto.

## 3. Etapas do trabalho em que foi utilizada

| Etapa | Uso da ferramenta |
|---|---|
| Planejamento | levantamento do que o enunciado exige e do esforço de cada item |
| Modelagem do vértice com coordenada | correção e reestruturação do código que escrevemos |
| Interface e visualização | produção do código |
| Persistência em arquivo | produção do código |
| Testes e validação | produção das baterias de teste |
| Documentação | produção dos comentários e documentos |

## 4. Autoria intelectual e decisões da dupla

Conforme o plano de ensino, a ferramenta foi usada como recurso complementar
e não substitui a nossa autoria. Coube à dupla:

- A implementação inicial dos algoritmos, escritos por nós e depois corrigidos com a ferramenta.
- A pesquisa das coordenadas geográficas das 12 cidades do mapa, consultadas
  no IBGE, Estrutura Territorial, Localidades, em
  https://www.ibge.gov.br/geociencias/organizacao-do-territorio/estrutura-territorial/27385-localidades.html
- A definição do escopo do trabalho, incluindo a interpretação dos requisitos
  do enunciado e a decisão de quais itens entram e quais não entram.
- Decisões de projeto sobre estruturas de dados, como a substituição do
  conjunto pela lista fechada no A*.
- Decisões sobre a apresentação, como a posição dos rótulos no desenho.
- A base do projeto, reaproveitada do T1, cuja autoria está declarada no
  documento correspondente daquela avaliação.
- A revisão, a execução e a conferência do programa em relação a cada item
  do enunciado.
- O estudo do código entregue, de forma a responder pelos algoritmos e pelas
  decisões de implementação durante a defesa.

## 5. Observação

Esta declaração é apresentada espontaneamente, antes da avaliação.
Permanecemos à disposição para esclarecer qualquer ponto do código durante a
defesa.

Kauan Rocha Dalfovo

Gustavo Odilon
