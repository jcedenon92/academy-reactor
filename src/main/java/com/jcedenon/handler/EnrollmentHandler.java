package com.jcedenon.handler;

import com.jcedenon.model.Enrollment;
import com.jcedenon.service.IEnrollmentService;
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
public class EnrollmentHandler {

    private final RequestValidator requestValidator;
    private final IEnrollmentService service;

    public Mono<ServerResponse> findAll(ServerRequest req){
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.findAll(), Enrollment.class);
    }

    public Mono<ServerResponse> findById(ServerRequest req){
        String id = req.pathVariable("id");

        return service.findById(id)
                .flatMap( enrollment -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(enrollment)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> create(ServerRequest req){
        Mono<Enrollment> monoEnrollment = req.bodyToMono(Enrollment.class);

        return monoEnrollment
                .flatMap(requestValidator::validate)
                .flatMap(service::save)
                .flatMap(enrollment -> ServerResponse
                        .created(URI.create(req.uri().toString().concat("/").concat(enrollment.getIdEnrollment())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(enrollment)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> update(ServerRequest req){
        String id = req.pathVariable("id");

        Mono<Enrollment> monoEnrollment = req.bodyToMono(Enrollment.class);
        Mono<Enrollment> monoEnrollmentDB = service.findById(id);

        return monoEnrollmentDB
                .zipWith(monoEnrollment, (db, di) -> {
                    db.setIdEnrollment(id);
                    db.setStudent(di.getStudent());
                    db.setDateEnrollment(di.getDateEnrollment());
                    db.setState(di.getState());
                    db.setCourses(di.getCourses());
                    return db;
                })
                .flatMap(requestValidator::validate)
                .flatMap(service::update)
                .flatMap(enrollment -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(enrollment)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> delete(ServerRequest req){
        String id = req.pathVariable("id");

        return service.findById(id)
                .flatMap(enrollment -> service.deleteById(enrollment.getIdEnrollment())
                        .then(ServerResponse.noContent().build()))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}
