import java.util.ArrayList;

/**
 * The StopAndFrisk class represents stop-and-frisk data, provided by
 * the New York Police Department (NYPD), that is used to compare
 * during when the policy was put in place and after the policy ended.
 * 
 * @author Tanvi Yamarthy
 * @author Vidushi Jindal
 */
public class StopAndFrisk {

    /*
     * The ArrayList keeps track of years that are loaded from CSV data file.
     * Each SFYear corresponds to 1 year of SFRecords.
     * Each SFRecord corresponds to one stop and frisk occurrence.
     */
    private ArrayList<SFYear> database;

    /*
     * Constructor creates and initializes the @database array
     * 
     * DO NOT update nor remove this constructor
     */
    public StopAndFrisk() {
        database = new ArrayList<>();
    }

    /*
     * Getter method for the database.
     * *** DO NOT REMOVE nor update this method ****
     */
    public ArrayList<SFYear> getDatabase() {
        return database;
    }

    /**
     * This method reads the records information from an input csv file and
     * populates
     * the database.
     * 
     * Each stop and frisk record is a line in the input csv file.
     * 
     * 1. Open file utilizing StdIn.setFile(csvFile)
     * 2. While the input still contains lines:
     * - Read a record line (see assignment description on how to do this)
     * - Create an object of type SFRecord containing the record information
     * - If the record's year has already is present in the database:
     * - Add the SFRecord to the year's records
     * - If the record's year is not present in the database:
     * - Create a new SFYear
     * - Add the SFRecord to the new SFYear
     * - Add the new SFYear to the database ArrayList
     * 
     * @param csvFile
     */
    public void readFile(String csvFile) {

        // DO NOT remove these two lines
        StdIn.setFile(csvFile); // Opens the file
        StdIn.readLine(); // Reads and discards the header line

        while (!StdIn.isEmpty()) {

            String[] entries = StdIn.readLine().split(",");

            int year = Integer.parseInt(entries[0]);

            String description = entries[2];
            Boolean arrested = entries[13].equals("Y");
            Boolean frisked = entries[16].equals("Y");
            String gender = entries[52];
            String race = entries[66];
            String location = entries[71];

            SFRecord record = new SFRecord(description, arrested, frisked, gender, race, location);

            boolean yearExists = false;
            for (SFYear sfYear : database) {
                if (sfYear.getcurrentYear() == year) {
                    sfYear.addRecord(record);
                    yearExists = true;
                    break;
                }
            }

            if (!yearExists) {
                SFYear newYear = new SFYear(year);
                newYear.addRecord(record);
                database.add(newYear);
            }
        }
    }

    /**
     * This method returns the stop and frisk records of a given year where
     * the people that was stopped was of the specified race.
     * 
     * @param year we are only interested in the records of year.
     * @param race we are only interested in the records of stops of people of race.
     * @return an ArrayList containing all stop and frisk records for people of the
     *         parameters race and year.
     */

    public ArrayList<SFRecord> populationStopped(int year, String race) {

        ArrayList<SFRecord> records = new ArrayList<SFRecord>();

        for (SFYear sfYear : database) {
            if (sfYear.getcurrentYear() == year) {
                ArrayList<SFRecord> yearRecords = sfYear.getRecordsForYear();
                for (SFRecord yearRecord : yearRecords) {
                    if (yearRecord.getRace().equals(race)) {
                        records.add(yearRecord);
                    }
                }
            }
        }
        return records;
    }

    /**
     * This method computes the percentage of records where the person was frisked
     * and the
     * percentage of records where the person was arrested.
     * 
     * @param year we are only interested in the records of year.
     * @return the percent of the population that were frisked and the percent that
     *         were arrested.
     */
    public double[] friskedVSArrested(int year) {

        double frisked = 0;
        double arrested = 0;
        double numOfRecords = 0;

        for (SFYear sfYear : database) {
            if (sfYear.getcurrentYear() == year) {
                ArrayList<SFRecord> yearRecords = sfYear.getRecordsForYear();
                numOfRecords = yearRecords.size();
                for (SFRecord yearRecord : yearRecords) {
                    if (yearRecord.getFrisked() == true) {
                        frisked++;
                    }
                    if (yearRecord.getArrested() == true) {
                        arrested++;
                    }
                }
            }
        }
        double percentageFrisked = frisked / numOfRecords * 100;
        double percentageArrested = arrested / numOfRecords * 100;

        double[] result = new double[] {
                percentageFrisked, percentageArrested
        };

        return result;
    }

