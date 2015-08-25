package lu.uni.bpmn;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.Task;

public class Tracer {

	List<FlowNode> loopFlowCache = null;

	public Tracer() {
		super();
		loopFlowCache = new ArrayList<FlowNode>();
	}

	Event findDataProtectionSourceEvent(SequenceFlow flow) {
		FlowNode sourceRef = flow.getSourceRef();

		if (sourceRef == null) {
			return null;
		}

		// detect loops...
		if (loopFlowCache.contains(sourceRef)) {
			// loop!
			return null;
		} else {
			loopFlowCache.add(sourceRef);
		}

		List<SequenceFlow> refList = sourceRef.getIncoming();
		for (SequenceFlow aflow : refList) {
			return (findDataProtectionSourceEvent(aflow));
		}
		return null;
	}

	Task findDataProtectionSourceTask(SequenceFlow flow) {
		FlowNode sourceRef = flow.getSourceRef();

		if (sourceRef == null) {
			return null;
		}

		// detect loops...
		if (loopFlowCache.contains(sourceRef)) {
			// loop!
			return null;
		} else {
			loopFlowCache.add(sourceRef);
		}

		if (DataProtectionBPMNPlugin.isDataProtectionTask(sourceRef)) {
			return (Task) sourceRef;
		}

		List<SequenceFlow> refList = sourceRef.getIncoming();
		for (SequenceFlow aflow : refList) {
			return (findDataProtectionSourceTask(aflow));
		}
		return null;
	}

	void findDataProtectionTargetEvents(FlowNode sourceRef, List<Event> resultList) {

		if (resultList == null)
			resultList = new ArrayList<Event>();

		if (sourceRef == null) {
			return;
		}

		// detect loops...
		if (loopFlowCache.contains(sourceRef)) {
			// loop!
			return;
		} else {
			loopFlowCache.add(sourceRef);
		}

		// check all outgoing flows....
		List<SequenceFlow> refList = sourceRef.getOutgoing();
		for (SequenceFlow aflow : refList) {

			FlowNode targetRef = aflow.getTargetRef();

			if (targetRef == null) {
				// stop
				return;
			}

			if (DataProtectionBPMNPlugin.isDataProtectionTask(targetRef)) {
				return;
			}

			// recursive call
			findDataProtectionTargetEvents(targetRef, resultList);
		}

	}

	void findDataProtectionStartEvents(FlowNode targetRef, List<Event> resultList) {

		if (resultList == null)
			resultList = new ArrayList<Event>();

		if (targetRef == null) {
			return;
		}

		// detect loops...
		if (loopFlowCache.contains(targetRef)) {
			// loop!
			return;
		} else {
			loopFlowCache.add(targetRef);
		}

		// check all incomming flows....
		List<SequenceFlow> refList = targetRef.getIncoming();
		for (SequenceFlow aflow : refList) {

			FlowNode sourceRef = aflow.getSourceRef();

			if (sourceRef == null) {
				// stop
				return;
			}

			if (DataProtectionBPMNPlugin.isDataProtectionTask(sourceRef)) {
				return;
			}

			// recursive call
			findDataProtectionStartEvents(sourceRef, resultList);
		}

	}

}
