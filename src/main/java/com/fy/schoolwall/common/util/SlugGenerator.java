package com.fy.schoolwall.common.util;

// 移除 tiny-pinyin 的导入
// import com.github.promeg.pinyinhelper.Pinyin;
// import com.github.promeg.pinyinhelper.PinyinFormat;

// 导入 pinyin4j 的相关类
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Slug生成器工具类
 * <p>
 * 意义：
 * 1. 生成SEO友好的URL标识符，提高搜索引擎优化效果
 * 2. 将中文或特殊字符转换为URL安全的字符，确保链接可访问性
 * 3. 提供统一的命名规范，保持URL的一致性和美观性
 * 4. 支持多语言内容的URL生成，提高国际化支持
 * <p>
 * 生产需求改进：
 * - 集成 pinyin4j 库，提供更准确和全面的中文拼音转换。
 * - 引入常量，提高代码可读性和可维护性。
 * - 优化 slug 生成流程。
 */
public class SlugGenerator {

    // 默认的slug值，当输入无法生成有效slug时使用
    public static final String DEFAULT_SLUG = "untitled";
    // 生成slug的最大长度限制，避免过长的URL
    public static final int MAX_SLUG_LENGTH = 100;

    // 匹配非字母数字字符的正则表达式，不包括连字符
    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    // 匹配多个连续空白字符的正则表达式
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    // 匹配多个连续连字符的正则表达式
    private static final Pattern MULTIPLE_HYPHENS = Pattern.compile("-{2,}");
    // 匹配开头或结尾的连字符的正则表达式
    private static final Pattern LEADING_TRAILING_HYPHENS = Pattern.compile("^-+|-+$");

    // Pinyin4j 输出格式配置 (线程安全，可以作为静态常量)
    private static final HanyuPinyinOutputFormat PINYIN_FORMAT = new HanyuPinyinOutputFormat();

    static {
        // 设置声调类型：不带声调
        PINYIN_FORMAT.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        // 设置大小写类型：小写
        PINYIN_FORMAT.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        // 设置'ü'的表示方式：'v' (兼容URL)
        PINYIN_FORMAT.setVCharType(HanyuPinyinVCharType.WITH_V);
    }

    /**
     * 根据标题生成slug
     * <p>
     * 流程：
     * 1. 空值/空白检查，返回默认slug。
     * 2. 转换为小写并去除首尾空格。
     * 3. 中文转拼音（使用 pinyin4j）。
     * 4. 标准化Unicode字符（处理变音符号等，例如 "résumé" -> "resume"）。
     * 5. 将空白字符替换为连字符。
     * 6. 移除非拉丁字母、数字和连字符的字符。
     * 7. 替换多个连续连字符为单个。
     * 8. 移除开头和结尾的连字符。
     * 9. 再次检查是否为空，若为空则返回默认slug。
     * 10. 限制slug长度，并在截断时确保不以连字符结尾。
     *
     * @param input 输入标题
     * @return 生成的slug
     */
    public static String generateSlug(String input) {
        if (input == null || input.trim().isEmpty()) {
            return DEFAULT_SLUG;
        }

        String slug = input.trim().toLowerCase();

        // 1. 中文转拼音 (核心改进，现在使用 pinyin4j)
        slug = handleChineseCharacters(slug);

        // 2. 标准化Unicode字符 (例如: résumé -> resume)
        // Normalizer.Form.NFD 将字符分解为基字符和组合字符，便于后续移除非拉丁字符。
        slug = Normalizer.normalize(slug, Normalizer.Form.NFD);

        // 3. 将所有空白字符（包括换行符、tab等）替换为单个连字符
        slug = WHITESPACE.matcher(slug).replaceAll("-");

        // 4. 移除非字母数字和连字符的字符
        slug = NON_LATIN.matcher(slug).replaceAll("");

        // 5. 替换多个连续连字符为单个连字符
        slug = MULTIPLE_HYPHENS.matcher(slug).replaceAll("-");

        // 6. 移除开头和结尾的连字符
        slug = LEADING_TRAILING_HYPHENS.matcher(slug).replaceAll("");

        // 7. 如果生成的slug为空，使用默认值
        if (slug.isEmpty()) {
            return DEFAULT_SLUG;
        }

        // 8. 限制长度，并确保不会在单词中间截断或以连字符结尾
        if (slug.length() > MAX_SLUG_LENGTH) {
            slug = slug.substring(0, MAX_SLUG_LENGTH);
            // 确保截断后不会以连字符结尾，回退到最后一个连字符
            if (slug.endsWith("-")) {
                slug = slug.substring(0, slug.length() - 1);
            } else if (slug.contains("-")) {
                int lastHyphenIndex = slug.lastIndexOf("-");
                // 如果最后一个连字符在靠近截断点的位置，回退到它，避免截断单词。
                // 这里的启发式是：如果最后10个字符内有连字符，并且不是以连字符结尾，
                // 则回退到该连字符。这是一种权衡，可以根据需求调整。
                if (lastHyphenIndex != -1 && (slug.length() - lastHyphenIndex) < 10) {
                    slug = slug.substring(0, lastHyphenIndex);
                }
            }
        }
        // 再次移除截断后可能产生的结尾连字符，以防万一
        slug = LEADING_TRAILING_HYPHENS.matcher(slug).replaceAll("");
        if (slug.isEmpty()) { // 极端情况，如"very-very-very-long-word"截断后变空
            return DEFAULT_SLUG;
        }

        return slug;
    }

