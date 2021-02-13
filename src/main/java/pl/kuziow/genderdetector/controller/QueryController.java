package pl.kuziow.genderdetector.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.kuziow.genderdetector.response.Response;
import pl.kuziow.genderdetector.service.QueryService;

import java.util.List;

/**
 * RestController class which sets out 2 endpoints
 * Controller is available under address /gender-detector/query
 */
@RestController
@RequestMapping("/query") //http://localhost:8080/gender-detector/query
public class QueryController {

    final
    QueryService queryService;

    public QueryController(QueryService queryService) {
        this.queryService = queryService;
    }

    /**
     * @param variant - defines whether method is going to analise only first part of the name String (value: ONE)
     *                or the whole String (any other value or nothing), this parameter is not required
     * @param name    - name String to be analysed, it could consist of one name like "Anna" or multiple name like "Anna Maria Jan"
     * @return 3 possible Responses: Male, Female or Inconclusive if the algorithm is unable to specify gender
     * <p>
     * This method works only on Polish names from PESEL database. It is worth mentioning that some some male names like Max, Jan or Zbigniew
     * also occur in the female database and vice versa.
     * This method is available under the endpoint
     * http://localhost:8080/gender-detector/query/name?variant={one OR other value}&name={nameString}
     */
    @GetMapping(path = "/name",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public Response getName(@RequestParam(required = false, defaultValue = "") String variant, @RequestParam String name) {
        List<String> namesList = queryService.createNameList(variant, name);
        List<Response> responseList = queryService.createGenderList(namesList);
        return queryService.genderResponse(responseList);
    }


    /**
     * @param gender - male or female.
     * @param page   - which page of the results (default is 0)
     * @param limit  - how many results of one page (default is 25)
     * @return List of names
     * <p>
     * This method returns the list of names for gender. Because the list is very big the result is paginated.
     * This method is available under the endpoint:
     * http://localhost:8080/gender-detector/query/gender?gender=&page=&limit=
     */
    @GetMapping(path = "/gender",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<String> getListOfNames(@RequestParam String gender,
                                       @RequestParam(value = "page", defaultValue = "0") int page,
                                       @RequestParam(value = "limit", defaultValue = "25") int limit) {
        return queryService.getListOfNames(gender, page, limit);
    }

}
