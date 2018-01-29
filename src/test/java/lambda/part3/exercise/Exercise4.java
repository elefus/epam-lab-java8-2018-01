package lambda.part3.exercise;

import lambda.data.Employee;
import lambda.data.JobHistoryEntry;
import lambda.part3.example.Example1;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

@SuppressWarnings({"unused", "ConstantConditions"})
public class Exercise4 {

    private static class LazyCollectionHelper<T, R> {

        private final List<T> source;
        private final Function<T, List<R>> function;

        private LazyCollectionHelper(List<T> source, Function<T, List<R>> function) {
            this.source = source;
            this.function = function;
        }

        public List<T> getMapped() {
            return new ArrayList<>(source);
        }

        public static <T> LazyCollectionHelper<T, T> from(List<T> collection) {
            return new LazyCollectionHelper<>(collection, Collections::singletonList);
        }

        public <U> LazyCollectionHelper<T, U> flatMap(Function<R, List<U>> flatMapping) {
            return new LazyCollectionHelper<>(source, function.andThen(transformUsing(flatMapping)));
        }

        public <U> LazyCollectionHelper<T, U> map(Function<R, U> flatMapping) {
            return new LazyCollectionHelper<>(source, function.andThen(transformUsing(
                    flatMapping.andThen(Collections::singletonList))));
        }

        public List<R> force() {
            return transformUsing(function).apply(source);
        }

        private <FROM, TO> Function<List<FROM>, List<TO>> transformUsing(Function<FROM, List<TO>> mapper) {
            return source -> {
                List<TO> result = new ArrayList<>();
                source.forEach(mapper.andThen(result::addAll)::apply);
                return result;
            };
        }
    }

    @Test
    public void mapEmployeesToCodesOfLetterTheirPositionsUsingLazyFlatMapHelper() {
        List<Employee> employees = Example1.getEmployees();

        List<Integer> codes = LazyCollectionHelper.from(employees)
                .flatMap(Employee::getJobHistory)
                .map(JobHistoryEntry::getPosition)
                .flatMap(string -> string.chars().boxed().collect(Collectors.toList()))
                .force();
        assertEquals(calcCodes("dev", "dev", "tester", "dev", "dev", "QA", "QA", "dev", "tester", "tester", "QA", "QA", "QA", "dev"), codes);
    }

    private static List<Integer> calcCodes(String... strings) {
        List<Integer> codes = new ArrayList<>();
        for (String string : strings) {
            for (char letter : string.toCharArray()) {
                codes.add((int) letter);
            }
        }
        return codes;
    }
}
