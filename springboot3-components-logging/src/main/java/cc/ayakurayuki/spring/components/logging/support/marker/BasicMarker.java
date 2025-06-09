package cc.ayakurayuki.spring.components.logging.support.marker;

import jakarta.annotation.Nonnull;
import java.io.Serial;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.slf4j.Marker;

/**
 * A simple implementation of the {@link Marker} interface.
 *
 * @author Ceki G&uuml;lc&uuml;
 * @author Joern Huxhorn
 */
public class BasicMarker implements Marker {

  @Serial
  private static final long serialVersionUID = -7954760104022636108L;

  private static final String OPEN  = "[ ";
  private static final String CLOSE = " ]";
  private static final String SEP   = ", ";

  private final String name;

  private List<Marker> referenceList;

  public BasicMarker(String name) {
    if (name == null) {
      throw new IllegalArgumentException("a marker name cannot be null");
    }
    this.name = name;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public void add(Marker reference) {
    if (reference == null) {
      throw new IllegalArgumentException("a null value cannot be added to a Marker as reference");
    }

    if (this.contains(reference)) {
      // no point in adding the reference multiple times
    } else if (reference.contains(this)) {
      // avoid recursion
      // a potential reference should not its future "parent" as a reference
    } else {
      // let's add the reference
      if (this.referenceList == null) {
        this.referenceList = new Vector<>();
      }
      this.referenceList.add(reference);
    }

  }

  @Override
  public boolean remove(Marker reference) {
    if (this.referenceList == null) {
      return false;
    }

    int size = this.referenceList.size();
    for (int i = 0; i < size; i++) {
      Marker m = this.referenceList.get(i);
      if (reference.equals(m)) {
        this.referenceList.remove(i);
        return true;
      }
    }
    return false;
  }

  @SuppressWarnings("deprecation")
  @Override
  public boolean hasChildren() {
    return hasReferences();
  }

  @Override
  public boolean hasReferences() {
    return this.referenceList != null && !this.referenceList.isEmpty();
  }

  @Nonnull
  @Override
  public Iterator<Marker> iterator() {
    if (this.referenceList == null) {
      return Collections.emptyIterator();
    }
    return this.referenceList.iterator();
  }

  @Override
  public boolean contains(Marker other) {
    if (other == null) {
      throw new IllegalArgumentException("other cannot be null");
    }
    if (this.equals(other)) {
      return true;
    }
    if (this.hasReferences()) {
      for (Marker ref : this.referenceList) {
        if (ref.contains(other)) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public boolean contains(String name) {
    if (name == null) {
      throw new IllegalArgumentException("name cannot be null");
    }
    if (this.name.equals(name)) {
      return true;
    }
    if (this.hasReferences()) {
      for (Marker ref : this.referenceList) {
        if (ref.contains(name)) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof Marker other)) {
      return false;
    }
    return this.name.equals(other.getName());
  }

  @Override
  public int hashCode() {
    return this.name.hashCode();
  }

  @Override
  public String toString() {
    if (!this.hasReferences()) {
      return this.getName();
    }

    Iterator<Marker> it = this.iterator();
    Marker reference;
    StringBuilder sb = new StringBuilder(this.getName());
    sb.append(' ').append(OPEN);
    while (it.hasNext()) {
      reference = it.next();
      sb.append(reference.getName());
      if (it.hasNext()) {
        sb.append(SEP);
      }
    }
    sb.append(CLOSE);
    return sb.toString();
  }

}
