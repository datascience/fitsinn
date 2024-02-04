package rocks.artur.FITSObjects;

//@JsonIgnoreProperties(ignoreUnknown = true)
public class IdentityProperty {
    private String format;
    private String toolname;
    private String toolversion;
    private String mimetype;
    private Object tool;
    private Object version;
    private Object externalIdentifier;

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getToolname() {
        return toolname;
    }

    public void setToolname(String toolname) {
        this.toolname = toolname;
    }

    public String getToolversion() {
        return toolversion;
    }

    public void setToolversion(String toolversion) {
        this.toolversion = toolversion;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public Object getTool() {
        return tool;
    }

    public void setTool(Object tool) {
        this.tool = tool;
    }

    public Object getExternalIdentifier() {
        return externalIdentifier;
    }

    public void setExternalIdentifier(Object externalIdentifier) {
        this.externalIdentifier = externalIdentifier;
    }

    @Override
    public String toString() {
        return "IdentityProperty{" +
                "format='" + format + '\'' +
                ", toolname='" + toolname + '\'' +
                ", toolversion='" + toolversion + '\'' +
                ", mimetype='" + mimetype + '\'' +
                ", tool=" + tool +
                ", externalIdentifier=" + externalIdentifier +
                '}';
    }

    public Object getVersion() {
        return version;
    }

    public void setVersion(Object version) {
        this.version = version;
    }
}
