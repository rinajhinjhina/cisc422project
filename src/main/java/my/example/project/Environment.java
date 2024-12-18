package my.example.project;
import java.util.ArrayList;
import java.util.List;

public class Environment<T> {
    private List<Owner<T>> listOfVariables;

    public Environment() {
        listOfVariables = new ArrayList<>();
    }

    public void add(Owner<T> o) {
        listOfVariables.add(o);
    }

    public void clear(){
        for(Owner<T> o : listOfVariables){
            o.release();
        }

        listOfVariables.clear();
    }
}
