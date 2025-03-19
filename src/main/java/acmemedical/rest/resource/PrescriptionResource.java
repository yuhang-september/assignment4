package acmemedical.rest.resource;

import static acmemedical.utility.MyConstants.ADMIN_ROLE;
import static acmemedical.utility.MyConstants.USER_ROLE;
import static acmemedical.utility.MyConstants.PRESCRIPTION_RESOURCE_NAME;

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
import acmemedical.entity.Prescription;

@Path(PRESCRIPTION_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PrescriptionResource {
    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMEMedicalService service;

    @Inject
    protected SecurityContext sc;

    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    public Response getAllPrescriptions() {
        LOG.debug("retrieving all prescriptions");
        List<Prescription> prescriptions = service.getAll(Prescription.class, "Prescription.findAll");
        Response response = Response.ok(prescriptions).build();
        return response;
    }

    @GET
    @Path("{physicianId}/{patientId}")
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    public Response getPrescriptionById(@PathParam("physicianId") int physicianId, @PathParam("patientId") int patientId) {
        LOG.debug("retrieving prescription with physicianId = {} and patientId = {}", physicianId, patientId);
        Prescription prescription = service.getPrescriptionById(physicianId, patientId);
        Response response = Response.ok(prescription).build();
        return response;
    }

    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addPrescription(Prescription newPrescription) {
        LOG.debug("adding new prescription = {}", newPrescription);
        Prescription prescription = service.persistPrescription(newPrescription);
        Response response = Response.ok(prescription).build();
        return response;
    }

    @PUT
    @Path("{physicianId}/{patientId}")
    @RolesAllowed({ADMIN_ROLE})
    public Response updatePrescription(@PathParam("physicianId") int physicianId, 
                                     @PathParam("patientId") int patientId, 
                                     Prescription prescriptionWithUpdates) {
        LOG.debug("updating prescription with physicianId = {} and patientId = {}", physicianId, patientId);
        Prescription prescription = service.updatePrescription(physicianId, patientId, prescriptionWithUpdates);
        Response response = Response.ok(prescription).build();
        return response;
    }

    @DELETE
    @Path("{physicianId}/{patientId}")
    @RolesAllowed({ADMIN_ROLE})
    public Response deletePrescription(@PathParam("physicianId") int physicianId, @PathParam("patientId") int patientId) {
        LOG.debug("deleting prescription with physicianId = {} and patientId = {}", physicianId, patientId);
        service.deletePrescription(physicianId, patientId);
        Response response = Response.ok().build();
        return response;
    }
} 