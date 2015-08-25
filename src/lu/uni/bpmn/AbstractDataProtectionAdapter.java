package lu.uni.bpmn;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.Task;
import org.eclipse.bpmn2.modeler.core.model.ModelDecorator;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EStructuralFeature;

abstract public class AbstractDataProtectionAdapter extends AdapterImpl {
	public final static int DEFAULT_ACTIVITY_ID = 10;

	static Logger logger = Logger.getLogger(DataProtectionBPMNPlugin.class.getName());

	void suggestNextActivityId(Event currentEvent, Task task) {
		// now test if the id is valid or suggest a new one...
		EStructuralFeature feature = ModelDecorator.getAnyAttribute(
				currentEvent, "activityid");
		if (feature != null) {

			List<Event> dataProtectionEvents = new ArrayList<Event>();
			new Tracer().findDataProtectionTargetEvents(task, dataProtectionEvents);
			new Tracer().findDataProtectionStartEvents(task, dataProtectionEvents);

			logger.fine("found " + dataProtectionEvents.size() + " Imxis Events");

			Integer currentActivityID = (Integer) currentEvent.eGet(feature);
			int bestID = -1;
			boolean duplicateID = false;
			for (Event aEvent : dataProtectionEvents) {

				if (aEvent == currentEvent)
					continue;

				int aID = (Integer) aEvent.eGet(feature);
				if (aID > bestID)
					bestID = aID;

				// test for dupplicates!
				if (aID == currentActivityID) {
					duplicateID = true;
				}

			}

			// if duplicate or currentID<=0 suggest a new one!
			if (duplicateID || currentActivityID <= 0) {
				if (bestID <= 0)
					currentActivityID = DEFAULT_ACTIVITY_ID;
				else
					currentActivityID = bestID + 10;
			}
			// suggest a new ProcessID...
			logger.fine("ActiviytID=" + currentActivityID);
			currentEvent.eSet(feature, currentActivityID);

		}
	}
}
