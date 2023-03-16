package com.jcedenon.service;

import com.jcedenon.model.User;
//import com.jcedenon.security.User;
import reactor.core.publisher.Mono;

public interface IUserService extends ICRUD<User, String>{

    Mono<User> saveHas(User user);

    Mono<com.jcedenon.security.User> searchByUser(String username);
}
