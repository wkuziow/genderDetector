package pl.kuziow.genderdetector.service.impl;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import pl.kuziow.genderdetector.response.Response;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * This class tests QueryServiceImpl.class
 */
class QueryServiceImplTest {

    @InjectMocks
    QueryServiceImpl queryService;


    final String variant = "one";
    final String name1 = "jan maria anna karol piotr";
    final String[] name1Arr = {"JAN", "MARIA", "ANNA", "KAROL", "PIOTR"};
    String name2 = "tomasz";
    final List<String> allTokenList = Arrays.asList("JAN", "MARIA", "ANNA", "KAROL", "PIOTR");
    final List<Response> responseList = Arrays.asList(Response.MALE, Response.FEMALE, Response.FEMALE, Response.MALE, Response.MALE);


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

    }

    /**
     * Method tests createNameList
     * it checks if the 2 arrays are equal
     * than it checks if the first position in two lists is the same
     * two lists are created using tested method and result are compared with the expected ones
     */
    @Test
    void createNameList() {
        String[] tokenArr = name1.toUpperCase().split(" +");
        Assert.assertArrayEquals(name1Arr, tokenArr);
        List<String> oneStringList = Arrays.asList("JAN");
        assertEquals(oneStringList, Arrays.asList(tokenArr[0]));
        List<String> expectedAllTokenList = queryService.createNameList("differentString", "jan maria anna karol piotr");
        List<String> expectedOneStringList = queryService.createNameList(variant, "jan maria anna karol piotr");
        Assert.assertEquals(expectedOneStringList, oneStringList);
        Assert.assertEquals(expectedAllTokenList, allTokenList);
    }

    /**
     * test of createGenderList method
     * list of Responses is created using tested method and than it is compared with the expected result
     */
    @Test
    void createGenderList() {

        List<Response> expectedResponseList = queryService.createGenderList(allTokenList);
        Assert.assertEquals(expectedResponseList, responseList);
    }


    /**
     * test of genderResponse method
     * two Responses are created using tested method and than compared to expcted results
     */
    @Test
    void genderResponse() {
        Response response = queryService.genderResponse(responseList);
        Response expectedResponse = Response.MALE;
        Assert.assertEquals(expectedResponse, response);
        Response response1 = queryService.genderResponse(Arrays.asList(Response.INCONCLUSIVE));
        Assert.assertEquals(Response.INCONCLUSIVE, response1);
    }

    /**
     * tests if the size of the created list is equalt to expected
     */
    @Test
    void csvReader() {
        List<String> nameList = queryService.getListOfNames("male", 1, 7);
        assertEquals(7, nameList.size());
    }


}