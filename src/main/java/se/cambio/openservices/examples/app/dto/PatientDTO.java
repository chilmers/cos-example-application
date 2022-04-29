package se.cambio.openservices.examples.app.dto;

import java.text.SimpleDateFormat;
import java.util.Objects;
import org.hl7.fhir.r4.model.Patient;

/**
 * Used for responses to the patient search
 */
public class PatientDTO {

    private String birthDate;
    private Boolean deceased;
    private String fhirId;
    private String fullName;
    private String gender;
    private String identifierSystem;
    private String identifierValue;

    public PatientDTO(Patient patient) {
        this.fhirId = patient.getId();

        this.identifierSystem = patient.getIdentifier().get(0).getSystem();
        this.identifierValue = patient.getIdentifier().get(0).getValue();

        this.gender = patient.getGender().getDisplay();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        this.birthDate = sdf.format(patient.getBirthDate());

        this.deceased = patient.getDeceasedBooleanType().getValue();

        this.fullName = patient.getName().get(0).getNameAsSingleString();
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public Boolean getDeceased() {
        return deceased;
    }

    public void setDeceased(Boolean deceased) {
        this.deceased = deceased;
    }

    public String getFhirId() {
        return fhirId;
    }

    public void setFhirId(String fhirId) {
        this.fhirId = fhirId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getIdentifierSystem() {
        return identifierSystem;
    }

    public void setIdentifierSystem(String identifierSystem) {
        this.identifierSystem = identifierSystem;
    }

    public String getIdentifierValue() {
        return identifierValue;
    }

    public void setIdentifierValue(String identifierValue) {
        this.identifierValue = identifierValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PatientDTO that = (PatientDTO) o;
        return Objects.equals(birthDate, that.birthDate) && Objects.equals(deceased, that.deceased) && fhirId.equals(
            that.fhirId) && Objects.equals(fullName, that.fullName) && Objects.equals(gender, that.gender)
            && identifierSystem.equals(that.identifierSystem) && identifierValue.equals(that.identifierValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(birthDate, deceased, fhirId, fullName, gender, identifierSystem, identifierValue);
    }

    @Override
    public String toString() {
        return "PatientDTO{" +
            "birthDate='" + birthDate + '\'' +
            ", deceased=" + deceased +
            ", fhirId='" + fhirId + '\'' +
            ", fullName='" + fullName + '\'' +
            ", gender='" + gender + '\'' +
            ", identifierSystem='" + identifierSystem + '\'' +
            ", identifierValue='" + identifierValue + '\'' +
            '}';
    }
}
