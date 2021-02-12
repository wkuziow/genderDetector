package pl.kuziow.genderdetector.service.impl;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import pl.kuziow.genderdetector.response.Response;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

class QueryServiceImplTest {

    @InjectMocks
    QueryServiceImpl queryService;


    String variant = "one";
    String name1 = "jan maria anna karol piotr";
    String[] name1Arr = {"JAN", "MARIA", "ANNA", "KAROL", "PIOTR"};
    String name2 = "tomasz";
    List<String> allTokenList = Arrays.asList("JAN", "MARIA", "ANNA", "KAROL", "PIOTR");
    List<Response> responseList = Arrays.asList(Response.MALE, Response.FEMALE, Response.FEMALE, Response.MALE, Response.MALE);


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

    }

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

    @Test
    void createGenderList() {
        Map<String, Integer> femaleMap = createFemaleMap();
        Map<String, Integer> maleMap = createMaleMap();
        List<Response> expectedResponseList = queryService.createGenderList(allTokenList);
        Assert.assertEquals(expectedResponseList, responseList);
    }

    private Map<String, Integer> createFemaleMap() {
        Map<String, Integer> femaleMap = new HashMap<>();
        femaleMap.put("JAN", 276);
        femaleMap.put("MARIA", 1);
        femaleMap.put("ANNA", 2);
        femaleMap.put("KAROL", -1);
        femaleMap.put("PIOTR", -1);

        return femaleMap;
    }

    private Map<String, Integer> createMaleMap() {
        Map<String, Integer> maleMap = new HashMap<>();
        maleMap.put("JAN", 1);
        maleMap.put("MARIA", 367);
        maleMap.put("ANNA", 5795);
        maleMap.put("KAROL", 2);
        maleMap.put("PIOTR", 3);

        return maleMap;
    }

    @Test
    void genderResponse() {
        Response response = queryService.genderResponse(responseList);
        Response expectedResponse = Response.MALE;
        Assert.assertEquals(expectedResponse, response);
        Response response1 = queryService.genderResponse(Arrays.asList(Response.INCONCLUSIVE));
        Assert.assertEquals(Response.INCONCLUSIVE, response1);
    }

    @Test
    void csvReader(){
        List<String> nameList = queryService.getListOfNames("male",1,7);
        assertEquals(7, nameList.size());
    }




}