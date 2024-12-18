package my.example.project;

import net.jqwik.api.*;
import org.assertj.core.api.*;

public class OwnershipExamples {

    @Example
    void ownershipTransferExample() {
        ImmutableOwner<String> o1 = new ImmutableOwner<>("hello");
        ImmutableOwner<String> o2 = new ImmutableOwner<>();

        Assertions.assertThat(o1.get()).isEqualTo("hello");
        o2.takeOwnership(o1);
        Assertions.assertThat(o2.get()).isEqualTo("hello");
        Assertions.assertThat(o1.get()).isNull();
    }

    @Example
    void ownershipTransferExample2() {
        Environment<String> main = new Environment<>();
        Environment<String> someFunction = new Environment<>();
        ImmutableOwner<String> o1 = new ImmutableOwner<>("hello");
        ImmutableOwner<String> o2 = new ImmutableOwner<>();

        main.add(o1);
        someFunction.add(o2);

        EnvStack<String> envStack = new EnvStack<>();
        envStack.push(main); // calling main
        envStack.push(someFunction); // calling someFunction

        o2.takeOwnership(o1); // passing variable to the function
        envStack.pop(); // return from function

        Assertions.assertThat(o1.get()).isNull();
        Assertions.assertThat(o2.get()).isNull();
    }

    @Example
    void borrowingExample1() throws Exception {
        Environment<String> main = new Environment<>();
        Environment<String> someFunction = new Environment<>();
        ImmutableOwner<String> o1 = new ImmutableOwner<>("hello");
        ImmutableOwner<String> o2 = new ImmutableOwner<>();

        main.add(o1);
        someFunction.add(o2);

        EnvStack<String> envStack = new EnvStack<>();
        envStack.push(main); // calling main
        envStack.push(someFunction); // calling someFunction

        o2.borrow(o1); // passing through borrow
        Assertions.assertThat(o1.get()).isNotNull();
        Assertions.assertThat(o2.get()).isNotNull();

        envStack.pop(); // function return
        Assertions.assertThat(o1.get()).isNotNull();
        Assertions.assertThat(o2.get()).isNull();

    }
}
