package com.jcedenon.service;

import com.jcedenon.model.Student;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ICRUD<T, ID> {

    Mono<T> save(T t);

    Mono<T> update(T t);

    Flux<T> findAll();

    Mono<T> findById(ID id);

    Mono<Void> deleteById(ID id);
}
