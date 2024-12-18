package my.example.project;
import java.util.Stack;

public class EnvStack<T> {
    Stack<Environment<T>> envStack;

    public EnvStack() {
        envStack = new Stack<>();
    }

    public void push(Environment<T> env) {
        envStack.push(env);
    }

    public Environment<T> pop() {
        Environment<T> env = envStack.pop();
        env.clear();
        return env;
    }
}
