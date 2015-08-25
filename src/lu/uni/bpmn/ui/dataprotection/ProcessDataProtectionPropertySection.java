package lu.uni.bpmn.ui.dataprotection;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractBpmn2PropertySection;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractDetailComposite;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.widgets.Composite;

import lu.uni.bpmn.DataProtectionBPMNPlugin;
import lu.uni.bpmn.model.Item;
import lu.uni.bpmn.ui.DataProtectionDetailComposite;
import lu.uni.bpmn.ui.task.AbstractProcessPropertySection;

public class ProcessDataProtectionPropertySection extends AbstractProcessPropertySection {

	public final static String REPLACE_PROPERTY_TAB_ID = "org.eclipse.bpmn2.modeler.activity.io.tab";

	@Override
	protected AbstractDetailComposite createSectionRoot() {
		return new SummaryDetailComposite(this);
	}

	@Override
	public AbstractDetailComposite createSectionRoot(Composite parent, int style) {
		return new SummaryDetailComposite(parent, style);
	}

	public class SummaryDetailComposite extends DataProtectionDetailComposite {

		public SummaryDetailComposite(AbstractBpmn2PropertySection section) {
			super(section);
		}

		public SummaryDetailComposite(Composite parent, int style) {
			super(parent, style);
		}

		@Override
		public void createBindings(EObject be) {
			setTitle("Workflow");

			// ProcessID
			this.bindAttribute(attributesComposite, be, "processid");

			/*
			 * // Summary Value itemValue =
			 * DataProtectionBPMNPlugin.getItemValueByName((BaseElement) be,
			 * "txtworkflowsummary", null, ""); TextObjectEditor valueEditor =
			 * new TextObjectEditor(this, itemValue,
			 * DataProtectionBPMNPlugin.DATAPROTECTION_ITEMVALUE);
			 * valueEditor.createControl(attributesComposite, "Summary");
			 * 
			 * // Abstract itemValue =
			 * DataProtectionBPMNPlugin.getItemValueByName((BaseElement) be,
			 * "txtworkflowabstract", "CDATA", ""); valueEditor = new
			 * TextObjectEditor(this, itemValue,
			 * DataProtectionBPMNPlugin.DATAPROTECTION_ITEMVALUE); valueEditor.setMultiLine(true);
			 * valueEditor.createControl(attributesComposite, "Abstract");
			 */
			// Roles
			Item item = DataProtectionBPMNPlugin.getItemByName((BaseElement) be, "$readaccess", null);
			OWLListEditor pluginEditor = new OWLListEditor(this, item);

			pluginEditor.setImage(DataProtectionBPMNPlugin.getDefault().getIcon("activity.png"));
			pluginEditor.createControl(attributesComposite, "Data protection activities");
		}

	}

}
