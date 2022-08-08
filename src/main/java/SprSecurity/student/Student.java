package SprSecurity.student;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document
public class Student {

    @Id
    private int id;

    private final String name;

    private final String email;

    private Gender gender;

    private Address address;

    private List<String> subjects;

    private BigDecimal totalSpent;

    private LocalDateTime created;
}
