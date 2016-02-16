package lu.uni.bpmn.ui.dataprotection;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.search.EntitySearcher;

/**
 * Wrapper class for an OWL ontology. This class is similar to the
 * {@link OWLOntology} class, but also contains a lot of helper methods to
 * simplify some operations needed to generate the mutants.
 * 
 * @author cesare.bartolini
 *
 */
public class Ontology {

	/**
	 * The ontology manager of the OWL API library.
	 */
	public OWLOntologyManager manager;

	/**
	 * The ontology in OWL API format. This is protected because it is also used
	 * by the {@link Mutant} subclass.
	 */
	protected OWLOntology ontology;

	/**
	 * Parses a file into a new ontology. The file must be OWL in any syntax,
	 * but if there are imports it must be OWL/XML or RDF/XML.
	 * 
	 * Ontology files must be stored in the resources directory.
	 * 
	 * @param url
	 *            The URL representing the OWL file containing the ontology
	 */
	public Ontology(URL url) {
		manager = OWLManager.createOWLOntologyManager();
		ontology = load(url);
		System.out.println("Number of axioms: " + ontology.getAxiomCount());
		System.out.println("IRI: " + ontology.getOntologyID().getOntologyIRI().get());
	}

	/**
	 * Creates a new ontology from an OWL mutant.
	 * 
	 * @param owlMutant
	 *            The mutated (modified) ontology
	 */
	public Ontology(OWLOntology owlMutant) {
		manager = OWLManager.createOWLOntologyManager();
		manager.getOWLDataFactory();
		ontology = owlMutant;
	}

	/**
	 * Creates a new empty ontology.
	 */
	public Ontology() {
		manager = OWLManager.createOWLOntologyManager();
		manager.getOWLDataFactory();
	}

	/**
	 * Getter method to fetch the OWL API model of the ontology.
	 * 
	 * @return the ontology in the format used by OWL API
	 */
	public OWLOntology getOntology() {
		return ontology;
	}

	/**
	 * Getter for the ontology identifier.
	 * 
	 * @return the identifier assigned to the ontology
	 */
	public OWLOntologyID getOntologyID() {
		return ontology.getOntologyID();
	}

	/**
	 * Getter for the version IRI of the ontology (i.e., the second part of the
	 * ontology identifier=).
	 * 
	 * @return the version IRI of the ontology
	 */
	public IRI getVersionIRI() {
		return ontology.getOntologyID().getVersionIRI().get();
	}

