public class Edge {

    private final int head;
    private final int tail;
    private final int label;
    private final char type;

    public Edge(int head, int tail, int label, char type) {
        this.head = head;
        this.tail = tail;
        this.label = label;
        this.type = type;
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

    public char getType() {
        return this.type;
    }

}
