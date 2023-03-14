package com.jcedenon.repo;

import com.jcedenon.model.Course;
import org.springframework.stereotype.Repository;

@Repository
public interface ICourseRepo extends IGenericRepo<Course, String> {
}
