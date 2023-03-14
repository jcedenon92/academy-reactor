package com.jcedenon.controller;

import com.jcedenon.model.Enrollment;
import com.jcedenon.service.IEnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final IEnrollmentService service;

    @GetMapping
    public Mono<ResponseEntity<List<Enrollment>>> findAll(){   //OPCION 2
//    public Mono<ResponseEntity<Flux<Enrollment>>> findAll(){

        /*return service.findAll()
                .hasElements()
                .map( status -> {
                    if(status)
                        return ResponseEntity.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(service.findAll());
                     else
                        return ResponseEntity.noContent().build();

                });*/

        //OPCION 2
        return service.findAll()
                .collectList()
                .map(list -> {
                    if(list.isEmpty()){
                        return ResponseEntity.noContent().build();
                    } else {
                        return ResponseEntity.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(list);
                    }
                });
    }

    @GetMapping("{id}")
    public Mono<ResponseEntity<Enrollment>> findById(@PathVariable("id") String id){
        return service.findById(id)
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e)
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Enrollment>> save(@Valid @RequestBody Enrollment enrollment, final ServerHttpRequest req){
        return service.save(enrollment)
                .map(e -> ResponseEntity
                        .created(URI.create(req.getURI().toString().concat("/").concat(e.getIdEnrollment())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("{id}")
    public Mono<ResponseEntity<Enrollment>> update(@PathVariable("id") String id, @RequestBody Enrollment enrollment){
        enrollment.setIdEnrollment(id);

        Mono<Enrollment> monoBody = Mono.just(enrollment);
        Mono<Enrollment> monoDB = service.findById(id);

        return monoDB.zipWith(monoBody, (db, b) -> {
            db.setIdEnrollment(id);
            db.setStudent(b.getStudent());
            db.setDateEnrollment(b.getDateEnrollment());
            db.setState(b.getState());
            db.setCourses(b.getCourses());
            return db;
        })
                .flatMap(service::update)
                .map(e -> ResponseEntity
                        .created(URI.create("/api/enrollments/".concat(e.getIdEnrollment())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    public Mono<ResponseEntity<Void>> deleteById(@PathVariable("id") String id){

        return service.findById(id)
                .flatMap(e -> service.deleteById(e.getIdEnrollment())
                    //.then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                    .thenReturn(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
