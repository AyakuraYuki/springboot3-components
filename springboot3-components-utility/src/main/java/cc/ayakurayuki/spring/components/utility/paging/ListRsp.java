package cc.ayakurayuki.spring.components.utility.paging;

import java.util.List;
import lombok.Data;

/**
 * Simple list response body
 *
 * @author Ayakura Yuki
 */
@Data
public class ListRsp<E> {

  private List<E> list;

}
