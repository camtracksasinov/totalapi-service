// 
// Decompiled by Procyon v0.5.30
// 

package io.camtrack.totalapi.user.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.camtrack.totalapi.user.entities.Apiaccount;

public interface ApiaccountRepository extends JpaRepository<Apiaccount, Integer> {
	List<Apiaccount> findByToken(final String token);

	@Query("from Apiaccount apiacc where apiacc.token =:token and apiacc.actives = true and apiacc.validdate >= :date")
	Optional<Apiaccount> findByActiveToken(final String token, final Date date);

	@Query("delete from Apiaccount apcc where apcc.id=:ids")
	Integer deleteByIdApiAcc(final Long ids);
}
