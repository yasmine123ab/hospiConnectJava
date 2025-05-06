package org.hospiconnect.utils;

import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Utility class that generates AI-based medication notes for common medications
 */
public class MedicationAIHelper {
    
    private static final Map<String, String> medicationDatabase = new HashMap<>();
    private static final Random random = new Random();
    
    // API key for Gemini API - you should replace this with your actual API key
    // This would ideally be stored in a configuration file or environment variable
    private static final String GEMINI_API_KEY = "AIzaSyBAJpdDvDlIQ-9_DTaZkM9VyT3SkN1nuKs"; // Replace with actual key
    
    // Populate the database for fallback if API fails
    static {
        // Antibiotics
        medicationDatabase.put("amoxicillin", 
            "MEDICATION: Amoxicillin\n" +
            "CLASSIFICATION: Antibiotic (penicillin-type)\n" +
            "USAGE: Take with or without food. Complete the full course even if symptoms improve.\n" +
            "COMMON SIDE EFFECTS: Diarrhea, nausea, skin rash.\n" +
            "PRECAUTIONS: Tell your doctor if you have allergies to penicillin or cephalosporin antibiotics.\n" +
            "STORAGE: Store at room temperature away from moisture and heat.");
            
        medicationDatabase.put("azithromycin", 
            "MEDICATION: Azithromycin\n" +
            "CLASSIFICATION: Antibiotic (macrolide-type)\n" +
            "USAGE: Take on an empty stomach, 1 hour before or 2 hours after meals.\n" +
            "COMMON SIDE EFFECTS: Diarrhea, nausea, abdominal pain.\n" +
            "PRECAUTIONS: Tell your doctor if you have liver or heart rhythm problems.\n" +
            "STORAGE: Store at room temperature away from moisture and heat.");
            
        // Pain Relievers
        medicationDatabase.put("ibuprofen", 
            "MEDICATION: Ibuprofen\n" +
            "CLASSIFICATION: Non-steroidal anti-inflammatory drug (NSAID)\n" +
            "USAGE: Take with food to reduce stomach upset. Do not exceed recommended dosage.\n" +
            "COMMON SIDE EFFECTS: Stomach pain, heartburn, dizziness.\n" +
            "PRECAUTIONS: Not recommended for individuals with heart conditions, ulcers, or kidney problems.\n" +
            "STORAGE: Store at room temperature away from moisture.");
            
        medicationDatabase.put("paracetamol", 
            "MEDICATION: Paracetamol (Acetaminophen)\n" +
            "CLASSIFICATION: Analgesic, antipyretic\n" +
            "USAGE: Take as directed, do not exceed maximum daily dose of 4000mg.\n" +
            "COMMON SIDE EFFECTS: Generally well-tolerated when used correctly.\n" +
            "PRECAUTIONS: Can cause liver damage in high doses or when combined with alcohol.\n" +
            "STORAGE: Store at room temperature away from moisture.");
            
        // Blood Pressure Medications
        medicationDatabase.put("amlodipine", 
            "MEDICATION: Amlodipine\n" +
            "CLASSIFICATION: Calcium channel blocker\n" +
            "USAGE: Take daily at the same time with or without food.\n" +
            "COMMON SIDE EFFECTS: Swelling in ankles/feet, flushing, headache, dizziness.\n" +
            "PRECAUTIONS: Do not discontinue without consulting your doctor.\n" +
            "STORAGE: Store at room temperature away from moisture and heat.");
            
        medicationDatabase.put("lisinopril", 
            "MEDICATION: Lisinopril\n" +
            "CLASSIFICATION: ACE inhibitor\n" +
            "USAGE: Take at the same time each day. May be taken with or without food.\n" +
            "COMMON SIDE EFFECTS: Dry cough, dizziness, headache.\n" +
            "PRECAUTIONS: Monitor for swelling of face/lips/tongue which could indicate an allergic reaction.\n" +
            "STORAGE: Store at room temperature away from moisture and heat.");
            
        // Diabetes Medications
        medicationDatabase.put("metformin", 
            "MEDICATION: Metformin\n" +
            "CLASSIFICATION: Oral antidiabetic (biguanide)\n" +
            "USAGE: Take with meals to reduce stomach upset. Do not crush extended-release tablets.\n" +
            "COMMON SIDE EFFECTS: Nausea, diarrhea, stomach pain, metallic taste.\n" +
            "PRECAUTIONS: Avoid alcohol consumption while taking this medication.\n" +
            "STORAGE: Store at room temperature away from moisture and heat.");
            
        // Cholesterol Medications
        medicationDatabase.put("atorvastatin", 
            "MEDICATION: Atorvastatin\n" +
            "CLASSIFICATION: Statin (HMG-CoA reductase inhibitor)\n" +
            "USAGE: Take at the same time each day, preferably in the evening.\n" +
            "COMMON SIDE EFFECTS: Muscle pain, weakness, constipation.\n" +
            "PRECAUTIONS: Avoid grapefruit juice while taking this medication.\n" +
            "STORAGE: Store at room temperature away from moisture.");
    }
    
