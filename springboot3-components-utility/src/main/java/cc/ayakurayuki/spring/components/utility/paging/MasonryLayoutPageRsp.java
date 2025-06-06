package cc.ayakurayuki.spring.components.utility.paging;

import java.util.List;
import lombok.Data;

/**
 * Masonry Layout Pagination response body
 *
 * @author Ayakura Yuki
 */
@Data
public class MasonryLayoutPageRsp<E> {

  private boolean more;
  private String  context;
  private List<E> list;

}
