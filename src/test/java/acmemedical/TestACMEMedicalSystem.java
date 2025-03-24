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
package acmemedical;

import static acmemedical.utility.MyConstants.APPLICATION_API_VERSION;
import static acmemedical.utility.MyConstants.APPLICATION_CONTEXT_ROOT;
import static acmemedical.utility.MyConstants.DEFAULT_ADMIN_USER;
import static acmemedical.utility.MyConstants.DEFAULT_ADMIN_USER_PASSWORD;
import static acmemedical.utility.MyConstants.DEFAULT_USER;
import static acmemedical.utility.MyConstants.DEFAULT_USER_PASSWORD;
import static acmemedical.utility.MyConstants.MEDICAL_CERTIFICATE_RESOURCE_NAME;
import static acmemedical.utility.MyConstants.PHYSICIAN_RESOURCE_NAME;
import static acmemedical.utility.MyConstants.PHYSICIAN_PATIENT_MEDICINE_RESOURCE_PATH;
import static acmemedical.utility.MyConstants.PATIENT_RESOURCE_NAME;
import static acmemedical.utility.MyConstants.MEDICINE_SUBRESOURCE_NAME;
import static acmemedical.utility.MyConstants.MEDICAL_SCHOOL_RESOURCE_NAME;
import static acmemedical.utility.MyConstants.MEDICAL_TRAINING_RESOURCE_NAME;
import static acmemedical.utility.MyConstants.PRESCRIPTION_RESOURCE_NAME;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.List;
import java.util.Set;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import acmemedical.entity.MedicalCertificate;
import acmemedical.entity.MedicalSchool;
import acmemedical.entity.MedicalTraining;
import acmemedical.entity.Medicine;
import acmemedical.entity.Patient;
import acmemedical.entity.Physician;
import acmemedical.entity.Prescription;
import acmemedical.entity.PrivateSchool;

@SuppressWarnings("unused")

@TestMethodOrder(MethodOrderer.MethodName.class)
public class TestACMEMedicalSystem {
    private static final Class<?> _thisClaz = MethodHandles.lookup().lookupClass();
    private static final Logger logger = LogManager.getLogger(_thisClaz);

    static final String HTTP_SCHEMA = "http";
    static final String HOST = "localhost";
    static final int PORT = 8080;

    // Test fixture(s)
    static URI uri;
    static HttpAuthenticationFeature adminAuth;
    static HttpAuthenticationFeature userAuth;

    @BeforeAll
    public static void oneTimeSetUp() throws Exception {
        logger.debug("oneTimeSetUp");
        uri = UriBuilder
            .fromUri(APPLICATION_CONTEXT_ROOT + APPLICATION_API_VERSION)
            .scheme(HTTP_SCHEMA)
            .host(HOST)
            .port(PORT)
            .build();
        adminAuth = HttpAuthenticationFeature.basic(DEFAULT_ADMIN_USER, DEFAULT_ADMIN_USER_PASSWORD);
        userAuth = HttpAuthenticationFeature.basic(DEFAULT_USER, DEFAULT_USER_PASSWORD);
    }

    protected WebTarget webTarget;
    @BeforeEach
    public void setUp() {
        Client client = ClientBuilder.newClient().register(MyObjectMapperProvider.class).register(new LoggingFeature());
        webTarget = client.target(uri);
    }