    /**
     * Generates AI-based notes for a medication using Gemini API
     * 
     * @param medicationName The name of the medication
     * @param timesPerDay Number of times the medication should be taken per day
     * @return AI-generated notes about the medication
     */
    public static String generateMedicationNotes(String medicationName, int timesPerDay) {
        // Try to generate notes with Gemini API first
        String geminiNotes = generateMedicationNotesWithGemini(medicationName, timesPerDay);
        
        // If Gemini API fails, fall back to the local database
        if (geminiNotes != null && !geminiNotes.isEmpty()) {
            return geminiNotes;
        }
        
        // Fallback to original method
        String medicationLower = medicationName.toLowerCase().trim();
        
        // Check if we have specific information about this medication
        for (String key : medicationDatabase.keySet()) {
            if (medicationLower.contains(key)) {
                String basicInfo = medicationDatabase.get(key);
                String dosageInfo = generateDosageRecommendation(medicationName, timesPerDay);
                return basicInfo + "\n\n" + dosageInfo;
            }
        }
        
        // If the medication is not in our database, generate generic information
        return generateGenericMedicationInfo(medicationName, timesPerDay);
    }
    
    /**
     * Generates medication notes using the Gemini API
     */
    private static String generateMedicationNotesWithGemini(String medicationName, int timesPerDay) {
        try {
            // Prepare the API request
            URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + GEMINI_API_KEY);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Create prompt for Gemini
            String prompt = String.format(
                "Generate detailed medication information about %s that is taken %d times a day. " +
                "Format the response with the following sections: " +
                "MEDICATION: [name] " +
                "CLASSIFICATION: [medication class] " +
                "USAGE: [how to take it] " +
                "DOSAGE: [specific information about taking it %d times daily] " +
                "COMMON SIDE EFFECTS: [list main side effects] " +
                "PRECAUTIONS: [important warnings and precautions] " +
                "STORAGE: [how to store the medication] " +
                "Keep it informative, factual and in a format suitable for a medical application.",
                medicationName, timesPerDay, timesPerDay
            );

            // Create request JSON
            String requestBody = String.format(
                "{\"contents\": [{\"parts\":[{\"text\": \"%s\"}]}]}",
                prompt.replace("\"", "\\\"") // Escape quotes in the prompt
            );

            // Send the request
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get the response
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }

            // Extract the text from the JSON response
            String jsonResponse = response.toString();
            
            // Improved JSON parsing that's more robust
            if (jsonResponse.contains("\"text\":")) {
                // Find the content section in the JSON
                int contentIndex = jsonResponse.indexOf("\"content\":");
                if (contentIndex >= 0) {
                    // Find the text field within the content
                    int textIndex = jsonResponse.indexOf("\"text\":", contentIndex);
                    if (textIndex >= 0) {
                        // Extract the text value
                        int valueStartIndex = jsonResponse.indexOf("\"", textIndex + 7) + 1;
                        int valueEndIndex = jsonResponse.indexOf("\"", valueStartIndex);
                        
                        if (valueStartIndex > 0 && valueEndIndex > valueStartIndex) {
                            String extractedText = jsonResponse.substring(valueStartIndex, valueEndIndex);
                            // Replace escaped quotes and newlines
                            extractedText = extractedText.replace("\\\"", "\"").replace("\\n", "\n");
                            return extractedText;
                        }
                    }
                }
            }
            
            System.out.println("Could not parse Gemini API response: " + jsonResponse);
            
        } catch (Exception e) {
            System.out.println("Error using Gemini API for medication notes: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Return null if API call fails, so we can fall back to the local database
        return null;
    }
    
    /**
     * Generates dosage recommendation based on the number of times per day
     */
    private static String generateDosageRecommendation(String medicationName, int timesPerDay) {
        StringBuilder sb = new StringBuilder();
        sb.append("DOSAGE RECOMMENDATION:\n");
        
        switch (timesPerDay) {
            case 1:
                sb.append("Take once daily, preferably at the same time each day.");
                break;
            case 2:
                sb.append("Take twice daily, once in the morning and once in the evening. Space doses about 12 hours apart.");
                break;
            case 3:
                sb.append("Take three times a day, preferably with meals. Space doses about 8 hours apart.");
                break;
            case 4:
                sb.append("Take four times a day, preferably with or after meals and at bedtime. Space doses about 6 hours apart.");
                break;
            default:
                if (timesPerDay > 4) {
                    sb.append("Take ").append(timesPerDay).append(" times daily as directed by your healthcare provider. ");
                    sb.append("Space doses evenly throughout the day.");
                } else {
                    sb.append("Follow your doctor's instructions for proper dosage.");
                }
        }
        
        return sb.toString();
    }
    
    /**
     * Generates generic medication information when specific information is not available
     */
    private static String generateGenericMedicationInfo(String medicationName, int timesPerDay) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("MEDICATION: ").append(medicationName).append("\n");
        sb.append("USAGE: Follow your healthcare provider's instructions carefully.\n");
        
        // Add general precautions
        String[] precautions = {
            "Take with a full glass of water unless instructed otherwise.",
            "Store at room temperature away from moisture and heat.",
            "Keep all medications out of reach of children.",
            "Do not share your medication with others.",
            "Inform your doctor of any side effects you experience.",
            "Complete the full course of medication unless instructed otherwise.",
            "Keep track of your medication schedule to ensure doses are not missed."
        };
        
        // Add random precautions
        sb.append("GENERAL PRECAUTIONS:\n");
        int numPrecautions = 3 + random.nextInt(3); // 3-5 precautions
        
        for (int i = 0; i < numPrecautions; i++) {
            int index = random.nextInt(precautions.length);
            sb.append("- ").append(precautions[index]).append("\n");
        }
        
        // Add dosage information
        sb.append("\n").append(generateDosageRecommendation(medicationName, timesPerDay));
        
        return sb.toString();
    }
}