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

/**
 * this class contains method necessary for the controller
 * Two strings are defined: path and and name of the files with female and male names from PESEL database
 */
@Service
public class QueryServiceImpl implements QueryService {

    /**
     * this is the list of female names from PESEL database. Each line consists of female name. Names are sorted by the popularity
     * in Poland, so for example Anna is higher (more popular) than Paulina (less popular). It is worth mentioning that
     * there are also names like Jan or Max which are in this list
     */
    private static final String femaleFile = "female_names.csv";

    /**
     * this is the list of male names from PESEL database. Each line consists of female name. Names are sorted by the popularity
     * in Poland, so for example Jan is higher (more popular) than Wojciech (less popular). It is worth mentioning that
     * there are also names like Maria in this list
     */
    private static final String maleFile = "male_names.csv";

    /**
     * @param variant - "one" if only first part of the String name should be analysed or any other String
     * @param name    - String name to be analysed
     * @return method returns List of Strings. Each element of the String name is one element of this list
     * names in the String name should be separated with one or more spaces.
     */
    @Override
    public List<String> createNameList(String variant, String name) {
        String[] tokenArray = name.toUpperCase().split(" +");
        if (variant.equalsIgnoreCase("one")) {
            return Arrays.asList(tokenArray[0]);
        }
        return Arrays.asList(tokenArray);
    }

    /**
     * @param nameList - List of names
     * @return method returns List of Response objects. For each element of the input String list, one element of the
     * Response is returned.
     * This method takes as an input the String List of names and than creates two maps: femaleMap and maleMap
     * Each entry set in both maps has a key - String with one name token and Integer - position of this token
     * in the list of names from PESEL database.
     * Names in this database are sorted by the popularity. For example JAN is more popular than WOJCIECH so JAN
     * will have smaller Integer as a value than WOJCIECH.
     * If name toked does not occur in the PESEL database its value is -1. For example WOJCIECH does not occur in female
     * names in PESEL so its value in femaleMap will be -1.
     */
    @Override
    public List<Response> createGenderList(List<String> nameList) {
        Map<String, Integer> femaleMap = createFemaleMap(nameList);
        Map<String, Integer> maleMap = createMaleMap(nameList);

        return mapComparer(nameList, femaleMap, maleMap);
    }


    /**
     * @param responseList List of Response objects each element represents gender of one name token
     * @return returns gender - single Response object, if it os not able to determine the gender, the response is
     * INCONCLUSIVE
     * the method counts how many elements in the input list are Female and how many are Male
     * if the majority is Female the method returns Female Response,
     * if the majority is Male the method returns Male Response,
     */
    @Override
    public Response genderResponse(List<Response> responseList) {
        Response result = Response.INCONCLUSIVE;
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
            result = Response.FEMALE;
        } else if (maleResponseCounter > femaleResponseCounter) {
            result = Response.MALE;
        }
        return result;
    }

    /**
     * @param gender - "male" or "female", otherwise exception is thrown
     * @param page   - which page of the list of names to be returned
     * @param limit  - how many names should be returned
     * @return method returns List of Strings with names from PE`seL database
     */
    @Override
    public List<String> getListOfNames(String gender, int page, int limit) {
        List<String> nameList = new ArrayList<>();
        int skipLines = limit * page;
        csvReader(gender, limit, nameList, skipLines);

        return nameList;
    }

    /**
     * @param gender    - "male" or "female", otherwise exception is thrown
     * @param limit     - how many names should be returned
     * @param nameList  - List of Strings to be filled with names
     * @param skipLines - how many lines from the begging of the file should be skipped
     *                  This method uses CSVReaderBuilder to read the CSV file with name list.
     *                  It could skip some lines from the begging of the file if skipLines parameter > 0 (pagination)
     *                  than the method fills the nameList with names until limit is full
     */
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

    /**
     * @param gender - "male" or "female", otherwise exception is thrown
     * @return method returns path and name of the file based on the gender. If the gender is not different than male or
     * female method throws exception
     */
    private String genderFileMatcher(String gender) {
        if (gender.equalsIgnoreCase("male")) {
            return maleFile;
        } else if (gender.equalsIgnoreCase("female")) {
            return femaleFile;
        } else {
            throw new QueryException(ErrorMessages.WRONG_QUERY.getErrorMessage());
        }
    }

    /**
     * @param nameList  - list of name tokens
     * @param femaleMap - entrySet has key String - name, value - Integer with position of this name in the PESEL database,
     *                  the higher number, the lower position of this name in the database
     * @param maleMap   - entrySet has key String - name, value - Integer with position of this name in the PESEL database,
     *                  the higher number, the lower position of this name in the database
     * @return method returns List of Response objects. Each element in this list represents an element in the nameList
     * This method iterates through nameList. For each elemnt it compares the value of this token in both maps.
     * First scenario - position in the femaleMap is > 0 and in maleMap equals -1, which means that this name is Female
     * Second scenario - opposite to the first one,
     * Third Scenario - position on the female is smaller than on the male list (this name is more popular as female than male
     * which means  that this name is female
     * Forth scenario - opposite to third one
     * Fifth scenario - any other possibility, which means that this name is neither female or male
     */
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

    /**
     * @param name - String single token name
     * @return method returns position of this name in the female names PESEL database
     */
    private int femaleNamePositionCheck(String name) {
        return namePositionCheck(name, femaleFile);
    }

    /**
     * @param name -  String single token name
     * @return method returns position of this name in the male names PESEL database
     */
    private int maleNamePositionCheck(String name) {
        return namePositionCheck(name, maleFile);
    }

    /**
     * @param name     - String single token name
     * @param fileName - name and path to the file to be searched
     * @return method returns int with position of name token in the file. The higher this position is the higher
     * the return value is. If the name is not found in the file method returns -1.
     * Method uses BufferedReader to query the file
     */
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

    /**
     * @param nameList - list of single name tokens
     * @return map where keys are single name tokens and values their positions in the female list from PESEL database
     * this method iterates through nameList and assigns value (position in the database) to each token
     */
    private Map<String, Integer> createFemaleMap(List<String> nameList) {
        Map<String, Integer> femaleMap = new HashMap<>();
        for (String n : nameList) {
            int f = femaleNamePositionCheck(n);
            femaleMap.put(n, f);
        }
        return femaleMap;
    }

    /**
     * @param nameList - list of single name tokens
     * @return map where keys are single name tokens and values their positions in the male list from PESEL database
     * this method iterates through nameList and assigns value (position in the database) to each token
     */
    private Map<String, Integer> createMaleMap(List<String> nameList) {
        Map<String, Integer> maleMap = new HashMap<>();
        for (String n : nameList) {
            int f = maleNamePositionCheck(n);
            maleMap.put(n, f);
        }
        return maleMap;
    }
}
