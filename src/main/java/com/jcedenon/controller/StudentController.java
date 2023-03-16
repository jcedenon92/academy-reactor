package com.jcedenon.controller;

import com.jcedenon.model.Student;
import com.jcedenon.service.IStudentService;
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
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final IStudentService service;

    @GetMapping
//    public Mono<ResponseEntity<List<Student>>> findAll(){   //OPCION 2
        public Mono<ResponseEntity<Flux<Student>>> findAll(){

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
        /*Flux<Student> fx = service.findAll(); //Flux<Student>

        return Mono.just(ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(fx))
                .defaultIfEmpty(ResponseEntity.noContent().build());*/
    }

    @GetMapping("{id}")
    public Mono<ResponseEntity<Student>> findById(@PathVariable("id") String id){
        return service.findById(id)
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e)
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Student>> save(@Valid @RequestBody Student student, final ServerHttpRequest req){
        return service.save(student)
                .map(e -> ResponseEntity
                        .created(URI.create(req.getURI().toString().concat("/").concat(e.getIdStudent())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("{id}")
    public Mono<ResponseEntity<Student>> update(@PathVariable("id") String id, @RequestBody Student student){
        student.setIdStudent(id);

        Mono<Student> monoBody = Mono.just(student);
        Mono<Student> monoDB = service.findById(id);

        return monoDB.zipWith(monoBody, (db, b) -> {
            db.setIdStudent(id);
            db.setNames(b.getNames());
            db.setSurname(b.getSurname());
            db.setDni(b.getDni());
            db.setAge(b.getAge());
            return db;
        })
                .flatMap(service::update)
                .map(e -> ResponseEntity
                        .created(URI.create("/api/students/".concat(e.getIdStudent())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    public Mono<ResponseEntity<Void>> deleteById(@PathVariable("id") String id){

        return service.findById(id)
                .flatMap(e -> service.deleteById(e.getIdStudent())
                    //.then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                    .thenReturn(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /*
    RETURN STUDENTS ORDERED BY AGE
    PARAMS: ASC or DESC
     */
    @GetMapping("/order/age/{order}")
    public Mono<ResponseEntity<Flux<Student>>> findStudentsByOrderByAge(@PathVariable("order") String order){
        Flux<Student> fx = service.findStudentsByOrderByAge(order); //Flux<Student>

        return Mono.just(ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fx))
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }
}
