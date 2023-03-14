package com.jcedenon.config;

import com.jcedenon.handler.CourseHandler;
import com.jcedenon.handler.EnrollmentHandler;
import com.jcedenon.handler.StudentHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterConfig {

    //Functional Endpoints
    @Bean
    public RouterFunction<ServerResponse> studentRoutes(StudentHandler handler){
        return route(GET("/api/fun/students"), handler::findAll)
                .andRoute(GET("/api/fun/students/{id}"), handler::findById)
                .andRoute(POST("/api/fun/students"), handler::create)
                .andRoute(PUT("/api/fun/students/{id}"), handler::update)
                .andRoute(DELETE("/api/fun/students/{id}"), handler::delete);
    }

    @Bean
    public RouterFunction<ServerResponse> courseRoutes(CourseHandler handler){
        return route(GET("/api/fun/courses"), handler::findAll)
                .andRoute(GET("/api/fun/courses/{id}"), handler::findById)
                .andRoute(POST("/api/fun/courses"), handler::create)
                .andRoute(PUT("/api/fun/courses/{id}"), handler::update)
                .andRoute(DELETE("/api/fun/courses/{id}"), handler::delete);
    }

    @Bean
    public RouterFunction<ServerResponse> enrollmentRoutes(EnrollmentHandler handler){
        return route(GET("/api/fun/enrollments"), handler::findAll)
                .andRoute(GET("/api/fun/enrollments/{id}"), handler::findById)
                .andRoute(POST("/api/fun/enrollments"), handler::create)
                .andRoute(PUT("/api/fun/enrollments/{id}"), handler::update)
                .andRoute(DELETE("/api/fun/enrollments/{id}"), handler::delete);
    }
}
