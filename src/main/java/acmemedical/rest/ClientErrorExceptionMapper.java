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
package acmemedical.rest;

import acmemedical.rest.resource.HttpErrorResponse;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Create a Jackson exception instead of the default Payara HTML response.<br>
 * This exception is mapped using "@{@link Provider}".<br>
 * This is not needed, optional design.
 * 
 * 
 * @see <a href="https://javaee.github.io/javaee-spec/javadocs/javax./ws/rs/ClientErrorException.html">JavaEE 8 ClientErrorException</a>
 * @see <a href="https://javaee.github.io/javaee-spec/javadocs/javax./ws/rs/ext/ExceptionMapper.html">JavaEE 8 ExceptionMapper</a>
 */
@Provider
public class ClientErrorExceptionMapper implements ExceptionMapper<ClientErrorException> {
    
    @Override
    public Response toResponse(ClientErrorException exception) {
      Response response = exception.getResponse();
      Response.StatusType statusType = response.getStatusInfo();
      int statusCode = statusType.getStatusCode();
      String reasonPhrase = statusType.getReasonPhrase();
      HttpErrorResponse entity = new HttpErrorResponse(statusCode, reasonPhrase);
      return Response.status(statusCode).entity(entity).build();
    }
}