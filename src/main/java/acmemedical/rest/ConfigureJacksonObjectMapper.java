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

import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Provider
public class ConfigureJacksonObjectMapper implements ContextResolver<ObjectMapper> {
    
    private final ObjectMapper objectMapper;

    public ConfigureJacksonObjectMapper() {
        this.objectMapper = createObjectMapper();
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return objectMapper;
    }

    //Configure JDK 8's new DateTime objects to use proper ISO-8601 time format
    protected ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            // Lenient parsing of JSON - if a field has a typo, don't fall to pieces
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        /* 
        HttpErrorAsJSONServlet.setObjectMapper(mapper);
        TypeResolverBuilder<?> typer = new StdTypeResolverBuilder()
                .init(JsonTypeInfo.Id.NAME, null)
                .inclusion(JsonTypeInfo.As.PROPERTY)
                .typeProperty("entity-type");
        mapper.setDefaultTyping(typer);
        mapper.registerSubtypes(new NamedType(PublicMedicalSchool.class, "public_medical_school"));
        mapper.registerSubtypes(new NamedType(PrivateMedicalSchool.class, "private_medical_school"));
        */
        return mapper;
    }
}