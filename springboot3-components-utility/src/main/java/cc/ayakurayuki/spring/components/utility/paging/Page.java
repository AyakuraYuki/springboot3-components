package cc.ayakurayuki.spring.components.utility.paging;

import lombok.Data;

/**
 * Page-based Pagination detail
 *
 * @author Ayakura Yuki
 */
@Data
public class Page {

  private long p;
  private long size;
  private long count;

  public Page(long p, long size, long count) {
    this.p = p;
    this.size = size;
    this.count = count;

    long _from = this.getFromIndex(); // init this.p
  }

  // export as attribute
  public long getFromIndex() {
    if (this.p < 1) {
      this.p = 1;
    }
    long pc = calculatePageCount();
    if (this.p > pc && this.count > this.size) {
      this.p = pc;
    }
    return (this.p - 1) * this.size;
  }

  // export as attribute
  public long getPageSize() {
    return this.size;
  }

  private long calculatePageCount() {
    long s = this.count / this.size;
    long m = this.count % this.size;
    if (m != 0) {
      s++;
    }
    if (this.count == 0) {
      return 1;
    }
    return s;
  }

}
