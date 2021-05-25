import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

public class Lost {

    public static final int INF = Integer.MAX_VALUE;
    private static final char GRASS = 'G';
    private static final char WATER = 'W';
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
        this.exitNode = -1;
        this.rows = rows;
        this.columns = columns;
        this.magicWheels = new int[magicWheels];

        for (int i = 0; i < this.numNodes; i++) {
            this.edgesKate[i] = new LinkedList<>();
        }
    }

    /**
     * Checks if the character given is a magic wheel,
     * if so, it saves the tail in the magicWheels
     * (for later use in the creation of the edges related)
     * Returns the type of cell (GRASS if it is a magic wheel).
     */
    private char isMagicWheel(char cell, int tail) {
        int index = Character.getNumericValue(cell);
        if (index > 0 && index <= 9) {
            this.magicWheels[index - 1] = tail;
            return GRASS;
        }
        return cell;
    }

    /**
     * Creates the edge and adds it to the graphs
     * (may not add to Johns graph if it is not useful - WATER).
     */
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

    /**
     * Given the char grid, this function will connect a node
     * to the nodes directly above, below and to his sides.
     */
    public void addRows(char[][] grid) {
        char cell;
        int tail;
        // Connect the grid
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {

                cell = grid[i][j];
                tail = i * this.columns + j;

                if (cell == OBSTACLE) continue;
                if (cell == EXIT) {
                    this.exitNode = tail;
                    continue;
                }

                // top
                if (i - 1 >= 0)
                    this.createEdge(cell, (i - 1) * this.columns + j, tail, grid[i - 1][j]);

                // right
                if (j + 1 < this.columns)
                    this.createEdge(cell, i * this.columns + j + 1, tail, grid[i][j + 1]);

                // bottom
                if (i + 1 < this.rows)
                    this.createEdge(cell, (i + 1) * this.columns + j, tail, grid[i + 1][j]);

                // left
                if (j - 1 >= 0)
                    this.createEdge(cell, i * this.columns + j - 1, tail, grid[i][j - 1]);
            }
        }
    }

    /**
     * Creates the magic wheel edges and adds them to Johns graph.
     */
    public void addMagicWheel(int r, int c, int label, int i) {
        this.edgesJohn.add(new Edge(r * this.columns + c, this.magicWheels[i], label));
    }

    private boolean updateLengths(int[] len) {
        boolean changes = false;

        for (Edge e : this.edgesJohn) {

            int tail = e.getTail();
            int head = e.getHead();

            if (len[tail] < INF) {
                int newLen = len[tail] + e.getLabel();
                if (newLen < len[head]) {
                    len[head] = newLen;
                    changes = true;
                }
            }
        }
        return changes;
    }

    private int bellmanFord(int origin) throws NegativeWeightCycleException {
        int[] length = new int[this.numNodes];

        for (int i = 0; i < this.numNodes; i++)
            length[i] = INF;

        length[origin] = 0;

        boolean changes = false;

        for (int i = 0; i < this.numNodes; i++) {
            changes = this.updateLengths(length);
            if (!changes) break;
        }

        // Negative-weight cycles detection
        if (changes && this.updateLengths(length))
            throw new NegativeWeightCycleException();

        return length[this.exitNode];
    }

    private void exploreNode(int source, boolean[] selected, int[] length, PriorityQueue<SimpleEntry<Integer, Integer>> connected) {
        for (Edge e : this.edgesKate[source]) {
            int node = e.getHead();
            if (!selected[node]) {
                int newLength = length[source] + e.getLabel();
                if (newLength < length[node]) {
                    boolean nodeIsInQueue = length[node] < INF;
                    SimpleEntry<Integer, Integer> oldPair = new SimpleEntry<>(length[node], node);
                    length[node] = newLength;
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
        boolean[] selected = new boolean[this.numNodes];
        int[] length = new int[this.numNodes];
        PriorityQueue<SimpleEntry<Integer, Integer>> connected = new PriorityQueue<>(this.numNodes, new EntryComparator());

        for (int v = 0; v < this.numNodes; v++) {
            selected[v] = false;
            length[v] = INF;
        }

        length[origin] = 0;

        connected.add(new SimpleEntry<>(0, origin));
        do {
            int node = connected.remove().getValue();
            selected[node] = true;
            exploreNode(node, selected, length, connected);
        }
        while (!connected.isEmpty());
        return length[this.exitNode];
    }

    /**
     * Solves the problem for John using Bellman Ford's algorithm.
     */
    public int solveJohn(int r, int c) throws NegativeWeightCycleException {
        return this.bellmanFord(r * this.columns + c);
    }

    /**
     * Solves the problem for Kate using Dijkstra's algorithm.
     */
    public int solveKate(int r, int c) {
        return this.dijkstra(r * this.columns + c);
    }
}
