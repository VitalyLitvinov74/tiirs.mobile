
package ru.toir.mobile.serverapi;

import java.util.Date;

import com.google.gson.annotations.Expose;

/**
 * Документы связанные с оборудованием
 * @author Dmitriy Logachov
 *
 */
public class EquipmentDocumentationSrv {

    @Expose
    private String Id;
    @Expose
    private String Title;
    @Expose
    private String Path;
    @Expose
    private DocumentationTypeSrv DocumentType;
    @Expose
	private Date CreatedAt;
	@Expose
	private Date ChangedAt;

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

	/**
	 * @return the createdAt
	 */
	public Date getCreatedAt() {
		return CreatedAt;
	}

	/**
	 * @param createdAt the createdAt to set
	 */
	public void setCreatedAt(Date createdAt) {
		CreatedAt = createdAt;
	}

	/**
	 * @return the changedAt
	 */
	public Date getChangedAt() {
		return ChangedAt;
	}

	/**
	 * @param changedAt the changedAt to set
	 */
	public void setChangedAt(Date changedAt) {
		ChangedAt = changedAt;
	}

}
