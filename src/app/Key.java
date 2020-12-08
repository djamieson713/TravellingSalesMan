package app;
//useful for adding multiple keys in a Map
public class Key {
    public final int X;
    public final int Y;

    public Key(final int X, final int Y) {
        this.X = X;
        this.Y = Y;
    }

    public boolean equals(final Object O) {
        if (!(O instanceof Key))
            return false;
        if (((Key) O).X != X)
            return false;
        if (((Key) O).Y != Y)
            return false;
        return true;
    }

    public int hashCode() {
        return (X << 16) + Y;
    }

}
