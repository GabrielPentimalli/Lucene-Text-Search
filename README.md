# Lucene Text Search

A text file indexing and search system built with Apache Lucene.

## Description

**Lucene Text Search** is a project that implements a full-text search engine for text files using Apache Lucene. The system includes:
- **Full-text indexing** with two distinct fields:
  - **filename**: Indexed with KeywordAnalyzer for exact matching
  - **content**: Indexed with StandardAnalyzer for full-text search
- **Relevance scoring**: Documents are ranked by their relevance score

## Project Structure

```
lucene-text-search/
├── src/main/java/it/uniroma3/lucenetextsearch/
│   ├── LuceneTextIndexer.java     # Indexing documents
│   └── LuceneSearcher.java         # Interactive search
├── data/                           # .txt files to index
├── lucene_index/                   # Lucene index directory
├── wiki.py                         # Optional: Generate random Wikipedia articles
├── pom.xml                         # Maven configuration
└── README.md
```

## Query Syntax

- **Search by filename**:
  ```
  Query> filename file007.txt
  ```

- **Search in content**:
  ```
  Query> content goldeneye
  ```

- **Phrase query (consecutive terms)**:
  ```
  Query> content "James Bond"
  ```

- **Boolean queries**:
  ```
  Query> content romanova OR bianchi
  Query> content connery AND fleming
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
echo "Dr. No is a 1962 British spy film directed by Terence Young." > data/file007.txt

# 3. Index the documents
mvn exec:java@indexer

# 4. Search interactively
mvn exec:java@searcher

```

## Optional: Generate Random Wikipedia Articles

The project includes a Python script (`wiki.py`) that can automatically generate text files by fetching random articles from Wikipedia.

### Prerequisites

Install the Wikipedia library:
```bash
pip install wikipedia
```

### Usage

The script generates 100 random Wikipedia articles by default:

```bash
python3 wiki.py
```

The files will be created in the `data/` directory with the following naming format:
```
001_Article_Title_1234.txt
002_Another_Article_5678.txt
...
```

Each file contains:
- A header with the article topic
- Content extracted from Wikipedia (4-7 sentences)

### Customize Number of Files

To generate a different number of files, modify the last line in `wiki.py`:
```python
if __name__ == "__main__":
    create_wiki_files(num_files=50)  # Change to desired number
```

After generating the files, run the indexer to index them:
```bash
mvn exec:java@indexer
```

---

Project for the second homework assignment in Data Engineering course @ Roma Tre University.
