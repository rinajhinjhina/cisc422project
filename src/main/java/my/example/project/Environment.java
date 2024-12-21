package my.example.project;
import java.util.ArrayList;
import java.util.List;

public class Environment<T> {
    private final List<Owner<T>> listOfVariables;

    public Environment() {
        this.listOfVariables = new ArrayList<>();
    }

    public void add(Owner<T> o) {
        this.listOfVariables.add(o);
    }

    public void addAll(List<Owner<T>> o) {
        this.listOfVariables.addAll(o);
    }

    public void clear(){
        for(Owner<T> o : this.listOfVariables){
            o.release();
        }

        this.listOfVariables.clear();
    }

    public List<Owner<T>> getVariables() {
        return this.listOfVariables;
    }
}
