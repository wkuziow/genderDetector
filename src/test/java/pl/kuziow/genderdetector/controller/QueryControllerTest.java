package pl.kuziow.genderdetector.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.kuziow.genderdetector.response.Response;
import pl.kuziow.genderdetector.service.QueryService;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * test class for QueryController.class
 */
class QueryControllerTest {

    @InjectMocks
    QueryController queryController;

    @Mock
    QueryService queryService;

    final String name1 = "jan maria anna karol piotr";
    String name2 = "unknown name";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * method tests getName method
     * it tests if first token of the name1 String is male
     */
    @Test
    void getName() {
        when(queryController.getName(anyString(), anyString())).thenReturn(Response.MALE);
        Response responseForName1 = Response.MALE;
        assertEquals(responseForName1, queryController.getName("one", name1));

    }


}