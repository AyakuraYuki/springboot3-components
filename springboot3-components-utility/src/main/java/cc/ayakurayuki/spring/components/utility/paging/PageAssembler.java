package cc.ayakurayuki.spring.components.utility.paging;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Ayakura Yuki
 */
public abstract class PageAssembler {

  /**
   * Creates a page-based pagination response containing the result data and pagination metadata.
   *
   * @param list the collection of data items for the current page
   * @param page the pagination metadata object containing page information
   * @param <E>  the type of elements in the result list
   * @param <P>  the type of the pagination metadata object
   *
   * @return a configured PageBasedPageRsp instance with the provided data and pagination information
   */
  public static <E, P> PageBasedPageRsp<E, P> pageRsp(List<E> list, P page) {
    PageBasedPageRsp<E, P> rsp = new PageBasedPageRsp<>();
    rsp.setList(list);
    rsp.setPage(page);
    return rsp;
  }

  /**
   * Creates a page-based pagination response using a custom response class that extends PageBasedPageRsp.
   * This method enables the creation of extended response objects with additional fields beyond the base
   * pagination structure.
   *
   * <p>Example usage:
   * <pre>{@code
   * public class Example {
   *   public static void main(String[] args) {
   *     List<Integer> list = Collections.emptyList();
   *     Page page = new Page(1, 10, 0);
   *     CustomResponse response = PageAssembler.pageRsp(list, page, CustomResponse.class);
   *   }
   *
   *   @lombok.Data
   *   static class CustomResponse extends PageBasedPageRsp<Integer, Page> {
   *     private String additionalContent;
   *   }
   * }
   * }</pre>
   *
   * @param list  the collection of data items for the current page
   * @param page  the pagination metadata object containing page information
   * @param clazz the Class object of the target response type that extends PageBasedPageRsp
   * @param <T>   the concrete response type that extends PageBasedPageRsp
   * @param <E>   the type of elements in the result list
   * @param <P>   the type of the pagination metadata object
   *
   * @return an instance of the specified response class populated with the provided data and pagination information
   *
   * @throws RuntimeException if the response object cannot be instantiated due to reflection errors
   */
  public static <T extends PageBasedPageRsp<E, P>, E, P> T pageRsp(List<E> list, P page, Class<T> clazz) {
    try {
      T rsp = clazz.getDeclaredConstructor().newInstance();
      rsp.setList(list);
      rsp.setPage(page);
      return rsp;
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Creates an empty Page-based Pagination response with no data items and specified pagination parameters.
   * This method is typically used when a query returns no results but pagination metadata is still required.
   *
   * @param p    the current page number
   * @param size the number of items per page
   * @param <T>  the type of elements that would be contained in the result list
   *
   * @return a PageBasedPageRsp instance with an empty list and pagination metadata indicating no results
   */
  public static <T> PageBasedPageRsp<T, Page> emptyPageRsp(int p, int size) {
    PageBasedPageRsp<T, Page> rsp = new PageBasedPageRsp<>();
    rsp.setList(Collections.emptyList());
    rsp.setPage(new Page(p, size, 0));
    return rsp;
  }

  // ----------------------------------------------------------------------------------------------------

  /**
   * Creates a Masonry Layout Pagination response with an empty result data and pagination state.
   *
   * @param <E> the type of elements in the result list
   *
   * @return a configured MasonryLayoutPageRsp instance with the empty pagination data
   */
  public static <E> MasonryLayoutPageRsp<E> emptyMasonryRsp() {
    return masonryRsp(false, "", Collections.emptyList());
  }

  /**
   * Creates a Masonry Layout Pagination response containing the result data and pagination state.
   *
   * @param hasMore indicates whether additional data is available beyond the current batch
   * @param nextId  the next id used to retrieve the next batch of results
   * @param list    the current batch of data items to be included in the response
   * @param <E>     the type of elements in the result list
   *
   * @return a configured MasonryLayoutPageRsp instance with the provided pagination data
   */
  public static <E> MasonryLayoutPageRsp<E> masonryRsp(boolean hasMore, int nextId, List<E> list) {
    return masonryRspWithContext(hasMore, toContext(nextId), list);
  }

  /**
   * Creates a Masonry Layout Pagination response containing the result data and pagination state.
   *
   * @param hasMore indicates whether additional data is available beyond the current batch
   * @param nextId  the next id used to retrieve the next batch of results
   * @param list    the current batch of data items to be included in the response
   * @param <E>     the type of elements in the result list
   *
   * @return a configured MasonryLayoutPageRsp instance with the provided pagination data
   */
  public static <E> MasonryLayoutPageRsp<E> masonryRsp(boolean hasMore, long nextId, List<E> list) {
    return masonryRspWithContext(hasMore, toContext(nextId), list);
  }

  /**
   * Creates a Masonry Layout Pagination response containing the result data and pagination state.
   *
   * @param hasMore indicates whether additional data is available beyond the current batch
   * @param nextId  the next id used to retrieve the next batch of results
   * @param list    the current batch of data items to be included in the response
   * @param <E>     the type of elements in the result list
   *
   * @return a configured MasonryLayoutPageRsp instance with the provided pagination data
   */
  public static <E> MasonryLayoutPageRsp<E> masonryRsp(boolean hasMore, double nextId, List<E> list) {
    return masonryRspWithContext(hasMore, toContext(nextId), list);
  }

  /**
   * Creates a Masonry Layout Pagination response containing the result data and pagination state.
   *
   * @param hasMore indicates whether additional data is available beyond the current batch
   * @param token   the next token used to retrieve the next batch of results
   * @param list    the current batch of data items to be included in the response
   * @param <E>     the type of elements in the result list
   *
   * @return a configured MasonryLayoutPageRsp instance with the provided pagination data
   */
  public static <E> MasonryLayoutPageRsp<E> masonryRsp(boolean hasMore, String token, List<E> list) {
    return masonryRspWithContext(hasMore, toContext(token), list);
  }

  /**
   * Creates a Masonry Layout Pagination response containing the result data and pagination state.
   *
   * @param hasMore indicates whether additional data is available beyond the current batch
   * @param context the pagination context string used to retrieve the next batch of results
   * @param list    the current batch of data items to be included in the response
   * @param <E>     the type of elements in the result list
   *
   * @return a configured MasonryLayoutPageRsp instance with the provided pagination data
   */
  public static <E> MasonryLayoutPageRsp<E> masonryRspWithContext(boolean hasMore, String context, List<E> list) {
    MasonryLayoutPageRsp<E> rsp = new MasonryLayoutPageRsp<>();
    rsp.setMore(hasMore);
    rsp.setContext(context);
    rsp.setList(list);
    return rsp;
  }

  // ----------------------------------------------------------------------------------------------------

  /**
   * Creates a Masonry Layout Pagination response using a custom response class that extends MasonryLayoutPageRsp.
   * This method enables the creation of extended response objects with additional fields beyond the base
   * pagination structure.
   *
   * @param hasMore indicates whether additional data is available beyond the current batch
   * @param nextId  the next id used to retrieve the next batch of results
   * @param list    the current batch of data items to be included in the response
   * @param clazz   the Class object of the target response type that extends MasonryLayoutPageRsp
   * @param <T>     the concrete response type that extends MasonryLayoutPageRsp
   * @param <E>     the type of elements in the result list
   *
   * @return an instance of the specified response class populated with the provided data and pagination information
   *
   * @throws RuntimeException if the response object cannot be instantiated due to reflection errors
   */
  public static <T extends MasonryLayoutPageRsp<E>, E> T masonryRsp(boolean hasMore, Number nextId, List<E> list, Class<T> clazz) {
    return masonryRspWithContext(hasMore, toContext(nextId), list, clazz);
  }

  /**
   * Creates a Masonry Layout Pagination response using a custom response class that extends MasonryLayoutPageRsp.
   * This method enables the creation of extended response objects with additional fields beyond the base
   * pagination structure.
   *
   * @param hasMore indicates whether additional data is available beyond the current batch
   * @param token   the next token used to retrieve the next batch of results
   * @param list    the current batch of data items to be included in the response
   * @param clazz   the Class object of the target response type that extends MasonryLayoutPageRsp
   * @param <T>     the concrete response type that extends MasonryLayoutPageRsp
   * @param <E>     the type of elements in the result list
   *
   * @return an instance of the specified response class populated with the provided data and pagination information
   *
   * @throws RuntimeException if the response object cannot be instantiated due to reflection errors
   */
  public static <T extends MasonryLayoutPageRsp<E>, E> T masonryRsp(boolean hasMore, String token, List<E> list, Class<T> clazz) {
    return masonryRspWithContext(hasMore, toContext(token), list, clazz);
  }

  /**
   * Creates a Masonry Layout Pagination response using a custom response class that extends MasonryLayoutPageRsp.
   * This method enables the creation of extended response objects with additional fields beyond the base
   * pagination structure.
   *
   * @param hasMore indicates whether additional data is available beyond the current batch
   * @param context the pagination context string used to retrieve the next batch of results
   * @param list    the current batch of data items to be included in the response
   * @param clazz   the Class object of the target response type that extends MasonryLayoutPageRsp
   * @param <T>     the concrete response type that extends MasonryLayoutPageRsp
   * @param <E>     the type of elements in the result list
   *
   * @return an instance of the specified response class populated with the provided data and pagination information
   *
   * @throws RuntimeException if the response object cannot be instantiated due to reflection errors
   */
  public static <T extends MasonryLayoutPageRsp<E>, E> T masonryRspWithContext(boolean hasMore, String context, List<E> list, Class<T> clazz) {
    try {
      T rsp = clazz.getDeclaredConstructor().newInstance();
      rsp.setMore(hasMore);
      rsp.setContext(context);
      rsp.setList(list);
      return rsp;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  // ----------------------------------------------------------------------------------------------------

  private static final Gson CONTEXT_JSON = new GsonBuilder().disableHtmlEscaping().create();

  public static String toContext(Number nextId) {
    PageContext context = new PageContext();
    context.setNextId(nextId);
    return CONTEXT_JSON.toJson(context);
  }

  public static String toContext(Integer nextId) {
    PageContext context = new PageContext();
    context.setNextId(nextId);
    return CONTEXT_JSON.toJson(context);
  }

  public static String toContext(Long nextId) {
    PageContext context = new PageContext();
    context.setNextId(nextId);
    return CONTEXT_JSON.toJson(context);
  }

  public static String toContext(Double nextId) {
    PageContext context = new PageContext();
    context.setNextId(nextId);
    return CONTEXT_JSON.toJson(context);
  }

  public static String toContext(String token) {
    PageContext context = new PageContext();
    context.setToken(token);
    return CONTEXT_JSON.toJson(context);
  }

  public static Number toNextId(String context) {
    Number nextId = 0;
    PageContext pc = null;
    if (StringUtils.isNotBlank(context)) {
      pc = CONTEXT_JSON.fromJson(context, PageContext.class);
    }
    if (pc != null && pc.getNextId() != null) {
      nextId = pc.getNextId();
    }
    return nextId;
  }

  public static long toNextIdLong(String context) {
    return toNextId(context).longValue();
  }

  public static int toNextIdInt(String context) {
    return toNextId(context).intValue();
  }

  public static double toNextIdDouble(String context) {
    return toNextId(context).doubleValue();
  }

  public static String toToken(String context) {
    String token = null; // default as null
    PageContext pc = null;
    if (StringUtils.isNotBlank(context)) {
      pc = CONTEXT_JSON.fromJson(context, PageContext.class);
    }
    if (pc != null && pc.getToken() != null) {
      token = pc.getToken();
    }
    return token;
  }

}
