package com.example.rakesh.mysql.dao;

import com.example.rakesh.mysql.model.VerificationToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends CrudRepository<VerificationToken, Integer> {
	VerificationToken findByToken(String token);

	VerificationToken save(VerificationToken token);

	void delete(VerificationToken verificationToken);
}
