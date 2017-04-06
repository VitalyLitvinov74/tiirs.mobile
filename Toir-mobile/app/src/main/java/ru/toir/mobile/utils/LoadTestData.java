package ru.toir.mobile.utils;

import java.util.Date;

import io.realm.Realm;
import ru.toir.mobile.db.realm.AlertType;
import ru.toir.mobile.db.realm.CriticalType;
import ru.toir.mobile.db.realm.Documentation;
import ru.toir.mobile.db.realm.DocumentationType;
import ru.toir.mobile.db.realm.Equipment;
import ru.toir.mobile.db.realm.EquipmentModel;
import ru.toir.mobile.db.realm.EquipmentStatus;
import ru.toir.mobile.db.realm.EquipmentType;
import ru.toir.mobile.db.realm.MeasureType;
import ru.toir.mobile.db.realm.Operation;
import ru.toir.mobile.db.realm.OperationStatus;
import ru.toir.mobile.db.realm.OperationTemplate;
import ru.toir.mobile.db.realm.OperationType;
import ru.toir.mobile.db.realm.OperationVerdict;
import ru.toir.mobile.db.realm.OrderLevel;
import ru.toir.mobile.db.realm.OrderStatus;
import ru.toir.mobile.db.realm.OrderVerdict;
import ru.toir.mobile.db.realm.Orders;
import ru.toir.mobile.db.realm.StageTemplate;
import ru.toir.mobile.db.realm.StageType;
import ru.toir.mobile.db.realm.StageVerdict;
import ru.toir.mobile.db.realm.TaskStageList;
import ru.toir.mobile.db.realm.TaskStageOperationList;
import ru.toir.mobile.db.realm.StageStatus;
import ru.toir.mobile.db.realm.TaskStages;
import ru.toir.mobile.db.realm.TaskStatus;
import ru.toir.mobile.db.realm.TaskTemplate;
import ru.toir.mobile.db.realm.TaskType;
import ru.toir.mobile.db.realm.TaskVerdict;
import ru.toir.mobile.db.realm.Tasks;
import ru.toir.mobile.db.realm.User;

public class LoadTestData {
    public static Equipment equipment;
    public static EquipmentType equipmentType;
    public static TaskStatus taskStatusUncomplete;
    private static Equipment equipment2;
    private static Equipment equipment3;
    private static Equipment equipment4;
    private static EquipmentType equipmentType2;
    private static EquipmentType equipmentType3;
    private static EquipmentType equipmentType4;
    private static MeasureType measureType1;
    private static MeasureType measureType2;
    private static MeasureType measureType3;
    private static OrderStatus orderStatus;
    private static OrderStatus orderStatus2;
    private static OrderStatus orderStatusUncomplete;
    private static OperationStatus operationStatusUncomplete;
    private static OperationStatus operationStatusComplete;
    private static StageStatus taskStageStatusComplete;
    private static StageStatus taskStageStatusUncomplete;
    private static TaskStatus taskStatus;
    private static User profile;
    private static EquipmentStatus equipmentStatus;
    private static EquipmentStatus equipmentStatus2;
    private static EquipmentStatus equipmentStatus3;
    private static EquipmentStatus equipmentStatus4;
    private static CriticalType criticalType;
    private static EquipmentModel equipmentModel;
    private static EquipmentModel equipmentModel2;
    private static EquipmentModel equipmentModel3;
    private static EquipmentModel equipmentModel4;

    private static Documentation documentation;
    private static Documentation documentation2;
    private static Documentation documentation3;
    private static Documentation documentation4;

    private static DocumentationType documentationType;
    private static DocumentationType documentationType2;
    private static DocumentationType documentationType3;
    private static DocumentationType documentationType4;

    private static AlertType alertType;
    private static OrderLevel orderLevel;
    private static OrderLevel orderLevel2;
    private static OrderLevel orderLevel3;
    private static Orders order, order2, order3, order4;

    private static OrderVerdict orderVerdict;
    private static OrderVerdict orderVerdict2;
    private static OrderVerdict orderVerdict3;

    private static Operation operation;
    private static Operation operation2;
    private static Operation operation3;
    private static OperationType operationType;
    private static OperationType operationType2;
    private static OperationType operationType3;

    private static OperationVerdict operationVerdict;
    private static OperationVerdict operationVerdict2;
    private static OperationVerdict operationVerdict3;

    private static OperationTemplate operationTemplate;
    private static OperationTemplate operationTemplate2;
    private static OperationTemplate operationTemplate3;
    private static StageVerdict taskStageVerdict;
    private static TaskStages taskStage;
    private static TaskStages taskStage2;
    private static StageType taskStageType;
    private static StageType taskStageType2;
    private static StageType taskStageType3;
    private static TaskStageList taskStageList;
    private static TaskStageOperationList taskStageOperationList;
    private static StageTemplate taskStageTemplate;
    private static StageTemplate taskStageTemplate2;
    private static TaskVerdict taskVerdict;

    private static Tasks task;
    private static Tasks task2;
    private static Tasks task3;
    private static Tasks task4;

    private static TaskType taskType;
    private static TaskType taskType2;
    private static TaskType taskType3;
    private static TaskType taskType4;

    private static TaskTemplate taskTemplate;
    private static TaskTemplate taskTemplate2;
    private static TaskTemplate taskTemplate3;
    private static TaskTemplate taskTemplate4;

