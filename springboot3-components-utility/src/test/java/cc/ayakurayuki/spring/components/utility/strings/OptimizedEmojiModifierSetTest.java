package cc.ayakurayuki.spring.components.utility.strings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.junit.jupiter.api.Test;

/**
 * @author Ayakura Yuki
 */
class OptimizedEmojiModifierSetTest {

  @Test
  void testContains() throws JsonProcessingException {
    OptimizedEmojiModifierSet bitset = new OptimizedEmojiModifierSet();
    bitset.add(UnicodeGraphemeAnalyzer.ModifierBlack);
    bitset.add(UnicodeGraphemeAnalyzer.ModifierColorFul);
    bitset.add(UnicodeGraphemeAnalyzer.ModifierKeyCap);
    UnicodeGraphemeAnalyzer.ModifierSkinTone.forEach(bitset::add);
    bitset.addRange(UnicodeGraphemeAnalyzer.ModifierTagRangeStart, UnicodeGraphemeAnalyzer.ModifierTagRangeEnd);

    assert bitset.contains(UnicodeGraphemeAnalyzer.ModifierBlack);
    assert bitset.contains(UnicodeGraphemeAnalyzer.ModifierColorFul);
    assert !bitset.contains(0x0);

    ObjectMapper mapper = new ObjectMapper();
    String raw = """
        {
          "nextId": 10.102
        }""";
    Req req = mapper.readValue(raw, Req.class);
    System.out.println(req.nextId.doubleValue());
    System.out.println(req.nextId.longValue());
    System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(req));
  }

  @Data
  static class Req {

    private Number nextId;

  }

}
