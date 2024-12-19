package my.example.project;

public class ImmutableOwner<T> extends Owner<T> {
    private Owner<T> immutablyBorrowsFrom;

    public ImmutableOwner() {
        super();
    }

    public ImmutableOwner(T value) {
        super();
        boolean isImmutable = true;
        this.val = new Value<>(value, isImmutable);
    }

    public boolean isSet(){
        return this.val != null || this.immutablyBorrowsFrom != null;
    }

    public void takeOwnership(Owner<T> owner){
        this.val = owner.getValue();
        this.val.setIsImmutable(true);
        owner.release();
    }

    public void borrow(ImmutableOwner<T> owner) throws Exception {
        if(this.val != null) {
            throw new Exception("Cannot borrow a value if owning a value");
        }

        if(owner.val == null) {
            throw new Exception("Cannot borrow a value if source is not set");
        }

        this.immutablyBorrowsFrom = owner;
        owner.hasBeenBorrowed = true;
    }

    public void release() {
        this.val = null;
        this.hasBeenBorrowed = false;
        this.immutablyBorrowsFrom = null;
    }

    public T get() {
        if(this.val != null) {
            return val.get();
        }else if(this.immutablyBorrowsFrom != null) {
            return immutablyBorrowsFrom.get();
        }

        return null;
    }
}
