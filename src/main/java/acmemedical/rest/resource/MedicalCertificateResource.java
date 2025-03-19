package acmemedical.rest.resource;

import static acmemedical.utility.MyConstants.ADMIN_ROLE;
import static acmemedical.utility.MyConstants.USER_ROLE;
import static acmemedical.utility.MyConstants.MEDICAL_CERTIFICATE_RESOURCE_NAME;

import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import acmemedical.ejb.ACMEMedicalService;
import acmemedical.entity.MedicalCertificate;

@Path(MEDICAL_CERTIFICATE_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MedicalCertificateResource {
    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMEMedicalService service;

    @Inject
    protected SecurityContext sc;

    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    public Response getAllMedicalCertificates() {
        LOG.debug("retrieving all medical certificates");
        List<MedicalCertificate> medicalCertificates = service.getAll(MedicalCertificate.class, "MedicalCertificate.findAll");
        Response response = Response.ok(medicalCertificates).build();
        return response;
    }

    @GET
    @Path("{id}")
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    public Response getMedicalCertificateById(@PathParam("id") int id) {
        LOG.debug("retrieving medical certificate with id = {}", id);
        MedicalCertificate medicalCertificate = service.getById(MedicalCertificate.class, MedicalCertificate.ID_CARD_QUERY_NAME, id);
        Response response = Response.ok(medicalCertificate).build();
        return response;
    }

    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addMedicalCertificate(MedicalCertificate newMedicalCertificate) {
        LOG.debug("adding new medical certificate = {}", newMedicalCertificate);
        MedicalCertificate medicalCertificate = service.persistMedicalCertificate(newMedicalCertificate);
        Response response = Response.ok(medicalCertificate).build();
        return response;
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed({ADMIN_ROLE})
    public Response deleteMedicalCertificate(@PathParam("id") int id) {
        LOG.debug("deleting medical certificate with id = {}", id);
        service.deleteMedicalCertificate(id);
        Response response = Response.ok().build();
        return response;
    }
} 