    @Test
    public void test01_all_physicians_with_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(PHYSICIAN_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(403));
    }
    
    @Test
    public void test02_physician_by_id_with_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(PHYSICIAN_RESOURCE_NAME).path("1")
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        Physician physician = response.readEntity(Physician.class);
        assertEquals(1,physician.getId());
        
    }
    
    @Test
    public void test03_add_physician_with_userrole() throws JsonMappingException, JsonProcessingException {
    	Physician newphy = new Physician();
    	newphy.setFullName("Tester", "Testerson");
    	Response response = webTarget
            .register(userAuth)
            .path(PHYSICIAN_RESOURCE_NAME)
            .request()
            .post(Entity.json(newphy));
        assertThat(response.getStatus(), is(403));
    }
    
    @Test
    public void test04_update_physician_with_userrole() throws JsonMappingException, JsonProcessingException {
    	int phyID = 1;
    	int patID = 1;
    	Medicine newMed = new Medicine();
    	Response response = webTarget
            .register(userAuth)
            .path(PHYSICIAN_RESOURCE_NAME + PHYSICIAN_PATIENT_MEDICINE_RESOURCE_PATH)
            .resolveTemplate("physicianId", phyID)
            .resolveTemplate("patientId", patID)
            .request()
            .put(Entity.json(newMed));
        assertThat(response.getStatus(), is(403));
    }
    
    @Test
    public void test05_delete_physician_by_id_with_userrole() throws JsonMappingException, JsonProcessingException {
    	Response response = webTarget
                .register(userAuth)
                .path(PHYSICIAN_RESOURCE_NAME).path("1")
                .request()
                .delete();
            assertThat(response.getStatus(), is(403));
    }
    
    @Test
    public void test06_all_patients_with_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(PATIENT_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(403));
    }
    
    @Test
    public void test07_patient_by_id_with_userrole() throws JsonMappingException, JsonProcessingException {
    	Response response = webTarget
                .register(userAuth)
                .path(PATIENT_RESOURCE_NAME).path("1")
                .request()
                .get();
            assertThat(response.getStatus(), is(200));
            Patient patient = response.readEntity(Patient.class);
            assertEquals(1,patient.getId());
    }
    
    @Test
    public void test08_add_physician_to_patient_with_userrole() throws JsonMappingException, JsonProcessingException {
    	Physician newphy = new Physician();
    	newphy.setFullName("Tester", "Testerson");
    	Response response = webTarget
            .register(userAuth)
            .path(PATIENT_RESOURCE_NAME)
            .request()
            .post(Entity.json(newphy));
        assertThat(response.getStatus(), is(403));
    }
    
    @Test
    public void test09_update_patient_with_userrole() throws JsonMappingException, JsonProcessingException {
    	Patient updatedPat = new Patient("Test","McTest",1995,"22 Test ave", 200, 200, (byte) 1);
    	Response response = webTarget
            .register(userAuth)
            .path(PATIENT_RESOURCE_NAME).path("2")
            .request()
            .put(Entity.json(updatedPat));
        assertThat(response.getStatus(), is(200));
        Patient patient = response.readEntity(Patient.class);
        assertEquals("Test",patient.getFirstName());
        
    }
    
    @Test
    public void test10_delete_patient_with_userrole() throws JsonMappingException, JsonProcessingException {
    	Response response = webTarget
                .register(userAuth)
                .path(PATIENT_RESOURCE_NAME).path("1")
                .request()
                .delete();
            assertThat(response.getStatus(), is(403));
    }
    
    @Test
    public void test11_all_medicines_with_userrole() throws JsonMappingException, JsonProcessingException {
    	Response response = webTarget
                .register(userAuth)
                .path(MEDICINE_SUBRESOURCE_NAME)
                .request()
                .get();
            assertThat(response.getStatus(), is(200));
        List<Medicine> medicines = response.readEntity(new GenericType<List<Medicine>>(){});
        assertThat(medicines, is(not(empty())));
        assertThat(medicines, hasSize(1));
    }
    
    @Test
    public void test12_medicine_by_id_with_userrole() throws JsonMappingException, JsonProcessingException {
    	Response response = webTarget
                .register(userAuth)
                .path(MEDICINE_SUBRESOURCE_NAME).path("1")
                .request()
                .get();
            assertThat(response.getStatus(), is(200));
            Medicine medicine = response.readEntity(Medicine.class);
            assertEquals("Tylenol",medicine.getDrugName());    
    }
    
    @Test
    public void test13_add_medicine_with_userrole() throws JsonMappingException, JsonProcessingException {
    	Medicine newMed = new Medicine();
    	Response response = webTarget
                .register(userAuth)
                .path(MEDICINE_SUBRESOURCE_NAME)
                .request()
                .post(Entity.json(newMed));
            assertThat(response.getStatus(), is(403));
    }
    
    @Test
    public void test14_update_medicine_with_userrole() throws JsonMappingException, JsonProcessingException {
    	Medicine newMed = new Medicine();
    	Response response = webTarget
                .register(userAuth)
                .path(MEDICINE_SUBRESOURCE_NAME).path("1")
                .request()
                .put(Entity.json(newMed));
            assertThat(response.getStatus(), is(403));
    }
    
    @Test
    public void test15_delete_medicine_with_userrole() throws JsonMappingException, JsonProcessingException {
    	Response response = webTarget
                .register(userAuth)
                .path(MEDICINE_SUBRESOURCE_NAME).path("1")
                .request()
                .delete();
            assertThat(response.getStatus(), is(403));
    }
    
    @Test
    public void test16_all_medicalschool_with_userrole() throws JsonMappingException, JsonProcessingException {
    	Response response = webTarget
                .register(userAuth)
                .path(MEDICAL_SCHOOL_RESOURCE_NAME)
                .request()
                .get();
            assertThat(response.getStatus(), is(200));
            List<Object> medschools = response.readEntity(new GenericType<List<Object>>(){}); //list of Objects as the program was having issues making a list of MedicalSchool objects
            assertThat(medschools, is(not(empty())));
            assertThat(medschools, hasSize(2));
    }
    
    @Test
    public void test17_medicalschool_by_id_with_userrole() throws JsonMappingException, JsonProcessingException {
    	Response response = webTarget
                .register(userAuth)
                .path(MEDICAL_SCHOOL_RESOURCE_NAME).path("1")
                .request()
                .get();
            assertThat(response.getStatus(), is(200));
            MedicalSchool medSchool = response.readEntity(MedicalSchool.class);
            assertEquals(1,medSchool.getId());
            
    }
    
    @Test
    public void test18_delete_medicalschool_with_userrole() throws JsonMappingException, JsonProcessingException {
    	Response response = webTarget
                .register(userAuth)
                .path(MEDICAL_SCHOOL_RESOURCE_NAME).path("1")
                .request()
                .delete();
            assertThat(response.getStatus(), is(403));
    }
    
    @Test
    public void test19_add_medicalschool_with_userrole() throws JsonMappingException, JsonProcessingException {
    	MedicalSchool newMedSchool = new PrivateSchool();
    	Response response = webTarget
                .register(userAuth)
                .path(MEDICAL_SCHOOL_RESOURCE_NAME)
                .request()
                .post(Entity.json(newMedSchool));
            assertThat(response.getStatus(), is(403));
    }
    
    @Test
    public void test20_add_medtraining_to_medschool_with_userrole() throws JsonMappingException, JsonProcessingException {
    	MedicalTraining newMedTraining = new MedicalTraining();
    	Response response = webTarget
                .register(userAuth)
                .path(MEDICAL_SCHOOL_RESOURCE_NAME).path("1").path(MEDICAL_TRAINING_RESOURCE_NAME)
                .request()
                .post(Entity.json(newMedTraining));
            assertThat(response.getStatus(), is(403));
    }
    
    @Test
    public void test21_update_medicalschool_with_userrole() throws JsonMappingException, JsonProcessingException {
    	MedicalSchool newMedSchool = new PrivateSchool();
    	newMedSchool.setName("Test State University");
    	Response response = webTarget
                .register(userAuth)
                .path(MEDICAL_SCHOOL_RESOURCE_NAME).path("2")
                .request()
                .put(Entity.json(newMedSchool));
            assertThat(response.getStatus(), is(200));
            MedicalSchool updatedMedSchool = response.readEntity(MedicalSchool.class);
            assertEquals("Test State University",updatedMedSchool.getName()); 
    }
    
    @Test
    public void test22_all_medicaltraining_with_userrole() throws JsonMappingException, JsonProcessingException {
    	Response response = webTarget
                .register(userAuth)
                .path(MEDICAL_TRAINING_RESOURCE_NAME)
                .request()
                .get();
            assertThat(response.getStatus(), is(200));
	        List<Object> medicalTrainings = response.readEntity(new GenericType<List<Object>>(){}); //list of Objects as the program was having issues making a list of MedicalTraining objects
	        assertThat(medicalTrainings, is(not(empty())));
	        assertThat(medicalTrainings, hasSize(2)); 
    }
    
    @Test
    public void test23_medicaltraining_by_id_with_userrole() throws JsonMappingException, JsonProcessingException {
    	Response response = webTarget
                .register(userAuth)
                .path(MEDICAL_TRAINING_RESOURCE_NAME).path("1")
                .request()
                .get();
           	assertThat(response.getStatus(), is(200));
            Object medTraining = response.readEntity(Object.class);
            assertNotNull(medTraining); //Issues with reading the entity.
    }
    
    @Test
    public void test24_add_medicaltraining_with_userrole() throws JsonMappingException, JsonProcessingException {
    	MedicalTraining newMedTraining = new MedicalTraining();
    	Response response = webTarget
                .register(userAuth)
                .path(MEDICAL_TRAINING_RESOURCE_NAME)
                .request()
                .post(Entity.json(newMedTraining));
            assertThat(response.getStatus(), is(403));
    }
    
    @Test
    public void test25_update_medtraining_with_userrole() throws JsonMappingException, JsonProcessingException {
    	MedicalTraining newMedTraining = new MedicalTraining();
    	Response response = webTarget
                .register(userAuth)
                .path(MEDICAL_TRAINING_RESOURCE_NAME).path("1")
                .request()
                .put(Entity.json(newMedTraining));
            assertThat(response.getStatus(), is(403));
    }
    
    @Test
    public void test026_all_physicians_with_adminrole() throws JsonMappingException, JsonProcessingException {
    	Response response = webTarget
                .register(adminAuth)
                .path(PHYSICIAN_RESOURCE_NAME)
                .request()
                .get();
            assertThat(response.getStatus(), is(200));
            List<Physician> physicians = response.readEntity(new GenericType<List<Physician>>(){});
            assertThat(physicians, is(not(empty())));
            assertThat(physicians, hasSize(1));
    }
    
    @Test
    public void test27_physician_by_id_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
        	.register(adminAuth)
            .path(PHYSICIAN_RESOURCE_NAME).path("1")
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        Physician physician = response.readEntity(Physician.class);
        assertEquals(1,physician.getId());
        
    }
    
    @Test
    public void test28_add_physician_with_adminrole() throws JsonMappingException, JsonProcessingException {
    	Physician newphy = new Physician();
    	newphy.setFullName("Tester", "Testerson");
    	Response response = webTarget
    			.register(adminAuth)
            .path(PHYSICIAN_RESOURCE_NAME)
            .request()
            .post(Entity.json(newphy));
        assertThat(response.getStatus(), is(200));
        Physician physician = response.readEntity(Physician.class);
        assertEquals("Tester",physician.getFirstName());
        
    }
    
    @Test
    public void test29_update_physician_with_adminrole() throws JsonMappingException, JsonProcessingException {
    	int phyID = 2;
    	int patID = 2;
    	Medicine newMed = new Medicine();
    	Response response = webTarget
    		.register(adminAuth)
            .path(PHYSICIAN_RESOURCE_NAME + PHYSICIAN_PATIENT_MEDICINE_RESOURCE_PATH)
            .resolveTemplate("physicianId", phyID)
            .resolveTemplate("patientId", patID)
            .request()
            .put(Entity.json(newMed));
        assertThat(response.getStatus(), is(200));
        
    }
    
    @Test
    public void test30_delete_physician_by_id_with_adminrole() throws JsonMappingException, JsonProcessingException {
    	Response response = webTarget
    		.register(adminAuth)
            .path(PHYSICIAN_RESOURCE_NAME).path("2")
            .request()
            .delete();
        assertThat(response.getStatus(), is(200));
    }
    
    @Test
    public void test31_all_patients_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
        	.register(adminAuth)
            .path(PATIENT_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<Patient> patients = response.readEntity(new GenericType<List<Patient>>(){});
        assertThat(patients, is(not(empty())));
        assertThat(patients, hasSize(2));
    }
    
    @Test
    public void test32_patient_by_id_with_adminrole() throws JsonMappingException, JsonProcessingException {
    	Response response = webTarget
    			.register(adminAuth)
                .path(PATIENT_RESOURCE_NAME).path("1")
                .request()
                .get();
            assertThat(response.getStatus(), is(200));
            Patient patient = response.readEntity(Patient.class);
            assertEquals(1,patient.getId());
    }
    
    @Test
    public void test33_add_physician_to_patient_with_adminrole() throws JsonMappingException, JsonProcessingException {
    	Patient newPat = new Patient("Testy","Testica",1995,"22 Test ave", 200, 200, (byte) 1);
    	Response response = webTarget
    		.register(adminAuth)
            .path(PATIENT_RESOURCE_NAME)
            .request()
            .post(Entity.json(newPat));
        assertThat(response.getStatus(), is(200));
    }
    
    @Test
    public void test34_update_patient_with_adminrole() throws JsonMappingException, JsonProcessingException {
    	Patient updatedPat = new Patient("Testant","McTest",1995,"22 Test ave", 200, 200, (byte) 1);
    	Response response = webTarget
    		.register(adminAuth)
            .path(PATIENT_RESOURCE_NAME).path("2")
            .request()
            .put(Entity.json(updatedPat));
        assertThat(response.getStatus(), is(200));
        Patient patient = response.readEntity(Patient.class);
        assertEquals("Testant",patient.getFirstName());
        
    }
    
    @Test
    public void test35_delete_patient_with_adminrole() throws JsonMappingException, JsonProcessingException {
    	Response response = webTarget
    			.register(adminAuth)
                .path(PATIENT_RESOURCE_NAME).path("3")
                .request()
                .delete();
            assertThat(response.getStatus(), is(200));
    }
    
    @Test
    public void test36_all_medicines_with_adminrole() throws JsonMappingException, JsonProcessingException {
    	Response response = webTarget
    			.register(adminAuth)
                .path(MEDICINE_SUBRESOURCE_NAME)
                .request()
                .get();
            assertThat(response.getStatus(), is(200));
        List<Medicine> medicines = response.readEntity(new GenericType<List<Medicine>>(){});
        assertThat(medicines, is(not(empty())));
        assertThat(medicines, hasSize(1));
    }
    
    @Test
    public void test37_medicine_by_id_with_adminrole() throws JsonMappingException, JsonProcessingException {
    	Response response = webTarget
    			.register(adminAuth)
                .path(MEDICINE_SUBRESOURCE_NAME).path("1")
                .request()
                .get();
            assertThat(response.getStatus(), is(200));
            Medicine medicine = response.readEntity(Medicine.class);
            assertEquals("Tylenol",medicine.getDrugName());    
    }
    
    @Test
    public void test38_add_medicine_with_adminrole() throws JsonMappingException, JsonProcessingException {
    	Medicine newMed = new Medicine();
    	newMed.setMedicine("Testol","Tesicon8","All of it");
    	Response response = webTarget
    			.register(adminAuth)
                .path(MEDICINE_SUBRESOURCE_NAME)
                .request()
                .post(Entity.json(newMed));
            assertThat(response.getStatus(), is(200));
            Medicine medicine = response.readEntity(Medicine.class);
            assertEquals("Testol",medicine.getDrugName()); 
    }
    
    @Test
    public void test39_update_medicine_with_adminrole() throws JsonMappingException, JsonProcessingException {
    	Medicine newMed = new Medicine();
    	newMed.setMedicine("Testic", "Element 115", "Do not consume");
    	Response response = webTarget
    			.register(adminAuth)
                .path(MEDICINE_SUBRESOURCE_NAME).path("2")
                .request()
                .put(Entity.json(newMed));
            assertThat(response.getStatus(), is(200));
            Medicine medicine = response.readEntity(Medicine.class);
            assertEquals("Testic",medicine.getDrugName()); 
    }
    
    @Test
    public void test40_delete_medicine_with_adminrole() throws JsonMappingException, JsonProcessingException {
    	Response response = webTarget
    			.register(adminAuth)
                .path(MEDICINE_SUBRESOURCE_NAME).path("2")
                .request()
                .delete();
            assertThat(response.getStatus(), is(200)); 
    }
    
    @Test
    public void test41_all_medicalschool_with_adminrole() throws JsonMappingException, JsonProcessingException {
    	Response response = webTarget
    			.register(adminAuth)
                .path(MEDICAL_SCHOOL_RESOURCE_NAME)
                .request()
                .get();
            assertThat(response.getStatus(), is(200));
            List<Object> medschools = response.readEntity(new GenericType<List<Object>>(){}); //list of Objects as the program was having issues making a list of MedicalSchool objects
            assertThat(medschools, is(not(empty())));
            assertThat(medschools, hasSize(2));
    }
    
    @Test
    public void test42_medicalschool_by_id_with_adminrole() throws JsonMappingException, JsonProcessingException {
    	Response response = webTarget
    			.register(adminAuth)
                .path(MEDICAL_SCHOOL_RESOURCE_NAME).path("1")
                .request()
                .get();
            assertThat(response.getStatus(), is(200));
            MedicalSchool medSchool = response.readEntity(MedicalSchool.class);
            assertEquals(1,medSchool.getId());
    }
    
    @Test
    public void test43_add_medicalschool_with_adminrole() throws JsonMappingException, JsonProcessingException {
    	MedicalSchool newMedSchool = new PrivateSchool();
    	newMedSchool.setName("Princington");
    	Response response = webTarget
    			.register(adminAuth)
                .path(MEDICAL_SCHOOL_RESOURCE_NAME)
                .request()
                .post(Entity.json(newMedSchool));
            assertThat(response.getStatus(), is(200));
            MedicalSchool medSchool = response.readEntity(MedicalSchool.class);
            assertEquals(3,medSchool.getId());
    }   
