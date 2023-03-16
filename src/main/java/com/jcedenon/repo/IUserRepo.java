package com.jcedenon.repo;

import com.jcedenon.model.User;
import org.springframework.data.mongodb.repository.Query;
import reactor.core.publisher.Mono;

public interface IUserRepo extends IGenericRepo<User, String>{

//    @Query("{ 'username' : ?0 }")
    Mono<User> findOneByUsername(String username);
}
