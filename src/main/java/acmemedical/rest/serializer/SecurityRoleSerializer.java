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
package acmemedical.rest.serializer;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import acmemedical.entity.SecurityRole;

public class SecurityRoleSerializer extends StdSerializer<Set<SecurityRole>> implements Serializable {
    private static final long serialVersionUID = 1L;

    public SecurityRoleSerializer() {
        this(null);
    }

    public SecurityRoleSerializer(Class<Set<SecurityRole>> t) {
        super(t);
    }

    /**
     * This is to prevent back and forth serialization between many-to-many relations.<br>
     * This is done by setting the relation to null.
     */
    @Override
    public void serialize(Set<SecurityRole> originalRoles, JsonGenerator generator, SerializerProvider provider)
        throws IOException {
        
        Set<SecurityRole> hollowRoles = new HashSet<>();
        for (SecurityRole originalRole : originalRoles) {
            // Create a 'hollow' copy of the original Security Roles entity
            SecurityRole hollowP = new SecurityRole();
            hollowP.setId(originalRole.getId());
            hollowP.setRoleName(originalRole.getRoleName());
            hollowP.setUsers(null);
            hollowRoles.add(hollowP);
        }
        generator.writeObject(hollowRoles);
    }
}