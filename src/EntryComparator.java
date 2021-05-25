import java.util.AbstractMap.SimpleEntry;
import java.util.Comparator;

class EntryComparator implements Comparator<SimpleEntry<Integer, Integer>> {

    @Override
    public int compare(SimpleEntry<Integer, Integer> p1, SimpleEntry<Integer, Integer> p2) {
        return p1.getKey() - p2.getKey();
    }
}
