package com.jcedenon.service.impl;

import com.jcedenon.model.Enrollment;
import com.jcedenon.repo.IEnrollmentRepo;
import com.jcedenon.repo.IGenericRepo;
import com.jcedenon.service.IEnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl extends CRUDImpl<Enrollment, String> implements IEnrollmentService {

    private final IEnrollmentRepo repo;


    @Override
    protected IGenericRepo<Enrollment, String> getRepo() {
        return repo;
    }
}
