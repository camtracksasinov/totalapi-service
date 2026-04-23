// 
// Decompiled by Procyon v0.5.30
// 

package io.camtrack.totalapi.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.camtrack.totalapi.user.entities.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
	@Query("from  Vehicle trk  where trk.vehicledesc  =:immat")
	Optional<Vehicle> findByImmat(final String immat);

	@Query("from  Vehicle trk  where trk.vehicleid  =:vehicleId")
	Optional<Vehicle> findByVehicleId(final Integer vehicleId);
}
