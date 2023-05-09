/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpoker;

import java.text.MessageFormat;

/**
 *
 * @author luisfernandomedinallorenti
 */
public class ExceptionUtil {
    public static final String NULL_ERR_MSG = "El argumento {0} no puede ser nulo.";
        public static final String LENGTH_ERR_MSG =
        "El argumento {0} no puede ser nulo y debe tener una longitud de {1}.";
        
        private ExceptionUtil() {
        }
        
        public static void checkNullArgument (Object o, String name) {
            if (o == null) {
                throw new IllegalArgumentException(MessageFormat.format(NULL_ERR_MSG , name));
            }
        }
        public static <T> void checkArrayLengthArgument (T[] a, String name, int l) {
            if (a == null || a.length != l) {
                throw new IllegalArgumentException(MessageFormat.format(LENGTH_ERR_MSG, name, l));
            }
        }
        public static void checkArgument(boolean throwEx, String msg, Object... args){
            if (throwEx) {
                throw new IllegalArgumentException(MessageFormat.format(msg, args));
            }
        }
}
