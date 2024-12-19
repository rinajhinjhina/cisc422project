package my.example.project;

import java.util.*;

import net.jqwik.api.*;
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

public class OwnershipProperties {



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

}


