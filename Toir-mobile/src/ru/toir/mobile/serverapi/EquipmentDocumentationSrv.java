package ru.toir.mobile.serverapi;

import java.util.ArrayList;
import java.util.List;
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
		item.setCreatedAt(getCreatedAtTime());
		item.setChangedAt(getChangedAtTime());

		return item;
	}

	public static ArrayList<EquipmentDocumentation> getEquipmentDocumentations(ArrayList<EquipmentSrv> equipments) {

		ArrayList<EquipmentDocumentation> list = new ArrayList<EquipmentDocumentation>();
		for (EquipmentSrv equipment : equipments) {
			List<EquipmentDocumentationSrv> documentations = equipment.getDocuments(); 
			for (EquipmentDocumentationSrv documentation : documentations) {
				list.add(documentation.getLocal(equipment.getId()));
			}
		}
		return list;
	}

}
