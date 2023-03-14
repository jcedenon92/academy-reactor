package com.jcedenon.repo;

import com.jcedenon.model.Student;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface IStudentRepo extends IGenericRepo<Student, String> {

    /*
    SELECT * FROM STUDENT ORDER BY AGE DESC
     */
    Flux<Student> findStudentsByOrderByAgeDesc();
    Flux<Student> findStudentsByOrderByAgeAsc();
}
