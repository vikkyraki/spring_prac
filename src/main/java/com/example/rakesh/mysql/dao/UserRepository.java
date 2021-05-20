package com.example.rakesh.mysql.dao;


import com.example.rakesh.mysql.model.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Integer> {
	UserEntity findByEmail(String email);

	UserEntity save(UserEntity userEntity);
}
