import java.io.*;

/**
 * Wrapper for a long that serializes more efficiently
 */
public class Sequence implements Comparable<Sequence>, Serializable {
    public long sequence;
    public int length;

    public Sequence(long sequence, int length) {
        this.sequence = sequence;
        this.length = length;
    }

    public Sequence(long sequence) {
        this(sequence, 31);
    }

    @Override
    public int compareTo(Sequence sequence) {
//        if(this.length == sequence.length) {
//            return Long.compare(this.sequence, sequence.sequence);
//        } else {
//            return Integer.compare(this.length, sequence.length);
//        }
        return Long.compare(this.sequence, sequence.sequence);
    }

//    private void writeObject(ObjectOutputStream out) throws IOException {
//        out.writeLong(sequence);
//    }
//
//    private void readObject(ObjectInputStream in) throws IOException {
//        sequence = in.readLong();
//    }

    @Override
    public String toString() {
//        return Long.toString(sequence);
        return BTreeUtil.convertLongToString(sequence, length);
    }
}
