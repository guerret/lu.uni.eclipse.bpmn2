package lu.uni.bpmn.ui.dataprotection;

import java.util.Iterator;

import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractDetailComposite;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.ObjectEditor;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import lu.uni.bpmn.DataProtectionBPMNPlugin;
import lu.uni.bpmn.model.Item;
import lu.uni.bpmn.model.ModelFactory;
import lu.uni.bpmn.model.Value;
import lu.uni.dapreco.parser.PrOntoParser;
import lu.uni.dapreco.parser.akn.AKNParser;
import lu.uni.dapreco.parser.lrml.LRMLParser;
import lu.uni.dapreco.parser.lrml.LRMLParser.RuleType;

public class OWLListEditor extends ObjectEditor {
	protected Composite editorComposite;
	// protected ValueListAdapter valueListAdapter;
	Image image;
	boolean sortable = false;
	Table table;
	Item item = null;
	Combo combo;
	Table table2;
	Table table3;

	private final static String resDir = "resources";

	private static final String aknPrefix = "GDPR";
	private final static String ontoPrefix = "prOnto";

	private final static String aknName = "akn-act-gdpr-full.xml";
	private static final String aknURI = "https://raw.githubusercontent.com/guerret/lu.uni.dapreco.parser/master/"
			+ resDir + "/" + aknName;

	private final static String lrmlName = "rioKB_GDPR.xml";
	private final static String lrmlURI = "https://raw.githubusercontent.com/dapreco/daprecokb/master/gdpr/" + lrmlName;

	private static PrOntoParser pParser;
	private static LRMLParser lParser;
	private static AKNParser aParser;