    public static void LoadAllTestData() {

        final Realm realmDB;
        realmDB = Realm.getDefaultInstance();

        final String userTestUuid = "4462ed77-9bf0-4542-b127-f4ecefce49da";
        final String userTestUuid2 = "5562ed77-9bf0-4542-b127-f4ecefce49da";

        final String equipmentUuid = "1dd8d4f8-5c98-4444-86ed-97ddbc2059f6";
        final String equipmentUuid2 = "1dd8d4f8-5c98-4444-86ed-97aabc2059f6";
        final String equipmentUuid3 = "1dd8d4f8-5c98-5445-86ed-97ddbc2059f6";
        final String equipmentUuid4 = "1dd8d4f8-5c98-4694-86ed-97aabc2059f6";

        final String equipmentTypeUuid = "1dd8d4f8-5c98-4444-86ed-97dddde";
        final String equipmentTypeUuid2 = "1dd8d4f8-5c98-4444-86ed-97ddddf";
        final String equipmentTypeUuid_nd = "00000000-5c98-4444-86ed-97dddde";
        final String equipmentTypeUuid3 = "1dd4d4f8-5c98-4444-86ed-97ddddf";
        final String equipmentTypeUuid4 = "1dd2d4f8-5c98-4444-86ed-97ddddf";
        final String equipmentTypeUuid5 = "1dd1d4f8-5c98-4444-86ed-97ddddf";

        final String measureTypeUuid1 = "1dd4d4f8-5c98-4444-86ed-97debdf";
        final String measureTypeUuid2 = "1dd2d4f8-5c98-4444-86ed-97dbedf";
        final String measureTypeUuid3 = "1dd1d4f8-5c98-4444-86ed-97dbbdf";

        final String equipmentStatusUuid = "1dd8d4f8-5c98-4124-86ed-9722222";
        final String equipmentStatusUuid2 = "1dd8d4f8-5c98-4124-86ed-9722332";
        final String equipmentStatusUuid3 = "1ee8d4f8-5c98-4124-86ed-9722222";
        final String equipmentStatusUuid4 = "1aa8d4f8-5c98-4124-86ed-9722332";

        final String documentationTypeUuid = "1dd8d4f8-5c98-4124-86ed-3722222";
        final String documentationTypeUuid2 = "1dd8d4f8-5c98-4124-86ed-4722222";
        final String documentationTypeUuid_nd = "00000000-5c98-4124-86ed-4722222";
        final String documentationTypeUuid3 = "1dd8d4f8-5c98-4124-86ed-3722211";
        final String documentationTypeUuid4 = "1dd8d4f8-5c98-4124-86ed-4722233";

        final String criticalTypeUuid = "1dd8d4f8-5c98-4444-86ed-823923832933";
        final String criticalTypeUuid2 = "1dd8d4f8-5c98-4444-86ed-823923832987";
        final String criticalTypeUuid3 = "1dd8d4f8-5c98-4444-86ed-823923832965";

        final String equipmentModelUuid = "6dd8a4f8-5c98-4444-86ed-823923832933";
        final String equipmentModelUuid2 = "6dd8a4f8-5c98-4444-86ed-823923832955";
        final String equipmentModelUuid3 = "6dd8a4f8-5c98-4564-86ed-823923832933";
        final String equipmentModelUuid4 = "6dd8a4f8-5c98-4554-86ed-823923832955";

        final String documentationUuid = "6dd8a4f8-5c98-4444-86ed-823923132922";
        final String documentationUuid2 = "8ee8a4f8-5c98-4444-86ed-823923132922";
        final String documentationUuid3 = "6dd8a4f8-5c98-2222-86ed-823923132922";
        final String documentationUuid4 = "8ee8a4f8-5c98-3333-86ed-823923132922";

        final String orderStatusUuid = "8ee8a4f8-5c98-4444-86ed-243923132922";
        final String orderStatusUuid2 = "8ee8a4f8-5c98-4444-86ed-243923132422";
        final String orderStatusUuid3 = "8ee8a4f8-5c98-4444-86ed-243923332922";

        final String orderUuid = "8ee8a4f8-5c98-4444-86ed-888923188922";
        final String orderUuid2 = "8ee8a4f8-5c98-4444-86ed-888923188933";
        final String orderUuid3 = "8ee8a4f8-5c98-4444-86ed-888923188944";
        final String orderUuid4 = "8ee8a4f8-5c98-4444-86ed-888923188955";

        final String orderVerdictUuid = "8ee8a4f8-5c98-5555-86ed-888923188922";
        final String orderVerdictUuid2 = "8ee8a4f8-5c98-5555-86ed-888923188923";
        final String orderVerdictUuid3 = "8ee8a4f8-5c98-5555-86ed-888923188924";

        final String operationStatusUuid = "8ee8a4f8-5c98-4444-86ed-243923132922";
        final String operationStatusUuid2 = "8ee8a4f8-5c98-4444-86ed-243923132348";

        final String orderLevelUuid1 = "8ee8a4f8-5c98-4444-86ed-421232123325";
        final String orderLevelUuid2 = "8ee8a4f8-5c98-4444-86ed-421232123324";
        final String orderLevelUuid3 = "8ee8a4f8-5c98-4444-86ed-421232123323";

        final String operationUuid = "8ee8a4f8-5c98-4444-86ed-888923188922";
        final String operationUuid2 = "8ee8a4f8-5c98-4444-86ed-888923188924";
        final String operationUuid3 = "8ee8a4f8-5c98-4444-86ed-888923188926";

        final String operationTypeUuid = "8ee8a4f8-5c98-4444-86ed-888923188922";
        final String operationTypeUuid2 = "8ee8a4f8-5c98-4454-86ed-888923188924";
        final String operationTypeUuid3 = "8ee8a4f8-5c98-4464-86ed-888923188926";

        final String operationVerdictUuid = "8ee8a4f8-5c98-5555-86ed-888923188922";
        final String operationVerdictUuid2 = "8ee8a4f8-5c98-5555-86ed-888923177922";
        final String operationVerdictUuid3 = "8ee8a4f8-5c98-5555-86ed-888923166922";

        final String operationTemplateUuid = "8ee8a4f8-5c98-5555-86ed-888911188922";
        final String operationTemplateUuid2 = "8ee8a4f8-5c98-5555-86ed-888911188911";
        final String operationTemplateUuid3 = "8ee9a4f8-5c98-5555-86ed-888911188911";

        final String taskStageStatusUuid = "8ee8a4f8-5c98-4444-86ed-133923132922";
        final String taskStageStatusUuid2 = "8ee8a4f8-5c98-4444-86ed-133923132322";
        final String taskStageVerdictUuid = "8ee8a4f8-5c98-1255-86ed-888923188922";

        final String taskStageListUuid = "8ee8a4f8-ff98-4444-86ed-133923132922";
        final String taskStageListUuid2 = "8ee8a6f8-ff98-4444-86ed-133923132922";
        final String taskStageOperationListUuid = "8ee8a4f8-5c98-1255-8764-888923188922";
        final String taskStageOperationListUuid2 = "8ee8a4f8-5c98-1255-8764-888923188922";

        final String taskStageUuid = "8ee8a4f8-5c98-4444-86ed-777923188922";
        final String taskStageUuid2 = "8ee8a4f8-5c98-4444-86ed-888532188924";

        final String taskStageTypeUuid = "8ee8a4f8-5c98-3124-86ed-888923188922";
        final String taskStageTypeUuid2 = "8ee8a4f8-5c98-4214-86ed-888923188924";
        final String taskStageTypeUuid3 = "8ee8a4f8-5c38-4364-86ed-888923188926";

        final String taskStageTemplateUuid = "8ee8a4f8-5c98-5555-86ed-888911188922";
        final String taskStageTemplateUuid2 = "8ee8a4f8-5c98-5555-86ed-888922288911";

        final String taskStatusUuid = "8ee8a4f8-5c98-4444-86ed-253923132922";
        final String taskVerdictUuid = "8ee8a4f8-5c98-1255-86ed-887923188922";

        final String taskUuid = "8ee8a4f8-5c98-4484-86ed-777923188922";
        final String taskUuid2 = "8ee8a4f8-5c98-1774-86ed-888532188924";

        final String taskTypeUuid = "8ee8a4f8-5c18-3124-86ed-888923188922";
        final String taskTypeUuid2 = "8ee8a4f8-5c38-4214-86ed-888923188924";

        final String taskTemplateUuid = "8ee8a4f8-5b98-5555-86ed-888911188922";
        final String taskTemplateUuid2 = "8ee8a4f8-5a98-5555-86ed-888922288911";


        // User --------------------
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                profile = realmDB.createObject(User.class);
                profile.set_id(1);
                profile.setUuid(userTestUuid);
                profile.setName("Иванов О.А.");
                profile.setImage("profile_ivanov");
                profile.setLogin("olejek8@yandex.ru");
                profile.setPass("12345");
                profile.setType(1);
                profile.setTagId("01234567");
                profile.setWhoIs("Руководитель отдела ИТ");
                profile.setActive(true);
                profile.setContact("+79227000285 Иван");
            }
        });
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                profile = realmDB.createObject(User.class);
                profile.set_id(2);
                profile.setUuid(userTestUuid2);
                profile.setName("Логачев Д.Н.");
                profile.setImage("profile_logachev");
                profile.setLogin("demonwork@yandex.ru");
                profile.setPass("12345");
                profile.setType(2);
                profile.setTagId("76543210");
                profile.setWhoIs("Ведущий специалист");
                profile.setActive(true);
                profile.setContact("+79227000285 Иван");
            }
        });

        // MeasureType -----------------
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                measureType1 = realmDB.createObject(MeasureType.class);
                measureType1.set_id(1);
                measureType1.setUuid(measureTypeUuid1);
                measureType1.setTitle("Температура");
            }
        });
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                measureType2 = realmDB.createObject(MeasureType.class);
                measureType2.set_id(2);
                measureType2.setUuid(measureTypeUuid1);
                measureType2.setTitle("Давление");
            }
        });
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                measureType3 = realmDB.createObject(MeasureType.class);
                measureType3.set_id(3);
                measureType3.setUuid(measureTypeUuid1);
                measureType3.setTitle("Частота");
            }
        });

        // CriticalType -----------------
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                criticalType = realmDB.createObject(CriticalType.class);
                criticalType.set_id(1);
                criticalType.setUuid(criticalTypeUuid);
                criticalType.setTitle("Не критичный");
            }
        });
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                criticalType = realmDB.createObject(CriticalType.class);
                criticalType.set_id(2);
                criticalType.setUuid(criticalTypeUuid2);
                criticalType.setTitle("Средний");
            }
        });
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                criticalType = realmDB.createObject(CriticalType.class);
                criticalType.set_id(3);
                criticalType.setUuid(criticalTypeUuid3);
                criticalType.setTitle("Критичный");
            }
        });

        // EquipmentType -----------------
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                equipmentType = realmDB.createObject(EquipmentType.class);
                equipmentType.set_id(1);
                equipmentType.setUuid(equipmentTypeUuid_nd);
                equipmentType.setTitle("Неизвестен");
            }
        });
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                equipmentType2 = realmDB.createObject(EquipmentType.class);
                equipmentType2.set_id(2);
                equipmentType2.setUuid(equipmentTypeUuid);
                equipmentType2.setTitle("Теплогенератор");
            }
        });
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                equipmentType3 = realmDB.createObject(EquipmentType.class);
                equipmentType3.set_id(3);
                equipmentType3.setUuid(equipmentTypeUuid2);
                equipmentType3.setTitle("Котел газовый");
            }
        });
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                equipmentType4 = realmDB.createObject(EquipmentType.class);
                equipmentType4.set_id(4);
                equipmentType4.setUuid(equipmentTypeUuid3);
                equipmentType4.setTitle("Задвижка ручная");
            }
        });
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                equipmentType3 = realmDB.createObject(EquipmentType.class);
                equipmentType3.set_id(5);
                equipmentType3.setUuid(equipmentTypeUuid4);
                equipmentType3.setTitle("Расходомер газовый");
            }
        });
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                equipmentType4 = realmDB.createObject(EquipmentType.class);
                equipmentType4.set_id(6);
                equipmentType4.setUuid(equipmentTypeUuid5);
                equipmentType4.setTitle("Датчик давления");
            }
        });

        // EquipmentStatus -----------------
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                equipmentStatus = realmDB.createObject(EquipmentStatus.class);
                equipmentStatus.set_id(1);
                equipmentStatus.setUuid(equipmentStatusUuid);
                equipmentStatus.setTitle("Исправно");
            }
        });
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                equipmentStatus2 = realmDB.createObject(EquipmentStatus.class);
                equipmentStatus2.set_id(2);
                equipmentStatus2.setUuid(equipmentStatusUuid2);
                equipmentStatus2.setTitle("Не исправно");
            }
        });
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                equipmentStatus3 = realmDB.createObject(EquipmentStatus.class);
                equipmentStatus3.set_id(3);
                equipmentStatus3.setUuid(equipmentStatusUuid3);
                equipmentStatus3.setTitle("Не установлен");
            }
        });
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                equipmentStatus4 = realmDB.createObject(EquipmentStatus.class);
                equipmentStatus4.set_id(4);
                equipmentStatus4.setUuid(equipmentStatusUuid4);
                equipmentStatus4.setTitle("В ремонте");
            }
        });

        // DocumentationType -----------------
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                documentationType = realmDB.createObject(DocumentationType.class);
                documentationType.set_id(1);
                documentationType.setUuid(documentationTypeUuid_nd);
                documentationType.setTitle("Неопределен");
            }
        });
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                documentationType = realmDB.createObject(DocumentationType.class);
                documentationType.set_id(2);
                documentationType.setUuid(documentationTypeUuid);
                documentationType.setTitle("Руководство");
            }
        });
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                documentationType2 = realmDB.createObject(DocumentationType.class);
                documentationType2.set_id(3);
                documentationType2.setUuid(documentationTypeUuid2);
                documentationType2.setTitle("Паспорт");
            }
        });
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                documentationType2 = realmDB.createObject(DocumentationType.class);
                documentationType2.set_id(4);
                documentationType2.setUuid(documentationTypeUuid3);
                documentationType2.setTitle("Инструкция");
            }
        });
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                documentationType2 = realmDB.createObject(DocumentationType.class);
                documentationType2.set_id(5);
                documentationType2.setUuid(documentationTypeUuid4);
                documentationType2.setTitle("Протокол обмена");
            }
        });

        // AlertType -----------------
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                alertType = realmDB.createObject(AlertType.class);
                alertType.set_id(1);
                alertType.setUuid("0dd8d4f8-5c98-4124-86ed-97eebc2659f6");
                alertType.setTitle("Критичный");
            }
        });

        // EquipmentModel -----------------
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                equipmentModel = realmDB.createObject(EquipmentModel.class);
                equipmentModel.set_id(1);
                //equipmentModel.setEquipmentTypeUuid(equipmentModelUuid);
                equipmentModel.setEquipmentType(equipmentType);
                equipmentModel.setUuid(equipmentModelUuid);
                equipmentModel.setTitle("Тепловей 250А");
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                equipmentModel2 = realmDB.createObject(EquipmentModel.class);
                equipmentModel2.set_id(2);
                //equipmentModel2.setEquipmentTypeUuid(equipmentModelUuid2);
                equipmentModel2.setEquipmentType(equipmentType2);
                equipmentModel2.setUuid(equipmentModelUuid2);
                equipmentModel2.setTitle("Unical-8800");
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                equipmentModel3 = realmDB.createObject(EquipmentModel.class);
                equipmentModel3.set_id(3);
                //equipmentModel3.setEquipmentTypeUuid(equipmentModelUuid3);
                equipmentModel3.setEquipmentType(equipmentType);
                equipmentModel3.setUuid(equipmentModelUuid3);
                equipmentModel3.setTitle("Эмис РГС-245");
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                equipmentModel4 = realmDB.createObject(EquipmentModel.class);
                equipmentModel4.set_id(4);
                //equipmentModel4.setEquipmentTypeUuid(equipmentModelUuid4);
                equipmentModel4.setEquipmentType(equipmentType2);
                equipmentModel4.setUuid(equipmentModelUuid4);
                equipmentModel4.setTitle("Эмис АИР-10");
            }
        });

        // Equipment -----------------
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                equipment = realmDB.createObject(Equipment.class);
                equipment.set_id(1);
                //equipment.setCriticalTypeUuid(criticalTypeUuid);
                //equipment.setEquipmentModelUuid(equipmentModelUuid);
                //equipment.setEquipmentStatusUuid(equipmentStatusUuid);
                equipment.setImage("teplovey.jpg");
                equipment.setInventoryNumber("IN:001212");
                equipment.setUuid(equipmentUuid);
                equipment.setTitle("Теплогенератор Тепловей-250А");
                equipment.setTagId("1234-5678-9101112");
                //equipment.setUserUuid(userTestUuid);
                equipment.setLocation("Цех изоляторов ПФИ");
                equipment.setLatitude((float) 55.343);
                equipment.setLongitude((float) 55.234);
                equipment.setStartDate(new Date());
                equipment.setEquipmentModel(equipmentModel);
                equipment.setCriticalType(criticalType);
                equipment.setEquipmentStatus(equipmentStatus);
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                equipment2 = realmDB.createObject(Equipment.class);
                equipment2.set_id(2);
                //equipment2.setCriticalTypeUuid(criticalTypeUuid);
                //equipment2.setEquipmentModelUuid(equipmentModelUuid2);
                //equipment2.setEquipmentStatusUuid(equipmentStatusUuid);
                equipment2.setImage("kotel.jpg");
                equipment2.setInventoryNumber("IN:001213");
                equipment2.setUuid(equipmentUuid2);
                equipment2.setTitle("Газовый котел Unical");
                equipment2.setTagId("02345567");
                //equipment2.setUserUuid(userTestUuid);
                equipment2.setLocation("Котельная №3");
                equipment2.setLatitude((float) 55.5311);
                equipment2.setLongitude((float) 55.1222);
                equipment2.setStartDate(new Date());
                equipment2.setEquipmentModel(equipmentModel2);
                equipment2.setCriticalType(criticalType);
                equipment2.setEquipmentStatus(equipmentStatus);
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                equipment3 = realmDB.createObject(Equipment.class);
                equipment3.set_id(3);
                //equipment3.setCriticalTypeUuid(criticalTypeUuid2);
                //equipment3.setEquipmentModelUuid(equipmentModelUuid3);
                //equipment3.setEquipmentStatusUuid(equipmentStatusUuid3);
                equipment3.setImage("gas_counter.jpg");
                equipment3.setInventoryNumber("IN:62211252");
                equipment3.setUuid(equipmentUuid3);
                equipment3.setTitle("Счетчик газа ВК-G10T");
                equipment3.setTagId("2321232-22322-74341");
                //equipment3.setUserUuid(userTestUuid2);
                equipment3.setLocation("Теплопункт");
                equipment3.setLatitude((float) 55.222143);
                equipment3.setLongitude((float) 55.212134);
                equipment3.setStartDate(new Date());
                equipment3.setEquipmentModel(equipmentModel3);
                equipment3.setCriticalType(criticalType);
                equipment3.setEquipmentStatus(equipmentStatus3);
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                equipment4 = realmDB.createObject(Equipment.class);
                equipment4.set_id(4);
                //equipment4.setCriticalTypeUuid(criticalTypeUuid);
                //equipment4.setEquipmentModelUuid(equipmentModelUuid4);
                //equipment4.setEquipmentStatusUuid(equipmentStatusUuid3);
                equipment4.setImage("pressure.jpg");
                equipment4.setInventoryNumber("IN:78921213");
                equipment4.setUuid(equipmentUuid4);
                equipment4.setTitle("Датчик давления YSO-04");
                equipment4.setTagId("19532-09021123-2562293");
                //equipment4.setUserUuid(userTestUuid);
                equipment4.setLocation("Котельная №2");
                equipment4.setLatitude((float) 55.53121);
                equipment4.setLongitude((float) 55.12222);
                equipment4.setStartDate(new Date());
                equipment4.setEquipmentModel(equipmentModel4);
                equipment4.setCriticalType(criticalType);
                equipment4.setEquipmentStatus(equipmentStatus3);
            }
        });

        // Documentation -----------------
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                documentation = realmDB.createObject(Documentation.class);
                documentation.set_id(1);
                //documentation.setDocumentationTypeUuid(documentationTypeUuid);
                documentation.setDocumentationType(documentationType);
                //documentation.setEquipmentUuid(equipmentUuid);
                documentation.setUuid(documentationUuid);
                documentation.setTitle("Паспорт на Тепловей-250/251");
                documentation.setPath("1.pdf");
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                documentation2 = realmDB.createObject(Documentation.class);
                documentation2.set_id(2);
                //documentation2.setDocumentationTypeUuid(documentationTypeUuid2);
                documentation2.setDocumentationType(documentationType2);
                //documentation2.setEquipmentUuid(equipmentUuid2);
                documentation2.setUuid(documentationUuid2);
                documentation2.setTitle("Руководство на котел GTV-40");
                documentation2.setPath("2.pdf");
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                documentation3 = realmDB.createObject(Documentation.class);
                documentation3.set_id(3);
                //documentation3.setDocumentationTypeUuid(documentationTypeUuid3);
                documentation3.setDocumentationType(documentationType4);
                //documentation3.setEquipmentUuid(equipmentUuid3);
                documentation3.setUuid(documentationUuid4);
                documentation3.setTitle("Инструкция по эксплуатации счетчика ВК");
                documentation3.setPath("3.pdf");
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                documentation4 = realmDB.createObject(Documentation.class);
                documentation4.set_id(4);
                //documentation4.setDocumentationTypeUuid(documentationTypeUuid4);
                documentation4.setDocumentationType(documentationType3);
                //documentation4.setEquipmentUuid(equipmentUuid4);
                documentation4.setUuid(documentationUuid3);
                documentation4.setTitle("Протокол обмена с датчиком давления");
                documentation4.setPath("4.pdf");
            }
        });

        // OrderStatus -----------------
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                orderStatus = realmDB.createObject(OrderStatus.class);
                orderStatus.set_id(1);
                orderStatus.setUuid(orderStatusUuid);
                orderStatus.setTitle("В работе");
                orderStatus.setIcon("status_easy_work.png");
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                orderStatus2 = realmDB.createObject(OrderStatus.class);
                orderStatus2.set_id(2);
                orderStatus2.setUuid(orderStatusUuid2);
                orderStatus2.setTitle("Выполнен");
                orderStatus2.setIcon("status_easy_ready.png");
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                orderStatusUncomplete = realmDB.createObject(OrderStatus.class);
                orderStatusUncomplete.set_id(3);
                orderStatusUncomplete.setUuid(orderStatusUuid3);
                orderStatusUncomplete.setTitle("Получен");
                orderStatusUncomplete.setIcon("status_easy_received.png");
            }
        });

        // OrderVerdict -----------------
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                orderVerdict = realmDB.createObject(OrderVerdict.class);
                orderVerdict.set_id(1);
                orderVerdict.setUuid(orderVerdictUuid);
                orderVerdict.setTitle("Выполнен");
            }
        });
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                orderVerdict2 = realmDB.createObject(OrderVerdict.class);
                orderVerdict2.set_id(2);
                orderVerdict2.setUuid(orderVerdictUuid2);
                orderVerdict2.setTitle("Не выполнен");
            }
        });
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                orderVerdict3 = realmDB.createObject(OrderVerdict.class);
                orderVerdict3.set_id(3);
                orderVerdict3.setUuid(orderVerdictUuid3);
                orderVerdict3.setTitle("Прерван");
            }
        });

        // OrderLevel -----------------
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                orderLevel = realmDB.createObject(OrderLevel.class);
                orderLevel.set_id(1);
                orderLevel.setUuid(orderLevelUuid1);
                orderLevel.setTitle("Низкий");
                orderLevel.setIcon("status_easy_receive.png");
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                orderLevel2 = realmDB.createObject(OrderLevel.class);
                orderLevel2.set_id(2);
                orderLevel2.setUuid(orderLevelUuid2);
                orderLevel2.setTitle("Средний");
                orderLevel2.setIcon("status_mid_receive.png");
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                orderLevel3 = realmDB.createObject(OrderLevel.class);
                orderLevel3.set_id(3);
                orderLevel3.setUuid(orderLevelUuid3);
                orderLevel3.setTitle("Высокий");
                orderLevel3.setIcon("status_high_receive.png");
            }
        });

        // ---------------------------------------------------------------------------------------------
        // OperationVerdict -----------------
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                operationVerdict = realmDB.createObject(OperationVerdict.class);
                operationVerdict.set_id(1);
                operationVerdict.setUuid(operationVerdictUuid);
                operationVerdict.setTitle("Выполнена");
            }
        });
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                operationVerdict2 = realmDB.createObject(OperationVerdict.class);
                operationVerdict2.set_id(2);
                operationVerdict2.setUuid(operationVerdictUuid2);
                operationVerdict2.setTitle("Не выполнена");
            }
        });
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                operationVerdict3 = realmDB.createObject(OperationVerdict.class);
                operationVerdict3.set_id(3);
                operationVerdict3.setUuid(operationVerdictUuid3);
                operationVerdict3.setTitle("Отменена");
            }
        });

        // OperationType -----------------
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                operationType = realmDB.createObject(OperationType.class);
                operationType.set_id(1);
                operationType.setUuid(operationTypeUuid);
                operationType.setTitle("Открутить гайку");
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                operationType2 = realmDB.createObject(OperationType.class);
                operationType2.set_id(2);
                operationType2.setUuid(operationTypeUuid2);
                operationType2.setTitle("Снять");
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                operationType3 = realmDB.createObject(OperationType.class);
                operationType3.set_id(3);
                operationType3.setUuid(operationTypeUuid3);
                operationType3.setTitle("Осмотреть");
            }
        });

        // OperationStatus -----------------
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                operationStatusUncomplete = realmDB.createObject(OperationStatus.class);
                operationStatusUncomplete.set_id(1);
                operationStatusUncomplete.setUuid(operationStatusUuid);
                operationStatusUncomplete.setTitle("Не выполнена");
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                operationStatusComplete = realmDB.createObject(OperationStatus.class);
                operationStatusComplete.set_id(2);
                operationStatusComplete.setUuid(operationStatusUuid2);
                operationStatusComplete.setTitle("Выполнена");
            }
        });

        // OperationTemplate -----------------
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                operationTemplate = realmDB.createObject(OperationTemplate.class);
                operationTemplate.set_id(1);
                operationTemplate.setUuid(operationTemplateUuid);
                operationTemplate.setEquipmentModel(equipmentModel);
                operationTemplate.setTitle("Снять заднюю крышку");
                operationTemplate.setDescription("Открутить четыре болта по краям ключом на 12");
                operationTemplate.setFirst_step(1);
                operationTemplate.setImage("");
                //operationTemplate.setEquipmentModelUuid(equipmentModelUuid);
                operationTemplate.setEquipmentModel(equipmentModel);
                operationTemplate.setNormative(180);
                operationTemplate.setLast_step(0);
                operationTemplate.setOperationType(operationType);
                //operationTemplate.setOperationTypeUuid(operationTypeUuid);
            }
        });
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                operationTemplate2 = realmDB.createObject(OperationTemplate.class);
                operationTemplate2.set_id(2);
                operationTemplate2.setUuid(operationTemplateUuid2);
                operationTemplate2.setEquipmentModel(equipmentModel);
                operationTemplate2.setTitle("Убрать заднюю крышку");
                operationTemplate2.setDescription("Снять заднюю крышку и отставить ее в сторону");
                operationTemplate2.setFirst_step(0);
                operationTemplate2.setImage("");
                //operationTemplate2.setEquipmentModelUuid(equipmentModelUuid);
                operationTemplate2.setEquipmentModel(equipmentModel);
                operationTemplate2.setNormative(100);
                operationTemplate2.setLast_step(0);
                operationTemplate2.setOperationType(operationType2);
                //operationTemplate2.setOperationTypeUuid(operationTypeUuid2);
            }
        });


        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                operationTemplate3 = realmDB.createObject(OperationTemplate.class);
                operationTemplate3.set_id(3);
                operationTemplate3.setUuid(operationTemplateUuid3);
                operationTemplate3.setEquipmentModel(equipmentModel);
                operationTemplate3.setTitle("Осмотреть накопитель");
                operationTemplate3.setDescription("Осмотреть накопитель на предмет утечек");
                operationTemplate3.setFirst_step(0);
                operationTemplate3.setImage("");
                //operationTemplate3.setEquipmentModelUuid(equipmentModelUuid);
                operationTemplate3.setEquipmentModel(equipmentModel);
                operationTemplate3.setNormative(110);
                operationTemplate3.setLast_step(0);
                operationTemplate3.setOperationType(operationType3);
                //operationTemplate3.setOperationTypeUuid(operationTypeUuid3);
            }
        });

        // Operation -----------------
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                operation = realmDB.createObject(Operation.class);
                operation.set_id(1);
                operation.setUuid(operationUuid);
                operation.setOperationStatus(operationStatusUncomplete);
                //operation.setOperationStatusUuid(operationStatusUuid);
                operation.setEndDate(new Date());
                operation.setFlowOrder(1);
                operation.setStartDate(new Date());
                operation.setOperationVerdict(operationVerdict);
                //operation.setOperationVerdictUuid(operationVerdictUuid);
                operation.setOperationTemplate(operationTemplate);
                //operation.setTaskStageUuid(taskStageUuid);
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                operation2 = realmDB.createObject(Operation.class);
                operation2.set_id(2);
                operation2.setUuid(operationUuid2);
                operation2.setOperationStatus(operationStatusUncomplete);
                //operation2.setOperationStatusUuid(operationStatusUuid);
                operation2.setEndDate(new Date());
                operation2.setFlowOrder(2);
                operation2.setStartDate(new Date());
                operation2.setOperationVerdict(operationVerdict);
                //operation2.setOperationVerdictUuid(operationVerdictUuid);
                operation2.setOperationTemplate(operationTemplate2);
                //operation2.setTaskStageUuid(taskStageUuid);
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                operation3 = realmDB.createObject(Operation.class);
                operation3.set_id(3);
                operation3.setUuid(operationUuid3);
                operation3.setOperationStatus(operationStatusUncomplete);
                //operation3.setOperationStatusUuid(operationStatusUuid);
                operation3.setEndDate(new Date());
                operation3.setFlowOrder(3);
                operation3.setStartDate(new Date());
                operation3.setOperationVerdict(operationVerdict);
                //operation3.setOperationVerdictUuid(operationVerdictUuid);
                operation3.setOperationTemplate(operationTemplate3);
                //operation3.setTaskStageUuid(taskStageUuid);
            }
        });
        // ---------------------------------------------------------------------------------------------
        // TaskStageVerdict -----------------
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                taskStageVerdict = realmDB.createObject(StageVerdict.class);
                taskStageVerdict.set_id(1);
                taskStageVerdict.setUuid(taskStageVerdictUuid);
                taskStageVerdict.setTitle("Выполнен");
            }
        });

        // TaskStageType -----------------
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                taskStageType = realmDB.createObject(StageType.class);
                taskStageType.set_id(1);
                taskStageType.setUuid(taskStageTypeUuid);
                taskStageType.setTitle("Снятие крышки");
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                taskStageType2 = realmDB.createObject(StageType.class);
                taskStageType2.set_id(2);
                taskStageType2.setUuid(taskStageTypeUuid2);
                taskStageType2.setTitle("Демонтаж экрана");
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                taskStageType3 = realmDB.createObject(StageType.class);
                taskStageType3.set_id(3);
                taskStageType3.setUuid(taskStageTypeUuid3);
                taskStageType3.setTitle("Осмотр горелки");
            }
        });

        // TaskStageStatus -----------------
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                taskStageStatusUncomplete = realmDB.createObject(StageStatus.class);
                taskStageStatusUncomplete.set_id(1);
                taskStageStatusUncomplete.setUuid(taskStageStatusUuid);
                taskStageStatusUncomplete.setTitle("Не выполнен");
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                taskStageStatusComplete = realmDB.createObject(StageStatus.class);
                taskStageStatusComplete.set_id(2);
                taskStageStatusComplete.setUuid(taskStageStatusUuid2);
                taskStageStatusComplete.setTitle("Выполнен");
            }
        });

        // TaskStageTemplate -----------------
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                taskStageTemplate = realmDB.createObject(StageTemplate.class);
                taskStageTemplate.set_id(1);
                taskStageTemplate.setUuid(taskStageTemplateUuid);
                taskStageTemplate.setEquipmentModel(equipmentModel);
                taskStageTemplate.setTitle("Снять заднюю крышку");
                taskStageTemplate.setDescription("Открутить четыре болта по краям ключом на 12");
                taskStageTemplate.setImage("");
                //taskStageTemplate.setEquipmentModelUuid(equipmentModelUuid);
                taskStageTemplate.setEquipmentModel(equipmentModel);
                taskStageTemplate.setNormative(480);
                taskStageTemplate.setTaskStageType(taskStageType);
                //taskStageTemplate.setTaskStageTypeUuid(taskStageTypeUuid);
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                taskStageTemplate2 = realmDB.createObject(StageTemplate.class);
                taskStageTemplate2.set_id(2);
                taskStageTemplate2.setUuid(taskStageTemplateUuid2);
                taskStageTemplate2.setEquipmentModel(equipmentModel);
                taskStageTemplate2.setTitle("Убрать заднюю крышку");
                taskStageTemplate2.setDescription("Снять заднюю крышку и отставить ее в сторону");
                taskStageTemplate2.setImage("");
                //taskStageTemplate2.setEquipmentModelUuid(equipmentModelUuid);
                taskStageTemplate2.setEquipmentModel(equipmentModel);
                taskStageTemplate2.setNormative(300);
                taskStageTemplate2.setTaskStageType(taskStageType2);
                //taskStageTemplate2.setTaskStageTypeUuid(taskStageTypeUuid2);
            }
        });

        // TaskStage -----------------
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                taskStage = realmDB.createObject(TaskStages.class);
                taskStage.set_id(1);
                taskStage.setUuid(taskStageUuid);
                taskStage.setTaskStageStatus(taskStageStatusUncomplete);
