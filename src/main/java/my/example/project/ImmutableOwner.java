package my.example.project;

public class ImmutableOwner<T> extends Owner<T> {
    private Value<T> immutablyBorrows;

    public ImmutableOwner() {
        super();
    }

    public ImmutableOwner(T value) {
        super();
        this.val = new Value<>(value, true);
    }

    public boolean isSet(){
        return this.val != null || this.immutablyBorrows != null;
    }

    public void borrow(ImmutableOwner<T> owner) throws Exception {
        if(this.val != null) {
            throw new Exception("Cannot borrow a value if owning a value");
        }

        this.immutablyBorrows = owner.getValue();
    }

    public void release() {
        this.val = null;
        this.immutablyBorrows = null;
    }

    public T get() {
        if(this.val != null) {
            return val.get();
        }else if(this.immutablyBorrows != null) {
            return immutablyBorrows.get();
        }

        return null;
    }
}
