package Storage.BPlusTree;

public class Key implements Comparable<Key> {
    private final Comparable<?> value;

    public Key(Comparable<?> value) {
        this.value = value;
    }

    public Comparable<?> getValue() {
        return value;
    }

    @Override
    public int compareTo(Key other) {
        return ((Comparable<Object>) this.value).compareTo(other.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Key)) return false;
        Key other = (Key) o;
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
