package my.example.project;
import java.util.Stack;

public class EnvStack<T> {
    private Stack<Environment<T>> envStack;

    public EnvStack() {
        this.envStack = new Stack<>();
    }

    public void push(Environment<T> env) {
        this.envStack.push(env);
    }

    public Stack<Environment<T>> getStack(){
        return this.envStack;
    }

    public Environment<T> pop() {
        Environment<T> env = this.envStack.pop();
        env.clear();
        return env;
    }
}
