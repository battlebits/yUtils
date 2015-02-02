package me.flame.utils.permissions.injector;

/**
 * Este codigo nao pertence ao autor do plugin.
 * Este codigo pertence ao criador do PermissionEX
 * 
 */
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegExpMatcher implements PermissionMatcher {
	public static final String RAW_REGEX_CHAR = "$";
	protected static Pattern rangeExpression = Pattern.compile("(\\d+)-(\\d+)");

	private final LoadingCache<String, Pattern> patternCache = CacheBuilder.newBuilder().maximumSize(500).build(new CacheLoader<String, Pattern>() {
		@Override
		public Pattern load(String permission) throws Exception {
			return createPattern(permission);
		}
	});

	@Override
	public boolean isMatches(String expression, String permission) {
		try {
			Pattern permissionMatcher = patternCache.get(expression);
			return permissionMatcher.matcher(permission).matches();
		} catch (ExecutionException e) {
			e.printStackTrace();
			return false;
		}
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