package pl.kuziow.genderdetector.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.kuziow.genderdetector.response.Response;
import pl.kuziow.genderdetector.service.QueryService;

import java.util.List;

@RestController
@RequestMapping("/query") //http://localhost:8080/gender-detector/query
public class QueryController {

    final
    QueryService queryService;

    public QueryController(QueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping(path = "/name", //http://localhost:8080/gender-detector/query/name?variant={one OR other value}&name={nameString}
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public Response getName(@RequestParam(required = false, defaultValue = "") String variant, @RequestParam String name) {
        List<String> namesList = queryService.createNameList(variant, name);
        List<Response> responseList = queryService.createGenderList(namesList);
        return queryService.genderResponse(responseList);
    }


    @GetMapping(path = "/gender", //http://localhost:8080/gender-detector/query/gender?gender=&page=&limit=
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<String> getListOfNames(@RequestParam String gender,
                                       @RequestParam(value = "page", defaultValue = "0") int page,
                                       @RequestParam(value = "limit", defaultValue = "25") int limit) {
        return queryService.getListOfNames(gender, page, limit);
    }

}
