package cc.ayakurayuki.spring.components.utility.strings;

import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

/**
 * @author Ayakura Yuki
 */
class RandomStringsTest {

  @Test
  void example() {
    System.out.println(RandomStrings.random(10, RandomStrings.Lower));
    System.out.println(RandomStrings.random(10, RandomStrings.Upper));
    System.out.println(RandomStrings.random(10, RandomStrings.Digit));
    System.out.println(RandomStrings.random(10, RandomStrings.LowerDigit));
    System.out.println(RandomStrings.random(10, RandomStrings.UpperDigit));
    System.out.println(RandomStrings.random(10, RandomStrings.LowerUpper));
    System.out.println(RandomStrings.random(10, RandomStrings.LowerUpperDigit));
  }

  @Test
  void test() {
    Pattern reLower = Pattern.compile("[a-z]*");
    assert reLower.matcher(RandomStrings.random(10, RandomStrings.Lower)).matches();
    assert !reLower.matcher(RandomStrings.random(10, RandomStrings.LowerDigit)).matches();

    Pattern reUpper = Pattern.compile("[A-Z]*");
    assert reUpper.matcher(RandomStrings.random(10, RandomStrings.Upper)).matches();
    assert !reUpper.matcher(RandomStrings.random(10, RandomStrings.LowerDigit)).matches();

    Pattern reDigit = Pattern.compile("[0-9]*");
    assert reDigit.matcher(RandomStrings.random(10, RandomStrings.Digit)).matches();
    assert !reDigit.matcher(RandomStrings.random(10, RandomStrings.Upper)).matches();

    Pattern reLowerDigit = Pattern.compile("[a-z0-9]*");
    assert reLowerDigit.matcher(RandomStrings.random(10, RandomStrings.LowerDigit)).matches();
    assert !reLowerDigit.matcher(RandomStrings.random(10, RandomStrings.Upper)).matches();

    Pattern reUpperDigit = Pattern.compile("[A-Z0-9]*");
    assert reUpperDigit.matcher(RandomStrings.random(10, RandomStrings.UpperDigit)).matches();
    assert !reUpperDigit.matcher(RandomStrings.random(10, RandomStrings.Lower)).matches();

    Pattern reLowerUpper = Pattern.compile("[a-zA-Z]*");
    assert reLowerUpper.matcher(RandomStrings.random(10, RandomStrings.LowerUpper)).matches();
    assert !reLowerUpper.matcher(RandomStrings.random(10, RandomStrings.Digit)).matches();

    Pattern reLowerUpperDigit = Pattern.compile("[a-zA-Z0-9]*");
    assert reLowerUpperDigit.matcher(RandomStrings.random(10, RandomStrings.LowerUpperDigit)).matches();
  }

  @Test
  void test_password() {
    assert RandomStrings.password(4).length() == 6;
    assert RandomStrings.password(10).length() == 10;

    for (int i = 0; i < 10; i++) {
      System.out.println(RandomStrings.password(20));
    }
  }

}
