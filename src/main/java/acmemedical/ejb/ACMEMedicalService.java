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
package acmemedical.ejb;

import static acmemedical.utility.MyConstants.DEFAULT_KEY_SIZE;
import static acmemedical.utility.MyConstants.DEFAULT_PROPERTY_ALGORITHM;
import static acmemedical.utility.MyConstants.DEFAULT_PROPERTY_ITERATIONS;
import static acmemedical.utility.MyConstants.DEFAULT_SALT_SIZE;
import static acmemedical.utility.MyConstants.DEFAULT_USER_PASSWORD;
import static acmemedical.utility.MyConstants.DEFAULT_USER_PREFIX;
import static acmemedical.utility.MyConstants.PARAM1;
import static acmemedical.utility.MyConstants.PROPERTY_ALGORITHM;
import static acmemedical.utility.MyConstants.PROPERTY_ITERATIONS;
import static acmemedical.utility.MyConstants.PROPERTY_KEY_SIZE;
import static acmemedical.utility.MyConstants.PROPERTY_SALT_SIZE;
import static acmemedical.utility.MyConstants.PU_NAME;
import static acmemedical.utility.MyConstants.USER_ROLE;
import static acmemedical.entity.Physician.ALL_PHYSICIANS_QUERY_NAME;
import static acmemedical.entity.MedicalSchool.ALL_MEDICAL_SCHOOLS_QUERY_NAME;
import static acmemedical.entity.MedicalSchool.IS_DUPLICATE_QUERY_NAME;
import static acmemedical.entity.MedicalSchool.SPECIFIC_MEDICAL_SCHOOL_QUERY_NAME;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import jakarta.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import acmemedical.entity.MedicalTraining;
import acmemedical.entity.Patient;
import acmemedical.entity.MedicalCertificate;
import acmemedical.entity.Medicine;
import acmemedical.entity.Prescription;
import acmemedical.entity.PrescriptionPK;
import acmemedical.entity.SecurityRole;
import acmemedical.entity.SecurityUser;
import acmemedical.entity.Physician;
import acmemedical.entity.MedicalSchool;

@SuppressWarnings("unused")

/**
 * Stateless Singleton EJB Bean - ACMEMedicalService
 */
