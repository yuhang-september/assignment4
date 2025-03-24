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
package acmemedical.rest.resource;

import static acmemedical.utility.MyConstants.ADMIN_ROLE;
import static acmemedical.utility.MyConstants.USER_ROLE;
import static acmemedical.utility.MyConstants.MEDICAL_TRAINING_RESOURCE_NAME;

import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import acmemedical.ejb.ACMEMedicalService;
import acmemedical.entity.MedicalTraining;

@Path(MEDICAL_TRAINING_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MedicalTrainingResource {
    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMEMedicalService service;

    @Inject
    protected SecurityContext sc;

    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    public Response getAllMedicalTrainings() {
        LOG.debug("retrieving all medical trainings");
        List<MedicalTraining> medicalTrainings = service.getAll(MedicalTraining.class, "MedicalTraining.findAll");
        Response response = Response.ok(medicalTrainings).build();
        return response;
    }

    @GET
    @Path("{id}")
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    public Response getMedicalTrainingById(@PathParam("id") int id) {
        LOG.debug("retrieving medical training with id = {}", id);
        MedicalTraining medicalTraining = service.getById(MedicalTraining.class, MedicalTraining.FIND_BY_ID, id);
        Response response = Response.ok(medicalTraining).build();
        return response;
    }

    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addMedicalTraining(MedicalTraining newMedicalTraining) {
        LOG.debug("adding new medical training = {}", newMedicalTraining);
        MedicalTraining medicalTraining = service.persistMedicalTraining(newMedicalTraining);
        Response response = Response.ok(medicalTraining).build();
        return response;
    }

    @PUT
    @Path("{id}")
    @RolesAllowed({ADMIN_ROLE})
    public Response updateMedicalTraining(@PathParam("id") int id, MedicalTraining medicalTrainingWithUpdates) {
        LOG.debug("updating medical training with id = {}", id);
        MedicalTraining medicalTraining = service.updateMedicalTraining(id, medicalTrainingWithUpdates);
        Response response = Response.ok(medicalTraining).build();
        return response;
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed({ADMIN_ROLE})
    public Response deleteMedicalTraining(@PathParam("id") int id) {
        LOG.debug("deleting medical training with id = {}", id);
        service.deleteMedicalTraining(id);
        Response response = Response.ok().build();
        return response;
    }
} 