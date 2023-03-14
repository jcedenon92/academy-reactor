package com.jcedenon.service.impl;

import com.jcedenon.model.Course;
import com.jcedenon.repo.ICourseRepo;
import com.jcedenon.repo.IGenericRepo;
import com.jcedenon.service.ICourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl extends CRUDImpl<Course, String> implements ICourseService {

    private final ICourseRepo repo;


    @Override
    protected IGenericRepo<Course, String> getRepo() {
        return repo;
    }
}
