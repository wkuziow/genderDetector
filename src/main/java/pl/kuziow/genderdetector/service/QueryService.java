package pl.kuziow.genderdetector.service;

import pl.kuziow.genderdetector.response.Response;

import java.util.List;

public interface QueryService {
    List<String> createNameList(String variant, String name);
    List<Response> createGenderList(List<String> nameList);

    Response genderResponse(List<Response> responseList);

    List<String> getListOfNames(String gender, int page, int limit);
}
