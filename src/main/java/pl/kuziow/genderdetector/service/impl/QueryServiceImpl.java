package pl.kuziow.genderdetector.service.impl;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.stereotype.Service;
import pl.kuziow.genderdetector.exceptions.ErrorMessages;
import pl.kuziow.genderdetector.exceptions.QueryException;
import pl.kuziow.genderdetector.response.Response;
import pl.kuziow.genderdetector.service.QueryService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Service
public class QueryServiceImpl implements QueryService {

    private static final String femaleFile = "female_names.csv";
    private static final String maleFile = "male_names.csv";

    @Override
    public List<String> createNameList(String variant, String name) {
        String[] tokenArray = name.toUpperCase().split(" +");
        if (variant.equalsIgnoreCase("one")) {
            return Collections.singletonList(tokenArray[0]);
        }
        return Arrays.asList(tokenArray);
    }

    @Override
    public List<Response> createGenderList(List<String> nameList) {
        Map<String, Integer> femaleMap = createFemaleMap(nameList);
        Map<String, Integer> maleMap = createMaleMap(nameList);

        return mapComparer(nameList, femaleMap, maleMap);
    }

    @Override
    public Response genderResponse(List<Response> responseList) {
        int femaleResponseCounter = 0;
        int maleResponseCounter = 0;

        for (Response r : responseList) {
            if (r.equals(Response.FEMALE)) {
                femaleResponseCounter++;
            } else if (r.equals(Response.MALE)) {
                maleResponseCounter++;
            }
        }
        if (femaleResponseCounter > maleResponseCounter) {
            return Response.FEMALE;
        } else if (maleResponseCounter > femaleResponseCounter) {
            return Response.MALE;
        }
        return Response.INCONCLUSIVE;
    }

    @Override
    public List<String> getListOfNames(String gender, int page, int limit) {
        List<String> nameList = new ArrayList<>();
        int skipLines = limit * page;
        csvReader(gender, limit, nameList, skipLines);

        return nameList;
    }

    private void csvReader(String gender, int limit, List<String> nameList, int skipLines) {
        try {
            CSVReader reader = new CSVReaderBuilder(new FileReader(genderFileMatcher(gender)))
                    .withSkipLines(skipLines)
                    .build();
            int i = 0;
            nameList.add(reader.peek()[0]);
            while (reader.readNext() != null && i < limit - 1) {
                nameList.add(reader.peek()[0]);
                i++;
            }
        } catch (CsvValidationException | IOException e) {
            e.printStackTrace();
        }
    }

    private String genderFileMatcher(String gender) {
        if (gender.equalsIgnoreCase("male")) {
            return maleFile;
        } else if (gender.equalsIgnoreCase("female")) {
            return femaleFile;
        } else {
            throw new QueryException(ErrorMessages.WRONG_QUERY.getErrorMessage());
        }
    }

    private List<Response> mapComparer(List<String> nameList, Map<String, Integer> femaleMap, Map<String, Integer> maleMap) {
        List<Response> responseList = new ArrayList<>();

        for (String name : nameList) {
            int f = femaleMap.get(name);
            int m = maleMap.get(name);
            if (f > 0 && m == -1) {
                responseList.add(Response.FEMALE);
            } else if (m > 0 && f == -1) {
                responseList.add(Response.MALE);
            } else if (f < m) {
                responseList.add(Response.FEMALE);
            } else if (f > m) {
                responseList.add(Response.MALE);
            } else {
                responseList.add(Response.INCONCLUSIVE);
            }
        }
        return responseList;
    }

    private int femaleNamePositionCheck(String name) {
        return namePositionCheck(name, femaleFile);
    }

    private int maleNamePositionCheck(String name) {
        return namePositionCheck(name, maleFile);
    }

    private int namePositionCheck(String name, String fileName) {
        String line;
        int lineCounter = 0;

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));

            while ((line = bufferedReader.readLine()) != null) {
                lineCounter++;
                if (line.equals(name)) {
                    return lineCounter;
                }

            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private Map<String, Integer> createFemaleMap(List<String> nameList) {
        Map<String, Integer> femaleMap = new HashMap<>();
        for (String n : nameList) {
            int f = femaleNamePositionCheck(n);
            femaleMap.put(n, f);
        }
        return femaleMap;
    }

    private Map<String, Integer> createMaleMap(List<String> nameList) {
        Map<String, Integer> maleMap = new HashMap<>();
        for (String n : nameList) {
            int f = maleNamePositionCheck(n);
            maleMap.put(n, f);
        }
        return maleMap;
    }
}