	/**
	 * Initialize the default values...
	 * 
	 * @param businessObject
	 * @param feature
	 */
	public OWLListEditor(AbstractDetailComposite parent, Item item) {
		super(parent, item, DataProtectionBPMNPlugin.DATAPROTECTION_ITEMLIST_FEATURE);

		this.item = item;
		pParser = new PrOntoParser();
		lParser = new LRMLParser(lrmlURI);
		aParser = new AKNParser(aknURI);
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public boolean isSortable() {
		return sortable;
	}

	public void setSortable(boolean sortable) {
		this.sortable = sortable;
	}

	protected Control createControl(Composite parentcomposite, String label, int style) {

		if (label != null) {
			Label labelWidget = getToolkit().createLabel(parentcomposite, label);
			labelWidget.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
			updateLabelDecorator();
		}

		// == editor composite
		editorComposite = new Composite(parentcomposite, SWT.NONE);
		GridData data = new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1);
		editorComposite.setLayoutData(data);
		editorComposite.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_LIST_BACKGROUND));

		GridLayout gridlayout = new GridLayout();
		gridlayout.numColumns = 2;
		editorComposite.setLayout(gridlayout);

		// == Table composite
		table = getToolkit().createTable(editorComposite, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		GridData tableGridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		table.setLayoutData(tableGridData);

		// add current values
		this.updateTable();

		// == Button composite
		Composite compositeButtons = getToolkit().createComposite(editorComposite, SWT.NONE);
		FillLayout fillLayoutButtons = new FillLayout();
		fillLayoutButtons.spacing = 2;
		fillLayoutButtons.type = SWT.VERTICAL;
		compositeButtons.setLayout(fillLayoutButtons);

		getToolkit().createLabel(editorComposite, "Processing type");
		getToolkit().createLabel(editorComposite, "");

		combo = new Combo(editorComposite, SWT.NONE);
		combo.setItems(pParser.getActions()); // the items must come from PrOnto
		combo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				writeProvisions(combo.getText());
				writeFormulae(combo.getText());
			}
		});
		combo.select(0);
		getToolkit().createLabel(editorComposite, "");

		table2 = getToolkit().createTable(editorComposite, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		GridData table2GridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		table2.setLayoutData(table2GridData);
		writeProvisions(combo.getText());
		getToolkit().createLabel(editorComposite, "");

		table3 = getToolkit().createTable(editorComposite, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		GridData table3GridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		table3.setLayoutData(table3GridData);
		writeFormulae(combo.getText());
		getToolkit().createLabel(editorComposite, "");

		final Shell shell = parent.getShell();

		Button button = getToolkit().createButton(compositeButtons, "Add...", SWT.PUSH);
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				OWLInputDialog inputDlg = new OWLInputDialog(shell, "Add Entry", "");
				if (inputDlg.open() == InputDialog.OK) {
					for (String s : inputDlg.getResult())
						addValue(s);
					updateTable();
				}
			}
		});

		// Remove Button
		button = getToolkit().createButton(compositeButtons, "Remove", SWT.PUSH);
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int[] iCurrents = table.getSelectionIndices();
				for (int i : iCurrents) {
					String sCurrent = table.getItems()[i].getText();
					removeValue(sCurrent);
					updateTable();
				}

			}
		});

		button = getToolkit().createButton(compositeButtons, "Edit", SWT.PUSH);
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int iCurrent = table.getSelectionIndex();
				if (iCurrent >= 0) {
					TableItem tableItem = table.getItem(iCurrent);
					String sOldValue = tableItem.getText();
					OWLInputDialog inputDlg = new OWLInputDialog(shell, "Edit Entry", sOldValue);
					if (inputDlg.open() == InputDialog.OK) {
						String sNewValue = inputDlg.getResult()[0];
						replaceValue(sOldValue, sNewValue);
						updateTable();
					}
				}
			}
		});

		if (sortable) {
			// Move Up Button
			button = getToolkit().createButton(compositeButtons, "Up", SWT.PUSH);
			button.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					int iCurrent = table.getSelectionIndex();
					if (iCurrent > 0) {
						moveUp(iCurrent);
						updateTable();
						table.select(iCurrent - 1);
					}
				}
			});

			// Move Down Button
			button = getToolkit().createButton(compositeButtons, "Down", SWT.PUSH);
			button.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					int iCurrent = table.getSelectionIndex();
					if (iCurrent < item.getValuelist().size() - 1) {
						moveDown(iCurrent);

						updateTable();
						table.select(iCurrent + 1);
					}
				}
			});
		}
		return editorComposite;
	}

	public void addValue(final String newvalue) {
		TransactionalEditingDomain domain = getDiagramEditor().getEditingDomain();
		domain.getCommandStack().execute(new RecordingCommand(domain) {
			@Override
			protected void doExecute() {

				// insert a new value element
				Value value = ModelFactory.eINSTANCE.createValue();
				value.setValue(newvalue);
				item.getValuelist().add(value);

			}
		});
	}

	public void removeValue(final String sCurrent) {
		TransactionalEditingDomain domain = getDiagramEditor().getEditingDomain();
		domain.getCommandStack().execute(new RecordingCommand(domain) {
			@Override
			protected void doExecute() {

				// iterate over all item values
				Iterator<Value> iter = item.getValuelist().iterator();
				while (iter.hasNext()) {

					Value val = iter.next();
					if (sCurrent.equals(val.getValue())) {
						item.getValuelist().remove(val);
						break;
					}
				}
			}
		});
	}

	public void replaceValue(final String sold, final String snew) {
		TransactionalEditingDomain domain = getDiagramEditor().getEditingDomain();
		domain.getCommandStack().execute(new RecordingCommand(domain) {
			@Override
			protected void doExecute() {

				// find position
				// iterate over all item values
				Iterator<Value> iter = item.getValuelist().iterator();
				while (iter.hasNext()) {
					Value val = iter.next();
					if (sold.equals(val.getValue())) {

						val.setValue(snew);
						// item.getValuelist().remove(val);
						break;
					}
				}

			}
		});
	}

	/**
	 * moves a given value up inside the value list
	 * 
	 * @param value
	 */
	public void moveUp(final int i) {
		if (i < 1)
			return;
		TransactionalEditingDomain domain = getDiagramEditor().getEditingDomain();
		domain.getCommandStack().execute(new RecordingCommand(domain) {
			@Override
			protected void doExecute() {
				String value1 = item.getValuelist().get(i).getValue();
				String value2 = item.getValuelist().get(i - 1).getValue();
				item.getValuelist().get(i).setValue(value2);
				item.getValuelist().get(i - 1).setValue(value1);
			}
		});

	}

	/**
	 * moves a given value down inside the value list
	 * 
	 * @param value
	 */
	public void moveDown(final int i) {

		if (i < item.getValuelist().size() - 1) {
			TransactionalEditingDomain domain = getDiagramEditor().getEditingDomain();
			domain.getCommandStack().execute(new RecordingCommand(domain) {
				@Override
				protected void doExecute() {
					String value1 = item.getValuelist().get(i).getValue();
					String value2 = item.getValuelist().get(i + 1).getValue();
					item.getValuelist().get(i).setValue(value2);
					item.getValuelist().get(i + 1).setValue(value1);
				}
			});
		}
	}

	private void updateTable() {

		table.removeAll();
		// add current values
		for (Value avalue : item.getValuelist()) {
			TableItem tabelItem = new TableItem(table, SWT.NONE);
			tabelItem.setText(avalue.getValue());
			if (image != null) {
				tabelItem.setImage(image);
			}
		}

	}

	@Override
	public Item getValue() {
		return item;
	}

	@Override
	public void notifyChanged(Notification notification) {
		super.notifyChanged(notification);
	}

	public void setVisible(boolean visible) {
		super.setVisible(visible);
		editorComposite.setVisible(visible);
		GridData data = (GridData) editorComposite.getLayoutData();
		data.exclude = !visible;
	}

	public void dispose() {
		super.dispose();
		if (editorComposite != null && !editorComposite.isDisposed()) {
			editorComposite.dispose();
			editorComposite = null;
		}
	}

	public Control getControl() {
		return editorComposite;
	}

	private void writeProvisions(String text) {
		table.removeAll();
		String[] articles = lParser.findArticles(ontoPrefix + ":" + text);
		for (String s : articles) {
			s = s.substring((aknPrefix + ":").length());
			TableItem item = new TableItem(table2, SWT.NONE);
			item.setText(aParser.getTextFromEId(s));
		}
	}

	private void writeFormulae(String text) {
		table.removeAll();
		String[] articles = lParser.findArticles(ontoPrefix + ":" + text);
		for (String s : articles) {
			String[] formulae = lParser.findFormulaeForArticle(s, RuleType.ALL);
			for (String f : formulae) {
				TableItem item = new TableItem(table3, SWT.NONE);
				item.setText(f);
			}
		}
	}

}