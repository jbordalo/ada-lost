public class Edge {

    private int head;
    private final int tail;
    private int label;
    private final String type;

    public Edge(int head, int tail, int label, String type) {
        this.head = head;
        this.tail = tail;
        this.label = label;
        this.type = type;
    }

    public int getHead() {
        return this.head;
    }

    public void setHead(int head) { this.head = head; }

    public int getTail() {
        return this.tail;
    }

    public int getLabel() {
        return this.label;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    public String getType() {
        return this.type;
    }

}
