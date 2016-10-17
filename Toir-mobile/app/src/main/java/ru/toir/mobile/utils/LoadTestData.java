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
import ru.toir.mobile.db.realm.Operation;
import ru.toir.mobile.db.realm.OperationStatus;
import ru.toir.mobile.db.realm.OperationTemplate;
import ru.toir.mobile.db.realm.OperationType;
import ru.toir.mobile.db.realm.OperationVerdict;
import ru.toir.mobile.db.realm.OrderLevel;
import ru.toir.mobile.db.realm.OrderStatus;
import ru.toir.mobile.db.realm.OrderVerdict;
import ru.toir.mobile.db.realm.Orders;
import ru.toir.mobile.db.realm.TaskStageList;
import ru.toir.mobile.db.realm.TaskStageOperationList;
import ru.toir.mobile.db.realm.TaskStageStatus;
import ru.toir.mobile.db.realm.TaskStageTemplate;
import ru.toir.mobile.db.realm.TaskStageType;
import ru.toir.mobile.db.realm.TaskStageVerdict;
import ru.toir.mobile.db.realm.TaskStages;
import ru.toir.mobile.db.realm.TaskStatus;
import ru.toir.mobile.db.realm.TaskTemplate;
import ru.toir.mobile.db.realm.TaskType;
import ru.toir.mobile.db.realm.TaskVerdict;
import ru.toir.mobile.db.realm.Tasks;
import ru.toir.mobile.db.realm.User;

public class LoadTestData {
    public static Equipment equipment;
    public static Equipment equipment2;
    public static EquipmentType equipmentType;
    public static EquipmentType equipmentType2;
    static User profile;
    static EquipmentStatus equipmentStatus;
    static EquipmentStatus equipmentStatus2;

    static CriticalType criticalType;

    static EquipmentModel equipmentModel;
    static EquipmentModel equipmentModel2;

    static Documentation documentation;
    static Documentation documentation2;

    static DocumentationType documentationType;
    static DocumentationType documentationType2;

    static AlertType alertType;

    static OrderStatus orderStatus;
    static OrderStatus orderStatus2;
    static OrderStatus orderStatus3;

    static OrderLevel orderLevel;
    static OrderLevel orderLevel2;
    static OrderLevel orderLevel3;

    static Orders order,order2,order3;

    static OrderVerdict orderVerdict;

    static OperationStatus operationStatus;

    static Operation operation;
    static Operation operation2;
    static Operation operation3;

    static OperationType operationType;
    static OperationType operationType2;
    static OperationType operationType3;

    static OperationVerdict operationVerdict;

    static OperationTemplate operationTemplate;
    static OperationTemplate operationTemplate2;
    static OperationTemplate operationTemplate3;

    static TaskStageStatus taskStageStatus;

    static TaskStageVerdict taskStageVerdict;

    static TaskStages taskStage;
    static TaskStages taskStage2;

    static TaskStageType taskStageType;
    static TaskStageType taskStageType2;
    static TaskStageType taskStageType3;

    static TaskStageList taskStageList;
    static TaskStageOperationList taskStageOperationList;

    static TaskStageTemplate taskStageTemplate;
    static TaskStageTemplate taskStageTemplate2;

    static TaskStatus taskStatus;

    static TaskVerdict taskVerdict;

    static Tasks task;
    static Tasks task2;

    static TaskType taskType;
    static TaskType taskType2;

    static TaskTemplate taskTemplate;
    static TaskTemplate taskTemplate2;

