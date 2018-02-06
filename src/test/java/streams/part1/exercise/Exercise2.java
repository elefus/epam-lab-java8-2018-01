package streams.part1.exercise;

import lambda.data.Employee;
import lambda.data.JobHistoryEntry;
import lambda.data.Person;
import lambda.part3.example.Example1;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

@SuppressWarnings({"ConstantConditions", "unused"})
public class Exercise2 {

    @Test
    public void calcAverageAgeOfEmployees() {
        List<Employee> employees = Example1.getEmployees();

        Double expected = employees.stream()
                                    .map(Employee::getPerson)
                                    .mapToInt(Person::getAge)
                                    .average()
                                    .orElseThrow(IllegalStateException::new);

        assertEquals(33.66, expected, 0.1);
    }

    @Test
    public void findPersonWithLongestFullName() {
        List<Employee> employees = Example1.getEmployees();

        Person expected = employees.stream()
                                    .map(Employee::getPerson)
                                    .max(Comparator.comparingInt(p -> p.getFullName().length()))
                                    .orElseThrow(IllegalStateException::new);

        assertEquals(expected, employees.get(1).getPerson());
    }

    @Test
    public void findEmployeeWithMaximumDurationAtOnePosition() {
        List<Employee> employees = Example1.getEmployees();

        Employee expected = employees.stream()
                                        .max(Comparator.comparingInt(e -> e.getJobHistory().stream()
                                                                                            .mapToInt(JobHistoryEntry::getDuration)
                                                                                            .max()
                                                                                            .orElse(0)))
                                        .orElseThrow(IllegalStateException::new);

        assertEquals(expected, employees.get(4));
    }

    /**
     * Вычислить общую сумму заработной платы для сотрудников.
     * Базовая ставка каждого сотрудника составляет 75_000.
     * Если на текущей позиции (последняя в списке) он работает больше трех лет - ставка увеличивается на 20%
     */
    @Test
    public void calcTotalSalaryWithCoefficientWorkExperience() {
        List<Employee> employees = Example1.getEmployees();

        Double expected = employees.stream()
                                    .map(Employee::getJobHistory)
                                    .map(jobHistoryEntries -> jobHistoryEntries.get(jobHistoryEntries.size()-1).getDuration())
                                    .mapToDouble(duration -> duration > 3 ? 75_000*1.2 : 75_000)
                                    .sum();

        assertEquals(465000.0, expected, 0.001);
    }
}