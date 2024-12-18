package my.example.project;

public class Value<T> {
    private T value;
    private Boolean isImmutable;
    private Boolean isMutablyBorrowed;

    public Value() {
    }

    public Value(T val) {
        this.value = val;
    }

    public Value(T val, boolean isImmutable) {
        this.isImmutable = isImmutable;
        this.value = val;
    }

    public void set(T value) throws Exception {
        if (this.isImmutable) {
            throw new Exception("Variable is immutable");
        }
        this.value = value;
    }

    public T get() {
        return this.value;
    }

    public void lock() {
        this.isImmutable = true;
    }

    public void unlock() {
        this.isImmutable = false;
    }

    public boolean isImmutable() {
        return this.isImmutable;
    }

    public void setIsMutablyBorrowed(Boolean isMutablyBorrowed) {
        this.isMutablyBorrowed = isMutablyBorrowed;
    }

    public Boolean getIsMutablyBorrowed() {
        return this.isMutablyBorrowed;
    }
}
