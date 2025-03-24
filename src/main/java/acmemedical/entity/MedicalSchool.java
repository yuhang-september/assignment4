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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.AttributeOverride;

/**
 * The persistent class for the medical_school database table.
 */
@Entity
@Table(name = "medical_school")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(columnDefinition = "bit(1)", name = "public", discriminatorType = DiscriminatorType.INTEGER)

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "entity-type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = PublicSchool.class, name = "public_school"),
    @JsonSubTypes.Type(value = PrivateSchool.class, name = "private_school")
})
@NamedQuery(name = "MedicalSchool.findAll", query = "SELECT ms FROM MedicalSchool ms")
@NamedQuery(name = "MedicalSchool.findSpecific", query = "SELECT ms FROM MedicalSchool ms LEFT JOIN FETCH ms.medicalTrainings WHERE ms.id = :param1")
@NamedQuery(name = "MedicalSchool.isDuplicate", query = "SELECT COUNT(ms) FROM MedicalSchool ms WHERE ms.name = :param1")
@AttributeOverride(name = "id", column = @Column(name = "school_id"))
@JsonIgnoreProperties({"entity-type", "hibernateLazyInitializer"})
public abstract class MedicalSchool extends PojoBase implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String ALL_MEDICAL_SCHOOLS_QUERY_NAME = "MedicalSchool.findAll";
	public static final String SPECIFIC_MEDICAL_SCHOOL_QUERY_NAME = "MedicalSchool.findSpecific";
	public static final String IS_DUPLICATE_QUERY_NAME = "MedicalSchool.isDuplicate";
	
	@Column(name = "name", unique = true)
	private String name;

	@OneToMany(mappedBy = "medicalSchool", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonIgnore
	private Set<MedicalTraining> medicalTrainings = new HashSet<>();

	@Transient
	private boolean isPublic;

	public MedicalSchool() {
		super();
	}

    public MedicalSchool(boolean isPublic) {
        this();
        this.isPublic = isPublic;
    }

	public Set<MedicalTraining> getMedicalTrainings() {
		return medicalTrainings;
	}

	public void setMedicalTrainings(Set<MedicalTraining> medicalTrainings) {
		this.medicalTrainings = medicalTrainings;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	//Inherited hashCode/equals is NOT sufficient for this entity class
	
	/**
	 * Very important:  Use getter's for member variables because JPA sometimes needs to intercept those calls<br/>
	 * and go to the database to retrieve the value
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		// Only include member variables that really contribute to an object's identity
		// i.e. if variables like version/updated/name/etc. change throughout an object's lifecycle,
		// they shouldn't be part of the hashCode calculation
		
		// The database schema for the MEDICAL_SCHOOL table has a UNIQUE constraint for the NAME column,
		// so we should include that in the hash/equals calculations
		
		return prime * result + Objects.hash(getId(), getName());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		
		if (obj instanceof MedicalSchool otherMedicalSchool) {
			// See comment (above) in hashCode():  Compare using only member variables that are
			// truly part of an object's identity
			return Objects.equals(this.getId(), otherMedicalSchool.getId()) &&
				Objects.equals(this.getName(), otherMedicalSchool.getName());
		}
		return false;
	}
}
