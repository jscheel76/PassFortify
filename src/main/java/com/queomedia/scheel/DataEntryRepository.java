package com.queomedia.scheel;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataEntryRepository {
    private final List<DataEntry> dataEntries;

    public DataEntryRepository() {
        dataEntries = new ArrayList<>();
    }

    public void addDataEntry(DataEntry dataEntry) {
        dataEntries.add(dataEntry);
    }

    public void removeDataEntry(DataEntry dataEntry) {
        dataEntries.remove(dataEntry);
    }

    public List<DataEntry> getAllDataEntries() {
        return new ArrayList<>(dataEntries);
    }

    public void printAllDataEntries() {
        for (DataEntry dataEntry : dataEntries) {
            System.out.println(dataEntry.getService() + " " + dataEntry.getUsername() + " " + dataEntry.getPassword());
        }
    }

    public void saveToTextFile(String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            for (DataEntry entry : dataEntries) {
                writer.write("Service: " + entry.getService() + "\n");
                writer.write("Username: " + entry.getUsername() + "\n");
                writer.write("Password: " + entry.getPassword() + "\n\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle file I/O exception
        }
    }
}
