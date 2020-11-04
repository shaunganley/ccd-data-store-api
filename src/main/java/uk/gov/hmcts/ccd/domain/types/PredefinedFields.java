package uk.gov.hmcts.ccd.domain.types;

/**
 * Lists the field ID of the predefined types' fields.
 */
public enum PredefinedFields {

    CASE_LINK_TEXT_CASE_REFERENCE("TextCaseReference"),
    ORG_POLICY_CASE_ASSIGNED_ROLE("OrgPolicyCaseAssignedRole");

    private String fieldID;

    PredefinedFields(String fieldID) {
        this.fieldID = fieldID;
    }

    public String getFieldID() {
        return fieldID;
    }
}
