/**
 * 
 */
package ru.toir.mobile;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import ru.toir.mobile.db.adapters.EquipmentDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentOperationDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentOperationResultDBAdapter;
import ru.toir.mobile.db.adapters.OperationPatternDBAdapter;
import ru.toir.mobile.db.adapters.OperationPatternStepDBAdapter;
import ru.toir.mobile.db.adapters.OperationPatternStepResultDBAdapter;
import ru.toir.mobile.db.adapters.OperationResultDBAdapter;
import ru.toir.mobile.db.adapters.TaskDBAdapter;
import ru.toir.mobile.db.adapters.TaskStatusDBAdapter;
import ru.toir.mobile.db.tables.EquipmentOperation;
import ru.toir.mobile.db.tables.EquipmentOperationResult;
import ru.toir.mobile.db.tables.OperationPattern;
import ru.toir.mobile.db.tables.OperationPatternStep;
import ru.toir.mobile.db.tables.OperationPatternStepResult;
import ru.toir.mobile.db.tables.OperationResult;
import ru.toir.mobile.db.tables.Task;
import android.app.Activity;
//import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.RelativeLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Dmitriy Logachov
 *
 */
public class OperationActivity extends Activity {
	public static final String OPERATION_UUID_EXTRA =  "operation_uuid";
	public static final String TASK_UUID_EXTRA =  "task_uuid";
	public static final String EQUIPMENT_UUID_EXTRA =  "equipment_uuid";
	public static final String EQUIPMENT_TAG_EXTRA =  "equipment_tag";
	//private EquipmentOperation operation;
	private OperationPattern pattern;
	private ArrayList<OperationPatternStep> patternSteps;
	private ArrayList<OperationPatternStepResult> stepsResults;
	private ArrayList<OperationResult> operationResults;
	private String task_uuid, operation_uuid, equipment_uuid;
	private String taskname="";
	private String operationname="";
	private LinearLayout layout;
	private TextView stepTitle;
	private TextView stepDescrition;
	private Button numStepButton;		

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.operation_layout);
		
        Bundle b = getIntent().getExtras();
        operation_uuid = b.getString(OPERATION_UUID_EXTRA); 
        task_uuid = b.getString(TASK_UUID_EXTRA); 
        equipment_uuid = b.getString(EQUIPMENT_UUID_EXTRA); 
        setContentView(R.layout.taskwork_fragment);

    	layout = (LinearLayout)findViewById(R.id.resultButtonLayout);
    	stepTitle = (TextView)findViewById(R.id.stepTitle);
    	stepDescrition = (TextView)findViewById(R.id.step_description);
    	numStepButton = (Button)findViewById(R.id.button1);		

        // получаем статус и время наряда
	 	TaskDBAdapter dbTask = new TaskDBAdapter(new TOiRDatabaseContext(getApplicationContext())).open();
	 	Task task = dbTask.getItem(task_uuid);
	 	TaskStatusDBAdapter taskStatusDBAdapter = new TaskStatusDBAdapter(new TOiRDatabaseContext(getApplicationContext())).open();
		taskname = "Наряд: " + dbTask.getCreateTimeByUUID(task_uuid) + " / Статус: " + taskStatusDBAdapter.getNameByUUID(task.getTask_status_uuid());

        // получаем тип операции
		//OperationTypeDBAdapter operationTypeDBAdapter = new OperationTypeDBAdapter(new TOiRDatabaseContext(getApplicationContext())).open();
		EquipmentDBAdapter eqDBAdapter = new EquipmentDBAdapter(new TOiRDatabaseContext(getApplicationContext())).open();
        EquipmentOperationDBAdapter operationDBAdapter = new EquipmentOperationDBAdapter(new TOiRDatabaseContext(getApplicationContext())).open();
		EquipmentOperation equipmentOperation = operationDBAdapter.getItem(operation_uuid);
        		
		// получаем шаблон операции
		OperationPatternDBAdapter patternDBAdapter = new OperationPatternDBAdapter(new TOiRDatabaseContext(getApplicationContext())).open();
		pattern = patternDBAdapter.getItem(equipmentOperation.getOperation_pattern_uuid());		
        operationname = "Оборудование: " + eqDBAdapter.getEquipsNameByUUID(equipment_uuid) + " / Операция: " + pattern.getTitle(); 						
		operationDBAdapter.close();		
		patternDBAdapter.close();
		
		// получаем шаги шаблона операции
		OperationPatternStepDBAdapter patternStepDBAdapter = new OperationPatternStepDBAdapter(new TOiRDatabaseContext(getApplicationContext())).open();
		patternSteps = patternStepDBAdapter.getItems(pattern.getUuid());
		patternDBAdapter.close();
		
		// получаем варианты выполнения шагов
		ArrayList <String> uuids = new ArrayList<String>();
		for (OperationPatternStep step: patternSteps) {
			uuids.add(step.getUuid());
		}
		OperationPatternStepResultDBAdapter stepResultDBAdapter = new OperationPatternStepResultDBAdapter(new TOiRDatabaseContext(getApplicationContext())).open();
		stepsResults = stepResultDBAdapter.getItems(uuids);
		stepResultDBAdapter.close();
		
		// получаем список вариантов завершения операции
		OperationResultDBAdapter resultDBAdapter = new OperationResultDBAdapter(new TOiRDatabaseContext(getApplicationContext())).open();
		operationResults = resultDBAdapter.getItems(equipmentOperation.getOperation_type_uuid());
		resultDBAdapter.close();

		TextView taskName = (TextView)findViewById(R.id.textView1);
		taskName.setText(taskname);
		TextView operationName = (TextView)findViewById(R.id.textView2);
		operationName.setText(operationname);
		
		// cоздаём запись с результатом выполнения операции для фиксации времени начала выполнения
		EquipmentOperationResult operationResult = new EquipmentOperationResult();
		UUID uuid = UUID.randomUUID();
		operationResult.setUuid(uuid.toString());
		operationResult.setEquipment_operation_uuid(operation_uuid);
		operationResult.setStart_date(new Date().getTime() / 1000);
		EquipmentOperationResultDBAdapter equipmentOperationResultDBAdapter = new EquipmentOperationResultDBAdapter(new TOiRDatabaseContext(getApplicationContext())).open();
		equipmentOperationResultDBAdapter.replace(operationResult);
		equipmentOperationResultDBAdapter.close();
		
		ShowFirstStep();
	}
	
	private void ShowNextStep(String step_uuid) {
		OperationPatternStep step = getStep(step_uuid);
		if (step == null) 
			{
			 Toast.makeText(this, "Шаг с UUID: " + step_uuid + " не найден", Toast.LENGTH_SHORT).show();
			 return;
			}
		//RelativeLayout.LayoutParams param;		
		stepTitle.setText(step.getName());
		stepDescrition.setText(step.getDescription());
		numStepButton.setText(step.get_id() + "");		
		layout.removeAllViewsInLayout();
		
		// получаем список результатов шагов
		ArrayList<OperationPatternStepResult> resultsList = getStepResult(step.getUuid());
		for (OperationPatternStepResult result: resultsList) {
			Button resultButton = new Button(getApplicationContext());
			final String next_step_uuid = result.getNext_operation_pattern_step_uuid();
			resultButton.setText(result.getTitle());
			if (result.getNext_operation_pattern_step_uuid().equals("00000000-0000-0000-0000-000000000000")) {
				resultButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						ShowFinalStep();
					}
				});
			} else {
				resultButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						ShowNextStep(next_step_uuid);
					}
				});
			}

			layout.addView(resultButton);
		}
	}
	
	private void ShowFinalStep() {
		Button resultButton = new Button(getApplicationContext());
		resultButton.setText("Завершить операцию");
		resultButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// сохраняем результат выполнения операции, фиксируем время окончания, статус выполнения
				Spinner spinner = (Spinner)((LinearLayout)v.getParent()).findViewWithTag("result");
				OperationResult result = (OperationResult)spinner.getSelectedItem();
				Log.d("test", result.getTitle());
				EquipmentOperationResult operationResult = null;
				EquipmentOperationResultDBAdapter equipmentOperationResultDBAdapter = new EquipmentOperationResultDBAdapter(new TOiRDatabaseContext(getApplicationContext())).open();
				operationResult = equipmentOperationResultDBAdapter.getItemByOperation(operation_uuid);
				operationResult.setOperation_result_uuid(result.getUuid());
				operationResult.setEnd_date(new Date().getTime() / 1000);
				equipmentOperationResultDBAdapter.replace(operationResult);
				equipmentOperationResultDBAdapter.close();
				finish();
			}
		});
		
		ArrayAdapter<OperationResult> resultsAdapter = new ArrayAdapter<OperationResult>(this, android.R.layout.simple_spinner_item, operationResults); 
		Spinner spinner = new Spinner(getApplicationContext());
		spinner.setAdapter(resultsAdapter);
		spinner.setTag("result");
		
		layout.removeAllViewsInLayout();
		layout.addView(spinner);
		layout.addView(resultButton);
	}
	
	private void ShowFirstStep() {
		// получаем первый шаг операции
		OperationPatternStep firstStep = getFirstStep();
		if (firstStep == null) 
		{
		 Toast.makeText(this, "Первый шаг не найден", Toast.LENGTH_SHORT).show();
		 return;
		}

		stepTitle.setText(firstStep.getName());
		//firstStep.getImage();		
		stepDescrition.setText(firstStep.getDescription());
		numStepButton.setText(firstStep.get_id() + "");
		
		// получаем список результатов шагов
		ArrayList<OperationPatternStepResult> resultsList = getStepResult(firstStep.getUuid());
		for (OperationPatternStepResult result: resultsList) {
			Button resultButton = new Button(getApplicationContext());
			final String next_step_uuid = result.getNext_operation_pattern_step_uuid();
			resultButton.setText(result.getTitle());
			resultButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ShowNextStep(next_step_uuid);
				}
			});
			layout.addView(resultButton);
		}
		
	}
	
	private OperationPatternStep getFirstStep() {
		for(OperationPatternStep step: patternSteps) {
			if (step.isFirst_step()) {
				return step;
			}
		}
		return null;
	}
	
	private OperationPatternStep getStep(String uuid) {
		for(OperationPatternStep step: patternSteps) {
			if (uuid.equals(step.getUuid())) {
				return step;
			}
		}
		return null;
	}
	
	private ArrayList<OperationPatternStepResult> getStepResult(String step_uuid) {
		ArrayList<OperationPatternStepResult> resultsList = new ArrayList<OperationPatternStepResult>();
		for (OperationPatternStepResult result: stepsResults) {
			if (step_uuid.equals(result.getOperation_pattern_step_uuid())) {
				resultsList.add(result);
			}
		}
		return resultsList;
	}
}
