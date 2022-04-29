package se.cambio.openservices.examples.app.controller;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.util.BundleUtil;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import se.cambio.openservices.examples.app.dto.PatientDTO;

/**
 * Spring Controller that handles the application's API.
 */
@RestController
public class ApiController {

    private static final Logger log = LoggerFactory.getLogger(ApiController.class);

    /**
     * Spring will create an instance of IGenericClient using the
     * factory we registered in
     */
    @Autowired
    private IGenericClient fhirClient;

    /**
     * Handles GET requests to /patient
     * @param pnr expects a query param named pnr (i.e. /patients?pnr=191212121212)
     * @return An instance of PatientDTO that will be transformed to JSON and returned to the client.
     * @throws ResponseStatusException
     * With status BAD_REQUEST if no pnr query parameter was given. Results in a 400 Bad Request response to the client.
     * With status NOT_FOUND if no such patient was found. Results in 404 NOT FOUND response to the client.
     */
    @GetMapping("/patient")
    public PatientDTO getPatient(@RequestParam("pnr") String pnr) throws ResponseStatusException {
        if (StringUtils.isBlank(pnr)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing mandatory query parameter 'pnr'");
        }
        PatientDTO response = null;
        try {
            // Perform a search for patients with the given pnr as value using the personal identity number code system.
            Bundle results = fhirClient.search()
                .forResource(Patient.class)
                .and(Patient.IDENTIFIER.exactly().systemAndIdentifier("urn:oid:1.2.752.129.2.1.3.1", pnr))
                .returnBundle(Bundle.class).execute();
            // Extract the patient from the bundle
            List<Patient> patients = BundleUtil.toListOfResourcesOfType(fhirClient.getFhirContext(), results, Patient.class);

            Patient fhirPatient = patients.get(0);
            log.debug("Got FHIR Patient with id: {}", fhirPatient.getId());
            response = new PatientDTO(fhirPatient);
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such patient found", e);
        }

        log.debug("Responding with: {}", response);
        return response;
    }

}
