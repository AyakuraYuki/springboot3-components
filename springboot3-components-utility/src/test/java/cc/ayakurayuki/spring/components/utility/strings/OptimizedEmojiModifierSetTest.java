package cc.ayakurayuki.spring.components.utility.strings;

import com.google.gson.Gson;
import lombok.Data;
import org.junit.jupiter.api.Test;

/**
 * @author Ayakura Yuki
 */
class OptimizedEmojiModifierSetTest {

  @Test
  void testContains() {
    var bitset = new OptimizedEmojiModifierSet();
    bitset.add(UnicodeGraphemeAnalyzer.ModifierBlack);
    bitset.add(UnicodeGraphemeAnalyzer.ModifierColorFul);
    bitset.add(UnicodeGraphemeAnalyzer.ModifierKeyCap);
    UnicodeGraphemeAnalyzer.ModifierSkinTone.forEach(bitset::add);
    bitset.addRange(UnicodeGraphemeAnalyzer.ModifierTagRangeStart, UnicodeGraphemeAnalyzer.ModifierTagRangeEnd);

    assert bitset.contains(UnicodeGraphemeAnalyzer.ModifierBlack);
    assert bitset.contains(UnicodeGraphemeAnalyzer.ModifierColorFul);
    assert !bitset.contains(0x0);

    String raw = """
        {
          "nextId": 10.102
        }""";
    Req req = new Gson().fromJson(raw, Req.class);
    System.out.println(req.nextId.doubleValue());
    System.out.println(req.nextId.longValue());
    System.out.println(new Gson().toJson(req));
  }

  @Data
  static class Req {

    private Number nextId;

  }

}
