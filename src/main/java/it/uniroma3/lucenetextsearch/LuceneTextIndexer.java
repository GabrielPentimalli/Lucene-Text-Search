package it.uniroma3.lucenetextsearch;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for indexing .txt files with Apache Lucene
 * Uses two fields: filename and content
 * 
 * Project: Lucene Text Search
 */
public class LuceneTextIndexer {
    
    private static final String FIELD_FILENAME = "filename";
    private static final String FIELD_CONTENT = "content";
    private static final String INDEX_DIR = "lucene_index";
    
    private Directory indexDirectory;
    private Analyzer analyzer;
    private IndexWriter writer;
    
    /**
     * Constructor that initializes the analyzers for each field
     */
    public LuceneTextIndexer() throws IOException {
        // Create the index directory
        Path indexPath = Paths.get(INDEX_DIR);
        indexDirectory = FSDirectory.open(indexPath);
        
        // Configure per-field analyzers
        Map<String, Analyzer> analyzerPerField = new HashMap<>();
        
        // KeywordAnalyzer for filename: does not tokenize, keeps the whole name
        // Useful for exact searches on file names
        analyzerPerField.put(FIELD_FILENAME, new KeywordAnalyzer());
        
        // StandardAnalyzer for content: tokenizes and normalizes text
        // Removes stopwords, converts to lowercase, optimal for full-text search
        Analyzer defaultAnalyzer = new StandardAnalyzer();
        
        // PerFieldAnalyzerWrapper allows using different analyzers for different fields
        analyzer = new PerFieldAnalyzerWrapper(defaultAnalyzer, analyzerPerField);
        
        // Configure the IndexWriter
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        
        writer = new IndexWriter(indexDirectory, config);
    }
    
    /**
     * Indexes all .txt files in a directory
     * 
     * @param dataDirectory The directory containing .txt files to index
     * @return The number of files indexed
     * @throws IOException If an I/O error occurs
     */
    public int indexDirectory(String dataDirectory) throws IOException {
        File dir = new File(dataDirectory);
        
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IllegalArgumentException("Invalid directory: " + dataDirectory);
        }
        
        File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".txt"));
        
        if (files == null) {
            return 0;
        }
        
        int indexed = 0;
        long startTime = System.currentTimeMillis();
        
        for (File file : files) {
            indexFile(file);
            indexed++;
        }
        
        long endTime = System.currentTimeMillis();
        
        System.out.println("\n=== INDEXING STATISTICS ===");
        System.out.println("Files indexed: " + indexed);
        System.out.println("Total time: " + (endTime - startTime) + " ms");
        System.out.println("Average time per file: " + 
                          (indexed > 0 ? (endTime - startTime) / indexed : 0) + " ms");
        
        return indexed;
    }
    
    /**
     * Indexes a single file
     * 
     * @param file The file to index
     * @throws IOException If an I/O error occurs
     */
    private void indexFile(File file) throws IOException {
        System.out.println("Indexing: " + file.getName());
        
        // Read the file content
        String content = new String(Files.readAllBytes(file.toPath()));
        
        // Create the Lucene document
        Document doc = new Document();
        
        // Filename field: StringField - not tokenized, indexed for exact search
        // Field.Store.YES stores the value to retrieve it in search results
        doc.add(new StringField(FIELD_FILENAME, file.getName(), Field.Store.YES));
        
        // Content field: TextField - tokenized and analyzed for full-text search
        // Field.Store.YES allows retrieving the content in search results
        doc.add(new TextField(FIELD_CONTENT, content, Field.Store.YES));
        
        // Add the document to the index
        writer.addDocument(doc);
    }
    
    /**
     * Closes the IndexWriter and completes indexing
     * 
     * @throws IOException If an I/O error occurs
     */
    public void close() throws IOException {
        if (writer != null) {
            writer.commit();
            writer.close();
        }
        if (indexDirectory != null) {
            indexDirectory.close();
        }
    }
    
    /**
     * Main method for testing indexing
     */
    public static void main(String[] args) {
        try {
            String dataDir = args.length > 0 ? args[0] : "./data";
            
            System.out.println("=== LUCENE TEXT SEARCH - INDEXER ===");
            System.out.println("Starting indexing from directory: " + dataDir);
            
            LuceneTextIndexer indexer = new LuceneTextIndexer();
            indexer.indexDirectory(dataDir);
            indexer.close();
            
            System.out.println("\nIndexing completed successfully!");
            
        } catch (Exception e) {
            System.err.println("Error during indexing: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
