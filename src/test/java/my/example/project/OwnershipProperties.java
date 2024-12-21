package my.example.project;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.ListArbitrary;
import net.jqwik.api.constraints.*;
import org.assertj.core.api.*;

import static java.util.Arrays.*;

class ImmutableOwnerBuilder {
    private String val;

    public ImmutableOwnerBuilder withValue(String val) {
        this.val = val;
        return this;
    }

    public ImmutableOwner<String> build() {
        if(this.val == null) {
            return new ImmutableOwner<>();
        }

        return new ImmutableOwner<>(val);
    }
}

class MutableOwnerBuilder {
    private String val;

    public MutableOwnerBuilder withValue(String val) {
        this.val = val;
        return this;
    }

    public MutableOwner<String> build() {
        if(this.val == null) {
            return new MutableOwner<>();
        }

        return new MutableOwner<>(val);
    }
}

class EnvironmentBuilder {
    private final List<Owner<String>> listOfVariables = new ArrayList<>();

    public EnvironmentBuilder withImmutableVariables(List<ImmutableOwner<String>> listOfVariables) {
        this.listOfVariables.addAll(listOfVariables);
        return this;
    }

    public EnvironmentBuilder withMutableVariables(List<MutableOwner<String>> listOfVariables) {
        this.listOfVariables.addAll(listOfVariables);
        return this;
    }

    public Environment<String> build() {
        Environment<String> env = new Environment<>();
        env.addAll(listOfVariables);

        return env;
    }
}

class EnvStackBuilder {
    private final List<Environment<String>> envs = new ArrayList<>();

    public EnvStackBuilder withEnvironments(List<Environment<String>> env) {
        this.envs.addAll(env);
        return this;
    }

    public EnvStack<String> build() {
        EnvStack<String> envStack = new EnvStack<>();
        for(Environment<String> env : envs) {
            envStack.push(env);
        }

        return envStack;
    }
}



public class OwnershipProperties {

    // Arbitrary number of immutable borrows can occur in multiple nested environments
    @Property
    void propertyCheckImmutableBorrowsAreCorrect(@ForAll("envStacksWithImmutableOwners") EnvStack<String> envStack) throws Exception {
        Environment<String> bottomOfStack = envStack.getStack().firstElement();
        Owner<String> rootVar = bottomOfStack.getVariables().get(0);

        for(int i=1; i<bottomOfStack.getVariables().size(); i++) {
            Owner<String> var = bottomOfStack.getVariables().get(i);
            var.borrow(rootVar);
            Assertions.assertThat(var.get()).isEqualTo(rootVar.get());
        }

        for(int i=1; i<envStack.getStack().size(); i++) {
            for(Owner<String> var: envStack.getStack().get(i).getVariables()) {
                var.borrow(rootVar);
                Assertions.assertThat(var.get()).isEqualTo(rootVar.get());
            }
        }
    }

    // Mutable borrows cannot occur if immutable borrows are still in scope
    @Property
    void propertyCheckIllegalMutableBorrowsCannotOccur(@ForAll("envStacksWithMutableOwners") EnvStack<String> envStack) throws Exception {
        MutableOwner<String> rootVar = new MutableOwner<>("hello");
        ImmutableOwner<String> firstBorrower = new ImmutableOwner<>();

        Environment<String> bottomOfStack = envStack.getStack().get(0);
        bottomOfStack.add(rootVar);
        bottomOfStack.add(firstBorrower);

        firstBorrower.borrow(rootVar);

        for(int i=1; i<envStack.getStack().size(); i++) {
            for (Owner<String> var : envStack.getStack().get(i).getVariables()) {
                if(var instanceof MutableOwner) {
                    Assertions.assertThatException().isThrownBy(() -> var.borrow(rootVar));
                }
            }
        }
    }
    
    @Provide
    Arbitrary<ImmutableOwner<String>> generateImmutableOwner(){
        Arbitrary<String> shortStringArb = Arbitraries.strings().withCharRange('a', 'z')
                .ofMinLength(4).ofMaxLength(8);

        return Builders.withBuilder(ImmutableOwnerBuilder::new)
                .use(shortStringArb).in(ImmutableOwnerBuilder::withValue)
                .build(ImmutableOwnerBuilder::build);
    }