//            taskStage.setTaskStageStatusUuid(taskStageStatusUuid);
                taskStage.setEndDate(new Date());
                taskStage.setFlowOrder(1);
                taskStage.setStartDate(new Date());
                taskStage.setEquipment(equipment);
//            taskStage.setEquipmentUuid(equipmentUuid);
                taskStage.setTaskStageVerdict(taskStageVerdict);
//            taskStage.setTaskStageVerdictUuid(taskStageVerdictUuid);
                taskStage.setTaskStageTemplate(taskStageTemplate);
//            taskStage.setTaskStageTemplateUuid(taskStageTemplateUuid);
//            taskStage.setTaskUuid(taskUuid);
                taskStage.addOperations(operation);
                taskStage.addOperations(operation2);
                taskStage.addOperations(operation3);
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                taskStage2 = realmDB.createObject(TaskStages.class);
                taskStage2.set_id(2);
                taskStage2.setUuid(taskStageUuid2);
                taskStage2.setTaskStageStatus(taskStageStatusUncomplete);
//            taskStage2.setTaskStageStatusUuid(taskStageStatusUuid);
                taskStage2.setEndDate(new Date());
                taskStage2.setFlowOrder(2);
                taskStage2.setStartDate(new Date());
                taskStage2.setEquipment(equipment2);
