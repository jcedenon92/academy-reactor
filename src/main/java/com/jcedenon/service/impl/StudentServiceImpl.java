package com.jcedenon.service.impl;

import com.jcedenon.model.Student;
import com.jcedenon.repo.IGenericRepo;
import com.jcedenon.repo.IStudentRepo;
import com.jcedenon.service.IStudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl extends CRUDImpl<Student, String> implements IStudentService {

    private final IStudentRepo repo;

    @Override
    protected IGenericRepo<Student, String> getRepo() {
        return repo;
    }

    @Override
    public Flux<Student> findStudentsByOrderByAge(String order) {
        if(order.equals("desc")) {
            return repo.findStudentsByOrderByAgeDesc();
        } else if (order.equals("asc")){
            return repo.findStudentsByOrderByAgeAsc();
        }
        return null;
    }
}
