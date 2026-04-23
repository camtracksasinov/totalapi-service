// 
// Decompiled by Procyon v0.5.30
// 

package io.camtrack.totalapi.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.camtrack.totalapi.user.entities.Endpoints;

public interface EndpointsRepository extends JpaRepository<Endpoints, Integer> {
	List<Endpoints> findByDescription(final String description);
}
