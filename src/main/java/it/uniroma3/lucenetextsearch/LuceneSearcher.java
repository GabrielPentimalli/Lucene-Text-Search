package it.uniroma3.lucenetextsearch;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class for performing searches on the Lucene index
 * Supports custom syntax: filename <terms> or content <terms>
 * 
 * Project: Lucene Text Search
 */
public class LuceneSearcher {
    
    private static final String FIELD_FILENAME = "filename";
    private static final String FIELD_CONTENT = "content";
    private static final String INDEX_DIR = "lucene_index";
    private static final int MAX_RESULTS = 20;
    
    private IndexReader reader;
    private IndexSearcher searcher;
    private Analyzer analyzer;
    
    /**
     * Constructor that opens the index and configures analyzers
     * 
     * @throws IOException If an I/O error occurs
     */
    public LuceneSearcher() throws IOException {
        Directory indexDirectory = FSDirectory.open(Paths.get(INDEX_DIR));
        reader = DirectoryReader.open(indexDirectory);
        searcher = new IndexSearcher(reader);
   
        // Same analyzer setup used in indexing
        Map<String, Analyzer> analyzerPerField = new HashMap<>();
        analyzerPerField.put(FIELD_FILENAME, new KeywordAnalyzer());
        Analyzer defaultAnalyzer = new StandardAnalyzer();
        analyzer = new PerFieldAnalyzerWrapper(defaultAnalyzer, analyzerPerField);
    }
    
    /**
     * Executes a search using custom syntax
     * Format: filename <terms> or content <terms>
     * If terms are in quotes, executes a phrase query
     *
     * @param queryString The query string to execute
     * @throws Exception If an error occurs during search
     */
    public void search(String queryString) throws Exception {
        // Pattern to parse the query: field followed by terms
        // Supports phrase queries with quotes
        Pattern pattern = Pattern.compile("^(filename|content)\\s+(.+)$");
        Matcher matcher = pattern.matcher(queryString.trim());
        
        if (!matcher.matches()) {
            System.out.println("Invalid syntax. Use: filename <terms> or content <terms>");
            System.out.println("Example: filename file1.txt");
            System.out.println("Example: content \"advanced search\"");
            return;
        }
        
        String field = matcher.group(1);
        String searchTerms = matcher.group(2).trim();
        
        // Map field name to internal field
        String luceneField = field.equals("filename") ? FIELD_FILENAME : FIELD_CONTENT;
        
        Query query;
        
        // Check if it's a phrase query (in quotes)
        if (searchTerms.startsWith("\"") && searchTerms.endsWith("\"")) {
            // Phrase query: remove quotes and create PhraseQuery
            String phraseText = searchTerms.substring(1, searchTerms.length() - 1);
            query = buildPhraseQuery(luceneField, phraseText);
            System.out.println("Searching phrase: \"" + phraseText + "\" in field: " + field);
        } else {
            // Normal query with QueryParser
            QueryParser parser = new QueryParser(luceneField, analyzer);
            query = parser.parse(searchTerms);
            System.out.println("Searching: \"" + searchTerms + "\" in field: " + field);
        }
        
        // Execute the search
        long startTime = System.currentTimeMillis();
        TopDocs results = searcher.search(query, MAX_RESULTS);
        long endTime = System.currentTimeMillis();
        
        // Display results
        displayResults(results, endTime - startTime);
    }
    
    /**
     * Builds a PhraseQuery for exact phrase search
     * 
     * @param field The field to search in
     * @param phraseText The phrase text
     * @return The constructed PhraseQuery
     */
    private Query buildPhraseQuery(String field, String phraseText) {
        // Split terms and create phrase query
        String[] terms = phraseText.toLowerCase().split("\\s+");
        PhraseQuery.Builder builder = new PhraseQuery.Builder();
        
        for (String term : terms) {
            if (!term.isEmpty()) {
                builder.add(new Term(field, term));
            }
        }
        
        // Slop 0 = terms must be consecutive
        builder.setSlop(0);
        return builder.build();
    }
    
    /**
     * Displays search results
     * 
     * @param results The search results
     * @param searchTime Time taken for search in milliseconds
     * @throws IOException If an I/O error occurs
     */
    private void displayResults(TopDocs results, long searchTime) throws IOException {
        System.out.println("\n=== SEARCH RESULTS ===");
        System.out.println("Found " + results.totalHits.value() + " documents");
        System.out.println("Search time: " + searchTime + " ms\n");
        
        if (results.scoreDocs.length == 0) {
            System.out.println("No documents found.");
            return;
        }
        
        int rank = 1;
        for (ScoreDoc scoreDoc : results.scoreDocs) {
            Document doc = searcher.storedFields().document(scoreDoc.doc);
            
            System.out.println(rank + ". File: " + doc.get(FIELD_FILENAME));
            System.out.println("   Score: " + String.format("%.4f", scoreDoc.score));
            
            // Show content preview (first 150 characters)
            String content = doc.get(FIELD_CONTENT);
            if (content != null && content.length() > 0) {
                String preview = content.length() > 150 
                    ? content.substring(0, 150) + "..." 
                    : content;
                System.out.println("   Preview: " + preview.replace("\n", " "));
            }
            System.out.println();
            rank++;
        }
    }
    
    /**
     * Closes resources
     * 
     * @throws IOException If an I/O error occurs
     */
    public void close() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }
    
    /**
     * Interactive main method for executing searches
     */
    public static void main(String[] args) {
        System.out.println("=== LUCENE TEXT SEARCH ===");
        System.out.println("Query syntax:");
        System.out.println("  filename <terms>       - Search in file names");
        System.out.println("  content <terms>        - Search in content");
        System.out.println("  content \"phrase\"       - Phrase query (consecutive terms)");
        System.out.println("\nType 'exit' to quit\n");
        
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            LuceneSearcher searcher = new LuceneSearcher();
            
            while (true) {
                System.out.print("Query> ");
                String input = br.readLine();
                
                if (input == null || input.trim().equalsIgnoreCase("exit")) {
                    break;
                }
                
                if (input.trim().isEmpty()) {
                    continue;
                }
                
                try {
                    searcher.search(input.trim());
                } catch (Exception e) {
                    System.err.println("Search error: " + e.getMessage());
                }
                
                System.out.println();
            }
            
            searcher.close();
            System.out.println("\nExiting Lucene Text Search...");
            
        } catch (IOException e) {
            System.err.println("Initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
