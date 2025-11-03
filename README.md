# Lucene Text Search

A text file indexing and search system built with Apache Lucene with automatic evaluation metrics.

## Description

**Lucene Text Search** is a project that implements a full-text search engine for text files using Apache Lucene. The system includes:
- **Full-text indexing** with two distinct fields:
  - **filename**: Indexed with KeywordAnalyzer
  - **content**: Indexed with StandardAnalyzer
- **Automatic evaluation metrics**: Precision, Recall, and F1-Score calculated for each search
- **Score-based relevance assessment**: Uses score distribution to estimate document relevance

## Project Structure

```
lucene-text-search/
├── src/main/java/it/uniroma3/lucenetextsearch/
│   ├── LuceneTextIndexer.java     # Indexing documents
│   ├── LuceneSearcher.java         # Interactive search with metrics
│   ├── SearchMetrics.java          # Metrics calculation (Precision, Recall, F1)
│   └── SearchEvaluator.java        # Automated evaluation
├── data/                           # .txt files to index
├── lucene_index/                   # Lucene index directory
├── pom.xml                         # Maven configuration
└── README.md
```

## Query Syntax

- **Search by filename**:
  ```
  Query> filename file1.txt
  ```

- **Search in content**:
  ```
  Query> content lucene
  ```

- **Phrase query (consecutive terms)**:
  ```
  Query> content "apache lucene"
  ```

- **Boolean queries**:
  ```
  Query> content apache AND lucene
  Query> content apache OR lucene
  ```

- **Exit the program**:
  ```
  Query> exit
  ```

## Maven Dependencies

The project uses Apache Lucene 10.3.1:

```xml
    <dependencies>

        <dependency>
            <groupId>org.apache.lucene</groupId>
            <artifactId>lucene-core</artifactId>
            <version>10.3.1</version>
        </dependency>

        <dependency>
            <groupId>org.apache.lucene</groupId>
            <artifactId>lucene-queryparser</artifactId>
            <version>10.3.1</version>
        </dependency>

        <dependency>
            <groupId>org.apache.lucene</groupId>
            <artifactId>lucene-analysis-common</artifactId>
            <version>10.3.1</version>
        </dependency>
    </dependencies>
```

## Quick Start Commands

```bash
# 1. Compile the project
mvn clean compile

# 2. Create data directory and add text files
mkdir data
echo "Sample content about Apache Lucene" > data/doc1.txt

# 3. Index the documents
mvn exec:java@indexer

# 4. Search interactively
mvn exec:java@searcher

```

---

Project for the "Ingegneria dei Dati" exam @Roma Tre University.
