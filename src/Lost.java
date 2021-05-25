import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

public class Lost {

    public static final int INF = Integer.MAX_VALUE;
    private static final char GRASS = 'G';
    private static final char WATER = 'W';
    private static final char WHEEL = 'M';
    private static final char OBSTACLE = 'O';
    private static final char EXIT = 'X';

    private final int numNodes;
    private final List<Edge> edgesJohn;
    private final List<Edge>[] edgesKate;
    private int exitNode;
    private final int columns;
    private final int rows;
    private final int[] magicWheels;

    @SuppressWarnings("unchecked")
    public Lost(int rows, int columns, int magicWheels) {
        this.numNodes = rows * columns;
        this.edgesJohn = new LinkedList<>();
        this.edgesKate = new LinkedList[this.numNodes];
        for (int i = 0; i < this.numNodes; i++) {
            this.edgesKate[i] = new LinkedList<>();
        }

        this.exitNode = -1;
        this.rows = rows;
        this.columns = columns;
        this.magicWheels = new int[magicWheels];
    }

    private char isMagicWheel(char cell, int tail) {
        int index = Character.getNumericValue(cell);
        if (index > 0 && index <= 9) {
            this.magicWheels[index - 1] = tail;
            return GRASS;
        }

        return cell;
    }

    private void createEdge(char cell, int head, int tail, char neighbour) {

        int label = cell == WATER ? 2 : 1;

        // Check for magic wheel
        cell = isMagicWheel(cell, tail);

        if (neighbour == OBSTACLE) return;

        Edge e = new Edge(head, tail, label);

        if (cell != WATER && neighbour != WATER)
            this.edgesJohn.add(e);

        this.edgesKate[tail].add(e);
    }

    public void addMagicWheel(int r, int c, int label, int i) {
        int tail = this.magicWheels[i];
        this.edgesJohn.add(new Edge(r * this.columns + c, tail, label));
    }

    public void addRows(char[][] grid) {
        // NxM
        int n = this.rows;
        int m = this.columns;

        // Connect the grid
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {

                char cell = grid[i][j];

                int tail = i * m + j;

                if (cell == OBSTACLE) continue;
                if (cell == EXIT) {
                    this.exitNode = tail;
                    continue;
                }

                char neighbour;
                int head;

                // top
                head = (i - 1) * m + j;
                if (i - 1 >= 0) {
                    neighbour = grid[i - 1][j];
                    this.createEdge(cell, head, tail, neighbour);
                }

                // right
                head = i * m + j + 1;
                if (j + 1 < m) {
                    neighbour = grid[i][j + 1];
                    this.createEdge(cell, head, tail, neighbour);
                }

                // bottom
                head = (i + 1) * m + j;
                if (i + 1 < n) {
                    neighbour = grid[i + 1][j];
                    this.createEdge(cell, head, tail, neighbour);
                }

                // left
                head = i * m + j - 1;
                if (j - 1 >= 0) {
                    neighbour = grid[i][j - 1];
                    this.createEdge(cell, head, tail, neighbour);
                }

            }
        }
    }

    private boolean updateLengths(int[] len, int[] via) {
        boolean changes = false;

        for (Edge e : this.edgesJohn) {

            int tail = e.getTail();
            int head = e.getHead();

            if (len[tail] < INF) {
                int newLen = len[tail] + e.getLabel();
                if (newLen < len[head]) {
                    len[head] = newLen;
                    via[head] = tail;
                    changes = true;
                }
            }
        }
        return changes;
    }

    // TODO VIA

    private int bellmanFord(int origin) throws NegativeWeightCycleException {
        int[] length = new int[this.numNodes];
        int[] via = new int[this.numNodes];

        for (int i = 0; i < this.numNodes; i++)
            length[i] = INF;

        length[origin] = 0;
        via[origin] = 0;

        boolean changes = false;

        for (int i = 0; i < this.numNodes; i++) {
            changes = this.updateLengths(length, via);
            if (!changes) break;
        }

        // Negative-weight cycles detection
        if (changes && this.updateLengths(length, via))
            throw new NegativeWeightCycleException();

        return length[this.exitNode];
    }

    private void exploreNode(int source, boolean[] selected, int[] length, int[] via, PriorityQueue<SimpleEntry<Integer, Integer>> connected) {
        for (Edge e: this.edgesKate[source]) {
            int node = e.getHead();
            if ( !selected[node] ) {
                int newLength = length[source] + e.getLabel();
                if ( newLength < length[node] ) {
                    boolean nodeIsInQueue = length[node] < INF;
                    SimpleEntry<Integer, Integer> oldPair = new SimpleEntry<>(length[node], node);
                    length[node] = newLength;
                    via[node] = source;
                    if (nodeIsInQueue) {
                        // This will emulate a decreaseKey
                        connected.remove(oldPair);
                    }

                    connected.add(new SimpleEntry<>(newLength, node));
                }
            }
        }
    }

    private int dijkstra(int origin) {
        boolean[] selected = new boolean[ this.numNodes ];
        int[] length = new int[ this.numNodes ];
        int[] via = new int[ this.numNodes ];
        PriorityQueue<SimpleEntry<Integer, Integer>> connected = new PriorityQueue<>(this.numNodes, new EntryComparator());

        for (int v = 0; v < this.numNodes; v++) {
            selected[v] = false;
            length[v] = INF;
        }

        length[origin] = 0;
        via[origin] = origin;

        connected.add(new SimpleEntry<>(0, origin));
        do {
            int node = connected.remove().getValue();
            selected[node] = true;
            exploreNode(node, selected, length, via, connected);
        }
        while ( !connected.isEmpty() );
        return length[this.exitNode];
    }

    public int solveJohn(int r, int c) throws NegativeWeightCycleException {
        return this.bellmanFord(r * this.columns + c);
    }

    public int solveKate(int r, int c) {
        return this.dijkstra(r * this.columns + c);
    }

}
