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

    response = requests.post(url, headers=headers, files=chunk_files)
    end_time = time.time()

    print(f"Uploaded {chunk_count} files. Time taken: {(end_time - start_time):.2f} seconds. Status Code: {response.status_code}")
    #print(response.text)

    return end_time - start_time

def upload_files_in_chunks_parallel(url, folder_path, chunk_size=100, num_parallel_requests=10, collection_name="dataset"):
    headers = {
        'accept': '*/*',
    }

    chunk_count = 0
    chunk_files = []
    total_duration = 0

    for root, _, filenames in os.walk(folder_path):
        for filename in filenames:
            if filename.endswith('.xml'):
                file_path = os.path.join(root, filename)
                relative_path = os.path.relpath(file_path, folder_path)
                chunk_files.append(('files', (relative_path, open(file_path, 'rb'), 'text/xml')))

                if len(chunk_files) == chunk_size:
                    with ThreadPoolExecutor(max_workers=num_parallel_requests) as executor:
                        future = executor.submit(upload_chunk, url + f"?datasetName={collection_name}", chunk_files, (chunk_count+1)*chunk_size)
                        duration = future.result()

                        total_duration += duration

                    chunk_count += 1
                    chunk_files = []

    if chunk_files:
        with ThreadPoolExecutor(max_workers=num_parallel_requests) as executor:
            future = executor.submit(upload_chunk, url + f"?datasetName={collection_name}", chunk_files, chunk_count)
            duration = future.result()
            total_duration += duration

    return total_duration

if __name__ == "__main__":
    if len(sys.argv) != 6:
        print("Usage: python script.py <upload_url> <folder_path> <chunk_size> <num_parallel_requests> <collection_name>")
        sys.exit(1)

    upload_url = sys.argv[1]
    folder_to_upload = sys.argv[2]
    chunk_size = int(sys.argv[3])
    num_parallel_requests = int(sys.argv[4])
    collection_name = sys.argv[5]
    start_script_time = time.time()

    total_duration = upload_files_in_chunks_parallel(upload_url, folder_to_upload, chunk_size, num_parallel_requests, collection_name)

    end_script_time = time.time()
    script_duration = end_script_time - start_script_time
    print(f"\nScript Execution Duration: {script_duration:.2f} seconds")
    print(f"Total Upload Duration: {total_duration:.2f} seconds")
