/********************************************************************************************************
 * File:  ACMEMedicalService.java Course Materials CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @author: professor at Algonquin College
 * modified and updated by group 8
 * 041094775, Tammy Liu (as from ACSIS)
 * 041127152, Yuhang Zhang  (as from ACSIS)
 * 040799347, Stephen Carpenter (as from ACSIS)
 * 040780701, Qi Wu  (as from ACSIS)
 */
package acmemedical.entity;

import java.time.LocalDateTime;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

@SuppressWarnings("unused")

public class PojoCompositeListener {

	@PrePersist
	public void setCreatedOnDate(PojoBaseCompositeKey<?> pojoBaseComposite) {
		LocalDateTime now = LocalDateTime.now();
		pojoBaseComposite.setCreated(now);
		pojoBaseComposite.setUpdated(now);
	}

	@PreUpdate
	public void setUpdatedDate(PojoBaseCompositeKey<?> pojoBaseComposite) {
		pojoBaseComposite.setUpdated(LocalDateTime.now());
	}

}
