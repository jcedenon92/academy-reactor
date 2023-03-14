package com.jcedenon.controller;

import com.jcedenon.model.Course;
import com.jcedenon.service.ICourseService;
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

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final ICourseService service;

    @GetMapping
//    public Mono<ResponseEntity<List<Course>>> findAll(){   //OPCION 2
        public Mono<ResponseEntity<Flux<Course>>> findAll(){

        return service.findAll()
                .hasElements()
                .map( status -> {
                    if(status)
                        return ResponseEntity.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(service.findAll());
                     else
                        return ResponseEntity.noContent().build();

                });
        //OPCION 2
        /*return service.findAll()
                .collectList()
                .map(list -> {
                    if(list.isEmpty()){
                        return ResponseEntity.noContent().build();
                    } else {
                        return ResponseEntity.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(list);
                    }
                });*/

        //OPCION 1
        /*Flux<Course> fx = service.findAll(); //Flux<Course>

        return Mono.just(ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(fx))
                .defaultIfEmpty(ResponseEntity.noContent().build());*/
    }

    @GetMapping("{id}")
    public Mono<ResponseEntity<Course>> findById(@PathVariable("id") String id){
        return service.findById(id)
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e)
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Course>> save(@Valid @RequestBody Course course, final ServerHttpRequest req){
        return service.save(course)
                .map(e -> ResponseEntity
                        .created(URI.create(req.getURI().toString().concat("/").concat(e.getIdCourse())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("{id}")
    public Mono<ResponseEntity<Course>> update(@PathVariable("id") String id, @RequestBody Course course){
        course.setIdCourse(id);

        Mono<Course> monoBody = Mono.just(course);
        Mono<Course> monoDB = service.findById(id);

        return monoDB.zipWith(monoBody, (db, b) -> {
            db.setIdCourse(id);
            db.setName(b.getName());
            db.setAcronym(b.getAcronym());
            db.setStatus(b.getStatus());
            return db;
        })
                .flatMap(service::update)
                .map(e -> ResponseEntity
                        .created(URI.create("/api/courses/".concat(e.getIdCourse())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    public Mono<ResponseEntity<Void>> deleteById(@PathVariable("id") String id){

        return service.findById(id)
                .flatMap(e -> service.deleteById(e.getIdCourse())
                    //.then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                    .thenReturn(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
