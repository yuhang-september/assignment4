/********************************************************************************************************
 * File:  PBKDF2HashGenerator.java
 * Course Materials CST 8277
 * @author Teddy Yap
 * @author Mike Norman
 * @author Shariar (Shawn) Emami
 * @author: professor at Algonquin College
 * modified and updated by group 8
 * 041094775, Tammy Liu (as from ACSIS)
 * 041127152, Yuhang Zhang  (as from ACSIS)
 * 040799347, Stephen Carpenter (as from ACSIS)
 * 040780701, Qi Wu  (as from ACSIS)
 * 
 * Note:  Students do NOT need to change anything in this class.
 *
 */
package acmemedical.utility;

import static acmemedical.utility.MyConstants.DEFAULT_KEY_SIZE;
import static acmemedical.utility.MyConstants.DEFAULT_PROPERTY_ALGORITHM;
import static acmemedical.utility.MyConstants.DEFAULT_PROPERTY_ITERATIONS;
import static acmemedical.utility.MyConstants.DEFAULT_SALT_SIZE;
import static acmemedical.utility.MyConstants.PROPERTY_ALGORITHM;
import static acmemedical.utility.MyConstants.PROPERTY_ITERATIONS;
import static acmemedical.utility.MyConstants.PROPERTY_KEY_SIZE;
import static acmemedical.utility.MyConstants.PROPERTY_SALT_SIZE;

import java.util.HashMap;
import java.util.Map;

import org.glassfish.soteria.identitystores.hash.Pbkdf2PasswordHashImpl;

import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;



public class PBKDF2HashGenerator {
    // The nickname of this hash algorithm is 'PBandJ' (Peanut-Butter-And-Jam, like the sandwich!)
    // I would like to use the constants from org.glassfish.soteria.identitystores.hash.Pbkdf2PasswordHashImpl
    // but they are not visible, so type in them all over again :-( Hope there are no typos!

    public static void main(String[] args) {
        
        Pbkdf2PasswordHash pbAndjPasswordHash = new Pbkdf2PasswordHashImpl();

        Map<String, String> pbAndjProperties = new HashMap<>();
        pbAndjProperties.put(PROPERTY_ALGORITHM, DEFAULT_PROPERTY_ALGORITHM);
        pbAndjProperties.put(PROPERTY_ITERATIONS, DEFAULT_PROPERTY_ITERATIONS);
        pbAndjProperties.put(PROPERTY_SALT_SIZE, DEFAULT_SALT_SIZE);
        pbAndjProperties.put(PROPERTY_KEY_SIZE, DEFAULT_KEY_SIZE);
        pbAndjPasswordHash.initialize(pbAndjProperties);
        String pwHash = pbAndjPasswordHash.generate(args[0].toCharArray());
        System.out.printf("Hash for %s is %s%n", args[0], pwHash);
    }
}