//            taskStage2.setEquipmentUuid(equipmentUuid2);
                taskStage2.setTaskStageVerdict(taskStageVerdict);
//            taskStage2.setTaskStageVerdictUuid(taskStageVerdictUuid);
                taskStage2.setTaskStageTemplate(taskStageTemplate2);
//            taskStage2.setTaskStageTemplateUuid(taskStageTemplateUuid2);
//            taskStage2.setTaskUuid(taskUuid);
            }
        });

        // TaskStageList -----------------
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                taskStageList = realmDB.createObject(TaskStageList.class);
                taskStageList.set_id(1);
                taskStageList.setUuid(taskStageListUuid);
                taskStageList.setFlowOrder(1);
                taskStageList.setTaskTemplate(taskTemplate);
                taskStageList.setTaskStageTemplate(taskStageTemplate);
            }
        });
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                taskStageList = realmDB.createObject(TaskStageList.class);
                taskStageList.set_id(2);
                taskStageList.setUuid(taskStageListUuid2);
                taskStageList.setFlowOrder(2);
                taskStageList.setTaskTemplate(taskTemplate);
                taskStageList.setTaskStageTemplate(taskStageTemplate2);
            }
        });
        // ---------------------------------------------------------------------------------------------
        // TaskVerdict -----------------
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                taskVerdict = realmDB.createObject(TaskVerdict.class);
                taskVerdict.set_id(1);
                taskVerdict.setUuid(taskVerdictUuid);
                taskVerdict.setTitle("Выполненa");
            }
        });

        // TaskType -----------------
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                taskType = realmDB.createObject(TaskType.class);
                taskType.set_id(1);
                taskType.setUuid(taskTypeUuid);
                taskType.setTitle("Детальный осмотр");
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                taskType2 = realmDB.createObject(TaskType.class);
                taskType2.set_id(2);
                taskType2.setUuid(taskTypeUuid2);
                taskType2.setTitle("Частичный ремонт");
            }
        });

        // TaskStatus -----------------
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                taskStatus = realmDB.createObject(TaskStatus.class);
                taskStatus.set_id(1);
                taskStatus.setUuid(taskStatusUuid);
                taskStatus.setTitle("Не выполнена");
            }
        });

        // TaskTemplate -----------------
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                taskTemplate = realmDB.createObject(TaskTemplate.class);
                taskTemplate.set_id(1);
                taskTemplate.setUuid(taskTemplateUuid);
                taskTemplate.setEquipmentModel(equipmentModel);
                taskTemplate.setTitle("Осмотр генератора Тепловея");
                taskTemplate.setDescription("Проверка на работоспособность и утечки");
                taskTemplate.setImage("");
                //taskTemplate.setEquipmentModelUuid(equipmentModelUuid);
                taskTemplate.setEquipmentModel(equipmentModel);
                taskTemplate.setNormative(1480);
                taskTemplate.setTaskType(taskType);
                //taskTemplate.setTaskTypeUuid(taskTypeUuid);
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                taskTemplate2 = realmDB.createObject(TaskTemplate.class);
                taskTemplate2.set_id(2);
                taskTemplate2.setUuid(taskStageTemplateUuid2);
                taskTemplate2.setEquipmentModel(equipmentModel);
                taskTemplate2.setTitle("Ремонт генератора Тепловей-250");
                taskTemplate2.setDescription("Ремонт компрессора теплогенератора");
                taskTemplate2.setImage("");
                //taskTemplate2.setEquipmentModelUuid(equipmentModelUuid);
                taskTemplate2.setEquipmentModel(equipmentModel);
                taskTemplate2.setNormative(7300);
                taskTemplate2.setTaskType(taskType2);
                //taskTemplate2.setTaskTypeUuid(taskTypeUuid2);
            }
        });
        // TaskStageOperationList -----------------
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                taskStageOperationList = realmDB.createObject(TaskStageOperationList.class);
                taskStageOperationList.set_id(1);
                taskStageOperationList.setUuid(taskStageOperationListUuid);
                taskStageOperationList.setFlowOrder(1);
                taskStageOperationList.setTaskStageTemplate(taskStageTemplate);
                taskStageOperationList.setOperationTemplate(operationTemplate);
            }
        });
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                taskStageOperationList = realmDB.createObject(TaskStageOperationList.class);
                taskStageOperationList.set_id(2);
                taskStageOperationList.setUuid(taskStageOperationListUuid2);
                taskStageOperationList.setFlowOrder(2);
                taskStageOperationList.setTaskStageTemplate(taskStageTemplate);
                taskStageOperationList.setOperationTemplate(operationTemplate2);
            }
        });

        // Task -----------------
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                task = realmDB.createObject(Tasks.class);
                task.set_id(1);
                task.setUuid(taskUuid);
                task.setTaskStatus(taskStatus);
                //task.setTaskStatusUuid(taskStatusUuid);
                task.setEndDate(new Date());
                task.setStartDate(new Date());
                task.setTaskVerdict(taskVerdict);
                //task.setTaskVerdictUuid(taskVerdictUuid);
                task.setTaskTemplate(taskTemplate);
                //task.setTaskTemplateUuid(taskTemplateUuid);
                task.setEquipment(equipment);
                //task.setEquipmentUuid(equipmentUuid);
                task.setComment("Там тепловей шумит сильно, из под него бежит и тепла нет. Следует разобраться.");
                task.setPrevCode(0);
                task.setNextCode(2);
                task.addTaskStage(taskStage);
                task.addTaskStage(taskStage2);
                //task.setOrder(order);
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                task2 = realmDB.createObject(Tasks.class);
                task2.set_id(2);
                task2.setUuid(taskUuid2);
                task2.setTaskStatus(taskStatus);
                //task2.setTaskStatusUuid(taskStatusUuid);
                task2.setEndDate(new Date());
                task2.setStartDate(new Date());
                task2.setTaskVerdict(taskVerdict);
                //task2.setTaskVerdictUuid(taskVerdictUuid);
                task2.setTaskTemplate(taskTemplate2);
                //task2.setTaskTemplateUuid(taskTemplateUuid2);
                task2.setEquipment(equipment2);
                //task2.setEquipmentUuid(equipmentUuid2);
                task2.setComment("Горелка котла в котельной не горит. Требуется починить.");
                task2.setPrevCode(0);
                task2.setNextCode(1);
                //task2.setOrder(order);
            }
        });

        // Orders -----------------
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                User user = realmDB.where(User.class).equalTo("_id", 1).findFirst();
                order = realmDB.createObject(Orders.class);
                order.set_id(1);
                order.setUuid(orderUuid);
