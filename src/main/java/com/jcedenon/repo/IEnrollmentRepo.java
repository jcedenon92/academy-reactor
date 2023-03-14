package com.jcedenon.repo;

import com.jcedenon.model.Enrollment;
import org.springframework.stereotype.Repository;

@Repository
public interface IEnrollmentRepo extends IGenericRepo<Enrollment, String> {
}
