package com.jcedenon.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "students")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class Student {

    @Id
    @EqualsAndHashCode.Include
    private String idStudent;

    @Size(min = 3)
    @Field
    private String names;

    @Size(min = 3)
    @Field
    private String surname;

    @Field
    private String dni;

    @Field
    private Integer age;
}
