package pl.kuziow.genderdetector.exceptions;

/**
 * Enum with defined errors and error messages
 */
public enum ErrorMessages {

    MISSING_REQUIRED_FIELD("Missing required field. Please check documentation for required fields"),
    INTERNAL_SERVER_ERROR("Internal server error"),
    WRONG_QUERY("Error in query. Please chceck documentation");



    private String errorMessage;

    ErrorMessages(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }


}