	private OWLOntology load(URL url) {
		try {
			return manager.loadOntologyFromOntologyDocument(IRI.create(url));
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Exports the ontology to an OWL file.
	 * 
	 * @param path
	 *            the full path where the file must be saved
	 * @param fileName
	 *            the file name for saving the ontology
	 */
	public void save(String path, String fileName) {
		File file = new File(path + File.separator + fileName);
		OWLDocumentFormat format = new RDFXMLDocumentFormat();
		try {
			manager.saveOntology(ontology, format, IRI.create(file.toURI()));
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the label for a given OWL entity.
	 * 
	 * In case more than one label is present, only the first one encountered is
	 * returned.
	 * 
	 * @param e
	 *            the OWL entity whose label is requested
	 * @return the first OWL annotation containing a label for the entity
	 */
	public String getLabel(OWLEntity e) {
		for (OWLAnnotation a : EntitySearcher.getAnnotations(e, ontology)) {
			if (a.getProperty().isLabel()) {
				OWLLiteral label = (OWLLiteral) a.getValue();
				return label.getLiteral();
			}
		}
		return e.getIRI().getShortForm();
	}

	/**
	 * Returns all labels associated with a given entity. The labels are not
	 * returned as strings but as OWL API {@link OWLAnnotation} objects, thus
	 * they also contain additional information such as the language. However,
	 * only label annotations are returned.
	 * 
	 * @param e
	 *            the OWL entity whose labels are requested
	 * @return a set containing all the label annotations associated to the
	 *         entity
	 */
	public Set<OWLAnnotation> getLabels(OWLEntity e) {
		Set<OWLAnnotation> ret = new HashSet<OWLAnnotation>();
		for (OWLAnnotation a : EntitySearcher.getAnnotations(e, ontology)) {
			if (a.getProperty().isLabel())
				ret.add(a);
		}
		return ret;
	}

	/**
	 * Returns all labels for a given OWL entity.
	 * 
	 * @param e
	 *            the OWL entity whose labels are requested
	 * @return the first OWL annotation containing a label for the entity
	 */
	public String[] getLabels(Set<? extends OWLEntity> entities) {
		String[] labels = new String[entities.size()];
		int i = 0;
		for (OWLEntity c : entities)
			labels[i++] = getLabel(c);
		return labels;
	}

	/**
	 * Helper method to fetch all classes among the entities in the ontology.
	 * 
	 * @return the set of classes making up the ontology
	 */
	public Set<OWLClass> getClasses() {
		return ontology.getClassesInSignature();
	}

	/**
	 * Helper method to fetch all object properties among the entities in the
	 * ontology.
	 * 
	 * @return the set of object properties making up the ontology
	 */
	public Set<OWLObjectProperty> getObjectProperties() {
		return ontology.getObjectPropertiesInSignature();
	}

	/**
	 * Helper method to fetch all named individuals among the entities in the
	 * ontology.
	 * 
	 * @return the set of named individuals making up the ontology
	 */
	public Set<OWLNamedIndividual> getIndividuals() {
		return ontology.getIndividualsInSignature();
	}

	/**
	 * Helper method to fetch all data properties among the entities in the
	 * ontology.
	 * 
	 * @return the set of data properties making up the ontology
	 */
	public Set<OWLDataProperty> getDataProperties() {
		return ontology.getDataPropertiesInSignature();
	}

	/**
	 * Helper method to fetch all classes of which a given class is a subclass.
	 * Note that the superclass is not returned if it
	 * {@link OWLClass#isTopEntity()}.
	 * 
	 * @param cls
	 *            the class whose superclasses are needed
	 * @return all superclasses of the given class
	 */
	public Collection<OWLClassExpression> getSuperClasses(OWLClass cls) {
		return EntitySearcher.getSuperClasses(cls, ontology);
	}

	/**
	 * Helper method to fetch all classes that are subclasses of a given class.
	 * 
	 * @param cls
	 *            the class whose subclasses are needed
	 * @return all subclasses of the given class
	 */
	public Set<OWLClass> getSubClasses(OWLClass cls) {
		Set<OWLClass> ret = new HashSet<OWLClass>();
		for (OWLClassExpression s : EntitySearcher.getSubClasses(cls, ontology)) {
			OWLClass child = s.asOWLClass();
			if (child != cls) {
				ret.add(child);
				ret.addAll(getSubClasses(child));
			}
		}
		return ret;
	}

	/**
	 * Helper method to fetch all classes that are types of a given individual.
	 * Note that the class is not returned if it {@link OWLClass#isTopEntity()}.
	 * 
	 * @param individual
	 *            the individual whose class types are needed
	 * @return all class types of the given individual
	 */
	public Collection<OWLClassExpression> getIndividualTypes(OWLNamedIndividual individual) {
		return EntitySearcher.getTypes(individual, ontology);
	}

	/**
	 * Helper method to fetch the domains of a given object property (unless the
	 * class {@link OWLClass#isTopEntity()}).
	 * 
	 * @param property
	 *            the property whose domains are requested
	 * @return all the classes which make up the domain of an object property
	 */
	public Collection<OWLClassExpression> getObjectPropertyDomains(OWLObjectProperty property) {
		return EntitySearcher.getDomains(property, ontology);
	}

	/**
	 * Helper method to fetch the range of a given object property (unless the
	 * class {@link OWLClass#isTopEntity()}).
	 * 
	 * @param property
	 *            the property whose domains are requested
	 * @return all the classes which make up the domain of an object property
	 */
	public Collection<OWLClassExpression> getRanges(OWLObjectProperty property) {
		return EntitySearcher.getRanges(property, ontology);
	}

	/**
	 * Helper method to fetch the domains of a given data property (unless the
	 * class {@link OWLClass#isTopEntity()}).
	 * 
	 * @param property
	 *            the property whose domains are requested
	 * @return all the classes which make up the domain of a data property
	 */
	public Collection<OWLClassExpression> getDataPropertyDomains(OWLDataProperty property) {
		return EntitySearcher.getDomains(property, ontology);
	}

	/**
	 * Helper method to fetch the domains of a given data property (unless the
	 * class {@link OWLDatatype#isTopDatatype()}).
	 * 
	 * @param property
	 *            the property whose domains are requested
	 * @return all the datatypes which make up the range of a data property
	 */
	public Collection<OWLDataRange> getDataPropertyRanges(OWLDataProperty property) {
		return EntitySearcher.getRanges(property, ontology);
	}

}
