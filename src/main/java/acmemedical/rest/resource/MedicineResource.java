package acmemedical.rest.resource;

import static acmemedical.utility.MyConstants.ADMIN_ROLE;
import static acmemedical.utility.MyConstants.USER_ROLE;
import static acmemedical.utility.MyConstants.MEDICINE_RESOURCE_NAME;

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
import acmemedical.entity.Medicine;

@Path(MEDICINE_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MedicineResource {
    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMEMedicalService service;

    @Inject
    protected SecurityContext sc;

    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    public Response getAllMedicines() {
        LOG.debug("retrieving all medicines");
        List<Medicine> medicines = service.getAll(Medicine.class, "Medicine.findAll");
        Response response = Response.ok(medicines).build();
        return response;
    }

    @GET
    @Path("{id}")
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    public Response getMedicineById(@PathParam("id") int id) {
        LOG.debug("retrieving medicine with id = {}", id);
        Medicine medicine = service.getById(Medicine.class, "Medicine.findById", id);
        Response response = Response.ok(medicine).build();
        return response;
    }

    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addMedicine(Medicine newMedicine) {
        LOG.debug("adding new medicine = {}", newMedicine);
        Medicine medicine = service.persistMedicine(newMedicine);
        Response response = Response.ok(medicine).build();
        return response;
    }

    @PUT
    @Path("{id}")
    @RolesAllowed({ADMIN_ROLE})
    public Response updateMedicine(@PathParam("id") int id, Medicine medicineWithUpdates) {
        LOG.debug("updating medicine with id = {}", id);
        Medicine medicine = service.updateMedicine(id, medicineWithUpdates);
        Response response = Response.ok(medicine).build();
        return response;
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed({ADMIN_ROLE})
    public Response deleteMedicine(@PathParam("id") int id) {
        LOG.debug("deleting medicine with id = {}", id);
        service.deleteMedicine(id);
        Response response = Response.ok().build();
        return response;
    }
} 