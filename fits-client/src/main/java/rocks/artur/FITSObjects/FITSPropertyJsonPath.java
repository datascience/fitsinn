package rocks.artur.FITSObjects;

public enum FITSPropertyJsonPath {
    FILENAME("$.fits.fileinfo.filename"),
    SIZE("$.fits.fileinfo.size"),
    FILEPATH("$.fits.fileinfo.filepath"),
    MD5CHECKSUM("$.fits.fileinfo.md5checksum"),
    FSLASTMODIFIED("$.fits.fileinfo.fslastmodified"),
    IDENTIFICATION("$.fits.identification"),
    CREATINGAPPLICATIONNAME("$.fits.fileinfo.creatingApplicationName"),
    CREATINGAPPLICATIONVERSION("$.fits.fileinfo.creatingApplicationVersion"),
    CREATED("$.fits.fileinfo.created"),
    CREATINGOS("$.fits.fileinfo.creatingos"),
    INHIBITORTYPE("$.fits.fileinfo.inhibitorType"),
    LASTMODIFIED("$.fits.fileinfo.lastmodified"),

    WELLFORMED("$.fits.filestatus.well-formed"),
    VALID("$.fits.filestatus.valid"),
    MESSAGE("$.fits.filestatus.message"),
    TITLE("$.fits.metadata.document.title"),
    AUTHOR("$.fits.metadata.document.author"),
    PAGECOUNT("$.fits.metadata.document.pageCount"),
    ISTAGGED("$.fits.metadata.document.isTagged"),
    HASOUTLINE("$.fits.metadata.document.hasOutline"),
    HASANNOTATIONS("$.fits.metadata.document.hasAnnotations"),
    ISRIGHTSMANAGED("$.fits.metadata.document.isRightsManaged"),
    ISPROTECTED("$.fits.metadata.document.isProtected"),
    HASFORMS("$.fits.metadata.document.hasForms"),
    WORDCOUNT("$.fits.metadata.document.wordCount"),
    CHARACTERCOUNT("$.fits.metadata.document.characterCount"),
    LANGUAGE("$.fits.metadata.document.language"),

    CHARSET("$.fits.metadata.text.charset"),
    MARKUPBASIS("$.fits.metadata.text.markupBasis"),
    MARKUPBASISVERSION("$.fits.metadata.text.markupBasisVersion"),
    LINEBREAK("$.fits.metadata.text.linebreak");


    private final String fitsProperty;

    FITSPropertyJsonPath(String fitsProperty) {
        this.fitsProperty = fitsProperty;
    }

    public String getFitsProperty() {
        return fitsProperty;
    }
}
