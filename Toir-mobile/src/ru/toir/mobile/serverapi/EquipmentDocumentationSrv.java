
package ru.toir.mobile.serverapi;

import com.google.gson.annotations.Expose;

/**
 * Документы связанные с оборудованием
 * @author Dmitriy Logachov
 *
 */
public class EquipmentDocumentationSrv extends BaseObjectSrv {

    @Expose
    private String Title;
    @Expose
    private String Path;
    @Expose
    private DocumentationTypeSrv DocumentType;

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
    public DocumentationTypeSrv getDocumentType() {
        return DocumentType;
    }

    /**
     * 
     * @param DocumentType
     *     The DocumentType
     */
    public void setDocumentType(DocumentationTypeSrv DocumentType) {
        this.DocumentType = DocumentType;
    }

}
