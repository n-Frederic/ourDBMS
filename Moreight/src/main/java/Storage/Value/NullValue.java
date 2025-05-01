package Storage.Value;

public class NullValue extends Value{
    @Override
    public int getLength() {
        return 0;
    }

    @Override
    public byte getType() {
        return 0;
    }

    @Override
    public int compare(Value value) {
        return -1;
    }

    public Object getValue() {return null;}
}
