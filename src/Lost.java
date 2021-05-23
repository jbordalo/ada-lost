import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Lost {

    public static final int INF = Integer.MAX_VALUE;
    private final String GRASS = "G";
    private final String WATER = "W";
    private final String WHEEL = "M";
    private final String OBSTACLE = "O";
    private final String EXIT = "X";

    private final boolean JOHN = true;
    private final boolean KATE = false;

    private final int numNodes;
    private final List<Edge> edges;
    private int exitNode;
    private final int columns;
    private final int rows;
    private List<Edge> magicWheels;

    public Lost(int rows, int columns, int magicWheels) {
        this.numNodes = rows * columns;
        this.edges = new LinkedList<>();
        this.exitNode = -1;
        this.rows = rows;
        this.columns = columns;
        this.magicWheels = new ArrayList<>(magicWheels);
    }

    private void createEdge(String cell, int head, int tail, String neighbour) {

        if (neighbour.equals(OBSTACLE)) return;

        int label = cell.equals(WATER) ? 2 : 1;

        try {
            Integer.parseInt(cell);
            cell = GRASS;
            Edge newEdge = new Edge(-1, tail, -1, WHEEL);
            this.magicWheels.add(newEdge);
        } catch (NumberFormatException ignored) {
        }

        this.edges.add(new Edge(head, tail, label, cell));
    }

    public void addMagicWheel(int r, int c, int label, int i) {

        Edge e = this.magicWheels.get(i);

        e.setLabel(label);
        e.setHead(r * this.columns + c);

        this.edges.add(e);

        if (i == this.magicWheels.size() - 1) this.magicWheels = null;
    }

    public void addRows(String[][] grid) {

        // NxM
        int n = this.rows;
        int m = this.columns;

        // Connect the grid
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {

                String cell = grid[i][j];
                int tail = i * m + j;

                if (cell.equals(OBSTACLE)) continue;

                if (cell.equals(EXIT)) this.exitNode = tail;

                String neighbour = "";
                int head = -1;
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
            if (e.getType().equals(OBSTACLE)) continue;
            if (e.getType().equals(WATER) && character) continue;
            if (e.getType().equals(WHEEL) && !character) continue;

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