@Singleton
public class ACMEMedicalService implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private static final Logger LOG = LogManager.getLogger();
    
    @PersistenceContext(name = PU_NAME)
    protected EntityManager em;
    
    @Inject
    protected Pbkdf2PasswordHash pbAndjPasswordHash;

    public List<Physician> getAllPhysicians() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Physician> cq = cb.createQuery(Physician.class);
        cq.select(cq.from(Physician.class));
        return em.createQuery(cq).getResultList();
    }

    public Physician getPhysicianById(int id) {
        return em.find(Physician.class, id);
    }

    @Transactional
    public Physician persistPhysician(Physician newPhysician) {
        em.persist(newPhysician);
        return newPhysician;
    }

    @Transactional
    public void buildUserForNewPhysician(Physician newPhysician) {
    	newPhysician = em.merge(newPhysician);
        SecurityUser userForNewPhysician = new SecurityUser();
        userForNewPhysician.setUsername(
            DEFAULT_USER_PREFIX + "_" + newPhysician.getFirstName() + "." + newPhysician.getLastName());
        Map<String, String> pbAndjProperties = new HashMap<>();
        pbAndjProperties.put(PROPERTY_ALGORITHM, DEFAULT_PROPERTY_ALGORITHM);
        pbAndjProperties.put(PROPERTY_ITERATIONS, DEFAULT_PROPERTY_ITERATIONS);
        pbAndjProperties.put(PROPERTY_SALT_SIZE, DEFAULT_SALT_SIZE);
        pbAndjProperties.put(PROPERTY_KEY_SIZE, DEFAULT_KEY_SIZE);
        pbAndjPasswordHash.initialize(pbAndjProperties);
        String pwHash = pbAndjPasswordHash.generate(DEFAULT_USER_PASSWORD.toCharArray());
        userForNewPhysician.setPwHash(pwHash);
        userForNewPhysician.setPhysician(newPhysician);
        TypedQuery<SecurityRole> findUserRole = em.createNamedQuery("SecurityRole.findByRoleName", SecurityRole.class);
        findUserRole.setParameter("roleName", USER_ROLE);
        SecurityRole userRole = findUserRole.getSingleResult();
        userForNewPhysician.getRoles().add(userRole);
        userRole.getUsers().add(userForNewPhysician);
        em.persist(userForNewPhysician);
    }

    @Transactional
    public Medicine setMedicineForPhysicianPatient(int physicianId, int patientId, Medicine newMedicine) {
        Physician physicianToBeUpdated = em.find(Physician.class, physicianId);
        if (physicianToBeUpdated != null) { // Physician exists
            Set<Prescription> prescriptions = physicianToBeUpdated.getPrescriptions();
            prescriptions.forEach(p -> {
                if (p.getPatient().getId() == patientId) {
                    if (p.getMedicine() != null) { // Medicine exists
                        Medicine medicine = em.find(Medicine.class, p.getMedicine().getId());
                        medicine.setMedicine(newMedicine.getDrugName(),
                        				  newMedicine.getManufacturerName(),
                        				  newMedicine.getDosageInformation());
                        em.merge(medicine);
                    }
                    else { // Medicine does not exist
                        p.setMedicine(newMedicine);
                        em.merge(physicianToBeUpdated);
                    }
                }
            });
            return newMedicine;
        }
        else return null;  // Physician doesn't exists
    }

    /**
     * To update a physician
     * 
     * @param id - id of entity to update
     * @param physicianWithUpdates - entity with updated information
     * @return Entity with updated information
     */
    @Transactional
    public Physician updatePhysicianById(int id, Physician physicianWithUpdates) {
    	Physician physicianToBeUpdated = getPhysicianById(id);
        if (physicianToBeUpdated != null) {
            em.refresh(physicianToBeUpdated);
            em.merge(physicianWithUpdates);
            em.flush();
        }
        return physicianToBeUpdated;
    }

    /**
     * To delete a physician by id
     * 
     * @param id - physician id to delete
     */
    @Transactional
    public void deletePhysicianById(int id) {
        Physician physician = getPhysicianById(id);
        if (physician != null) {
            em.refresh(physician);
            TypedQuery<SecurityUser> findUser = em.createNamedQuery("SecurityUser.findByPhysicianId", SecurityUser.class);
            findUser.setParameter("physicianId", id);
            SecurityUser sUser = findUser.getSingleResult();
            sUser = em.merge(sUser);
            em.remove(sUser);
            em.remove(physician);
        }
    }
    
    public List<MedicalSchool> getAllMedicalSchools() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<MedicalSchool> cq = cb.createQuery(MedicalSchool.class);
        cq.select(cq.from(MedicalSchool.class));
        return em.createQuery(cq).getResultList();
    }

    // Why not use the build-in em.find?  The named query SPECIFIC_MEDICAL_SCHOOL_QUERY_NAME
    // includes JOIN FETCH that we cannot add to the above API
    public MedicalSchool getMedicalSchoolById(int id) {
        TypedQuery<MedicalSchool> specificMedicalSchoolQuery = em.createNamedQuery(SPECIFIC_MEDICAL_SCHOOL_QUERY_NAME, MedicalSchool.class);
        specificMedicalSchoolQuery.setParameter(PARAM1, id);
        return specificMedicalSchoolQuery.getSingleResult();
    }
    
    // These methods are more generic.

    public <T> List<T> getAll(Class<T> entity, String namedQuery) {
        TypedQuery<T> allQuery = em.createNamedQuery(namedQuery, entity);
        return allQuery.getResultList();
    }
    
    public <T> T getById(Class<T> entity, String namedQuery, int id) {
        TypedQuery<T> allQuery = em.createNamedQuery(namedQuery, entity);
        allQuery.setParameter(PARAM1, id);
        return allQuery.getSingleResult();
    }

    @Transactional
    public MedicalSchool deleteMedicalSchool(int id) {
        //MedicalSchool ms = getMedicalSchoolById(id);
    	MedicalSchool ms = getById(MedicalSchool.class, MedicalSchool.SPECIFIC_MEDICAL_SCHOOL_QUERY_NAME, id);
        if (ms != null) {
            Set<MedicalTraining> medicalTrainings = ms.getMedicalTrainings();
            List<MedicalTraining> list = new LinkedList<>();
            medicalTrainings.forEach(list::add);
            list.forEach(mt -> {
                if (mt.getCertificate() != null) {
                    MedicalCertificate mc = getById(MedicalCertificate.class, MedicalCertificate.ID_CARD_QUERY_NAME, mt.getCertificate().getId());
                    mc.setMedicalTraining(null);
                }
                mt.setCertificate(null);
                em.merge(mt);
            });
            em.remove(ms);
            return ms;
        }
        return null;
    }
    
    // Please study & use the methods below in your test suites
    
    public boolean isDuplicated(MedicalSchool newMedicalSchool) {
        TypedQuery<Long> allMedicalSchoolsQuery = em.createNamedQuery(IS_DUPLICATE_QUERY_NAME, Long.class);
        allMedicalSchoolsQuery.setParameter(PARAM1, newMedicalSchool.getName());
        return (allMedicalSchoolsQuery.getSingleResult() >= 1);
    }

    @Transactional
    public MedicalSchool persistMedicalSchool(MedicalSchool newMedicalSchool) {
        em.persist(newMedicalSchool);
        return newMedicalSchool;
    }

    @Transactional
    public MedicalSchool updateMedicalSchool(int id, MedicalSchool updatingMedicalSchool) {
    	MedicalSchool medicalSchoolToBeUpdated = getMedicalSchoolById(id);
        if (medicalSchoolToBeUpdated != null) {
            em.refresh(medicalSchoolToBeUpdated);
            medicalSchoolToBeUpdated.setName(updatingMedicalSchool.getName());
            em.merge(medicalSchoolToBeUpdated);
            em.flush();
        }
        return medicalSchoolToBeUpdated;
    }
    
    @Transactional
    public MedicalTraining persistMedicalTraining(MedicalTraining newMedicalTraining) {
        em.persist(newMedicalTraining);
        return newMedicalTraining;
    }
    
    public MedicalTraining getMedicalTrainingById(int mtId) {
        TypedQuery<MedicalTraining> allMedicalTrainingQuery = em.createNamedQuery(MedicalTraining.FIND_BY_ID, MedicalTraining.class);
        allMedicalTrainingQuery.setParameter(PARAM1, mtId);
        return allMedicalTrainingQuery.getSingleResult();
    }

    @Transactional
    public MedicalTraining updateMedicalTraining(int id, MedicalTraining medicalTrainingWithUpdates) {
    	MedicalTraining medicalTrainingToBeUpdated = getMedicalTrainingById(id);
        if (medicalTrainingToBeUpdated != null) {
            em.refresh(medicalTrainingToBeUpdated);
            em.merge(medicalTrainingWithUpdates);
            em.flush();
        }
        return medicalTrainingToBeUpdated;
    }
    
    public Patient getPatientById(int id) {
        return em.find(Patient.class, id);
    }
    
    public List<Patient> getAllPatients() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Patient> cq = cb.createQuery(Patient.class);
        cq.select(cq.from(Patient.class));
        return em.createQuery(cq).getResultList();
    }
    
    @Transactional
    public Patient updatePatientById(int id, Patient patientWithUpdates) {
    	Patient patientToBeUpdated = getPatientById(id);
        if (patientToBeUpdated != null) {
        	em.refresh(patientToBeUpdated);
        	patientToBeUpdated.setFirstName(patientWithUpdates.getFirstName());
        	patientToBeUpdated.setLastName(patientWithUpdates.getLastName());
        	patientToBeUpdated.setYear(patientWithUpdates.getYear());
        	patientToBeUpdated.setAddress(patientWithUpdates.getAddress());
        	patientToBeUpdated.setHeight(patientWithUpdates.getHeight());
        	patientToBeUpdated.setWeight(patientWithUpdates.getWeight());
        	patientToBeUpdated.setSmoker(patientWithUpdates.getSmoker());
            em.merge(patientToBeUpdated);
            em.flush();
        }
        return patientToBeUpdated;
    }
    
    @Transactional
    public void deletePatientById(int id) {
        Patient patient = getPatientById(id);
        if (patient != null) {
            em.refresh(patient);
            em.remove(patient);
        }
    }
    
    @Transactional
    public Patient persistPatient(Patient newPatient) {
        em.persist(newPatient);
        return newPatient;
    }

    public Prescription getPrescriptionById(int physicianId, int patientId) {
        TypedQuery<Prescription> query = em.createNamedQuery("Prescription.findByIds", Prescription.class);
        query.setParameter("physicianId", physicianId);
        query.setParameter("patientId", patientId);
        return query.getSingleResult();
    }
    
    @Transactional
    public Prescription persistPrescription(Prescription newPrescription) {
        Physician physician = em.find(Physician.class, newPrescription.getPhysician().getId());
        Patient patient = em.find(Patient.class, newPrescription.getPatient().getId());
        Medicine medicine = em.find(Medicine.class, newPrescription.getMedicine().getId());
        newPrescription.setPhysician(physician);
        newPrescription.setPatient(patient);
        newPrescription.setMedicine(medicine);
        
        em.persist(newPrescription);
        return newPrescription;
    }

    @Transactional
    public Prescription updatePrescription(int physicianId, int patientId, Prescription prescriptionWithUpdates) {
        Prescription prescriptionToBeUpdated = getPrescriptionById(physicianId, patientId);
        if (prescriptionToBeUpdated != null) {
            em.refresh(prescriptionToBeUpdated);
            prescriptionToBeUpdated.setMedicine(prescriptionWithUpdates.getMedicine());
            prescriptionToBeUpdated.setNumberOfRefills(prescriptionWithUpdates.getNumberOfRefills());
            prescriptionToBeUpdated.setPrescriptionInformation(prescriptionWithUpdates.getPrescriptionInformation());
            em.merge(prescriptionToBeUpdated);
            em.flush();
        }
        return prescriptionToBeUpdated;
    }

    @Transactional
    public void deletePrescription(int physicianId, int patientId) {
        Prescription prescription = getPrescriptionById(physicianId, patientId);
        if (prescription != null) {
            em.refresh(prescription);
            prescription.setMedicine(null);
            em.remove(prescription);
        }
    }
    
    @Transactional
    public Medicine persistMedicine(Medicine newMedicine) {
        em.persist(newMedicine);
        return newMedicine;
    }
    
    @Transactional
    public Medicine updateMedicine(int medicineId, Medicine medicineWithUpdates) {
    	Medicine medicineToBeUpdated = getById(Medicine.class, "Medicine.findById", medicineId);
        if (medicineToBeUpdated != null) {
            em.refresh(medicineToBeUpdated);
            medicineToBeUpdated.setDrugName(medicineWithUpdates.getDrugName());
            medicineToBeUpdated.setManufacturerName(medicineWithUpdates.getManufacturerName());
            medicineToBeUpdated.setDosageInformation(medicineWithUpdates.getDosageInformation());
            em.merge(medicineToBeUpdated);
            em.flush();
        }
        return medicineToBeUpdated;
    }
    
    @Transactional
    public void deleteMedicine(int id) {
    	Medicine medicine = getById(Medicine.class, "Medicine.findById", id);
        if (medicine != null) {
            em.refresh(medicine);
            em.remove(medicine);
        }
    }
    
    @Transactional
    public void deleteMedicalTraining(int id) {
    	MedicalTraining training = getById(MedicalTraining.class, MedicalTraining.FIND_BY_ID, id);
        if (training != null) {
            em.refresh(training);
            if (training.getCertificate() != null) {
                MedicalCertificate certificate = training.getCertificate();
                certificate.setMedicalTraining(null);
                em.merge(certificate);
            }
            if (training.getMedicalSchool() != null) {
                MedicalSchool school = training.getMedicalSchool();
                school.getMedicalTrainings().remove(training);
                em.merge(school);
            }
            em.remove(training);
        }
    }

    @Transactional
    public MedicalCertificate persistMedicalCertificate(MedicalCertificate newMedicalCertificate) {
        em.persist(newMedicalCertificate);
        return newMedicalCertificate;
    }

    @Transactional
    public void deleteMedicalCertificate(int id) {
        MedicalCertificate medicalCertificate = getById(MedicalCertificate.class, MedicalCertificate.ID_CARD_QUERY_NAME, id);
        if (medicalCertificate != null) {
            em.refresh(medicalCertificate);
            if (medicalCertificate.getMedicalTraining() != null) {
                medicalCertificate.getMedicalTraining().setCertificate(null);
            }
            em.remove(medicalCertificate);
        }
    }
}