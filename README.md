# Teoria dos Grafos - Trabalho M1

Kauan Rocha Dalfovo
Gustavo Odilon

## Como executar

Abrir a pasta `Grafos_m1_1` no IntelliJ e executar a classe `Main` (`src/Main.java`).

Pela linha de comando, a partir de `Grafos_m1_1/src`:

```bash
javac -encoding UTF-8 -d ../out *.java && java -cp ../out Main
```

Requer JDK 17 ou superior.

## Estrutura

| Arquivo | Conteúdo |
|---|---|
| `Vertice.java` | Vértice com identificador e sua lista de adjacência |
| `Aresta.java` | Aresta/arco com identificador, extremidades e peso |
| `Grafo.java` | Grafo dirigido ou não dirigido em lista de adjacência, operações, matrizes e algoritmos |
| `PainelGrafo.java` | Desenho gráfico do grafo, das árvores e das componentes |
| `TelaPrincipal.java` | Janela com moldura, menu e visualização do grafo ativo |
| `Main.java` | Ponto de entrada |

## Representação

Lista de adjacência. Cada `Vertice` guarda a lista das arestas/arcos incidentes sobre ele.
O `Grafo` mantém ainda a lista geral de arestas, usada para busca por identificador e para
montar as matrizes. O atributo `dirigido` define se o par (v, w) é ordenado (arco) ou não
ordenado (aresta).

Em grafo não dirigido a aresta é registrada na lista dos dois extremos. Em grafo dirigido o
arco é registrado apenas na lista do vértice de origem. O laço é registrado uma única vez.

## Itens do trabalho e onde estão

| Item | Implementação | Menu |
|---|---|---|
| Inserir vértice isolado | `Grafo.inserirVertice` | Vértice |
| Inserir aresta/arco | `Grafo.inserirAresta` | Aresta/Arco |
| Remover vértice e suas ligações | `Grafo.removerVertice` | Vértice |
| Remover aresta/arco | `Grafo.removerAresta` | Aresta/Arco |
| Verificar adjacência | `Grafo.saoAdjacentes` | Aresta/Arco |
| Valor da aresta/arco | `Grafo.retornarValorAresta` | Aresta/Arco |
| Extremidades da aresta/arco | `Grafo.formatarExtremidades` | Aresta/Arco |
| Mostrar o grafo graficamente | `PainelGrafo` | sempre visível na janela principal |
| Matriz de adjacência | `Grafo.matrizAdjacencia` | Matrizes |
| Matriz de incidência | `Grafo.matrizIncidencia` | Matrizes |
| Prim e custo da AGM | `Grafo.prim` | Algoritmos |
| Busca em profundidade guiada | `Grafo.buscaEmProfundidade` | Algoritmos |
| Roy: componentes conexas e fortemente conexas | `Grafo.royComponentes` | Algoritmos |
| Interface com moldura e menu | `TelaPrincipal` | janela principal |

## Decisões de implementação

Prim é definido para grafo não dirigido. Em grafo dirigido o programa pergunta antes e
aplica o algoritmo sobre o grafo subjacente, ignorando o sentido dos arcos. Se o grafo for
desconexo, o resultado é uma floresta geradora mínima, uma árvore por componente, e a
janela informa essa condição.

A busca em profundidade é guiada: recebe o vértice de saída e o de chegada e para assim que
alcança o destino. Em grafo dirigido respeita o sentido dos arcos. A janela de resultado
desenha a árvore gerada e destaca em vermelho o caminho da saída até a chegada, com o
vértice de saída em verde e o de chegada em vermelho claro.

Roy é aplicado como fecho transitivo (Roy-Warshall) sobre a matriz booleana de adjacência.
Dois vértices pertencem ao mesmo conjunto quando um alcança o outro nos dois sentidos, o que
dá componentes conexas no grafo não dirigido e fortemente conexas no dirigido. Cada conjunto
recebe uma cor no desenho.

Na matriz de adjacência, havendo arestas paralelas entre o mesmo par prevalece o menor peso.
Na matriz de incidência, o laço vale 2 em grafo não dirigido e 0 em grafo dirigido, conforme
a convenção usual.

Os vértices podem ser arrastados com o mouse para reposicionar o desenho.
