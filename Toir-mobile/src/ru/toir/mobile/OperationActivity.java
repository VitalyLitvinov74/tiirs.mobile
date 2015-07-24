/**
 * 
 */
package ru.toir.mobile;

import java.util.ArrayList;
import ru.toir.mobile.db.adapters.EquipmentOperationDBAdapter;
import ru.toir.mobile.db.adapters.OperationPatternDBAdapter;
import ru.toir.mobile.db.adapters.OperationPatternStepDBAdapter;
import ru.toir.mobile.db.adapters.OperationPatternStepResultDBAdapter;
import ru.toir.mobile.db.adapters.OperationResultDBAdapter;
import ru.toir.mobile.db.tables.EquipmentOperation;
import ru.toir.mobile.db.tables.OperationPattern;
import ru.toir.mobile.db.tables.OperationPatternStep;
import ru.toir.mobile.db.tables.OperationPatternStepResult;
import ru.toir.mobile.db.tables.OperationResult;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * @author Dmitriy Logachov
 *
 */
public class OperationActivity extends Activity {
	public static final String OPERATION_UUID_EXTRA =  "operation_uuid";
	private EquipmentOperation operation;
	private OperationPattern pattern;
	private ArrayList<OperationPatternStep> patternSteps;
	private ArrayList<OperationPatternStepResult> stepsResults;
	private ArrayList<OperationResult> operationResults;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.operation_layout);
		
		Intent intent = getIntent();
		String operationUuid = intent.getStringExtra(OPERATION_UUID_EXTRA);
		Log.d("test", operationUuid);
		
		// получаем операцию
		EquipmentOperationDBAdapter operationDBAdapter = new EquipmentOperationDBAdapter(new TOiRDatabaseContext(getApplicationContext())).open();
		operation = operationDBAdapter.getItem(operationUuid);
		operationDBAdapter.close();
		
		// получаем шаблон операции
		OperationPatternDBAdapter patternDBAdapter = new OperationPatternDBAdapter(new TOiRDatabaseContext(getApplicationContext())).open();
		pattern = patternDBAdapter.getItem(operation.getOperation_pattern_uuid());
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
		operationResults = resultDBAdapter.getItems(operation.getOperation_type_uuid());
		resultDBAdapter.close();
		ShowFirstStep();

	}
	
	private void ShowNextStep(String step_uuid) {
		RelativeLayout layout = (RelativeLayout)findViewById(R.id.resultButtonLayout);
		OperationPatternStep step = getStep(step_uuid);
		RelativeLayout.LayoutParams param;
		
		TextView stepTitle = (TextView)findViewById(R.id.stepTitle);
		stepTitle.setText(step.getDescription());
		
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
			// TODO реализовать выравнивание кнопок результатов относительно друг друга
			/*
			param = new RelativeLayout.LayoutParams(null);
			param.addRule(RelativeLayout.LEFT_OF, prev_button_id);
			resultButton.setLayoutParams(param);
			*/
			layout.addView(resultButton);
		}
	}
	
	private void ShowFinalStep() {
		RelativeLayout layout = (RelativeLayout)findViewById(R.id.resultButtonLayout);
		TextView stepTitle = (TextView)findViewById(R.id.stepTitle);
		stepTitle.setText("Завершение операции");
		
		Button resultButton = new Button(getApplicationContext());
		resultButton.setText("Завершить операцию");
		resultButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO реализовать сохранение результата выполнения операции
				Spinner spinner = (Spinner)((RelativeLayout)v.getParent()).findViewWithTag("result");
				OperationResult result = (OperationResult)spinner.getSelectedItem();
				Log.d("test", result.getTitle());
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

		// TODO разобраться как вывести кнопку под выпадающим списком 
		RelativeLayout.LayoutParams params = (LayoutParams) resultButton.getLayoutParams();
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, -1);
		resultButton.setLayoutParams(params);
		
	}
	
	private void ShowFirstStep() {
		RelativeLayout layout = (RelativeLayout)findViewById(R.id.resultButtonLayout);
		// получаем первый шаг операции
		OperationPatternStep firstStep = getFirstStep();
		TextView stepTitle = (TextView)findViewById(R.id.stepTitle);
		stepTitle.setText(firstStep.getDescription());
		
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
