package top.sharehome.springbootinittemplate.utils.field;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 实体类字段（属性）工具类
 * 所有操作仅针对标准的POJO类，POJO类中如果还存在其他实体类，则当作普通字段处理；
 *
 * @author AntonyCheng
 */
@Slf4j
public class FieldUtils {

    /**
     * 是否所有字段（属性）全为null，可排除不判断的字段（属性）
     *
     * @param obj 实体类
     * @return 如果所有字段（属性）全部为null，那么就返回true；否则返回false。
     */
    public static boolean allNull(Object obj, String... exclude) {
        Class<?> aClass = obj.getClass();
        Field[] fields = aClass.getDeclaredFields();
        List<String> excludeList = Arrays.stream(exclude).toList();
        for (Field field : fields) {
            field.setAccessible(true);
            if (excludeList.contains(field.getName())) {
                continue;
            }
            int modifiers = field.getModifiers();
            // 如果是静态属性或者final属性则不进行判断
            if (Modifier.isStatic(modifiers)
                || Modifier.isFinal(modifiers)) {
                continue;
            }
            Object fieldValue = null;
            try {
                fieldValue = field.get(obj);
            } catch (IllegalAccessException e) {
                log.error(e.getMessage());
            }
            if (fieldValue != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否所有字段（属性）全为空值，可排除不判断的字段（属性）
     *
     * @param obj     实体类
     * @param exclude 需要排除的字段（属性）名称
     * @return 如果所有字段（属性）全部为空值，那么就返回true；否则返回false。
     */
    public static boolean allEmpty(Object obj, String... exclude) {
        Class<?> aClass = obj.getClass();
        Field[] fields = aClass.getDeclaredFields();
        List<String> excludeList = Arrays.stream(exclude).toList();
        for (Field field : fields) {
            field.setAccessible(true);
            if (excludeList.contains(field.getName())) {
                continue;
            }
            int modifiers = field.getModifiers();
            // 如果是静态属性或者final属性则不进行判断
            if (Modifier.isStatic(modifiers)
                || Modifier.isFinal(modifiers)) {
                continue;
            }
            Object fieldValue = null;
            try {
                fieldValue = field.get(obj);
            } catch (IllegalAccessException e) {
                log.error(e.getMessage());
            }
            if (fieldValue != null) {
                if (fieldValue instanceof CharSequence && ((CharSequence) fieldValue).isEmpty()) {
                    continue;
                }
                if (fieldValue instanceof Collection && ((Collection<?>) fieldValue).isEmpty()) {
                    continue;
                }
                if (fieldValue.getClass().isArray() && Array.getLength(fieldValue) == 0) {
                    continue;
                }
                if (fieldValue instanceof Map<?, ?> && ((Map<?, ?>) fieldValue).isEmpty()) {
                    continue;
                }
                return false;
            }
        }
        return true;
    }

    /**
     * 是否所有字段（属性）全为空白值，可排除不判断的字段（属性）
     *
     * @param obj     实体类
     * @param exclude 需要排除的字段（属性）名称
     * @return 如果所有字段（属性）全部为空值，那么就返回true；否则返回false。
     */
    public static boolean allBlank(Object obj, String... exclude) {
        Class<?> aClass = obj.getClass();
        Field[] fields = aClass.getDeclaredFields();
        List<String> excludeList = Arrays.stream(exclude).toList();
        for (Field field : fields) {
            field.setAccessible(true);
            if (excludeList.contains(field.getName())) {
                continue;
            }
            int modifiers = field.getModifiers();
            // 如果是静态属性或者final属性则不进行判断
            if (Modifier.isStatic(modifiers)
                || Modifier.isFinal(modifiers)) {
                continue;
            }
            Object fieldValue = null;
            try {
                fieldValue = field.get(obj);
            } catch (IllegalAccessException e) {
                log.error(e.getMessage());
            }
            if (fieldValue != null) {
                if (fieldValue instanceof CharSequence && StringUtils.isBlank((CharSequence) fieldValue)) {
                    continue;
                }
                if (fieldValue instanceof Collection && ((Collection<?>) fieldValue).isEmpty()) {
                    continue;
                }
                if (fieldValue.getClass().isArray() && Array.getLength(fieldValue) == 0) {
                    continue;
                }
                if (fieldValue instanceof Map<?, ?> && ((Map<?, ?>) fieldValue).isEmpty()) {
                    continue;
                }
                return false;
            }
        }
        return true;
    }

    /**
     * 是否所有字段（属性）全不为null，可排除不判断的字段（属性）
     *
     * @param obj     实体类
     * @param exclude 需要排除的字段（属性）名称
     * @return 如果没有为null的字段，那么就返回true；所有字段都不为null则返回false。
     */
    public static boolean notNull(Object obj, String... exclude) {
        return !existNull(obj, exclude);
    }

    /**
     * 是否所有字段（属性）全不为空值，可排除不判断的字段（属性）
     *
     * @param obj     实体类
     * @param exclude 需要排除的字段（属性）名称
     * @return 如果没有为空值的字段，那么就返回true；所有字段都不为空值则返回false。
     */
    public static boolean notEmpty(Object obj, String... exclude) {
        return !existEmpty(obj, exclude);
    }

    /**
     * 是否所有字段（属性）全不为空白值，可排除不判断的字段（属性）
     *
     * @param obj     实体类
     * @param exclude 需要排除的字段（属性）名称
     * @return 如果没有为空值的字段，那么就返回true；所有字段都不为空值则返回false。
     */
    public static boolean notBlank(Object obj, String... exclude) {
        return !existBlank(obj,exclude);
    }

    /**
     * 是否存在为null的字段（属性），可排除不判断的字段（属性）
     *
     * @param obj     实体类
     * @param exclude 需要排除的字段（属性）名称
     * @return 如果有为null的字段，那么就返回true；所有字段都不为null则返回false。
     */
    public static boolean existNull(Object obj, String... exclude) {
        Class<?> aClass = obj.getClass();
        Field[] fields = aClass.getDeclaredFields();
        List<String> excludeList = Arrays.stream(exclude).toList();
        for (Field field : fields) {
            field.setAccessible(true);
            if (excludeList.contains(field.getName())) {
                continue;
            }
            int modifiers = field.getModifiers();
            // 如果是静态属性或者final属性则不进行判断
            if (Modifier.isStatic(modifiers)
                || Modifier.isFinal(modifiers)) {
                continue;
            }
            Object fieldValue = null;
            try {
                fieldValue = field.get(obj);
            } catch (IllegalAccessException e) {
                log.error(e.getMessage());
            }
            if (fieldValue == null) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否存在为空值的字段（属性），可排除不判断的字段（属性）
     *
     * @param obj     实体类
     * @param exclude 需要排除的字段（属性）名称
     * @return 如果有为空值的字段，那么就返回true；所有字段都不为空值则返回false。
     */
    public static boolean existEmpty(Object obj, String... exclude) {
        Class<?> aClass = obj.getClass();
        Field[] fields = aClass.getDeclaredFields();
        List<String> excludeList = Arrays.stream(exclude).toList();
        for (Field field : fields) {
            field.setAccessible(true);
            if (excludeList.contains(field.getName())) {
                continue;
            }
            int modifiers = field.getModifiers();
            // 如果是静态属性或者final属性则不进行判断
            if (Modifier.isStatic(modifiers)
                || Modifier.isFinal(modifiers)) {
                continue;
            }
            Object fieldValue = null;
            try {
                fieldValue = field.get(obj);
            } catch (IllegalAccessException e) {
                log.error(e.getMessage());
            }
            if (fieldValue == null) {
                return true;
            }
            if (fieldValue instanceof CharSequence && ((CharSequence) fieldValue).isEmpty()) {
                return true;
            }
            if (fieldValue instanceof Collection && ((Collection<?>) fieldValue).isEmpty()) {
                return true;
            }
            if (fieldValue.getClass().isArray() && Array.getLength(fieldValue) == 0) {
                return true;
            }
            if (fieldValue instanceof Map<?, ?> && ((Map<?, ?>) fieldValue).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否存在为空白值的字段（属性），可排除不判断的字段（属性）
     *
     * @param obj     实体类
     * @param exclude 需要排除的字段（属性）名称
     * @return 如果有为空值的字段，那么就返回true；所有字段都不为空值则返回false。
     */
    public static boolean existBlank(Object obj, String... exclude) {
        Class<?> aClass = obj.getClass();
        Field[] fields = aClass.getDeclaredFields();
        List<String> excludeList = Arrays.stream(exclude).toList();
        for (Field field : fields) {
            field.setAccessible(true);
            if (excludeList.contains(field.getName())) {
                continue;
            }
            int modifiers = field.getModifiers();
            // 如果是静态属性或者final属性则不进行判断
            if (Modifier.isStatic(modifiers)
                || Modifier.isFinal(modifiers)) {
                continue;
            }
            Object fieldValue = null;
            try {
                fieldValue = field.get(obj);
            } catch (IllegalAccessException e) {
                log.error(e.getMessage());
            }
            if (fieldValue == null) {
                return true;
            }
            if (fieldValue instanceof CharSequence && StringUtils.isBlank((CharSequence) fieldValue)) {
                return true;
            }
            if (fieldValue instanceof Collection && ((Collection<?>) fieldValue).isEmpty()) {
                return true;
            }
            if (fieldValue.getClass().isArray() && Array.getLength(fieldValue) == 0) {
                return true;
            }
            if (fieldValue instanceof Map<?, ?> && ((Map<?, ?>) fieldValue).isEmpty()) {
                return true;
            }
        }
        return false;
    }

}