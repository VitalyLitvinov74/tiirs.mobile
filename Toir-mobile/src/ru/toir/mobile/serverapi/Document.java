
package ru.toir.mobile.serverapi;

import com.google.gson.annotations.Expose;

public class Document {

    @Expose
    private String Id;
    @Expose
    private String Title;
    @Expose
    private String Path;
    @Expose
    private ru.toir.mobile.serverapi.DocumentType DocumentType;

    /**
     * 
     * @return
     *     The Id
     */
    public String getId() {
        return Id;
    }

    /**
     * 
     * @param Id
     *     The Id
     */
    public void setId(String Id) {
        this.Id = Id;
    }

    /**
     * 
     * @return
     *     The Title
     */
    public String getTitle() {
        return Title;
    }

    /**
     * 
     * @param Title
     *     The Title
     */
    public void setTitle(String Title) {
        this.Title = Title;
    }

    /**
     * 
     * @return
     *     The Path
     */
    public String getPath() {
        return Path;
    }

    /**
     * 
     * @param Path
     *     The Path
     */
    public void setPath(String Path) {
        this.Path = Path;
    }

    /**
     * 
     * @return
     *     The DocumentType
     */
    public ru.toir.mobile.serverapi.DocumentType getDocumentType() {
        return DocumentType;
    }

    /**
     * 
     * @param DocumentType
     *     The DocumentType
     */
    public void setDocumentType(ru.toir.mobile.serverapi.DocumentType DocumentType) {
        this.DocumentType = DocumentType;
    }

}
