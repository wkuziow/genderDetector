package pl.kuziow.genderdetector.service.impl;

import org.springframework.stereotype.Service;
import pl.kuziow.genderdetector.service.QueryService;

import java.util.Arrays;
import java.util.List;

@Service
public class QueryServiceImpl implements QueryService {

    public List<String> createNameList(String name) {
        String[] tokenArray = name.split(" +");
        return Arrays.asList(tokenArray);
    }
}
