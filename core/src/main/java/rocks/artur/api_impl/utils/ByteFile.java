package rocks.artur.api_impl.utils;

public class ByteFile {
    byte[] file;
    String filename;

    public ByteFile(byte[] file, String filename) {
        this.file = file;
        this.filename = filename;
    }

    public byte[] getFile() {
        return file;
    }

    public String getFilename() {
        return filename;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
