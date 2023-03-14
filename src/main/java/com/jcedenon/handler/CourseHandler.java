package com.jcedenon.handler;

import com.jcedenon.model.Course;
import com.jcedenon.service.ICourseService;
import com.jcedenon.validator.RequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
@RequiredArgsConstructor
public class CourseHandler {

    private final RequestValidator requestValidator;
    private final ICourseService service;

    public Mono<ServerResponse> findAll(ServerRequest req){
        /*return service.findAll()
                .hasElements()
                .flatMap( status -> {
                    if (status)
                        return ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(fromValue(service.findAll()));
                    else
                        return ServerResponse.noContent().build();
                });*/

        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.findAll(), Course.class);
    }

    public Mono<ServerResponse> findById(ServerRequest req){
        String id = req.pathVariable("id");

        return service.findById(id)
                .flatMap( course -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(course)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> create(ServerRequest req){
        Mono<Course> monoCourse = req.bodyToMono(Course.class);

        return monoCourse
                .flatMap(requestValidator::validate)
                .flatMap(service::save)
                .flatMap(course -> ServerResponse
                        .created(URI.create(req.uri().toString().concat("/").concat(course.getIdCourse())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(course)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> update(ServerRequest req){
        String id = req.pathVariable("id");

        Mono<Course> monoCourse = req.bodyToMono(Course.class);
        Mono<Course> monoCourseDB = service.findById(id);

        return monoCourseDB
                .zipWith(monoCourse, (db, di) -> {
                    db.setIdCourse(id);
                    db.setName(di.getName());
                    db.setAcronym(di.getAcronym());
                    db.setStatus(di.getStatus());
                    return db;
                })
                .flatMap(requestValidator::validate)
                .flatMap(service::update)
                .flatMap(course -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(course)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> delete(ServerRequest req){
        String id = req.pathVariable("id");

        return service.findById(id)
                .flatMap(course -> service.deleteById(course.getIdCourse())
                        .then(ServerResponse.noContent().build()))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}
