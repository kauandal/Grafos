# Declaração de uso de inteligência artificial

UNIVALI
Disciplina Grafos T1, professora Fernanda Cunha
Trabalho M1

Kauan Rocha Dalfovo
Gustavo Odilon

Data: 21/09/2026

## Declaração

Declaramos, em atendimento ao plano de ensino da disciplina e ao regimento institucional, que
utilizamos ferramenta de inteligência artificial no desenvolvimento deste trabalho.

Ferramenta utilizada: Claude (Anthropic), por meio do Claude Code.

## O que foi feito pela dupla

- A modelagem e a lógica do grafo: as classes `Vertice`, `Aresta` e `Grafo`, e a decisão de
  representar o grafo por lista de adjacência, com cada vértice guardando as arestas
  incidentes sobre ele.
- As operações sobre a estrutura: inserir vértice isolado, inserir aresta/arco, remover
  vértice com as suas ligações, remover aresta/arco, e as consultas de adjacência, peso e
  extremidades.
- O tratamento das diferenças entre grafo dirigido e não dirigido na estrutura, incluindo o
  registro da aresta nas duas extremidades e o caso do laço.
- A definição dos requisitos, do escopo e das decisões de projeto passadas à ferramenta.
- A revisão, a execução e a conferência do programa em relação a cada item do enunciado, e a
  verificação dos resultados dos algoritmos nos grafos de teste.
- O estudo do código entregue, de forma a responder pelos algoritmos, pela representação
  escolhida e pelas decisões de implementação durante a defesa.

## O que foi produzido com a ferramenta

- Toda a parte visual: o desenho do grafo, das árvores e das componentes, na classe
  `PainelGrafo`, e a janela com moldura e menu, na classe `TelaPrincipal`.
- Auxílio e refatoração da implementação dos algoritmos mais complexos: Prim para a árvore
  geradora mínima, a busca em profundidade guiada e o algoritmo de Roy para as componentes
  conexas e fortemente conexas.
- Refatorações pontuais do código que já havíamos escrito, como a separação das classes em
  arquivos próprios, a padronização do tratamento de erro e a simplificação de trechos
  discutidos ao longo do desenvolvimento.
- Grande parte dos comentários do código e a documentação de apoio do projeto.