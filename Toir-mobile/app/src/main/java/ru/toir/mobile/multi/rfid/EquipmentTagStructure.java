package ru.toir.mobile.multi.rfid;

import java.util.ArrayList;

/**
 * @author olejek
 *
 */
public class EquipmentTagStructure {
	private static EquipmentTagStructure mInstance;
	private String tag;                    // 01234567 [16]
	private String equipment_uuid;		// BDFF4E95-F0AB-4E07-9DFD-4CA314FFE05B [16]
	private String status;				// D818A97E-B6EB-4AEC-9168-174C780E365B [2!] B6EB
	private String last;				// 1 [1]
	private ArrayList<TagRecordStructure> records = new ArrayList<TagRecordStructure>();

	public EquipmentTagStructure() {
	}

	public static synchronized EquipmentTagStructure getInstance() {
		if (mInstance == null) {
			mInstance = new EquipmentTagStructure();
		}
		return mInstance;
	}

	/**
	 * @return the tag
	 */
	public String get_tag() {
		return tag;
	}

	/**
	 * @param tag the tag to set
	 */
	public void set_tag(String tag) {
		this.tag = tag;
	}
	
	/**
	 * @return the equipment_uuid
	 */
	public String get_equipment_uuid() {
		return equipment_uuid;
	}

	/**
	 * @param equipment_uuid the equipment_uuid to set
	 */
	public void set_equipment_uuid(String equipment_uuid) {
		this.equipment_uuid = equipment_uuid;
	}
	
	/**
	 * @return the last position
	 */
	public String get_last() {
		return last;
	}
	/**
	 * @param last position to set
	 */
	public void set_last(String last) {
		this.last = last;
	}

	/**
	 * @return the status
	 */
	public String get_status() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void set_status(String status) {
		this.status = status;
	}
	
	/**
	 * @return the operation_date
	 */
	public long get_operation_date(int record) {
		if (record<records.size())
			return records.get(record).operation_date;
		else return 0;
	}

	/**
	 * @param record the record to set
	 */
	public TagRecordStructure get_record(int record) {		
		if (record<records.size())
			return records.get(record);
		else return null;
	}

	/**
	 * @param tagrecord ""
	 * @param record    ""
	 */
	public void set_record(TagRecordStructure tagrecord, int record) {		
		records.add(record, tagrecord);
	}
}