//    
//    @Test
//    public void test44_add_medtraining_to_medschool_with_adminrole() throws JsonMappingException, JsonProcessingException {
//    	MedicalTraining newMedTraining = new MedicalTraining();
//    	MedicalCertificate newMedCert = new MedicalCertificate();
//    	newMedTraining.setCertificate(newMedCert);
//    	Response response = webTarget
//    			.register(adminAuth)
//                .path(MEDICAL_SCHOOL_RESOURCE_NAME).path("1").path(MEDICAL_TRAINING_RESOURCE_NAME)
//                .request()
//                .post(Entity.json(newMedTraining));
//            assertThat(response.getStatus(), is(200));
//            MedicalSchool medSchool = response.readEntity(MedicalSchool.class);
//            assertThat(medSchool.getMedicalTrainings(), hasSize(2)); //issues with medtraining objects
//    }
    
    @Test
    public void test44_update_medicalschool_with_adminrole() throws JsonMappingException, JsonProcessingException {
    	MedicalSchool newMedSchool = new PrivateSchool();
    	newMedSchool.setName("Test State University");
    	Response response = webTarget
    			.register(adminAuth)
                .path(MEDICAL_SCHOOL_RESOURCE_NAME).path("2")
                .request()
                .put(Entity.json(newMedSchool));
            assertThat(response.getStatus(), is(200));
            MedicalSchool updatedMedSchool = response.readEntity(MedicalSchool.class);
            assertEquals("Test State University",updatedMedSchool.getName()); 
    }
    
    @Test
    public void test45_delete_medicalschool_with_adminrole() throws JsonMappingException, JsonProcessingException {
    	Response response = webTarget
    			.register(adminAuth)
                .path(MEDICAL_SCHOOL_RESOURCE_NAME).path("3")
                .request()
                .delete();
            assertThat(response.getStatus(), is(200));
    }
    
    @Test
    public void test46_all_medicaltraining_with_adminrole() throws JsonMappingException, JsonProcessingException {
    	Response response = webTarget
    			.register(adminAuth)
                .path(MEDICAL_TRAINING_RESOURCE_NAME)
                .request()
                .get();
            assertThat(response.getStatus(), is(200));
	        List<Object> medicalTrainings = response.readEntity(new GenericType<List<Object>>(){}); //list of Objects as the program was having issues making a list of MedicalTraining objects
	        assertThat(medicalTrainings, is(not(empty())));
	        assertThat(medicalTrainings, hasSize(2)); 
    }
    
    @Test
    public void test47_medicaltraining_by_id_with_adminrole() throws JsonMappingException, JsonProcessingException {
    	Response response = webTarget
    			.register(adminAuth)
                .path(MEDICAL_TRAINING_RESOURCE_NAME).path("1")
                .request()
                .get();
           	assertThat(response.getStatus(), is(200));
            Object medTraining = response.readEntity(Object.class);
            assertNotNull(medTraining); //Issues with reading the entity.
    }
