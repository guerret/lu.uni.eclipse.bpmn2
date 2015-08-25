package lu.uni.bpmn.ui.dataprotection;

import java.util.Set;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.semanticweb.owlapi.model.OWLClass;

public class OWLInputDialog extends ElementListSelectionDialog {

	protected static boolean localExec = true;
	protected static final String OWL_FILE = "/resources/dataprotection.owl";
	protected static String[] lines;
	protected static Ontology ontology;

	public OWLInputDialog(Shell parentShell, String dialogTitle, String dialogMessage, String initialValue,
			IInputValidator validator) {
		super(parentShell, new LabelProvider());
		lines = parseOLines(OWL_FILE, false);
		setElements(lines);
		setTitle(dialogTitle);
	}

	public String getValue() {
		return (String) getResult()[0];
	}

	private static String[] parseOLines(String fileName, boolean local) {
		if (local)
			fileName = System.getProperty("user.dir") + fileName;
		ontology = new Ontology(fileName, local);
		OWLClass ruleClass = ontology.findByLabel("Rule");
		Set<OWLClass> subClasses = ontology.getSubClasses(ruleClass);
		return ontology.getClassLabels(subClasses);
	}

	public static void main(String[] args) {
		lines = parseOLines(OWL_FILE, true);
		for (String l : lines)
			System.out.println(l);
	}

}
