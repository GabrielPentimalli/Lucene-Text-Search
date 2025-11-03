package it.uniroma3.lucenetextsearch;

import java.util.Set;
import java.util.HashSet;

/**
 * Class for calculating and storing search evaluation metrics
 * including precision, recall, and F1-score
 * 
 * Project: Lucene Text Search
 */
public class SearchMetrics {
    
    private Set<String> retrievedDocs;
    private Set<String> relevantDocs;
    private int totalRetrieved;
    private int totalRelevant;
    private int relevantRetrieved;
    
    /**
     * Constructor
     * 
     * @param retrievedDocs Set of retrieved document filenames
     * @param relevantDocs Set of relevant document filenames (ground truth)
     */
    public SearchMetrics(Set<String> retrievedDocs, Set<String> relevantDocs) {
        this.retrievedDocs = new HashSet<>(retrievedDocs);
        this.relevantDocs = new HashSet<>(relevantDocs);
        calculateMetrics();
    }
    
    /**
     * Calculates the metrics based on retrieved and relevant documents
     */
    private void calculateMetrics() {
        totalRetrieved = retrievedDocs.size();
        totalRelevant = relevantDocs.size();
        
        // Calculate intersection: relevant documents that were retrieved
        Set<String> intersection = new HashSet<>(retrievedDocs);
        intersection.retainAll(relevantDocs);
        relevantRetrieved = intersection.size();
    }
    
    /**
     * Calculates precision: (Relevant Retrieved) / (Total Retrieved)
     * 
     * @return Precision value between 0 and 1, or 0 if no documents retrieved
     */
    public double getPrecision() {
        if (totalRetrieved == 0) {
            return 0.0;
        }
        return (double) relevantRetrieved / totalRetrieved;
    }
    
    /**
     * Calculates recall: (Relevant Retrieved) / (Total Relevant)
     * 
     * @return Recall value between 0 and 1, or 0 if no relevant documents
     */
    public double getRecall() {
        if (totalRelevant == 0) {
            return 0.0;
        }
        return (double) relevantRetrieved / totalRelevant;
    }
    
    /**
     * Calculates F1-score: 2 * (Precision * Recall) / (Precision + Recall)
     * Harmonic mean of precision and recall
     * 
     * @return F1-score value between 0 and 1
     */
    public double getF1Score() {
        double precision = getPrecision();
        double recall = getRecall();
        
        if (precision + recall == 0) {
            return 0.0;
        }
        
        return 2 * (precision * recall) / (precision + recall);
    }
    
    /**
     * Gets the number of relevant documents that were retrieved
     * 
     * @return Number of relevant retrieved documents
     */
    public int getRelevantRetrieved() {
        return relevantRetrieved;
    }
    
    /**
     * Gets the total number of retrieved documents
     * 
     * @return Total retrieved documents
     */
    public int getTotalRetrieved() {
        return totalRetrieved;
    }
    
    /**
     * Gets the total number of relevant documents (ground truth)
     * 
     * @return Total relevant documents
     */
    public int getTotalRelevant() {
        return totalRelevant;
    }
    
    /**
     * Returns a formatted string with all metrics
     * 
     * @return Formatted metrics string
     */
    public String getFormattedMetrics() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n=== EVALUATION METRICS ===\n");
        sb.append(String.format("Retrieved documents:    %d\n", totalRetrieved));
        sb.append(String.format("Relevant documents:     %d\n", totalRelevant));
        sb.append(String.format("Relevant retrieved:     %d\n", relevantRetrieved));
        sb.append(String.format("Precision:              %.4f (%.2f%%)\n", getPrecision(), getPrecision() * 100));
        sb.append(String.format("Recall:                 %.4f (%.2f%%)\n", getRecall(), getRecall() * 100));
        sb.append(String.format("F1-Score:               %.4f\n", getF1Score()));
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return getFormattedMetrics();
    }
}
