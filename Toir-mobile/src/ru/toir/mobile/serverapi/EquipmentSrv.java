package ru.toir.mobile.serverapi;

import java.util.ArrayList;
import java.util.Date;
import ru.toir.mobile.db.tables.DocumentationType;
import ru.toir.mobile.db.tables.Equipment;
import com.google.gson.annotations.Expose;
import ru.toir.mobile.db.tables.EquipmentStatus;
import ru.toir.mobile.db.tables.EquipmentType;
import ru.toir.mobile.db.tables.CriticalType;
import ru.toir.mobile.db.tables.EquipmentDocumentation;

/**
 * Оборудование
 * 
 * @author Dmitriy Logachov
 * 
 */
public class EquipmentSrv extends BaseObjectSrv {

	@Expose
	private String Name;
	@Expose
	private EquipmentTypeSrv EquipmentType;
	@Expose
	private GeoCoordinatesSrv GeoCoordinates;
	@Expose
	private CriticalTypeSrv CriticalityType;
	@Expose
	private ArrayList<EquipmentDocumentationSrv> Documents = new ArrayList<EquipmentDocumentationSrv>();
	@Expose
	private String Tag;
	@Expose
	private Date StartupDate;
	@Expose
	private EquipmentStatusSrv EquipmentStatus;
	@Expose
	private String InventoryNumber;
	@Expose
	private String Location;
	@Expose
	private ArrayList<String> ImageLinks;

	/**
	 * @return
	 */
	public long getStartupDateTime() {
		return StartupDate == null ? 0 : StartupDate.getTime();
	}

	/**
	 * 
	 * @return The Name
	 */
	public String getName() {
		return Name;
	}

	/**
	 * 
	 * @param Name
	 *            The Name
	 */
	public void setName(String Name) {
		this.Name = Name;
	}

	/**
	 * 
	 * @return The EquipmentType
	 */
	public EquipmentTypeSrv getEquipmentType() {
		return EquipmentType;
	}

	/**
	 * 
	 * @param EquipmentType
	 *            The EquipmentType
	 */
	public void setEquipmentType(EquipmentTypeSrv EquipmentType) {
		this.EquipmentType = EquipmentType;
	}

	/**
	 * 
	 * @return The GeoCoordinates
	 */
	public GeoCoordinatesSrv getGeoCoordinates() {
		return GeoCoordinates;
	}

	/**
	 * 
	 * @param GeoCoordinates
	 *            The GeoCoordinates
	 */
	public void setGeoCoordinates(GeoCoordinatesSrv GeoCoordinates) {
		this.GeoCoordinates = GeoCoordinates;
	}

	/**
	 * 
	 * @return The CriticalityType
	 */
	public CriticalTypeSrv getCriticalityType() {
		return CriticalityType;
	}

	/**
	 * 
	 * @param CriticalityType
	 *            The CriticalityType
	 */
	public void setCriticalityType(CriticalTypeSrv CriticalityType) {
		this.CriticalityType = CriticalityType;
	}

	/**
	 * 
	 * @return The Documents
	 */
	public ArrayList<EquipmentDocumentationSrv> getDocuments() {
		return Documents;
	}

	/**
	 * 
	 * @param Documents
	 *            The Documents
	 */
	public void setDocuments(ArrayList<EquipmentDocumentationSrv> Documents) {
		this.Documents = Documents;
	}

	/**
	 * @return the tag
	 */
	public String getTag() {
		return Tag;
	}

	/**
	 * @param tag
	 *            the tag to set
	 */
	public void setTag(String tag) {
		Tag = tag;
	}

	/**
	 * @return the startupDate
	 */
	public Date getStartupDate() {
		return StartupDate;
	}

	/**
	 * @param startupDate
	 *            the startupDate to set
	 */
	public void setStartupDate(Date startupDate) {
		StartupDate = startupDate;
	}

	/**
	 * @return the equipmentStatus
	 */
	public EquipmentStatusSrv getEquipmentStatus() {
		return EquipmentStatus;
	}

	/**
	 * @param equipmentStatus
	 *            the equipmentStatus to set
	 */
	public void setEquipmentStatus(EquipmentStatusSrv equipmentStatus) {
		EquipmentStatus = equipmentStatus;
	}

	/**
	 * Возвращает объект в локальном представлении
	 * 
	 * @return Equipment
	 */
	public Equipment getLocal() {

		Equipment item = new Equipment();

		item.set_id(0);
		item.setUuid(Id);
		item.setTitle(Name);
		item.setEquipment_type_uuid(EquipmentType.getId());
		item.setCritical_type_uuid(CriticalityType.getId());
		item.setStart_date(getStartupDate().getTime());
		item.setLatitude(GeoCoordinates.getLatitude());
		item.setLongitude(GeoCoordinates.getLongitude());
		item.setTag_id(Tag);
		// TODO на сервере это просто куча файлов, решить что с этим делать!
		// видимо нужно отдельно запрашивать инфо по получаемому оборудованию, и
		// складировать так же в папку, для начала...
		// проблема в том что сейчас нет ни каких механизмов для проверки того
		// что изображение уже загружено на устройство, что изображение не нужно
		// тянуть по новой и т.д.
		// пока тупо ставим первый файл из списка
		if (ImageLinks.size() > 0) {
			item.setImage(ImageLinks.get(0));
		} else {
			item.setImage("");
		}
		item.setEquipmentStatus_uuid(EquipmentStatus.getId());
		item.setInventoryNumber(InventoryNumber);
		item.setLocation(Location);
		item.setCreatedAt(getCreatedAtTime());
		item.setChangedAt(getChangedAtTime());

		return item;
	}

