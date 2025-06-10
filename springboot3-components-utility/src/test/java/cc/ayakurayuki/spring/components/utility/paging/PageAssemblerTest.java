package cc.ayakurayuki.spring.components.utility.paging;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

public class PageAssemblerTest {

  @Test
  void testToContext() {
    String want = """
        {"nextId":"114"}""".trim();
    assert PageAssembler.toContext(114).equals(want);
    assert PageAssembler.toContext(114L).equals(want);

    want = """
        {"nextId":"114.0"}""".trim();
    assert PageAssembler.toContext(114D).equals(want);

    want = """
        {"nextId":"1919"}""".trim();
    assert PageAssembler.toContext(new BigDecimal("1919")).equals(want);

    want = """
        {"token":"2233"}""".trim();
    assert PageAssembler.toContext("2233").equals(want);
  }

  @Test
  void testToNextId() {
    String raw = """
        {"nextId":"114"}""".trim();
    assert PageAssembler.toNextId(raw).intValue() == new BigDecimal("114").intValue();
    assert PageAssembler.toNextIdInt(raw) == 114;
    assert PageAssembler.toNextIdLong(raw) == 114L;
    assert PageAssembler.toNextIdDouble(raw) == 114D;

    raw = """
        {"nextId":"114.0"}""".trim();
    assert PageAssembler.toNextIdDouble(raw) == 114D;

    raw = """
        {"token":"2233"}""".trim();
    assert PageAssembler.toToken(raw).equals("2233");
  }

}
