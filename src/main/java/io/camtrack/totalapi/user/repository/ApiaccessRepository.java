// 
// Decompiled by Procyon v0.5.30
// 

package io.camtrack.totalapi.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.camtrack.totalapi.user.entities.Apiaccess;

public interface ApiaccessRepository extends JpaRepository<Apiaccess, Integer> {
	/**
	 * @Query("from Apiaccess apiacc where apiacc.apiaccountdid =:account and
	 * apiacc.platformaccountid=:platformaccount") Optional<Apiaccess>
	 * findByPlatformAndApiaccount(final Apiaccount account, final Platformaccount
	 * platformaccount);
	 */
}