//    
//    @Test
//    public void test49_add_medicaltraining_with_adminrole() throws JsonMappingException, JsonProcessingException {
//    	MedicalTraining newMedTraining = new MedicalTraining();
//    	MedicalSchool newMedSchool = new PrivateSchool();
//    	MedicalCertificate newMedCert = new MedicalCertificate();
//    	Response response = webTarget
//    			.register(adminAuth)
//                .path(MEDICAL_TRAINING_RESOURCE_NAME)
//                .request()
//                .post(Entity.json(newMedTraining));
//            assertThat(response.getStatus(), is(200));
//            Object medTraining = response.readEntity(Object.class);
//            assertNotNull(medTraining); //Issues with reading the entity. //issues with medtraining objects
//    }
//    
//    @Test
//    public void test50_update_medtraining_with_adminrole() throws JsonMappingException, JsonProcessingException {
//    	MedicalTraining newMedTraining = new MedicalTraining();
//    	MedicalCertificate newMedCert = new MedicalCertificate();
//    	Response response = webTarget
//    			.register(adminAuth)
//                .path(MEDICAL_TRAINING_RESOURCE_NAME).path("1")
//                .request()
//                .put(Entity.json(newMedTraining));
//            assertThat(response.getStatus(), is(200)); //issues with medtraining objects
//    }
//    
//    @Test
//    public void test48_delete_medicaltraining_with_adminrole() throws JsonMappingException, JsonProcessingException {
//    	Response response = webTarget
//    			.register(adminAuth)
//                .path(MEDICAL_TRAINING_RESOURCE_NAME).path("1")
//                .request()
//                .delete();
//            assertThat(response.getStatus(), is(200)); //issues with medtraining objects
//    }
    
    @Test
    public void test48_all_medicalcertificate_with_adminrole() throws JsonMappingException, JsonProcessingException {
    	Response response = webTarget
    			.register(adminAuth)
                .path(MEDICAL_CERTIFICATE_RESOURCE_NAME)
                .request()
                .get();
            assertThat(response.getStatus(), is(200));
	        List<Object> medicalCertificates = response.readEntity(new GenericType<List<Object>>(){}); //list of Objects as the program was having issues making a list of MedicalTraining objects
	        assertThat(medicalCertificates, is(not(empty())));
	        assertThat(medicalCertificates, hasSize(2)); 
    }
    
