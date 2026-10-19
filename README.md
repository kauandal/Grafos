# Teoria dos Grafos

Kauan Rocha Dalfovo
Gustavo Odilon


## Como executar

Abrir a pasta `Grafos` no IntelliJ e executar a classe `Main` (`src/Main.java`).

Pela linha de comando, a partir desta pasta:

```bash
java -jar Grafos.jar
```

Para compilar do fonte, a partir de `Grafos/src`:

```bash
javac -encoding UTF-8 -d ../out *.java && java -cp ../out Main
```

Requer Java 11 ou superior. O `Grafos.jar` entregue foi compilado para essa versão.

## Estrutura

| Arquivo | Conteúdo |
|---|---|
| `Vertice.java` | Vértice com identificador, lista de adjacência e coordenada geográfica |
| `Aresta.java` | Aresta/arco com identificador, extremidades e peso |
| `Grafo.java` | Grafo em lista de adjacência, operações e todos os algoritmos |
| `PainelGrafo.java` | Desenho do grafo, das árvores, do caminho e das cores |
| `TelaPrincipal.java` | Janela com moldura, menu e visualização do grafo ativo |
| `ArquivoGrafo.java` | Leitura e gravação de grafo em arquivo de texto |
| `Main.java` | Ponto de entrada |

## Representação

Lista de adjacência. Cada `Vertice` guarda a lista das arestas/arcos incidentes sobre ele.
O `Grafo` mantém ainda a lista geral de arestas, usada para busca por identificador e pelo
algoritmo de Roy. O atributo `dirigido` define se o par (v, w) é ordenado (arco) ou não
ordenado (aresta).

Em grafo não dirigido a aresta é registrada na lista dos dois extremos. Em grafo dirigido o
arco é registrado apenas na lista do vértice de origem. O laço é registrado uma única vez.

O vértice pode guardar latitude e longitude. Sem elas o grafo funciona normalmente; com elas,
o A* passa a dispor de heurística.

## Operações e algoritmos

| Item | Implementação | Menu |
|---|---|---|
| DSATUR, coloração do grafo | `Grafo.dsatur` | Algoritmos |
| A*, caminho mínimo | `Grafo.aEstrela` | Algoritmos |
| h(n) por Haversine | `Vertice.distanciaHaversine` | exibida na saída do A* |
| Tabela h(n) em relação ao destino | `Grafo.heuristicas` | saída do A* |
| Grafo original e grafo colorido | `PainelGrafo` | Exibição, Limpar destaques |
| Caminho mínimo em destaque | `PainelGrafo` | aplicado pelo A* |
| Mapa do Paraná | `TelaPrincipal.carregarMapaParana` | Grafo |
| Testar vários grafos por arquivo | `ArquivoGrafo` | Grafo |

| Inserir vértice isolado | `Grafo.inserirVertice` | Operações |
| Inserir aresta/arco | `Grafo.inserirAresta` | Operações |
| Remover vértice e suas ligações | `Grafo.removerVertice` | Operações |
| Remover aresta/arco | `Grafo.removerAresta` | Operações |
| Verificar adjacência | `Grafo.saoAdjacentes` | Operações |
| Valor da aresta/arco | `Grafo.retornarValorAresta` | Operações |
| Extremidades da aresta/arco | `Grafo.formatarExtremidades` | Operações |
| Prim e custo da AGM | `Grafo.prim` | Algoritmos |
| Busca em profundidade guiada | `Grafo.buscaEmProfundidade` | Algoritmos |
| Roy, componentes conexas e fortemente conexas | `Grafo.royComponentes` | Algoritmos |

## Algoritmo A*

Cada vértice é avaliado por f(n) igual a g(n) mais h(n). O g(n) é o custo real percorrido
desde a origem, somando os pesos das arestas, que no mapa do Paraná são quilômetros de
estrada. O h(n) é a distância em linha reta até o destino, pela fórmula de Haversine sobre
as coordenadas geográficas.

A lista aberta é uma fila de prioridade ordenada por f(n) e a lista fechada é a lista dos
vértices já resolvidos, que também registra a ordem de expansão mostrada na tela.

Como a distância em linha reta nunca é maior que a distância por estrada, h(n) jamais
superestima o que falta. A heurística é admissível e o A* devolve o caminho de menor custo.
Em grafo sem coordenadas h(n) vale zero e o algoritmo se comporta como o de Dijkstra.

A tabela h(n) é montada em relação à cidade de destino escolhida e aparece na área de saída
a cada execução.

## Algoritmo DSATUR

A cada passo escolhe o vértice ainda não colorido com maior grau de saturação, que é o número
de cores distintas já presentes entre os seus vizinhos, desempata pelo maior grau dentro do
subgrafo ainda não colorido, e atribui a menor cor que nenhum vizinho esteja usando.

A coloração ignora o sentido dos arcos, porque dois vértices ligados precisam de cores
diferentes em qualquer direção, e ignora laços, já que um vértice não pode ter cor diferente
de si mesmo.

DSATUR é heurística: não garante o número cromático mínimo em qualquer grafo, mas é exato
para grafos bipartidos e acerta nos casos pequenos. Após colorir, o programa confere e
informa na tela se a coloração é própria, isto é, se nenhuma aresta liga dois vértices da
mesma cor.

## Coordenadas geográficas

As latitudes e longitudes das 12 cidades são as das sedes municipais, conforme o IBGE,
Estrutura Territorial, Localidades:

https://www.ibge.gov.br/geociencias/organizacao-do-territorio/estrutura-territorial/27385-localidades.html

Conferência feita: em todas as 14 estradas do mapa, a distância em linha reta calculada por
Haversine ficou entre 71 e 92 por cento da distância por estrada, sempre menor, o que
confirma a admissibilidade da heurística.

## Arquivos de grafo

A pasta `exemplos` traz três arquivos prontos:

| Arquivo | Conteúdo |
|---|---|
| `mapa-parana.txt` | o mapa do enunciado, 12 cidades e 14 estradas, com coordenadas |
| `coloracao-roda.txt` | roda W5, número cromático conhecido: 4 cores |
| `coloracao-completo.txt` | grafo completo K5, número cromático conhecido: 5 cores |

Formato, uma linha por item, campos separados por ponto e vírgula:

```
# comentário, e linha em branco também é ignorada
NAO_DIRIGIDO
V;Curitiba;-25.4133;-49.2679
V;Toledo
A;e1;Curitiba;Toledo;114
```

A coordenada do vértice é opcional. Aceita vírgula ou ponto como separador decimal. Erro de
formato informa a linha em que ocorreu.

## Outras decisões de implementação

O resultado de cada algoritmo é mostrado no próprio painel do grafo ativo, limitando as
ligações desenhadas e realçando o subconjunto relevante. Limpar destaques, no menu Exibição,
devolve o desenho ao estado original, que é o par antes e depois exigido pelo enunciado do T2.

O Prim não pede vértice inicial, porque a árvore geradora mínima é propriedade do grafo e o
custo total não depende de onde a construção começa.

A busca em profundidade é guiada: recebe o vértice de saída e o de chegada e para assim que
alcança o destino.

Roy é aplicado como fecho transitivo, também conhecido como Roy-Warshall, sobre a matriz
booleana de adjacência.

No desenho, o identificador do vértice fica sempre abaixo do círculo, o laço aparece como um
círculo acima do vértice, e em grafo dirigido a reta é deslocada para a lateral, de modo que
o arco de ida e o de volta entre o mesmo par não se sobreponham.
