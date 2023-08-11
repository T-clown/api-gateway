package com.api.gateway.utils;

import com.api.gateway.constants.MatchMethodEnum;
import com.api.gateway.exception.GatewayException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StringTools {
    private final static Logger LOGGER = LoggerFactory.getLogger(StringTools.class);
    public static final String CHARSET_UTF8 = "UTF-8";
    /**
     * key: regex
     */
    private static final Map<String, Pattern> PATTERN_MAP = new HashMap<>();

    /**
     * @param value
     * @param matchMethod {@link MatchMethodEnum}
     * @param matchRule
     * @return
     */
    public static boolean match(String value, Byte matchMethod, String matchRule) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        MatchMethodEnum methodEnum = MatchMethodEnum.getByCode(matchMethod);
        return switch (methodEnum) {
            case EQUAL -> value.equals(matchRule);
            case REGEX -> {
                // 将 Pattern 缓存下来，避免反复编译Pattern
                Pattern p = PATTERN_MAP.computeIfAbsent(matchRule, Pattern::compile);
                Matcher m = p.matcher(value);
                yield m.matches();
            }
            case LIKE -> value.contains(matchRule);
            default -> throw new GatewayException("invalid matchMethod");
        };
    }

    public static String byteToStr(byte[] data) {
        try {
            return new String(data, CHARSET_UTF8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, CHARSET_UTF8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String md5Digest(String value, String salt) {
        String plainText = value + salt;
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(
                    plainText.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有这个md5算法！");
        }
        String md5code = new BigInteger(1, secretBytes).toString(16);
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }

}