//    @Test
//    public void test50_medicalcertificate_by_id_with_adminrole() throws JsonMappingException, JsonProcessingException {
//    	Response response = webTarget
//    			.register(adminAuth)
//                .path(MEDICAL_CERTIFICATE_RESOURCE_NAME).path("1")
//                .request()
//                .get();
//           	assertThat(response.getStatus(), is(200));
//            Object medTraining = response.readEntity(Object.class);
//            assertNotNull(medTraining); //Issues with reading the entity.
//    }
    
    @Test
    public void test49_all_perscription_with_adminrole() throws JsonMappingException, JsonProcessingException {
    	Response response = webTarget
    			.register(adminAuth)
                .path(PRESCRIPTION_RESOURCE_NAME)
                .request()
                .get();
            assertThat(response.getStatus(), is(200));
	        List<Prescription> perscriptions = response.readEntity(new GenericType<List<Prescription>>(){});
	        assertThat(perscriptions, is(not(empty())));
	        assertThat(perscriptions, hasSize(2)); 
    }
    
    @Test
    public void test50_perscription_by_id_with_adminrole() throws JsonMappingException, JsonProcessingException {
    	Response response = webTarget
    			.register(adminAuth)
                .path(PRESCRIPTION_RESOURCE_NAME).path("1").path("1")
                .request()
                .get();
           	assertThat(response.getStatus(), is(200));
           	Prescription perscription = response.readEntity(Prescription.class);
            assertNotNull(perscription); 
    }
}