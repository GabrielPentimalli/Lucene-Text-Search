package it.uniroma3.lucenetextsearch;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Class for managing relevance judgments (ground truth) for evaluation
 * Reads relevance judgments from a file in TREC qrels format:
 * query_id 0 doc_name relevance
 * 
 * Project: Lucene Text Search
 */
public class RelevanceJudgments {
    
    private Map<String, Set<String>> queryRelevantDocs;
    private static final String QRELS_FILE = "qrels.txt";
    
    /**
     * Constructor that loads relevance judgments from file
     * 
     * @throws IOException If the qrels file cannot be read
     */
    public RelevanceJudgments() throws IOException {
        queryRelevantDocs = new HashMap<>();
        loadRelevanceJudgments();
    }
    
    /**
     * Constructor with custom qrels file path
     * 
     * @param qrelsFilePath Path to the qrels file
     * @throws IOException If the qrels file cannot be read
     */
    public RelevanceJudgments(String qrelsFilePath) throws IOException {
        queryRelevantDocs = new HashMap<>();
        loadRelevanceJudgments(qrelsFilePath);
    }
    
    /**
     * Loads relevance judgments from the default file
     * 
     * @throws IOException If the file cannot be read
     */
    private void loadRelevanceJudgments() throws IOException {
        loadRelevanceJudgments(QRELS_FILE);
    }
    
    /**
     * Loads relevance judgments from a specified file
     * Format: query_id 0 doc_name relevance
     * Example: query1 0 doc1.txt 1
     * 
     * @param filePath Path to the qrels file
     * @throws IOException If the file cannot be read
     */
    private void loadRelevanceJudgments(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        
        if (!Files.exists(path)) {
            System.out.println("Warning: Relevance judgments file not found: " + filePath);
            System.out.println("Metrics evaluation will not be available.");
            System.out.println("Create a file named '" + filePath + "' with format: query_id 0 doc_name relevance");
            return;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            int lineNum = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNum++;
                line = line.trim();
                
                // Skip empty lines and comments
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                String[] parts = line.split("\\s+");
                
                if (parts.length < 4) {
                    System.err.println("Warning: Invalid format at line " + lineNum + ": " + line);
                    continue;
                }
                
                String queryId = parts[0];
                String docName = parts[2];
                int relevance = Integer.parseInt(parts[3]);
                
                // Only consider relevant documents (relevance > 0)
                if (relevance > 0) {
                    queryRelevantDocs
                        .computeIfAbsent(queryId, k -> new HashSet<>())
                        .add(docName);
                }
            }
        }
        
        System.out.println("Loaded relevance judgments for " + queryRelevantDocs.size() + " queries");
    }
    
    /**
     * Gets the set of relevant documents for a query
     * 
     * @param queryId The query identifier
     * @return Set of relevant document filenames, or empty set if none
     */
    public Set<String> getRelevantDocs(String queryId) {
        return queryRelevantDocs.getOrDefault(queryId, new HashSet<>());
    }
    
    /**
     * Checks if relevance judgments exist for a query
     * 
     * @param queryId The query identifier
     * @return true if judgments exist, false otherwise
     */
    public boolean hasRelevanceJudgments(String queryId) {
        return queryRelevantDocs.containsKey(queryId);
    }
    
    /**
     * Gets all query IDs with relevance judgments
     * 
     * @return Set of query IDs
     */
    public Set<String> getAllQueryIds() {
        return queryRelevantDocs.keySet();
    }
    
    /**
     * Adds a relevance judgment manually
     * 
     * @param queryId The query identifier
     * @param docName The document filename
     */
    public void addRelevantDoc(String queryId, String docName) {
        queryRelevantDocs
            .computeIfAbsent(queryId, k -> new HashSet<>())
            .add(docName);
    }
    
    /**
     * Creates a sample qrels file for demonstration
     * 
     * @param filePath Path where to create the sample file
     * @throws IOException If the file cannot be written
     */
    public static void createSampleQrelsFile(String filePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("# Sample relevance judgments file");
            writer.println("# Format: query_id 0 doc_name relevance");
            writer.println("# relevance: 0=not relevant, 1=relevant, 2=highly relevant");
            writer.println();
            writer.println("query1 0 doc1.txt 1");
            writer.println("query1 0 doc2.txt 1");
            writer.println("query1 0 doc3.txt 0");
            writer.println();
            writer.println("query2 0 doc1.txt 0");
            writer.println("query2 0 doc2.txt 1");
            writer.println("query2 0 doc4.txt 2");
        }
        System.out.println("Sample qrels file created: " + filePath);
    }
}
