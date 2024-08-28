package com.bajaj.test;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONArray;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class Test {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("give command line arguememnt");
            System.exit(1);
        }

        String prnNumber = args[0];
        String jsonFilePath = args[1];

        try {
           
            Reader reader = new FileReader(jsonFilePath);
            JSONTokener tokener = new JSONTokener(reader);
            JSONObject jsonObject = new JSONObject(tokener);

            
            String destination = findDestination(jsonObject);

            if (destination == null) {
                System.out.println("Key 'destination' not found.");
                System.exit(1);
            }

            
            String randomString = generateRandomString(8);

           
            String stringToHash = prnNumber + destination + randomString;

            
            String hash = computeHash(stringToHash);

            // Format the output as "hash;random string"
            String output = hash + ";" + randomString;
            System.out.println(output);

        } catch (IOException e) {
            System.err.println("Error reading JSON file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error processing JSON file: " + e.getMessage());
        }
    }

    private static String findDestination(JSONObject jsonObject) {
        if (jsonObject.has("destination")) {
            return jsonObject.getString("destination");
        }

        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            if (value instanceof JSONObject) {
                String result = findDestination((JSONObject) value);
                if (result != null) {
                    return result;
                }
            } else if (value instanceof JSONArray) {
                JSONArray array = (JSONArray) value;
                for (int i = 0; i < array.length(); i++) {
                    Object item = array.get(i);
                    if (item instanceof JSONObject) {
                        String result = findDestination((JSONObject) item);
                        if (result != null) {
                            return result;
                        }
                    }
                }
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }

    private static String computeHash(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
