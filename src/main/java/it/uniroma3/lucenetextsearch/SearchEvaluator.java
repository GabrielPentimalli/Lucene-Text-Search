package it.uniroma3.lucenetextsearch;

import java.io.IOException;

/**
 * Test class for evaluating search queries with metrics
 * Runs predefined queries and calculates precision, recall, and F1-score
 * 
 * Project: Lucene Text Search
 */
public class SearchEvaluator {
    
    /**
     * Main method for running evaluation tests
     */
    public static void main(String[] args) {
        System.out.println("=== LUCENE SEARCH EVALUATION ===\n");
        
        try {
            // Initialize searcher
            LuceneSearcher searcher = new LuceneSearcher();
            
            // Define test queries
            String[][] testQueries = {
                {"Query 1", "content lucene"},
                {"Query 2", "content \"information retrieval\""},
                {"Query 3", "content natural language processing"},
                {"Query 4", "filename .txt"}
            };
            
            System.out.println("Running " + testQueries.length + " test queries...\n");
            System.out.println("=".repeat(70));
            
            // Run each test query
            for (String[] queryData : testQueries) {
                String queryName = queryData[0];
                String queryString = queryData[1];
                
                System.out.println("\n" + queryName);
                System.out.println("Query: " + queryString);
                System.out.println("-".repeat(70));
                
                try {
                    searcher.search(queryString, queryName);
                } catch (Exception e) {
                    System.err.println("Error executing query: " + e.getMessage());
                }
                
                System.out.println("=".repeat(70));
            }
            
            searcher.close();
            System.out.println("\nEvaluation complete!");
            System.out.println("\nNote: Metrics are calculated based on score distribution:");
            System.out.println("  - Precision: ratio of high-scoring documents to total retrieved");
            System.out.println("  - Recall: ratio of retrieved documents to total matching in index");
            System.out.println("  - F1-Score: harmonic mean of precision and recall");
            
        } catch (IOException e) {
            System.err.println("Initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
