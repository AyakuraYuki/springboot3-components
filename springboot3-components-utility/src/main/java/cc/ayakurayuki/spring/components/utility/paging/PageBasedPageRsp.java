package cc.ayakurayuki.spring.components.utility.paging;

import java.util.List;
import lombok.Data;

/**
 * Page-based Pagination response body
 *
 * @author Ayakura Yuki
 */
@Data
public class PageBasedPageRsp<E, P> {

  private List<E> list;
  private P       page;

}
