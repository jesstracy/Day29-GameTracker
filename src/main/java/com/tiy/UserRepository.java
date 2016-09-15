package com.tiy;

import org.springframework.data.repository.CrudRepository;

/**
 * Created by jessicatracy on 9/15/16.
 */
public interface UserRepository extends CrudRepository<User, Integer> {
    User findFirstByName(String name);
}
