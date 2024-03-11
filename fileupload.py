import os
import sys
import requests
from concurrent.futures import ThreadPoolExecutor
import time

def upload_chunk(url, chunk_files, chunk_count):
    headers = {
        'accept': '*/*',
    }

    start_time = time.time()
    # Upload the chunk
    response = requests.post(url, headers=headers, files=chunk_files)
    end_time = time.time()

    # Print the response status code and content
    print(f"Uploaded {chunk_count} - Status Code: {response.status_code}")
    #print(response.text)

    return end_time - start_time

def upload_files_in_chunks_parallel(url, folder_path, chunk_size=100, num_parallel_requests=10):
    headers = {
        'accept': '*/*',
    }

    # Initialize variables
    chunk_count = 0
    chunk_files = []
    total_duration = 0

    # Traverse through the folder and its subfolders
    for root, _, filenames in os.walk(folder_path):
        for filename in filenames:
            if filename.endswith('.xml'):
                file_path = os.path.join(root, filename)
                relative_path = os.path.relpath(file_path, folder_path)
                chunk_files.append(('files', (relative_path, open(file_path, 'rb'), 'text/xml')))

                # Check if the chunk size is reached
                if len(chunk_files) == chunk_size:
                    # Use ThreadPoolExecutor to create parallel requests
                    with ThreadPoolExecutor(max_workers=num_parallel_requests) as executor:
                        # Submit the upload_chunk function with arguments
                        future = executor.submit(upload_chunk, url, chunk_files, (chunk_count+1)*chunk_size)
                        duration = future.result()

                        # Accumulate duration for statistics
                        total_duration += duration

                    # Reset variables for the next chunk
                    chunk_count += 1
                    chunk_files = []

    # Upload the remaining files if any
    if chunk_files:
        with ThreadPoolExecutor(max_workers=num_parallel_requests) as executor:
            future = executor.submit(upload_chunk, url, chunk_files, chunk_count)
            duration = future.result()
            total_duration += duration

    return total_duration

if __name__ == "__main__":
    if len(sys.argv) != 5:
        print("Usage: python script.py <upload_url> <folder_path> <chunk_size> <num_parallel_requests>")
        sys.exit(1)

    upload_url = sys.argv[1]
    folder_to_upload = sys.argv[2]
    chunk_size = int(sys.argv[3])
    num_parallel_requests = int(sys.argv[4])

    # Measure the script execution duration
    start_script_time = time.time()

    # Call the function to upload files in chunks in parallel
    total_duration = upload_files_in_chunks_parallel(upload_url, folder_to_upload, chunk_size, num_parallel_requests)

    # Print the script execution duration statistics
    end_script_time = time.time()
    script_duration = end_script_time - start_script_time
    print(f"\nScript Execution Duration: {script_duration:.2f} seconds")
    print(f"Total Upload Duration: {total_duration:.2f} seconds")
