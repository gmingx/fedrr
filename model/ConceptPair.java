/**
 * 
 */
package fedrr.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Gaungming Xing
 * @date Feb 12, 2015
 */
public class ConceptPair {
	public Concept a;
	public Concept b;
	public Set<Concept> lca;

	public boolean filter(String[] ids) {
		for (String id : ids) {
			if (a.getId().equals(id) || b.getId().equals(id))
				return true;
		}

		return false;
	}

	/**
	 * @param i
	 * @param j
	 * @return
	 */
	public boolean filter(String i, String j) {
		if (a.getId().equals(i) && b.getId().equals(j))
			return true;

		return false;
	}

	public ConceptPair(Concept a, Concept b) {
		super();
		if (a.getId().compareTo(b.getId()) > 0) {
			this.a = b;
			this.b = a;
		} else {
			this.a = a;
			this.b = b;
		}
	}

	public ConceptPair(Concept a, Concept b, Concept lca) {
		super();
		if (a.getId().compareTo(b.getId()) > 0) {
			this.a = b;
			this.b = a;
		} else {
			this.a = a;
			this.b = b;
		}
		this.lca = new LinkedHashSet<Concept>();
		this.lca.add(lca);
	}

	public ConceptPair(Concept a, Concept b, Set<Concept> lca) {
		super();
		if (a.getId().compareTo(b.getId()) > 0) {
			this.a = b;
			this.b = a;
		} else {
			this.a = a;
			this.b = b;
		}
		this.lca = new LinkedHashSet<Concept>();
		this.lca.addAll(lca);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		if (a.hashCode() < b.hashCode()) {
			result = ((a == null) ? 0 : a.hashCode());
			result = prime * result + ((b == null) ? 0 : b.hashCode());
		} else {
			result = ((b == null) ? 0 : b.hashCode());
			result = prime * result + ((a == null) ? 0 : a.hashCode());
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		ConceptPair other = (ConceptPair) obj;

		if ((other.a.equals(this.a) && other.b.equals(this.b))
				|| (other.a.equals(this.b) && other.b.equals(this.a)))
			return true;

		return false;
	}

	/**
	 * @param lca2
	 */
	public void addLca(Set<Concept> lca2) {
		if (lca2 == null)
			return;

		if (this.lca == null)
			this.lca = new LinkedHashSet<Concept>();

		ArrayList<Concept> toAdd = new ArrayList<Concept>();

		for (Concept candidate : lca2) {
			boolean good = true;
			for (Concept old : lca) {
				if (old.ancestor.contains(candidate)) {
					good = false;
					break;
				}

			}

			if (good)
				toAdd.add(candidate);
		}
		List<Concept> toRemove = new ArrayList<Concept>();
		for (Concept old : lca) {

			boolean good = true;
			for (Concept mtoadd : toAdd) {
				if (mtoadd.ancestor.contains(old)) {
					good = false;
					break;
				}

			}

			if (!good)
				toRemove.add(old);
		}

		this.lca.removeAll(toRemove);
		this.lca.addAll(toAdd);
	}

	/**
	 * @param con
	 */
	public void addLca(Concept con) {
		if (this.lca == null)
			this.lca = new LinkedHashSet<Concept>();

		this.lca.add(con);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(a.getId());
		builder.append("/");
		builder.append(b.getId());

		if (lca == null) {
			builder.append("########");
			return builder.toString();

		}

		Iterator<Concept> it = lca.iterator();
		builder.append("\t");
		builder.append(it.next().getId());
		while (it.hasNext()) {
			builder.append(",");
			builder.append(it.next().getId());
		}
		return builder.toString();
	}

}