//                order.setUserUuid(userTestUuid);
                order.setUser(user);
                order.setOrderLevel(orderLevel);
                order.setAttemptCount(0);
                order.setAttemptSendDate(new Date());
//                order.setAuthorUuid(userTestUuid);
                order.setAuthor(user);
                order.setCloseDate(new Date());
                order.setStartDate(new Date());
                order.setOpenDate(new Date());
                //order.setOrderStatusUuid(orderStatusUuid);
                order.setOrderStatus(orderStatus);
                //order.setOrderVerdictUuid(orderVerdictUuid);
                order.setOrderVerdict(orderVerdict);
                order.setTitle("Осмотр камер наблюдения");
                order.addTask(task);
                order.addTask(task2);
            }
        });


        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                User user = realmDB.where(User.class).equalTo("_id", 1).findFirst();
                order2 = realmDB.createObject(Orders.class);
                order2.set_id(2);
//                order2.setUserUuid(userTestUuid);
                order2.setUser(user);
                order2.setUuid(orderUuid2);
                order2.setOrderLevel(orderLevel2);
                order2.setAttemptCount(0);
                order2.setAttemptSendDate(new Date());
//                order2.setAuthorUuid(userTestUuid);
                order2.setAuthor(user);
                order2.setCloseDate(new Date());
                order2.setStartDate(new Date());
                order2.setOpenDate(new Date());
                //order2.setOrderStatusUuid(orderStatusUuid2);
                order2.setOrderStatus(orderStatus2);
                //order2.setOrderVerdictUuid(orderVerdictUuid);
                order2.setOrderVerdict(orderVerdict);
                order2.setTitle("Ремонтные работы по котельным");
                order2.addTask(task);
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                User user = realmDB.where(User.class).equalTo("_id", 1).findFirst();
                order3 = realmDB.createObject(Orders.class);
                order3.set_id(3);
                order3.setUuid(orderUuid3);
