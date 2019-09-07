package eu.thesimplecloud.launcher.invoker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 07.09.2019
 * Time: 14:11
 */
public class MethodInvokeHelper {

    public static Object invoke(Method method, Object source, Object[] array) throws InvocationTargetException, IllegalAccessException {
        return method.invoke(source, (Object[]) array);
    }

}
