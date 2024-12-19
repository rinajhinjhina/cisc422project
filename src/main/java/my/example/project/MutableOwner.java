package my.example.project;

public class MutableOwner<T> extends Owner<T> {
    private MutableOwner<T> mutablyBorrowsFrom;

    public MutableOwner() {
        super();
    }

    public MutableOwner(T value) {
        super();
        boolean isImmutable = false;
        this.val = new Value<>(value, isImmutable);
    }

    public boolean isSet(){
        return this.val != null || this.mutablyBorrowsFrom != null;
    }

    public void takeOwnership(Owner<T> owner){
        this.val = owner.getValue();
        this.val.setIsImmutable(false);
        owner.release();
    }

    public void borrow(MutableOwner<T> owner) throws Exception {
        if(this.val != null) {
            throw new Exception("Cannot borrow if owning a value");
        }

        if(owner.hasBeenBorrowed){
            throw new Exception("Cannot mutably borrow a value that has already been borrowed in the same scope");
        }

        this.mutablyBorrowsFrom = owner;
        owner.hasBeenBorrowed = true;
    }

    public void release() {
        this.val = null;
        this.mutablyBorrowsFrom.hasBeenBorrowed = false;
        this.mutablyBorrowsFrom = null;
    }

    public T get() {
        if(this.val != null) {
            return val.get();
        }else if(this.mutablyBorrowsFrom != null) {
            return mutablyBorrowsFrom.get();
        }

        return null;
    }

    public void mutate(T value) throws Exception {
        if(this.val != null) {
            this.val.set(value);
        }else if(this.mutablyBorrowsFrom != null) {
            this.mutablyBorrowsFrom.val.set(value);
        }else{
            throw new Exception("Cannot mutate if there is no value set");
        }
    }

}
