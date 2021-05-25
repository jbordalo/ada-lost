public class Edge {

    private final int head;
    private final int tail;
    private final int label;

    public Edge(int head, int tail, int label) {
        this.head = head;
        this.tail = tail;
        this.label = label;

    }

    public int getHead() {
        return this.head;
    }

    public int getTail() {
        return this.tail;
    }

    public int getLabel() {
        return this.label;
    }

}