//                order3.setUserUuid(userTestUuid);
                order3.setUser(user);
                order3.setOrderLevel(orderLevel3);
                order3.setAttemptCount(0);
                order3.setAttemptSendDate(new Date());
//                order3.setAuthorUuid(userTestUuid);
                order3.setAuthor(user);
                order3.setCloseDate(new Date());
                order3.setReceiveDate(new Date());
                order3.setStartDate(new Date());
                order3.setOpenDate(new Date());
                //order3.setOrderStatusUuid(orderStatusUuid3);
                order3.setOrderStatus(orderStatusUncomplete);
                //order3.setOrderVerdictUuid(orderVerdictUuid2);
                order3.setOrderVerdict(orderVerdict2);
                order3.setTitle("Демонтаж устаревшего оборудования");
                order3.addTask(task);
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                User user = realmDB.where(User.class).equalTo("_id", 1).findFirst();
                order4 = realmDB.createObject(Orders.class);
                order4.set_id(4);
//                order4.setUserUuid(userTestUuid2);
                order4.setUser(user);
                order4.setUuid(orderUuid4);
                order4.setOrderLevel(orderLevel3);
                order4.setAttemptCount(2);
                order4.setAttemptSendDate(new Date());
//                order4.setAuthorUuid(userTestUuid2);
                order4.setAuthor(user);
                order4.setCloseDate(new Date());
                order4.setStartDate(new Date());
                order4.setOpenDate(new Date());
                //order4.setOrderStatusUuid(orderStatusUuid2);
                order4.setOrderStatus(orderStatus2);
                //order4.setOrderVerdictUuid(orderVerdictUuid2);
                order4.setOrderVerdict(orderVerdict2);
                order4.setTitle("Демонтаж датчиков давления и счетчиков газа");
                order4.addTask(task2);
            }
        });
    }

}
