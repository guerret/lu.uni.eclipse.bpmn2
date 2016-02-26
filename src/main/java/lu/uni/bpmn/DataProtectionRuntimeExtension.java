package lu.uni.bpmn;

import org.eclipse.bpmn2.modeler.core.IBpmn2RuntimeExtension;
import org.eclipse.bpmn2.modeler.core.LifecycleEvent;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil.Bpmn2DiagramType;
import org.eclipse.bpmn2.modeler.ui.AbstractBpmn2RuntimeExtension.RootElementParser;
import org.eclipse.bpmn2.modeler.ui.wizards.FileService;
import org.eclipse.ui.IEditorInput;
import org.xml.sax.InputSource;

public class DataProtectionRuntimeExtension implements IBpmn2RuntimeExtension {

	public static final String RUNTIME_ID = "lu.uni.dataprotection.bpmn.runtime";

	public static final String targetNamespace = "http://www.uni.lu/bpmn2";

	@Override
	public String getTargetNamespace(Bpmn2DiagramType diagramType) {
		return targetNamespace;
	}

	/**
	 * IMPORTANT: The plugin is responsible for inspecting the file contents!
	 * Unless you are absolutely sure that the file is targeted for this runtime
	 * (by, e.g. looking at the targetNamespace or some other feature) then this
	 * method must return FALSE.
	 */
	@Override
	public boolean isContentForRuntime(IEditorInput input) {
		InputSource source = new InputSource(FileService.getInputContents(input));
		RootElementParser parser = new RootElementParser("http://www.uni.lu/bpmn2");
		parser.parse(source);
		return parser.getResult();
	}

	@Override
	public void notify(LifecycleEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
