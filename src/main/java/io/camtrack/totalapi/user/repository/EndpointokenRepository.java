// 
// Decompiled by Procyon v0.5.30
// 

package io.camtrack.totalapi.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.camtrack.totalapi.user.entities.Apiaccount;
import io.camtrack.totalapi.user.entities.Endpointoken;

public interface EndpointokenRepository extends JpaRepository<Endpointoken, Integer> {
	@Query("from Endpointoken endpt where endpt.apiaccountdid = :apiacc and endpt.endpointtid.ids = :endpointokenid")
	Optional<Endpointoken> findByApiAccountEndPoint(final Apiaccount apiacc, final Integer endpointokenid);
}