    @Provide
    Arbitrary<List<ImmutableOwner<String>>> generateImmutableOwners() {
        final int minNumberOfOwner = 3;
        final int maxNumberOfOwner = 6;

        return generateImmutableOwner()
                .list()
                .ofMinSize(minNumberOfOwner)
                .ofMaxSize(maxNumberOfOwner);
    }

    @Provide
    Arbitrary<MutableOwner<String>> generateMutableOwner(){
        Arbitrary<String> shortStringArb = Arbitraries.strings().withCharRange('a', 'z')
                .ofMinLength(4).ofMaxLength(8);

        return Builders.withBuilder(MutableOwnerBuilder::new)
                .use(shortStringArb).in(MutableOwnerBuilder::withValue)
                .build(MutableOwnerBuilder::build);
    }

    @Provide
    Arbitrary<List<MutableOwner<String>>> generateMutableOwners() {
        final int minNumberOfOwner = 1;
        final int maxNumberOfOwner = 2;

        return generateMutableOwner()
                .list()
                .ofMinSize(minNumberOfOwner)
                .ofMaxSize(maxNumberOfOwner);
    }

    @Provide
    Arbitrary<Environment<String>> environmentWithImmutableOwners(){
        Arbitrary<List<ImmutableOwner<String>>> arbImmutableOwners = generateImmutableOwners();

        return Builders.withBuilder(EnvironmentBuilder::new)
                .use(arbImmutableOwners).in(EnvironmentBuilder::withImmutableVariables)
                .build(EnvironmentBuilder::build);
    }

    @Provide
    Arbitrary<Environment<String>> environmentWithMutableOwners(){
        Arbitrary<List<MutableOwner<String>>> arbMutableOwners = generateMutableOwners();

        return Builders.withBuilder(EnvironmentBuilder::new)
                .use(arbMutableOwners).in(EnvironmentBuilder::withMutableVariables)
                .build(EnvironmentBuilder::build);
    }

    @Provide
    Arbitrary<Environment<String>> environmentWithOwners(){
        Arbitrary<List<MutableOwner<String>>> arbMutableOwners = generateMutableOwners();
        Arbitrary<List<ImmutableOwner<String>>> arbImmutableOwners = generateImmutableOwners();

        return Builders.withBuilder(EnvironmentBuilder::new)
                .use(arbMutableOwners).in(EnvironmentBuilder::withMutableVariables)
                .use(arbImmutableOwners).in(EnvironmentBuilder::withImmutableVariables)
                .build(EnvironmentBuilder::build);
    }

    @Provide
    Arbitrary<EnvStack<String>> envStacks(){
        ListArbitrary<Environment<String>> arbEnvs = Arbitraries
                .oneOf(environmentWithImmutableOwners(), environmentWithMutableOwners(), environmentWithOwners())
                .list()
                .ofMinSize(2)
                .ofMaxSize(5);

        return Builders.withBuilder(EnvStackBuilder::new)
                .use(arbEnvs).in(EnvStackBuilder::withEnvironments)
                .build(EnvStackBuilder::build);
    }

    @Provide
    Arbitrary<EnvStack<String>> envStacksWithMutableOwners(){
        ListArbitrary<Environment<String>> arbEnvs = environmentWithMutableOwners()
                .list()
                .ofMinSize(2)
                .ofMaxSize(5);

        return Builders.withBuilder(EnvStackBuilder::new)
                .use(arbEnvs).in(EnvStackBuilder::withEnvironments)
                .build(EnvStackBuilder::build);
    }

    @Provide
    Arbitrary<EnvStack<String>> envStacksWithImmutableOwners(){
        ListArbitrary<Environment<String>> arbEnvs = environmentWithImmutableOwners()
                .list()
                .ofMinSize(2)
                .ofMaxSize(5);

        return Builders.withBuilder(EnvStackBuilder::new)
                .use(arbEnvs).in(EnvStackBuilder::withEnvironments)
                .build(EnvStackBuilder::build);
    }

    @Provide
    Arbitrary<EnvStack<String>> envStacksWithBothImmutableAndMutableOwners(){
        ListArbitrary<Environment<String>> arbEnvs = environmentWithOwners()
                .list()
                .ofMinSize(2)
                .ofMaxSize(5);

        return Builders.withBuilder(EnvStackBuilder::new)
                .use(arbEnvs).in(EnvStackBuilder::withEnvironments)
                .build(EnvStackBuilder::build);
    }

}