    public static void LoadAllTestData(){

        final Realm realmDB;
        realmDB = Realm.getDefaultInstance();

        final String userTestUuid = "4462ed77-9bf0-4542-b127-f4ecefce49da";
        final String equipmentUuid="1dd8d4f8-5c98-4444-86ed-97ddbc2059f6";
        final String equipmentUuid2="1dd8d4f8-5c98-4444-86ed-97aabc2059f6";

        final String equipmentTypeUuid="1dd8d4f8-5c98-4444-86ed-97dddde";
        final String equipmentTypeUuid2="1dd8d4f8-5c98-4444-86ed-97ddddf";
        final String equipmentTypeUuid_nd="00000000-5c98-4444-86ed-97dddde";

        final String equipmentStatusUuid="1dd8d4f8-5c98-4124-86ed-9722222";
        final String equipmentStatusUuid2="1dd8d4f8-5c98-4124-86ed-9722332";

        final String documentationTypeUuid="1dd8d4f8-5c98-4124-86ed-3722222";
        final String documentationTypeUuid2="1dd8d4f8-5c98-4124-86ed-4722222";
        final String documentationTypeUuid_nd="00000000-5c98-4124-86ed-4722222";

        final String criticalTypeUuid="1dd8d4f8-5c98-4444-86ed-823923832933";
        final String criticalTypeUuid2="1dd8d4f8-5c98-4444-86ed-823923832987";
        final String criticalTypeUuid3="1dd8d4f8-5c98-4444-86ed-823923832965";

        final String equipmentModelUuid="6dd8a4f8-5c98-4444-86ed-823923832933";
        final String equipmentModelUuid2="6dd8a4f8-5c98-4444-86ed-823923832955";

        final String documentationUuid="6dd8a4f8-5c98-4444-86ed-823923132922";
        final String documentationUuid2="8ee8a4f8-5c98-4444-86ed-823923132922";

        final String orderStatusUuid="8ee8a4f8-5c98-4444-86ed-243923132922";
        final String orderStatusUuid2="8ee8a4f8-5c98-4444-86ed-243923132422";
        final String orderStatusUuid3="8ee8a4f8-5c98-4444-86ed-243923332922";

        final String orderUuid="8ee8a4f8-5c98-4444-86ed-888923188922";
        final String orderUuid2="8ee8a4f8-5c98-4444-86ed-888923188933";
        final String orderUuid3="8ee8a4f8-5c98-4444-86ed-888923188944";

        final String orderVerdictUuid="8ee8a4f8-5c98-5555-86ed-888923188922";
        final String operationStatusUuid="8ee8a4f8-5c98-4444-86ed-243923132922";
        final String operationStatusUuid2 = "8ee8a4f8-5c98-4444-86ed-243923132348";

        final String orderLevelUuid1="8ee8a4f8-5c98-4444-86ed-421232123325";
        final String orderLevelUuid2="8ee8a4f8-5c98-4444-86ed-421232123324";
        final String orderLevelUuid3="8ee8a4f8-5c98-4444-86ed-421232123323";

        final String operationUuid="8ee8a4f8-5c98-4444-86ed-888923188922";
        final String operationUuid2="8ee8a4f8-5c98-4444-86ed-888923188924";
        final String operationUuid3="8ee8a4f8-5c98-4444-86ed-888923188926";

        final String operationTypeUuid="8ee8a4f8-5c98-4444-86ed-888923188922";
        final String operationTypeUuid2="8ee8a4f8-5c98-4454-86ed-888923188924";
        final String operationTypeUuid3="8ee8a4f8-5c98-4464-86ed-888923188926";

        final String operationVerdictUuid="8ee8a4f8-5c98-5555-86ed-888923188922";

        final String operationTemplateUuid="8ee8a4f8-5c98-5555-86ed-888911188922";
        final String operationTemplateUuid2="8ee8a4f8-5c98-5555-86ed-888911188911";
        final String operationTemplateUuid3="8ee9a4f8-5c98-5555-86ed-888911188911";

        final String taskStageStatusUuid="8ee8a4f8-5c98-4444-86ed-133923132922";
        final String taskStageVerdictUuid="8ee8a4f8-5c98-1255-86ed-888923188922";

        final String taskStageListUuid="8ee8a4f8-ff98-4444-86ed-133923132922";
        final String taskStageListUuid2="8ee8a6f8-ff98-4444-86ed-133923132922";
        final String taskStageOperationListUuid="8ee8a4f8-5c98-1255-8764-888923188922";
        final String taskStageOperationListUuid2="8ee8a4f8-5c98-1255-8764-888923188922";

        final String taskStageUuid="8ee8a4f8-5c98-4444-86ed-777923188922";
        final String taskStageUuid2="8ee8a4f8-5c98-4444-86ed-888532188924";

        final String taskStageTypeUuid="8ee8a4f8-5c98-3124-86ed-888923188922";
        final String taskStageTypeUuid2="8ee8a4f8-5c98-4214-86ed-888923188924";
        final String taskStageTypeUuid3="8ee8a4f8-5c38-4364-86ed-888923188926";

        final String taskStageTemplateUuid="8ee8a4f8-5c98-5555-86ed-888911188922";
        final String taskStageTemplateUuid2="8ee8a4f8-5c98-5555-86ed-888922288911";

        final String taskStatusUuid="8ee8a4f8-5c98-4444-86ed-253923132922";
        final String taskVerdictUuid="8ee8a4f8-5c98-1255-86ed-887923188922";

        final String taskUuid="8ee8a4f8-5c98-4484-86ed-777923188922";
        final String taskUuid2="8ee8a4f8-5c98-1774-86ed-888532188924";

        final String taskTypeUuid="8ee8a4f8-5c18-3124-86ed-888923188922";
        final String taskTypeUuid2="8ee8a4f8-5c38-4214-86ed-888923188924";

        final String taskTemplateUuid="8ee8a4f8-5b98-5555-86ed-888911188922";
        final String taskTemplateUuid2="8ee8a4f8-5a98-5555-86ed-888922288911";


    // User --------------------
    realmDB.executeTransaction(new Realm.Transaction() {
        @Override
        public void execute(Realm realm) {
            profile = realmDB.createObject(User.class);
            profile.set_id(1);
            profile.setUuid(userTestUuid);
            profile.setName("Иванов О.А.");
            profile.setImage("profile");
            profile.setLogin("olejek8@yandex.ru");
            profile.setPass("12345");
            profile.setType(2);
            profile.setTagId("01234567");
            profile.setWhoIs("бугорчик");
            profile.setActive(true);
            profile.setContact("+79227000285 Иван");
            profile.setImage("");
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
            equipmentType = realmDB.createObject(EquipmentType.class);
            equipmentType.set_id(2);
            equipmentType.setUuid(equipmentTypeUuid);
            equipmentType.setTitle("Теплогенератор");
        }
    });
    realmDB.executeTransaction(new Realm.Transaction() {
        @Override
        public void execute(Realm realm) {
            equipmentType2 = realmDB.createObject(EquipmentType.class);
            equipmentType2.set_id(3);
            equipmentType2.setUuid(equipmentTypeUuid2);
            equipmentType2.setTitle("Котел газовый");
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
            equipmentModel.setEquipmentTypeUuid(equipmentModelUuid);
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
            equipmentModel2.setEquipmentTypeUuid(equipmentModelUuid2);
            equipmentModel2.setEquipmentType(equipmentType2);
            equipmentModel2.setUuid(equipmentModelUuid2);
            equipmentModel2.setTitle("Unical-8800");
        }
    });

    // Equipment -----------------
    realmDB.executeTransaction(new Realm.Transaction() {
        @Override
        public void execute(Realm realm) {
            equipment = realmDB.createObject(Equipment.class);
            equipment.set_id(1);
            equipment.setCriticalTypeUuid(criticalTypeUuid);
            equipment.setEquipmentModelUuid(equipmentModelUuid);
            equipment.setEquipmentStatusUuid(equipmentStatusUuid);
            equipment.setImage("teplovey.jpg");
            equipment.setInventoryNumber("IN:001212");
            equipment.setUuid(equipmentUuid);
            equipment.setTitle("Теплогенератор Тепловей-250А");
            equipment.setTagId("1234-5678-9101112");
            equipment.setUserUuid(userTestUuid);
            equipment.setLocation("Цех изоляторов ПФИ");
            equipment.setLatitude((float)55.343);
            equipment.setLongitude((float)55.234);
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
            equipment2.setCriticalTypeUuid(criticalTypeUuid);
            equipment2.setEquipmentModelUuid(equipmentModelUuid2);
            equipment2.setEquipmentStatusUuid(equipmentStatusUuid);
            equipment2.setImage("kotel.jpg");
            equipment2.setInventoryNumber("IN:001213");
            equipment2.setUuid(equipmentUuid2);
            equipment2.setTitle("Газовый котел Unical");
            equipment2.setTagId("02345567");
            equipment2.setUserUuid(userTestUuid);
            equipment2.setLocation("Котельная №3");
            equipment2.setLatitude((float)55.5311);
            equipment2.setLongitude((float)55.1222);
            equipment2.setStartDate(new Date());
            equipment2.setEquipmentModel(equipmentModel2);
            equipment2.setCriticalType(criticalType);
            equipment2.setEquipmentStatus(equipmentStatus);
        }
    });

    // Documentation -----------------
    realmDB.executeTransaction(new Realm.Transaction() {
        @Override
        public void execute(Realm realm) {
            documentation = realmDB.createObject(Documentation.class);
            documentation.set_id(1);
            documentation.setDocumentationTypeUuid(documentationTypeUuid);
            documentation.setDocumentationType(documentationType);
            documentation.setEquipmentUuid(equipmentUuid);
            documentation.setUuid(documentationUuid);
            documentation.setTitle("Паспорт на Тепловей-250/251");
            documentation.setFileName("1.pdf");
        }
    });

    realmDB.executeTransaction(new Realm.Transaction() {
        @Override
        public void execute(Realm realm) {
            documentation2 = realmDB.createObject(Documentation.class);
            documentation2.set_id(2);
            documentation2.setDocumentationTypeUuid(documentationTypeUuid2);
            documentation2.setDocumentationType(documentationType2);
            documentation2.setEquipmentUuid(equipmentUuid2);
            documentation2.setUuid(documentationUuid2);
            documentation2.setTitle("Руководство на котел GTV-40");
            documentation2.setFileName("2.pdf");
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
             orderStatus3 = realmDB.createObject(OrderStatus.class);
             orderStatus3.set_id(3);
             orderStatus3.setUuid(orderStatusUuid3);
             orderStatus3.setTitle("Получен");
             orderStatus3.setIcon("status_easy_received.png");
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
            operationStatus = realmDB.createObject(OperationStatus.class);
            operationStatus.set_id(1);
            operationStatus.setUuid(operationStatusUuid);
            operationStatus.setTitle("Не выполнена");
        }
    });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                operationStatus = realmDB.createObject(OperationStatus.class);
                operationStatus.set_id(2);
                operationStatus.setUuid(operationStatusUuid2);
                operationStatus.setTitle("Выполнена");
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
            operationTemplate.setEquipmentModelUuid(equipmentModelUuid);
            operationTemplate.setEquipmentModel(equipmentModel);
            operationTemplate.setNormative(180);
            operationTemplate.setLast_step(0);
            operationTemplate.setOperationType(operationType);
            operationTemplate.setOperationTypeUuid(operationTypeUuid);
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
            operationTemplate2.setEquipmentModelUuid(equipmentModelUuid);
            operationTemplate2.setEquipmentModel(equipmentModel);
            operationTemplate2.setNormative(100);
            operationTemplate2.setLast_step(0);
            operationTemplate2.setOperationType(operationType2);
            operationTemplate2.setOperationTypeUuid(operationTypeUuid2);
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
            operationTemplate3.setEquipmentModelUuid(equipmentModelUuid);
            operationTemplate3.setEquipmentModel(equipmentModel);
            operationTemplate3.setNormative(110);
            operationTemplate3.setLast_step(0);
            operationTemplate3.setOperationType(operationType3);
            operationTemplate3.setOperationTypeUuid(operationTypeUuid3);
        }
    });

    // Operation -----------------
    realmDB.executeTransaction(new Realm.Transaction() {
        @Override
        public void execute(Realm realm) {
            operation = realmDB.createObject(Operation.class);
            operation.set_id(1);
            operation.setUuid(operationUuid);
            operation.setOperationStatus(operationStatus);
            operation.setOperationStatusUuid(operationStatusUuid);
            operation.setEndDate(new Date());
            operation.setFlowOrder(1);
            operation.setStartDate(new Date());
            operation.setOperationVerdict(operationVerdict);
            operation.setOperationVerdictUuid(operationVerdictUuid);
            operation.setOperationTemplate(operationTemplate);
            operation.setTaskStageUuid(taskStageUuid);
        }
    });

    realmDB.executeTransaction(new Realm.Transaction() {
        @Override
        public void execute(Realm realm) {
            operation2 = realmDB.createObject(Operation.class);
            operation2.set_id(2);
            operation2.setUuid(operationUuid2);
            operation2.setOperationStatus(operationStatus);
            operation2.setOperationStatusUuid(operationStatusUuid);
            operation2.setEndDate(new Date());
            operation2.setFlowOrder(2);
            operation2.setStartDate(new Date());
            operation2.setOperationVerdict(operationVerdict);
            operation2.setOperationVerdictUuid(operationVerdictUuid);
            operation2.setOperationTemplate(operationTemplate2);
            operation2.setTaskStageUuid(taskStageUuid);
        }
    });

    realmDB.executeTransaction(new Realm.Transaction() {
        @Override
        public void execute(Realm realm) {
            operation3 = realmDB.createObject(Operation.class);
            operation3.set_id(3);
            operation3.setUuid(operationUuid3);
            operation3.setOperationStatus(operationStatus);
            operation3.setOperationStatusUuid(operationStatusUuid);
            operation3.setEndDate(new Date());
            operation3.setFlowOrder(3);
            operation3.setStartDate(new Date());
            operation3.setOperationVerdict(operationVerdict);
            operation3.setOperationVerdictUuid(operationVerdictUuid);
            operation3.setOperationTemplate(operationTemplate3);
            operation3.setTaskStageUuid(taskStageUuid);
        }
    });
    // ---------------------------------------------------------------------------------------------
    // TaskStageVerdict -----------------
    realmDB.executeTransaction(new Realm.Transaction() {
        @Override
        public void execute(Realm realm) {
            taskStageVerdict = realmDB.createObject(TaskStageVerdict.class);
            taskStageVerdict.set_id(1);
            taskStageVerdict.setUuid(taskStageVerdictUuid);
            taskStageVerdict.setTitle("Выполнен");
        }
    });

    // TaskStageType -----------------
    realmDB.executeTransaction(new Realm.Transaction() {
        @Override
        public void execute(Realm realm) {
            taskStageType = realmDB.createObject(TaskStageType.class);
            taskStageType.set_id(1);
            taskStageType.setUuid(taskStageTypeUuid);
            taskStageType.setTitle("Снятие крышки");
        }
    });

    realmDB.executeTransaction(new Realm.Transaction() {
        @Override
        public void execute(Realm realm) {
            taskStageType2 = realmDB.createObject(TaskStageType.class);
            taskStageType2.set_id(2);
            taskStageType2.setUuid(taskStageTypeUuid2);
            taskStageType2.setTitle("Демонтаж экрана");
        }
    });

    realmDB.executeTransaction(new Realm.Transaction() {
        @Override
        public void execute(Realm realm) {
            taskStageType3 = realmDB.createObject(TaskStageType.class);
            taskStageType3.set_id(3);
            taskStageType3.setUuid(taskStageTypeUuid3);
            taskStageType3.setTitle("Осмотр горелки");
        }
    });

    // TaskStageStatus -----------------
    realmDB.executeTransaction(new Realm.Transaction() {
        @Override
        public void execute(Realm realm) {
            taskStageStatus = realmDB.createObject(TaskStageStatus.class);
            taskStageStatus.set_id(1);
            taskStageStatus.setUuid(taskStageStatusUuid);
            taskStageStatus.setTitle("Не выполнен");
        }
    });

    // TaskStageTemplate -----------------
    realmDB.executeTransaction(new Realm.Transaction() {
        @Override
        public void execute(Realm realm) {
            taskStageTemplate = realmDB.createObject(TaskStageTemplate.class);
            taskStageTemplate.set_id(1);
            taskStageTemplate.setUuid(taskStageTemplateUuid);
            taskStageTemplate.setEquipmentModel(equipmentModel);
            taskStageTemplate.setTitle("Снять заднюю крышку");
            taskStageTemplate.setDescription("Открутить четыре болта по краям ключом на 12");
            taskStageTemplate.setImage("");
            taskStageTemplate.setEquipmentModelUuid(equipmentModelUuid);
            taskStageTemplate.setEquipmentModel(equipmentModel);
            taskStageTemplate.setNormative(480);
            taskStageTemplate.setTaskStageType(taskStageType);
            taskStageTemplate.setTaskStageTypeUuid(taskStageTypeUuid);
        }
    });

    realmDB.executeTransaction(new Realm.Transaction() {
        @Override
        public void execute(Realm realm) {
            taskStageTemplate2 = realmDB.createObject(TaskStageTemplate.class);
            taskStageTemplate2.set_id(2);
            taskStageTemplate2.setUuid(taskStageTemplateUuid2);
            taskStageTemplate2.setEquipmentModel(equipmentModel);
            taskStageTemplate2.setTitle("Убрать заднюю крышку");
            taskStageTemplate2.setDescription("Снять заднюю крышку и отставить ее в сторону");
            taskStageTemplate2.setImage("");
            taskStageTemplate2.setEquipmentModelUuid(equipmentModelUuid);
            taskStageTemplate2.setEquipmentModel(equipmentModel);
            taskStageTemplate2.setNormative(300);
            taskStageTemplate2.setTaskStageType(taskStageType2);
            taskStageTemplate2.setTaskStageTypeUuid(taskStageTypeUuid2);
        }
    });

    // TaskStage -----------------
    realmDB.executeTransaction(new Realm.Transaction() {
        @Override
        public void execute(Realm realm) {
            taskStage = realmDB.createObject(TaskStages.class);
            taskStage.set_id(1);
            taskStage.setUuid(taskStageUuid);
            taskStage.setTaskStageStatus(taskStageStatus);
            taskStage.setTaskStageStatusUuid(taskStageStatusUuid);
            taskStage.setEndDate(new Date());
            taskStage.setFlowOrder(1);
            taskStage.setStartDate(new Date());
            taskStage.setTaskStageVerdict(taskStageVerdict);
            taskStage.setTaskStageVerdictUuid(taskStageVerdictUuid);
            taskStage.setTaskStageTemplate(taskStageTemplate);
            taskStage.setTaskStageTemplateUuid(taskStageTemplateUuid);
            taskStage.setTaskUuid(taskUuid);
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
            taskStage2.setTaskStageStatus(taskStageStatus);
            taskStage2.setTaskStageStatusUuid(taskStageStatusUuid);
            taskStage2.setEndDate(new Date());
            taskStage2.setFlowOrder(2);
            taskStage2.setStartDate(new Date());
            taskStage2.setTaskStageVerdict(taskStageVerdict);
            taskStage2.setTaskStageVerdictUuid(taskStageVerdictUuid);
            taskStage2.setTaskStageTemplate(taskStageTemplate2);
            taskStage2.setTaskStageTemplateUuid(taskStageTemplateUuid2);
            taskStage2.setTaskUuid(taskUuid);
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
            taskTemplate.setEquipmentModelUuid(equipmentModelUuid);
            taskTemplate.setEquipmentModel(equipmentModel);
            taskTemplate.setNormative(1480);
            taskTemplate.setTaskType(taskType);
            taskTemplate.setTaskTypeUuid(taskTypeUuid);
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
            taskTemplate2.setEquipmentModelUuid(equipmentModelUuid);
            taskTemplate2.setEquipmentModel(equipmentModel);
            taskTemplate2.setNormative(7300);
            taskTemplate2.setTaskType(taskType2);
            taskTemplate2.setTaskTypeUuid(taskTypeUuid2);
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
            task.setTaskStatusUuid(taskStatusUuid);
            task.setEndDate(new Date());
            task.setStartDate(new Date());
            task.setTaskVerdict(taskVerdict);
            task.setTaskVerdictUuid(taskVerdictUuid);
            task.setTaskTemplate(taskTemplate);
            task.setTaskTemplateUuid(taskTemplateUuid);
            task.setEquipment(equipment);
            task.setEquipmentUuid(equipmentUuid);
            task.setComment("Там тепловей шумит сильно, из под него бежит и тепла нет. Следует разобраться.");
            task.setPrevCode(0);
            task.setNextCode(2);
            task.addTaskStage(taskStage);
            task.addTaskStage(taskStage2);
            task.setOrderUuid(orderUuid);
        }
    });

    realmDB.executeTransaction(new Realm.Transaction() {
        @Override
        public void execute(Realm realm) {
            task2 = realmDB.createObject(Tasks.class);
            task2.set_id(2);
            task2.setUuid(taskUuid2);
            task2.setTaskStatus(taskStatus);
            task2.setTaskStatusUuid(taskStatusUuid);
            task2.setEndDate(new Date());
            task2.setStartDate(new Date());
            task2.setTaskVerdict(taskVerdict);
            task2.setTaskVerdictUuid(taskVerdictUuid);
            task2.setTaskTemplate(taskTemplate2);
            task2.setTaskTemplateUuid(taskTemplateUuid2);
            task2.setEquipment(equipment2);
            task2.setEquipmentUuid(equipmentUuid2);
            task2.setComment("Горелка котла в котельной не горит. Требуется починить.");
            task2.setPrevCode(0);
            task2.setNextCode(1);
            task2.setOrderUuid(orderUuid);
        }
    });

    // Orders -----------------
    realmDB.executeTransaction(new Realm.Transaction() {
        @Override
        public void execute(Realm realm) {
            order = realmDB.createObject(Orders.class);
            order.set_id(1);
            order.setUuid(orderUuid);
            order.setUserUuid(userTestUuid);
            order.setOrderLevel(orderLevel);
            order.setAttemptCount(0);
            order.setAttemptSendDate(new Date());
            order.setAuthorUuid(userTestUuid);
            order.setCloseDate(new Date());
            order.setStartDate(new Date());
            order.setOpenDate(new Date());
            order.setOrderStatusUuid(orderStatusUuid);
            order.setOrderStatus(orderStatus);
            order.setOrderVerdictUuid(orderVerdictUuid);
            order.setOrderVerdict(orderVerdict);
            order.setTitle("Осмотр камер наблюдения");
            order.addTask(task);
            order.addTask(task2);
        }
    });

    realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
            order2 = realmDB.createObject(Orders.class);
            order2.set_id(2);
            order2.setUserUuid(userTestUuid);
            order2.setUuid(orderUuid2);
            order2.setOrderLevel(orderLevel2);
            order2.setAttemptCount(0);
            order2.setAttemptSendDate(new Date());
            order2.setAuthorUuid(userTestUuid);
            order2.setCloseDate(new Date());
            order2.setStartDate(new Date());
            order2.setOpenDate(new Date());
            order2.setOrderStatusUuid(orderStatusUuid2);
            order2.setOrderStatus(orderStatus2);
            order2.setOrderVerdictUuid(orderVerdictUuid);
            order2.setOrderVerdict(orderVerdict);
            order2.setTitle("Ремонтные работы по котельным");
            order2.addTask(task);
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                order = realmDB.createObject(Orders.class);
                order.set_id(3);
                order.setUuid(orderUuid3);
                order.setUserUuid(userTestUuid);
                order.setOrderLevel(orderLevel3);
                order.setAttemptCount(0);
                order.setAttemptSendDate(new Date());
                order.setAuthorUuid(userTestUuid);
                order.setCloseDate(new Date());
                order.setReceiveDate(new Date());
                order.setStartDate(new Date());
                order.setOpenDate(new Date());
                order.setOrderStatusUuid(orderStatusUuid3);
                order.setOrderStatus(orderStatus3);
                order.setOrderVerdictUuid(orderVerdictUuid);
                order.setOrderVerdict(orderVerdict);
                order.setTitle("Демонтаж устаревшего оборудования");
                order.addTask(task);
            }
        });

 }

}
