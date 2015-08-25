package lu.uni.bpmn.ui;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractBpmn2PropertySection;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractDetailComposite;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.TextObjectEditor;
import org.eclipse.swt.widgets.Composite;

import lu.uni.bpmn.DataProtectionBPMNPlugin;
import lu.uni.bpmn.model.Value;

public abstract class DataProtectionDetailComposite extends AbstractDetailComposite {

	public DataProtectionDetailComposite(AbstractBpmn2PropertySection section) {
		super(section);
	}

	public DataProtectionDetailComposite(Composite parent, int style) {
		super(parent, style);

	}

	/**
	 * This method binds a item EObject by a given name to a TextEditor
	 * component.
	 * 
	 * @param be
	 * @param itemName
	 * @param defaultValue
	 * @param label
	 */
	public void bindTextEditor(Composite parent, BaseElement be, String itemName, String defaultValue, String label) {

		Value value = DataProtectionBPMNPlugin.getItemValueByName(be, itemName, null, defaultValue);
		TextObjectEditor valueEditor = new TextObjectEditor(this, value,
				DataProtectionBPMNPlugin.DATAPROTECTION_ITEMVALUE);
		valueEditor.createControl(parent, label);
	}

}
