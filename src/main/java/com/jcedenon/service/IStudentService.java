package com.jcedenon.service;

import com.jcedenon.model.Student;
import reactor.core.publisher.Flux;

public interface IStudentService extends ICRUD<Student, String>{

    Flux<Student> findStudentsByOrderByAge(String order);
}
