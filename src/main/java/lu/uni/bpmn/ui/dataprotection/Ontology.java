package lu.uni.bpmn.ui.dataprotection;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.search.EntitySearcher;

public class Ontology {

	private OWLOntology ontology;

	public Ontology(String fileName, boolean local) {
		if (local)
			ontology = getLocalOntology(fileName);
		else
			ontology = getOntology(fileName);
	}

	private OWLOntology getOntology(String fileName) {
		try {
			InputStream is = OWLInputDialog.class.getResourceAsStream(fileName);
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			return manager.loadOntologyFromOntologyDocument(is);
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
		return null;
	}

	private OWLOntology getLocalOntology(String fileName) {
		try {
			InputStream is = new FileInputStream(fileName);
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			return manager.loadOntologyFromOntologyDocument(is);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getLabel(OWLClass cls) {
		for (OWLAnnotation a : EntitySearcher.getAnnotations(cls, ontology)) {
			if (a.getProperty().isLabel())
				return ((OWLLiteral) a.getValue()).getLiteral();
		}
		return null;
	}

	public String[] getClassLabels(Set<OWLClass> classes) {
		String[] labels = new String[classes.size()];
		int i = 0;
		for (OWLClass c : classes)
			labels[i++] = getLabel(c);
		return labels;
	}

	public String[] getClassLabels() {
		return getClassLabels(ontology.getClassesInSignature());
	}

	public OWLClass findByLabel(String label) {
		for (OWLClass c : ontology.getClassesInSignature())
			if (getLabel(c).equals(label))
				return c;
		return null;
	}

	public Set<OWLClass> getSubClasses(OWLClass cls) {
		Set<OWLClass> ret = new HashSet<OWLClass>();
		for (OWLClassExpression s : EntitySearcher.getSubClasses(cls, ontology)) {
			OWLClass c = (OWLClass) s;
			ret.add(c);
			ret.addAll(getSubClasses(c));
		}
		return ret;
	}

}
