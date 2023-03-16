package com.jcedenon.service.impl;

import com.jcedenon.model.User;
import com.jcedenon.repo.IGenericRepo;
import com.jcedenon.repo.IRoleRepo;
import com.jcedenon.repo.IUserRepo;
import com.jcedenon.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends CRUDImpl<User, String> implements IUserService {

    private final IUserRepo repo;

    private final IRoleRepo roleRepo;

    private final BCryptPasswordEncoder bCrypt;

    @Override
    protected IGenericRepo<User, String> getRepo() {
        return repo;
    }

    @Override
    public Mono<User> saveHas(User user) {
        user.setPassword(bCrypt.encode(user.getPassword()));
        return repo.save(user);
    }

    @Override
    public Mono<com.jcedenon.security.User> searchByUser(String username) {
        Mono<User> monoUser = repo.findOneByUsername(username);
        List<String> roles = new ArrayList<>();

        return monoUser.flatMap(u -> {
            return Flux.fromIterable(u.getRoles())
                    .flatMap(rol -> {
                        return roleRepo.findById(rol.getId())
                                .map(r -> {
                                    roles.add(r.getName());
                                    return r;
                                });
                    }).collectList().flatMap(list -> {
                        u.setRoles(list);
                        return Mono.just(u);
                    });
        }).flatMap(u -> Mono.just(new com.jcedenon.security.User(u.getUsername(), u.getPassword(), u.getStatus(), roles)));
    }
}