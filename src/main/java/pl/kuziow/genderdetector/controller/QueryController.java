package pl.kuziow.genderdetector.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.kuziow.genderdetector.service.QueryService;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/query") //http://localhost:8080/gender-detector/query
public class QueryController {

    @Autowired
    QueryService queryService;

    @GetMapping(path = "/name", //http://localhost:8080/gender-detector/query/name?name=
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public String getName(@RequestParam String name) {
        queryService.createNameList(name);
        return name;
    }



    @GetMapping(path = "/gender", //http://localhost:8080/gender-detector/query/gender?gender=
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public String getListOfNames(@RequestParam String gender) {
        return gender;
    }

}
