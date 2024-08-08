package com.custom.trade.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommonUtil {

    /**
     *
     * @param List<?>
     * @return boolean
     */
    public static boolean isEmpty(List<?> object) {
        if(object == null || object.size() <=0) {
            return true;
        }
        return false;
    }

    /**
     *
     * @param Map<?, ?>
     * @return boolean
     */
    public static boolean isEmpty(Map<?, ?> object) {
        if(object == null || object.size() <=0) {
            return true;
        }
        return false;
    }

    /**
     *
     * @param String
     * @return boolean
     */
    public static boolean isEmpty(String str) {
        if(str == null || "".equals(str.trim())
                || str.length() == 0 || str.toLowerCase() == "null") {
            return true;
        }
        return false;
    }

    /**
     *
     * @param Object[]
     * @return
     */
    public static boolean isEmpty(Object[] objects) {
        if(objects != null && objects.length >0) {
            for(Object obj : objects) {
                if(obj instanceof String) {
                    if((String)obj == null || "".equals((String)obj) ||"NULL".equals((String)obj) || "null".equals((String)obj) || "".equals((String)obj.toString().trim())) {
                        return true;
                    }
                }

                if(obj instanceof Integer) {
                    if((Integer)obj == 0) {
                        return true;
                    }
                }

                if(obj instanceof Long) {
                    if((Long)obj == 0) {
                        return true;
                    }
                }


                if(obj instanceof Double) {
                    if((Double)obj == 0.0) {
                        return true;
                    }
                }


                if(obj instanceof String[]) {
                    if((String[])obj == null || ((String[]) obj).length <= 0) {
                        return true;
                    }
                }


                if(obj instanceof int[]) {
                    if((int[])obj == null || ((int[]) obj).length <= 0) {
                        return true;
                    }
                }

                if(obj instanceof List) {
                    if(obj == null || ((ArrayList<?>) obj ).size() <= 0) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /*
     * @param byte[]
     * @return
     */
    public static boolean isEmpty(byte[] obj) {
        if(obj == null || obj.length <= 0) {
            return true;
        }
        return false;
    }
}
