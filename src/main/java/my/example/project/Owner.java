package my.example.project;

public abstract class Owner<T> {
    protected Value<T> val;
    protected boolean hasBeenBorrowed;

    public Owner() {
        this.hasBeenBorrowed = false;
    }

    public void takeOwnership(Owner<T> owner){
        this.val = owner.getValue();
        owner.release();
    }

    public void release() {
        this.val = null;
        this.hasBeenBorrowed = false;
    }

    public boolean isSet(){
        return this.val != null;
    }

    public Value<T> getValue() {
        return this.val;
    }

    public void setValue(T val) {
        this.val = new Value<>(val);
    }

    public T get() {
        if(!this.isSet()){
            return null;
        }
        return this.getValue().get();
    }
}
