package ch.zzh.tool.demos;

import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * created by Steven Zou on 2019/3/27
 */

public class LambdaDemo {

    public static void main(String[] args){
        System.out.println("use Lambda to simplify inner class declaration");
        new Thread(() -> System.out.print("test")).start();

        System.out.println("use foreach to traverse List");
        List<String> features = Arrays.asList("Lambdas", "Default Method", "Stream API", "Date and Time API");
        features.forEach(System.out::println);
        features.forEach(System.out::println);

        System.out.println("Predicate in Lambda");
        List<String > languages = Arrays.asList("Java", "Scala", "C++", "Haskell", "Lisp");
        filter(languages, (str)->str.startsWith("S"));

        System.out.println("complex predicate in Lambda");
        Predicate<String> startWithFunc = str->str.startsWith("J");
        Predicate<String> lengthFunc = str->str.length()==4;
        languages.stream().filter(startWithFunc.and(lengthFunc)).forEach(System.out::println);


        System.out.println("use Lambda to map and reduce");
        List<Integer> costBeforeTax = Arrays.asList(100, 200,200, 300, 400, 500);
        costBeforeTax.stream().map(tax->tax + 0.2*tax).forEach(System.out::println);
        Optional<Double> bill = costBeforeTax.stream().map(tax->tax+.12*tax).reduce((sum, tax)->sum+tax);
        System.out.println(bill.isPresent()? bill.get():"");

        System.out.println("filter and collect in Lambda");
        costBeforeTax.stream().filter(value -> value > 200).collect(Collectors.toList()).forEach(System.out::println);

        System.out.println(("distinct in Lambda"));
        String result = costBeforeTax.stream().distinct().map(String::valueOf).collect(Collectors.joining(","));
        System.out.println(result);

        System.out.println(("statistics in Lambda"));
        IntSummaryStatistics statistics =costBeforeTax.stream().mapToInt(x->x).summaryStatistics();
        System.out.println(statistics.getMax());
        System.out.println(statistics.getAverage());
        System.out.println(statistics.getSum());

    }

    private static void filter(List<String> list, Predicate<String> condition){
        list.forEach((String value)->{
            if(condition.test(value))
                System.out.println(value);
        });

    }
}