	/**
	 * Возвращает список типов документов связанных с оборудованием в локальном
	 * представлении
	 * 
	 * @param equipments
	 *            {@link ArrayList}<{@link EquipmentSrv}>
	 * @return {@link ArrayList}<{@link DocumentationType}>
	 */
	public static ArrayList<DocumentationType> getDocumentationTypes(
			ArrayList<EquipmentSrv> equipments) {

		ArrayList<DocumentationType> list = new ArrayList<DocumentationType>();
		for (EquipmentSrv equipment : equipments) {
			ArrayList<EquipmentDocumentationSrv> documentations = equipment
					.getDocuments();
			for (EquipmentDocumentationSrv documentation : documentations) {
				list.add(documentation.getDocumentType().getLocal());
			}
		}
		return list;
	}

	/**
	 * Возвращает список оборудования в локальном представлении
	 * 
	 * @param equipments
	 *            {@link ArrayList}<{@link EquipmentSrv}>
	 * @return {@link ArrayList}<{@link Equipment}>
	 */
	public static ArrayList<Equipment> getEquipments(
			ArrayList<EquipmentSrv> equipments) {

		ArrayList<Equipment> list = new ArrayList<Equipment>();
		for (EquipmentSrv equipment : equipments) {
			list.add(equipment.getLocal());
		}
		return list;
	}

	/**
	 * Возвращает список статусов оборудования в локальном представлении
	 * 
	 * @param equipments
	 *            {@link ArrayList}<{@link EquipmentSrv}>
	 * @return {@link ArrayList}<{@link EquipmentStatus}>
	 */
	public static ArrayList<EquipmentStatus> getEquipmentStatuses(
			ArrayList<EquipmentSrv> equipments) {

		ArrayList<EquipmentStatus> list = new ArrayList<EquipmentStatus>();
		for (EquipmentSrv equipment : equipments) {
			list.add(equipment.getEquipmentStatus().getLocal());
		}
		return list;
	}

	/**
	 * Возвращает список типов оборудования в локальном представлении
	 * 
	 * @param equipments
	 *            {@link ArrayList}<{@link EquipmentSrv}>
	 * @return {@link ArrayList}<{@link EquipmentType}>
	 */
	public static ArrayList<EquipmentType> getEquipmentTypes(
			ArrayList<EquipmentSrv> equipments) {

		ArrayList<EquipmentType> list = new ArrayList<EquipmentType>();
		for (EquipmentSrv equipment : equipments) {
			list.add(equipment.getEquipmentType().getLocal());
		}
		return list;
	}

	/**
	 * Возвращает список типов критичности оборудования в локальном
	 * представлении
	 * 
	 * @param equipments
	 *            {@link ArrayList}<{@link EquipmentSrv}>
	 * @return {@link ArrayList}<{@link CriticalType}>
	 */
	public static ArrayList<CriticalType> getCriticalTypes(
			ArrayList<EquipmentSrv> equipments) {

		ArrayList<CriticalType> list = new ArrayList<CriticalType>();
		for (EquipmentSrv equipment : equipments) {
			list.add(equipment.getCriticalityType().getLocal());
		}
		return list;
	}

	/**
	 * Возвращает список документации оборудования в локальном представлении
	 * 
	 * @param equipments
	 *            {@link ArrayList}<{@link EquipmentSrv}>
	 * @return {@link ArrayList}<{@link EquipmentDocumentation}>
	 */
	public static ArrayList<EquipmentDocumentation> getEquipmentDocumentations(
			ArrayList<EquipmentSrv> equipments) {

		EquipmentDocumentation item;
		ArrayList<EquipmentDocumentation> list = new ArrayList<EquipmentDocumentation>();
		for (EquipmentSrv equipment : equipments) {
			ArrayList<EquipmentDocumentationSrv> documentations = equipment
					.getDocuments();
			for (EquipmentDocumentationSrv documentation : documentations) {
				item = documentation.getLocal(equipment.getId());
				// TODO есть рассогласованость в полях с именами и путями
				item.setPath(documentation.getFileName());
				list.add(item);
			}
		}
		return list;
	}

	/**
	 * @return the inventoryNumber
	 */
	public String getInventoryNumber() {
		return InventoryNumber;
	}

	/**
	 * @param inventoryNumber
	 *            the inventoryNumber to set
	 */
	public void setInventoryNumber(String inventoryNumber) {
		InventoryNumber = inventoryNumber;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return Location;
	}

	/**
	 * @param location
	 *            the location to set
	 */
	public void setLocation(String location) {
		Location = location;
	}

	/**
	 * @return the imageLinks
	 */
	public ArrayList<String> getImageLinks() {
		return ImageLinks;
	}

	/**
	 * @param imageLinks the imageLinks to set
	 */
	public void setImageLinks(ArrayList<String> imageLinks) {
		ImageLinks = imageLinks;
	}

}