    /**
     * This method keeps track of the fraction of Black females, Black males,
     * White females and White males that were stopped for any reason.
     * Drawing out the exact table helps visualize the gender bias.
     * 
     * @param year we are only interested in the records of year.
     * @return a 2D array of percent of number of White and Black females
     *         versus the number of White and Black males.
     */
    public double[][] genderBias(int year) {

        double black = 0;
        double white = 0;
        double blackM = 0;
        double blackF = 0;
        double whiteM = 0;
        double whiteF = 0;

        for (SFYear sfYear : database) {
            if (sfYear.getcurrentYear() == year) {

                ArrayList<SFRecord> yearRecords = sfYear.getRecordsForYear();

                for (SFRecord yearRecord : yearRecords) {
                    if (yearRecord.getRace().equals("B")) {
                        black++;
                    }
                    if (yearRecord.getRace().equals("W")) {
                        white++;
                    }
                    if (yearRecord.getRace().equals("B") && yearRecord.getGender().equals("F")) {
                        blackF++;
                    } else if (yearRecord.getRace().equals("B") && yearRecord.getGender().equals("M")) {
                        blackM++;
                    } else if (yearRecord.getRace().equals("W") && yearRecord.getGender().equals("F")) {
                        whiteF++;
                    } else if (yearRecord.getRace().equals("W") && yearRecord.getGender().equals("M")) {
                        whiteM++;
                    }
                }
            }
        }
        
        double blackMPerc = blackM / black * 0.5 * 100;
        double blackFPerc = blackF / black * 0.5 * 100;
        double whiteMPerc = whiteM / white * 0.5 * 100;
        double whiteFPerc = whiteF / white * 0.5 * 100;

        double totalF = blackFPerc + whiteFPerc;
        double totalM = blackMPerc + whiteMPerc;

        double[][] result = new double[][] {
                { blackFPerc, whiteFPerc, totalF },
                { blackMPerc, whiteMPerc, totalM }
        };

        return result;
    }

    /**
     * This method checks to see if there has been increase or decrease
     * in a certain crime from year 1 to year 2.
     * 
     * Expect year1 to preceed year2 or be equal.
     * 
     * @param crimeDescription
     * @param year1            first year to compare.
     * @param year2            second year to compare.
     * @return
     */

    public double crimeIncrease(String crimeDescription, int year1, int year2) {

        SFYear sfYear1 = null;
        SFYear sfYear2 = null;
        for (SFYear sfYear : database) {
            if (sfYear.getcurrentYear() == year1) {
                sfYear1 = sfYear;
            } else if (sfYear.getcurrentYear() == year2) {
                sfYear2 = sfYear;
            }
        }

        double count1 = 0;
        ArrayList<SFRecord> year1Records = sfYear1.getRecordsForYear();
        double year1Total = year1Records.size();
        for (SFRecord yearRecord : year1Records) {
            if (yearRecord.getDescription().indexOf(crimeDescription) >= 0) {
                count1++;
            }
        }

        double count2 = 0;
        ArrayList<SFRecord> year2Records = sfYear2.getRecordsForYear();
        double year2Total = year2Records.size();
        for (SFRecord yearRecord : year2Records) {
            if (yearRecord.getDescription().indexOf(crimeDescription) >= 0) {
                count2++;
            }
        }

        double perc1 = count1 / year1Total * 100;
        double perc2 = count2 / year2Total * 100;

        double percentageIncrease = perc2 - perc1;
        return percentageIncrease;
    }

    /**
     * This method results the NYC borough where the most amount of stops
     * occurred in a given year. This method will mainly analyze the five
     * following boroughs in New York City: Brooklyn, Manhattan, Bronx,
     * Queens, and Staten Island.
     * 
     * @param year we are only interested in the records of year.
     * @return the borough with the greatest number of stops
     */
    public String mostCommonBorough(int year) {
        String[] boroughs = new String[]{
            "Brooklyn", "Manhattan", "Bronx", "Queens", "Staten Island"
        };
        int[] counts = new int[5];
        for (SFYear sfYear : database) {
            if (sfYear.getcurrentYear() == year) {
                ArrayList<SFRecord> yearRecords = sfYear.getRecordsForYear();
                for (SFRecord yearRecord : yearRecords) {
                    if (yearRecord.getLocation().equalsIgnoreCase("BROOKLYN")) {
                        counts[0]++;
                    } else if (yearRecord.getLocation().equalsIgnoreCase("MANHATTAN")) {
                        counts[1]++;
                    } else if (yearRecord.getLocation().equalsIgnoreCase("BRONX")) {
                        counts[2]++;
                    } else if (yearRecord.getLocation().equalsIgnoreCase("QUEENS")) {
                        counts[3]++;
                    } else if (yearRecord.getLocation().equalsIgnoreCase("STATEN ISLAND")) {
                        counts[4]++;
                    }
                }
            }
        }

        int maxIndex = 0;
        for (int i = 0; i < counts.length; i++) {
            if (counts[i] > counts[maxIndex]) {
                maxIndex = i;
            }
        }
        return boroughs[maxIndex]; 
    }

}
