package SprSecurity.student;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("students")
public class StudentController {

    private static final List<Student> STUDENTS = Arrays.asList(
            new Student("1", "James"),
            new Student("2", "Maria"),
            new Student("3", "Anna")
    );

    @GetMapping(path = "{studentId}")
    public Student getStudent(@PathVariable Integer studentId){
    return STUDENTS.stream().filter(student -> studentId.equals(student.getId()))
            .findFirst()
            .orElseThrow(()->new IllegalArgumentException("No Student with id " + studentId));
    }
}
