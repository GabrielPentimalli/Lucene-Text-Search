import wikipedia
import os
import random


def get_random_wikipedia_article(num_sentences: int = 5) -> tuple[str, str] | None:
    """Gets a random Wikipedia article and returns (title, summary)."""
    try:
        wikipedia.set_lang("en")
        
        # Get a random Wikipedia page
        random_title = wikipedia.random(pages=1)
        
        # Get the page content
        page = wikipedia.page(random_title, auto_suggest=False, redirect=True)
        summary_text = wikipedia.summary(random_title, sentences=num_sentences)
        
        return (page.title, summary_text)
    except wikipedia.exceptions.PageError:
        print(f"Page not found for random article. Retrying...")
        return None
    except wikipedia.exceptions.DisambiguationError as e:
        # If the search is ambiguous, choose the first suggestion
        print(f"Disambiguation encountered. Trying '{e.options[0]}'.")
        try:
            page = wikipedia.page(e.options[0], auto_suggest=False, redirect=True)
            summary_text = wikipedia.summary(e.options[0], sentences=num_sentences)
            return (page.title, summary_text)
        except:
            return None
    except Exception as e:
        print(f"An error occurred: {e}. Skipping...")
        return None

def create_wiki_files(output_folder: str = "data", num_files: int = 100):
    
    if not os.path.exists(output_folder):
        os.makedirs(output_folder)
    
    files_created = 0
    attempts = 0
    
    print(f"Starting to generate {num_files} files with random Wikipedia articles...\n")
    
    # Generate files with random Wikipedia articles
    while files_created < num_files:
        attempts += 1
        
        # Get a random Wikipedia article
        result = get_random_wikipedia_article(num_sentences=random.randint(4, 7))
        
        # If result is None, skip file creation and try another article
        if result is None:
            continue
        
        topic, logical_content = result
        files_created += 1
        unique_id = ''.join(random.choices("0123456789", k=4))
        
        # Remove invalid characters from the filename
        base_name = topic.replace(' ', '_').replace('(', '').replace(')', '').replace('/', '_')[:30] 
        file_name = os.path.join(output_folder, f"{files_created:03d}_{base_name}_{unique_id}.txt")
        
        try:
            with open(file_name, 'w', encoding='utf-8') as f:
                f.write(logical_content)
            
            # Print progress every 10 files to avoid clutter
            if files_created % 10 == 0:
                 print(f"✓ File {files_created}/{num_files} created ({topic}).")

        except IOError as e:
            print(f"Error creating file {file_name}: {e}")
            files_created -= 1  # Don't count failed file creation

    print(f"\nGeneration completed. Created {files_created} files from random Wikipedia articles in {attempts} attempts.")

if __name__ == "__main__":
    create_wiki_files(num_files=100)
