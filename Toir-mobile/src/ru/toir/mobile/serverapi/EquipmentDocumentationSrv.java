package ru.toir.mobile.serverapi;

import java.util.ArrayList;
import ru.toir.mobile.db.tables.EquipmentDocumentation;
import com.google.gson.annotations.Expose;

/**
 * Документы связанные с оборудованием
 * 
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
	@Expose
	private ArrayList<String> Links;
	@Expose
	private boolean Required; 

	/**
	 * 
	 * @return The Title
	 */
	public String getTitle() {
		return Title;
	}

	/**
	 * 
	 * @param Title
	 *            The Title
	 */
	public void setTitle(String Title) {
		this.Title = Title;
	}

	/**
	 * 
	 * @return The Path
	 */
	public String getPath() {
		return Path;
	}

	/**
	 * 
	 * @param Path
	 *            The Path
	 */
	public void setPath(String Path) {
		this.Path = Path;
	}

	/**
	 * 
	 * @return The DocumentType
	 */
	public DocumentationTypeSrv getDocumentType() {
		return DocumentType;
	}

	/**
	 * 
	 * @param DocumentType
	 *            The DocumentType
	 */
	public void setDocumentType(DocumentationTypeSrv DocumentType) {
		this.DocumentType = DocumentType;
	}

	/**
	 * Возвращает объект в локальном представлении
	 * 
	 * @param uuid
	 *            единицы оборудования
	 * @return EquipmentDocumentation
	 */
	public EquipmentDocumentation getLocal(String uuid) {

		EquipmentDocumentation item = new EquipmentDocumentation();

		item.set_id(0);
		item.setUuid(Id);
		item.setEquipment_uuid(uuid);
		item.setDocumentation_type_uuid(DocumentType.getId());
		item.setTitle(Title);
		item.setPath(Path);
		item.setRequired(Required);
		item.setCreatedAt(getCreatedAtTime());
		item.setChangedAt(getChangedAtTime());

		return item;
	}

	public static ArrayList<EquipmentDocumentation> getEquipmentDocumentations(
			ArrayList<EquipmentDocumentationSrv> documentations, String equipmentUuid) {

		ArrayList<EquipmentDocumentation> list = new ArrayList<EquipmentDocumentation>();
		for (EquipmentDocumentationSrv documentation : documentations) {
			list.add(documentation.getLocal(equipmentUuid));
		}
		return list;
	}

	public static ArrayList<DocumentationTypeSrv> getDocumentationTypesSrv(
			ArrayList<EquipmentDocumentationSrv> documentations) {

		ArrayList<DocumentationTypeSrv> list = new ArrayList<DocumentationTypeSrv>();
		for (EquipmentDocumentationSrv documentation : documentations) {
			list.add(documentation.getDocumentType());
		}
		return list;
	}

	/**
	 * @return the required
	 */
	public boolean getRequired() {
		return Required;
	}

	/**
	 * @param required the required to set
	 */
	public void setRequired(boolean required) {
		Required = required;
	}

	/**
	 * @return the links
	 */
	public ArrayList<String> getLinks() {
		return Links;
	}

	/**
	 * @param links the links to set
	 */
	public void setLinks(ArrayList<String> links) {
		Links = links;
	}

}
