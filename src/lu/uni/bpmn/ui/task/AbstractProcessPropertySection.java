package lu.uni.bpmn.ui.task;

import org.eclipse.bpmn2.modeler.core.merrimac.clad.DefaultPropertySection;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;

import lu.uni.bpmn.DataProtectionBPMNPlugin;

public class AbstractProcessPropertySection extends DefaultPropertySection {

	@Override
	public boolean doReplaceTab(String id, IWorkbenchPart part,
			ISelection selection) {

		EObject businessObject = BusinessObjectUtil
				.getBusinessObjectForSelection(selection);

		if (DataProtectionBPMNPlugin.isDataProtectionTask(businessObject)) {
			return true;
		}

		return false;
	}
}