    /**
     * 处理中文字符，将其转换为不带声调的小写拼音。
     * 使用 pinyin4j 库。
     */
    private static String handleChineseCharacters(String input) {
        StringBuilder pinyinBuilder = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            try {
                // 判断是否是汉字字符
                if (Character.getType(c) == Character.OTHER_LETTER) { // 通常用于匹配中日韩字符
                    String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c, PINYIN_FORMAT);
                    if (pinyinArray != null && pinyinArray.length > 0) {
                        // 对于多音字，pinyin4j会返回一个数组。通常我们取第一个拼音。
                        pinyinBuilder.append(pinyinArray[0]);
                    } else {
                        // 对于无法转换的汉字（极少见）或非汉字字符，直接保留
                        pinyinBuilder.append(c);
                    }
                } else {
                    // 非汉字字符直接保留
                    pinyinBuilder.append(c);
                }
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                // 这通常发生在格式化设置不兼容时，在我们的静态配置中不应该发生
                // 但作为健壮性考虑，捕获并打印，然后保留原字符
                System.err.println("Pinyin4j format error for character '" + c + "': " + e.getMessage());
                pinyinBuilder.append(c);
            }
        }
        return pinyinBuilder.toString();
    }

    /**
     * 生成唯一slug（带时间戳）
     *
     * @param input 输入标题
     * @return 带时间戳的唯一slug
     */
    public static String generateUniqueSlug(String input) {
        String baseSlug = generateSlug(input);
        long timestamp = System.currentTimeMillis();
        // 确保时间戳与baseSlug之间有连字符，且不会出现多余连字符
        return baseSlug + "-" + timestamp;
    }

    /**
     * 生成带随机数的slug
     *
     * @param input 输入标题
     * @return 带随机数的slug
     */
    public static String generateSlugWithRandom(String input) {
        String baseSlug = generateSlug(input);
        // 生成一个4位数的随机数 (0000-9999)
        int random = (int) (Math.random() * 10000);
        // 使用String.format确保随机数至少是4位，不足前面补0
        return baseSlug + "-" + String.format("%04d", random);
    }

    /**
     * 验证slug格式是否正确
     * <p>
     * 有效slug应满足以下条件：
     * 1. 非空。
     * 2. 只包含小写字母、数字和连字符。
     * 3. 不能以连字符开头或结尾。
     * 4. 不能包含连续的连字符。
     * 5. 长度不超过 {@link #MAX_SLUG_LENGTH}。
     *
     * @param slug 要验证的slug
     * @return 是否为有效的slug格式
     */
    public static boolean isValidSlug(String slug) {
        if (slug == null || slug.trim().isEmpty()) {
            return false;
        }

        return slug.matches("^[a-z0-9]+(-[a-z0-9]+)*$") && slug.length() <= MAX_SLUG_LENGTH;
    }

    /**
     * 清理现有的slug。
     * 实际上是重新生成一个符合规范的slug，这对于处理旧数据或不规范的slug很有用。
     *
     * @param slug 现有的slug
     * @return 清理后的slug
     */
    public static String cleanSlug(String slug) {
        return generateSlug(slug);
    }
}