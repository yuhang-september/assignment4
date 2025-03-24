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

import static acmemedical.utility.MyConstants.ADMIN_ROLE;
import static acmemedical.utility.MyConstants.APPLICATION_API_VERSION;
import static acmemedical.utility.MyConstants.USER_ROLE;

import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.security.DeclareRoles;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath(APPLICATION_API_VERSION)
//This used to be in web.xml
@DeclareRoles({USER_ROLE, ADMIN_ROLE})
public class RestConfig extends Application {

    /*
     * Without the following 'feature', the default serialization/deserialization for Jakarta EE 8
     * is JSON-B via a project called 'Yasson'.
     * 
     * However Yasson does not 'nicely' handle a variety of issues ... so substitute a non-standard
     * (but much more popular!) package called 'Jackson'
     */
    
    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("jersey.config.jsonFeature", "JacksonFeature");
        // Add x-jersey-tracing tracing headers Note:  If more than 100 trace messages, need to alter configuration to allow for more
        //props.put("jersey.config.server.tracing.type", "ALL");
        //props.put("jersey.config.server.tracing.threshold", "VERBOSE");
        return props;
    }

}