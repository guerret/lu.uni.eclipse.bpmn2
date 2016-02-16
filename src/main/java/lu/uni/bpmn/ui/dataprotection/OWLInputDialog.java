package lu.uni.bpmn.ui.dataprotection;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Set;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.semanticweb.owlapi.model.OWLClass;

public class OWLInputDialog extends ElementListSelectionDialog {

	protected static boolean localExec = true;
	public static final String OWL_PATH = "/resources";
	public static final String OWL_FILE = OWL_PATH + File.separator + "dataprotection.owl";
	protected static String[] lines;
	protected static Ontology ontology;

	public OWLInputDialog(Shell parentShell, String dialogTitle, String initialValue) {
		super(parentShell, new LabelProvider());
		setMultipleSelection(true);
		lines = parseOLines(OWL_FILE, false);
		setElements(lines);
		String[] selection = { initialValue };
		setInitialSelections(selection);
		setTitle(dialogTitle);
	}

	public String[] getResult() {
		Object[] results = super.getResult();
		return Arrays.copyOf(results, results.length, String[].class);
	}

	private static String[] parseOLines(String fileName, boolean local) {
		URL url = null;
		if (local)
			try {
				url = new File(System.getProperty("user.dir") + fileName).toURI().toURL();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		else
			url = OWLInputDialog.class.getResource(fileName);
		ontology = new Ontology(url);
		OWLClass ruleClass = null;
		for (OWLClass c : ontology.getClasses())
			if (ontology.getLabel(c).equals("Rule"))
				ruleClass = c;
		Set<OWLClass> subClasses = ontology.getSubClasses(ruleClass);
		return ontology.getLabels(subClasses);
	}

	public static void main(String[] args) {
		lines = parseOLines(OWL_FILE, true);
		for (String l : lines)
			System.out.println(l);
	}

}
