import java.util.LinkedList;
import java.util.List;

public class Lost {

    public static final int INF = Integer.MAX_VALUE;
    private static final char GRASS = 'G';
    private static final char WATER = 'W';
    private static final char WHEEL = 'M';
    private static final char OBSTACLE = 'O';
    private static final char EXIT = 'X';

    private final boolean JOHN = true;
    private final boolean KATE = false;

    private final int numNodes;
    private final List<Edge> edges;
    private int exitNode;
    private final int columns;
    private final int rows;
    private final int[] magicWheels;

    public Lost(int rows, int columns, int magicWheels) {
        this.numNodes = rows * columns;
        this.edges = new LinkedList<>();
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

        this.edges.add(new Edge(head, tail, label, cell));
    }

    public void addMagicWheel(int r, int c, int label, int i) {
        int tail = this.magicWheels[i];
        this.edges.add(new Edge(r * this.columns + c, tail, label, WHEEL));
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

    private boolean updateLengths(int[] len, int[] via, boolean character) {
        boolean changes = false;

        for (Edge e : this.edges) {
            if (e.getType() == OBSTACLE
                    || (e.getType() == WATER && character)
                    || (e.getType() == WHEEL && !character)) continue;

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

    private int bellmanFord(boolean character, int origin) throws NegativeWeightCycleException {
        int[] length = new int[this.numNodes];
        int[] via = new int[this.numNodes];

        for (int i = 0; i < this.numNodes; i++)
            length[i] = INF;

        length[origin] = 0;
        via[origin] = 0;

        boolean changes = false;

        for (int i = 0; i < this.numNodes; i++) {
            changes = this.updateLengths(length, via, character);
            if (!changes) break;
        }

        // Negative-weight cycles detection
        if (changes && this.updateLengths(length, via, character))
            throw new NegativeWeightCycleException();

        return length[this.exitNode];
    }

    public int solveJohn(int r, int c) throws NegativeWeightCycleException {
        return this.bellmanFord(JOHN, r * this.columns + c);
    }

    public int solveKate(int r, int c) {
        try {
            return this.bellmanFord(KATE, r * this.columns + c);
        } catch (NegativeWeightCycleException e) {
            // Should never happen
            e.printStackTrace();
            return -INF;
        }
    }

}
