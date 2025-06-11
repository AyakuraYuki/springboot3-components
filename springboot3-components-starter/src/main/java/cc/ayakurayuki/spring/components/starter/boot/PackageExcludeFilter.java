package cc.ayakurayuki.spring.components.starter.boot;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.core.type.filter.AbstractTypeHierarchyTraversingFilter;

class PackageExcludeFilter extends AbstractTypeHierarchyTraversingFilter {

  private final Set<String> excludePackages = new HashSet<>();

  PackageExcludeFilter(String... exclude) {
    super(false, false);
    excludePackages.addAll(Arrays.asList(exclude));
  }

  @Override
  protected boolean matchClassName(String className) {
    return excludePackages.stream().anyMatch(className::contains);
  }

}
