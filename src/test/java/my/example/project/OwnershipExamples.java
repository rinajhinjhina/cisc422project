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

    @Example
    void mutableBorrowingExample1() throws Exception {
        Environment<String> main = new Environment<>();
        Environment<String> changeFunction = new Environment<>();

        MutableOwner<String> o1 = new MutableOwner<>("hello");
        MutableOwner<String> o2 = new MutableOwner<>();

        main.add(o1);
        changeFunction.add(o2);

        EnvStack<String> envStack = new EnvStack<>();
        envStack.push(main);
        envStack.push(changeFunction);

        o2.borrow(o1);

        o2.mutate(o2.get() + " world");
        Assertions.assertThat(o1.get()).isEqualTo("hello world");
        Assertions.assertThat(o2.get()).isEqualTo("hello world");

        envStack.pop();
        Assertions.assertThat(o1.get()).isEqualTo("hello world");
        Assertions.assertThat(o2.get()).isNull();
    }

    @Example
    void mutableBorrowingExample2() throws Exception {
        Environment<String> main = new Environment<>();

        MutableOwner<String> o1 = new MutableOwner<>("hello");
        MutableOwner<String> o2 = new MutableOwner<>();
        MutableOwner<String> o3 = new MutableOwner<>();

        main.add(o1);
        main.add(o2);
        main.add(o3);

        EnvStack<String> envStack = new EnvStack<>();
        envStack.push(main);

        o2.borrow(o1);

        Assertions.assertThatException().isThrownBy(() -> { o3.borrow(o1); });
    }

    @Example
    void danglingReferencesExample() throws Exception {
        Environment<String> main = new Environment<>();
        Environment<String> someFunction = new Environment<>();
        ImmutableOwner<String> o1 = new ImmutableOwner<>();
        ImmutableOwner<String> o2 = new ImmutableOwner<>("hello");

        main.add(o1);
        someFunction.add(o2);

        EnvStack<String> envStack = new EnvStack<>();
        envStack.push(main); // calling main
        envStack.push(someFunction); // calling someFunction

        envStack.pop();
        Assertions.assertThatException().isThrownBy(() -> { o1.borrow(o2); });
    }
}
