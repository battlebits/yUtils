package me.flame.utils.permissions.injector;

/**
 * Este codigo nao pertence ao autor do plugin.
 * Este codigo pertence ao criador do PermissionEX
 * 
 */
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import me.flame.utils.nms.Utils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

public class RegExpMatcher implements PermissionMatcher {
	public static final String RAW_REGEX_CHAR = "$";
	protected static Pattern rangeExpression = Pattern.compile("(\\d+)-(\\d+)");

	private final Object patternCache = CacheBuilder.newBuilder().maximumSize(500).build(new CacheLoader<String, Pattern>() {
		@Override
		public Pattern load(String permission) throws Exception {
			return createPattern(permission);
		}
	});

	@Override
	public boolean isMatches(String expression, String permission) {
		try {
			Pattern permissionMatcher = (Pattern) Utils.getMethod(patternCache.getClass(), "get").invoke(patternCache, expression);
			return permissionMatcher.matcher(permission).matches();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return false;
	}

	protected static Pattern createPattern(String expression) {
		try {
			return Pattern.compile(prepareRegexp(expression), Pattern.CASE_INSENSITIVE);
		} catch (PatternSyntaxException e) {
			return Pattern.compile(Pattern.quote(expression), Pattern.CASE_INSENSITIVE);
		}
	}

	public static String prepareRegexp(String expression) {
		if (expression.startsWith("-")) {
			expression = expression.substring(1);
		}
		if (expression.startsWith("#")) {
			expression = expression.substring(1);
		}
		boolean rawRegexp = expression.startsWith(RAW_REGEX_CHAR);
		if (rawRegexp) {
			expression = expression.substring(1);
		}
		String regexp = rawRegexp ? expression : expression.replace(".", "\\.").replace("*", "(.*)");
		return regexp;
	}
}