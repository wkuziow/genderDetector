package pl.kuziow.genderdetector.response;

public enum Response {
    FEMALE("FEMALE"), MALE("MALE"), INCONCLUSIVE("INCONCLUSIVE");

    private String response;

    Response(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
