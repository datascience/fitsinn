# FITSInn

Place where your FITS files feel good.

## Purpose

FITSInn is an easy-to-use tool for storing and analyzing technical metadata extracted by characterisation tools
like [FITS](https://projects.iq.harvard.edu/fits/).

![FITSInn Screenshot](docs/img.png)

### Key Features:

- **Intuitive User Interface**: Enhanced user experience through a sleek and streamlined interface.
- **File Characterisation**: Analyze uploaded files using FITS without storing the original files.
- **Data Analysis Tools**:
    - Advanced filtering,
    - Drill-down capabilities,
    - Property value aggregations,
    - Distribution visualizations,
    - Sampling options.
- **Conflict Management**: Resolve metadata conflicts effortlessly.
- **Automation Support**: Comes with a REST API to integrate into your workflows.

---

## Installation

### Deployment (Production)

To deploy FITSInn, use the Docker images provided:

```bash
docker-compose -f docker-compose.yaml up --pull
```

> **Note**:
> - Deployment to Docker Swarm or Kubernetes (K8S) is possible but not covered in this guide.

### Local Development Build

To build the Docker images from scratch and start FITSInn locally:

```bash
docker-compose -f docker-compose.dev.yaml up --build
```

---

### Uploading Files to FITSInn

#### Using Bash:

```bash
bash ./utils/fileupload.sh http://localhost:8082 ~/path/to/files collection_name
```

#### Using Python:

Ensure you have the `requests` library installed. Then run:

```python
python ./utils/fileupload.py http://localhost:8082/multipleupload ~/path/to/files 100 3 collection_name
```

- **URL**: `http://localhost:8082` is suitable for local deployments.
- **Path to Files**: Replace `~/path/to/files` with the actual directory path containing the files.
- **Collection Name**: Replace `collection_name` with a name for your collection.

---

## Reporting Issues

If you encounter any issues while using FITSInn, please report them on GitHub:

[Submit an Issue](https://github.com/datascience/fitsinn/issues)

---

## License

FITSInn is released under the MIT license. For more details, see the [LICENSE](LICENSE) file.