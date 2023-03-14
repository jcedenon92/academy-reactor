package com.jcedenon.handler;

import com.jcedenon.dto.ValidationDTO;
import com.jcedenon.model.Student;
import com.jcedenon.service.IStudentService;
import com.jcedenon.validator.RequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
@RequiredArgsConstructor
public class StudentHandler {

//    private final Validator validator;
    private final RequestValidator requestValidator;
    private final IStudentService service;

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
                .body(service.findAll(), Student.class);
    }

    public Mono<ServerResponse> findById(ServerRequest req){
        String id = req.pathVariable("id");

        return service.findById(id)
                .flatMap( student -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(student)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> create(ServerRequest req){
        Mono<Student> monoStudent = req.bodyToMono(Student.class);

        /*return monoStudent
                .flatMap(s -> {
                    Errors errors = new BeanPropertyBindingResult(s, Student.class.getName());
                    validator.validate(s, errors);

                    if (errors.hasErrors()){
                        return Flux.fromIterable(errors.getFieldErrors())
                                .map(error -> new ValidationDTO(error.getField(), error.getDefaultMessage()))
                                .collectList()
                                .flatMap(list -> ServerResponse
                                        .badRequest()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(fromValue(list))
                                );
                    }else {
                        return service.save(s)
                                .flatMap(studentDB -> ServerResponse
                                        .created(URI.create(req.uri().toString().concat("/").concat(studentDB.getIdStudent())))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(fromValue(studentDB))
                                );
                    }

                });*/

        return monoStudent
                .flatMap(requestValidator::validate)
                .flatMap(service::save)
                .flatMap(student -> ServerResponse
                        .created(URI.create(req.uri().toString().concat("/").concat(student.getIdStudent())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(student)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> update(ServerRequest req){
        String id = req.pathVariable("id");

        Mono<Student> monoStudent = req.bodyToMono(Student.class);
        Mono<Student> monoStudentDB = service.findById(id);

        return monoStudentDB
                .zipWith(monoStudent, (db, di) -> {
                    db.setIdStudent(id);
                    db.setNames(di.getNames());
                    db.setSurname(di.getSurname());
                    db.setDni(di.getDni());
                    db.setAge(di.getAge());
                    return db;
                })
                .flatMap(requestValidator::validate)
                .flatMap(service::update)
                .flatMap(student -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(student)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> delete(ServerRequest req){
        String id = req.pathVariable("id");

        return service.findById(id)
                .flatMap(student -> service.deleteById(student.getIdStudent())
                        .then(ServerResponse.noContent().build()))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}
