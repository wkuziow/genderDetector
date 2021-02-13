package pl.kuziow.genderdetector.response;

/**
 * Enum with possible responses.
 * Inconclusive response is sent when algorithm is not able to specify gender
 */
public enum Response {
    FEMALE("FEMALE"), MALE("MALE"), INCONCLUSIVE("INCONCLUSIVE");

    private final String response;

    Response(String response) {
        this.response = response;
    }

//    public String getResponse() {
//        return response;
//    }
//
//    public void setResponse(String response) {
//        this.response = response;
//    }
}
