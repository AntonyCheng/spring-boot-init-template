package top.sharehome.springbootinittemplate.utils.field;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 实体类字段（属性）工具类
 * 所有操作仅针对标准的POJO类，POJO类中如果还存在其他实体类，则当作普通字段处理；
 *
 * @author AntonyCheng
 */
@Slf4j
public class FieldUtils {

    /**
     * 是否存在为null的字段（属性）
     *
     * @param obj 实体类
     * @return 如果有为null的字段，那么就返回true；所有字段都不为null则返回false。
     */
    public static boolean existNull(Object obj) {
        Class<?> aClass = obj.getClass();
        Field[] fields = aClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
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
     * 是否存在为null的字段（属性），可排除不判断的字段（属性）
     *
     * @param obj     实体类
     * @param exclude 需要排除的字段（属性）名称
     * @return 如果有为null的字段，那么就返回true；所有字段都不为null则返回false。
     */
    public static boolean existNull(Object obj, String... exclude) {
        Class<?> aClass = obj.getClass();
        Field[] fields = aClass.getDeclaredFields();
        List<String> excludeList = Arrays.stream(exclude).collect(Collectors.toList());
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
     * 是否不存在为null的字段（属性）
     *
     * @param obj 实体类
     * @return 所有字段都不为null则返回true；如果有为null的字段，那么就返回false。
     */
    public static boolean notExistNull(Object obj) {
        return !existNull(obj);
    }

    /**
     * 是否不存在为null的字段（属性），可排除不判断的字段（属性）
     *
     * @param obj     实体类
     * @param exclude 需要排除的字段（属性）名称
     * @return 所有字段都不为null则返回true；如果有为null的字段，那么就返回false。
     */
    public static boolean notExistNull(Object obj, String... exclude) {
        return !existNull(obj, exclude);
    }

    /**
     * 是否存在为空值的字段（属性）
     *
     * @param obj 实体类
     * @return 如果有为空值的字段，那么就返回true；所有字段都不为空值则返回false。
     */
    public static boolean existEmpty(Object obj) {
        Class<?> aClass = obj.getClass();
        Field[] fields = aClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
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
            if (fieldValue instanceof CharSequence && ((CharSequence) fieldValue).length() == 0) {
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
     * 是否存在为空值的字段（属性），可排除不判断的字段（属性）
     *
     * @param obj     实体类
     * @param exclude 需要排除的字段（属性）名称
     * @return 如果有为空值的字段，那么就返回true；所有字段都不为空值则返回false。
     */
    public static boolean existEmpty(Object obj, String... exclude) {
        Class<?> aClass = obj.getClass();
        Field[] fields = aClass.getDeclaredFields();
        List<String> excludeList = Arrays.stream(exclude).collect(Collectors.toList());
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
            if (fieldValue instanceof CharSequence && ((CharSequence) fieldValue).length() == 0) {
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
     * 是否不存在为空值的字段（属性）
     *
     * @param obj 实体类
     * @return 所有字段都不为空值则返回true；如果有为空值的字段，那么就返回false。
     */
    public static boolean notExistEmpty(Object obj) {
        return !existEmpty(obj);
    }

    /**
     * 是否不存在为空值的字段（属性），可排除不判断的字段（属性）
     *
     * @param obj     实体类
     * @param exclude 需要排除的字段（属性）名称
     * @return 所有字段都不为空值则返回true；如果有为空值的字段，那么就返回false。
     */
    public static boolean notExistEmpty(Object obj, String... exclude) {
        return !existEmpty(obj, exclude);
    }

}