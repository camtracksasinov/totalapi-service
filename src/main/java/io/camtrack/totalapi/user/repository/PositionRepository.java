// 
// Decompiled by Procyon v0.5.30
// 

package io.camtrack.totalapi.user.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.camtrack.totalapi.user.entities.Positions;
import io.camtrack.totalapi.user.entities.Vehicle;

public interface PositionRepository extends JpaRepository<Positions, Integer> {
	@Query("from  Positions posi  where posi.vehicleid.vehicledesc  =:vehicleName")
	List<Positions> findByVehicleName(@Param("vehicleName") final String vehicleName);

	@Query("from  Positions posi  where posi.vehicleid  =:truck and posi.eventtimestamp between :date1 and :date2")
	List<Positions> findByVehicleRange(@Param("truck") final Vehicle truck, @Param("date1") final Date date1,
			@Param("date2") final Date date2);
}
