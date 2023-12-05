package com.cedarsoftware.util;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author John DeRegnaucourt (jdereg@gmail.com)
 *         <br>
 *         Copyright (c) Cedar Software LLC
 *         <br><br>
 *         Licensed under the Apache License, Version 2.0 (the "License");
 *         you may not use this file except in compliance with the License.
 *         You may obtain a copy of the License at
 *         <br><br>
 *         <a href="http://www.apache.org/licenses/LICENSE-2.0">License</a>
 *         <br><br>
 *         Unless required by applicable law or agreed to in writing, software
 *         distributed under the License is distributed on an "AS IS" BASIS,
 *         WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *         See the License for the specific language governing permissions and
 *         limitations under the License.
 */
public class TestReflectionUtils
{
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Inherited
    public @interface ControllerClass
    {
    }

    @ControllerClass
    static class Foo
    {
    }

    static class Bar extends Foo
    {
    }

    @ControllerClass
    static interface Baz
    {
    }

    static interface Qux extends Baz
    {
    }

    static class Beta implements Qux
    {
    }

    static class Alpha extends Beta
    {
    }

    static interface Blart
    {
    }

    static class Bogus implements Blart
    {
    }

    public interface AAA {
    }

    public interface BBB extends AAA {
    }

    public class CCC implements BBB, AAA {
    }

    @Test
    public void testConstructorIsPrivate() throws Exception {
        Constructor<ReflectionUtils> con = ReflectionUtils.class.getDeclaredConstructor();
        assertEquals(Modifier.PRIVATE, con.getModifiers() & Modifier.PRIVATE);
        con.setAccessible(true);

        assertNotNull(con.newInstance());
    }

    @Test
    public void testClassAnnotation()
    {
        Annotation a = AnnotationUtils.getClassAnnotation(Bar.class, ControllerClass.class);
        assertNotNull(a);
        assertTrue(a instanceof ControllerClass);

        a = AnnotationUtils.getClassAnnotation(Alpha.class, ControllerClass.class);
        assertNotNull(a);
        assertTrue(a instanceof ControllerClass);

        a = AnnotationUtils.getClassAnnotation(Bogus.class, ControllerClass.class);
        assertNull(a);

        a = AnnotationUtils.getClassAnnotation(CCC.class, ControllerClass.class);
        assertNull(a);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface ControllerMethod
    {
        String allow();
    }

    static class Foo1
    {
        @ControllerMethod(allow = "false")
        public void yelp()
        {
        }
    }

    static class Bar1 extends Foo1
    {
    }

    static interface Baz1
    {
        @ControllerMethod(allow = "false")
        void yelp();
    }

    static interface Qux1 extends Baz1
    {
    }

    static class Beta1 implements Qux1
    {
        public void yelp()
        {
        }
    }

    static class Alpha1 extends Beta1
    {
    }

    static interface Blart1
    {
        void yelp();
    }

    static class Bogus1 implements Blart1
    {
        public void yelp()
        {
        }
    }

    @Test
    public void testMethodAnnotation() throws Exception
    {
        Method m = ReflectionUtils.getMethod(Bar1.class, "yelp");
        Annotation a = AnnotationUtils.getMethodAnnotation(m, ControllerMethod.class);
        assertNotNull(a);
        assertTrue(a instanceof ControllerMethod);
        assertEquals("false", ((ControllerMethod) a).allow());

        m = ReflectionUtils.getMethod(Alpha1.class, "yelp");
        a = AnnotationUtils.getMethodAnnotation(m, ControllerMethod.class);
        assertNotNull(a);
        assertTrue(a instanceof ControllerMethod);

        m = ReflectionUtils.getMethod(Bogus1.class, "yelp");
        a = AnnotationUtils.getMethodAnnotation(m, ControllerMethod.class);
        assertNull(a);
    }

    @Test
    public void testDeepDeclaredFields() throws Exception
    {
        Calendar c = Calendar.getInstance();
        Collection<Field> fields = ReflectionUtils.getDeepDeclaredFields(c.getClass());
        assertTrue(fields.size() > 0);

        boolean miss = true;
        boolean found = false;
        for (Field field : fields)
        {
            if ("firstDayOfWeek".equals(field.getName()))
            {
                found = true;
                break;
            }

            if ("blart".equals(field.getName()))
            {
                miss = false;
            }
        }

        assertTrue(found);
        assertTrue(miss);
    }

    @Test
    public void testDeepDeclaredFieldMap() throws Exception
    {
        Calendar c = Calendar.getInstance();
        Map<String, Field> fields = ReflectionUtils.getDeepDeclaredFieldMap(c.getClass());
        assertTrue(fields.size() > 0);
        assertTrue(fields.containsKey("firstDayOfWeek"));
        assertFalse(fields.containsKey("blart"));


        Map<String, Field> test2 = ReflectionUtils.getDeepDeclaredFieldMap(Child.class);
        assertEquals(2, test2.size());
        assertTrue(test2.containsKey("com.cedarsoftware.util.TestReflectionUtils$Parent.foo"));
        assertFalse(test2.containsKey("com.cedarsoftware.util.TestReflectionUtils$Child.foo"));
    }

    @Test
    public void testGetClassName() throws Exception
    {
        assertEquals("null", ReflectionUtils.getClassName((Object)null));
        assertEquals("java.lang.String", ReflectionUtils.getClassName("item"));
        assertEquals("java.lang.String", ReflectionUtils.getClassName(""));
        assertEquals("null", ReflectionUtils.getClassName(null));
    }

    @Test
    public void testGetClassAnnotationsWithNull() throws Exception
    {
        assertNull(AnnotationUtils.getClassAnnotation(null, null));
    }

    @Test
    public void testCachingGetMethod()
    {
        Method m1 = ReflectionUtils.getMethod(TestReflectionUtils.class, "methodWithNoArgs");
        assert m1 != null;
        assert m1 instanceof Method;
        assert m1.getName() == "methodWithNoArgs";

        Method m2 = ReflectionUtils.getMethod(TestReflectionUtils.class, "methodWithNoArgs");
        assert m1 == m2;
    }

    @Test
    public void testGetMethod1Arg()
    {
        Method m1 = ReflectionUtils.getMethod(TestReflectionUtils.class, "methodWithOneArg", Integer.TYPE);
        assert m1 != null;
        assert m1 instanceof Method;
        assert m1.getName() == "methodWithOneArg";
    }

    @Test
    public void testGetMethod2Args()
    {
        Method m1 = ReflectionUtils.getMethod(TestReflectionUtils.class, "methodWithTwoArgs", Integer.TYPE, String.class);
        assert m1 != null;
        assert m1 instanceof Method;
        assert m1.getName() == "methodWithTwoArgs";
    }

    @Test
    public void testCallWithNoArgs()
    {
        TestReflectionUtils gross = new TestReflectionUtils();
        Method m1 = ReflectionUtils.getMethod(TestReflectionUtils.class, "methodWithNoArgs");
        assert "0".equals(ReflectionUtils.call(gross, m1));

        // Ensuring that methods from both reflection approaches are different
        Method m2 = ReflectionUtils.getMethod(gross, "methodWithNoArgs", 0);
        assert m1 != m2;
        assert m1.getName().equals(m2.getName());

        // Note, method not needed below
        assert "0".equals(ReflectionUtils.call(gross, "methodWithNoArgs"));
    }

    @Test
    public void testCallWith1Arg()
    {
        TestReflectionUtils gross = new TestReflectionUtils();
        Method m1 = ReflectionUtils.getMethod(TestReflectionUtils.class, "methodWithOneArg", int.class);
        assert "1".equals(ReflectionUtils.call(gross, m1, 5));

        Method m2 = ReflectionUtils.getMethod(gross, "methodWithOneArg", 1);
        assert m1 != m2;
        assert m1.getName().equals(m2.getName());

        assert "1".equals(ReflectionUtils.call(gross, "methodWithOneArg", 5));
    }

    @Test
    public void testCallWithTwoArgs()
    {
        TestReflectionUtils gross = new TestReflectionUtils();
        Method m1 = ReflectionUtils.getMethod(TestReflectionUtils.class, "methodWithTwoArgs", Integer.TYPE, String.class);
        assert "2".equals(ReflectionUtils.call(gross, m1, 9, "foo"));

        Method m2 = ReflectionUtils.getMethod(gross, "methodWithTwoArgs", 2);
        assert m1 != m2;
        assert m1.getName().equals(m2.getName());

        assert "2".equals(ReflectionUtils.call(gross, "methodWithTwoArgs", 9, "foo"));
    }

    @Test
    public void testGetMethodWithNullBean()
    {
        try
        {
            ReflectionUtils.getMethod(null, "foo", 1);
            fail("should not make it here");
        }
        catch (IllegalArgumentException e)
        {
            TestUtil.assertContainsIgnoreCase(e.getMessage(), "getMethod", "null");
        }
    }

    @Test
    public void testCallWithNullBean()
    {
        try
        {
            Method m1 = ReflectionUtils.getMethod(TestReflectionUtils.class, "methodWithNoArgs");
            ReflectionUtils.call(null, m1, 1);
            fail("should not make it here");
        }
        catch (IllegalArgumentException e)
        {
            TestUtil.assertContainsIgnoreCase(e.getMessage(), "cannot", "methodWithNoArgs", "null object");
        }
    }

    @Test
    public void testCallWithNullBeanAndNullMethod()
    {
        try
        {
            ReflectionUtils.call(null, (Method)null, 0);
            fail("should not make it here");
        }
        catch (IllegalArgumentException e)
        {
            TestUtil.assertContainsIgnoreCase(e.getMessage(), "null Method", "null bean");
        }
    }

    @Test
    public void testGetMethodWithNullMethod()
    {
        try
        {
            ReflectionUtils.getMethod(new Object(), null,0);
            fail("should not make it here");
        }
        catch (IllegalArgumentException e)
        {
            TestUtil.assertContainsIgnoreCase(e.getMessage(), "getMethod", "null", "method name");
        }
    }

    @Test
    public void testGetMethodWithNullMethodAndNullBean()
    {
        try
        {
            ReflectionUtils.getMethod(null, null,0);
            fail("should not make it here");
        }
        catch (IllegalArgumentException e)
        {
            TestUtil.assertContainsIgnoreCase(e.getMessage(), "getMethod", "null", "null instance");
        }
    }

    @Test
    public void testInvocationException()
    {
        TestReflectionUtils gross = new TestReflectionUtils();
        Method m1 = ReflectionUtils.getMethod(TestReflectionUtils.class, "pitaMethod");
        try
        {
            ReflectionUtils.call(gross, m1);
            fail("should never make it here");
        }
        catch (Exception e)
        {
            assert e instanceof RuntimeException;
            assert e.getCause() instanceof IllegalStateException;
        }
    }

    @Test
    public void testInvocationException2()
    {
        TestReflectionUtils gross = new TestReflectionUtils();
        try
        {
            ReflectionUtils.call(gross, "pitaMethod");
            fail("should never make it here");
        }
        catch (Exception e)
        {
            assert e instanceof RuntimeException;
            assert e.getCause() instanceof IllegalStateException;
        }
    }

    @Test
    public void testCantAccessNonPublic()
    {
        Method m1 = ReflectionUtils.getMethod(TestReflectionUtils.class, "notAllowed");
        assert m1 == null;

        try
        {
            ReflectionUtils.getMethod(new TestReflectionUtils(), "notAllowed", 0);
            fail("should not make it here");
        }
        catch (IllegalArgumentException e)
        {
            TestUtil.assertContainsIgnoreCase(e.getMessage(), "notAllowed", "not found");
        }
    }

    @Test
    public void testGetMethodWithNoArgs()
    {
        Method m1 = ReflectionUtils.getNonOverloadedMethod(TestReflectionUtils.class, "methodWithNoArgs");
        Method m2 = ReflectionUtils.getNonOverloadedMethod(TestReflectionUtils.class, "methodWithNoArgs");
        assert m1 == m2;
    }

    @Test
    public void testGetMethodWithNoArgsNull()
    {
        try
        {
            ReflectionUtils.getNonOverloadedMethod(null, "methodWithNoArgs");
            fail();
        }
        catch (Exception e) { }

        try
        {
            ReflectionUtils.getNonOverloadedMethod(TestReflectionUtils.class, null);
            fail();
        }
        catch (Exception e) { }
    }

    @Test
    public void testGetMethodWithNoArgsOverloaded()
    {
        try
        {
            ReflectionUtils.getNonOverloadedMethod(TestReflectionUtils.class, "methodWith0Args");
            fail("shant be here");
        }
        catch (Exception e)
        {
            TestUtil.assertContainsIgnoreCase(e.getMessage(), "methodWith0Args", "overloaded", "ambiguous");
        }
    }

    @Test
    public void testGetMethodWithNoArgsException()
    {
        try
        {
            ReflectionUtils.getNonOverloadedMethod(TestReflectionUtils.class, "methodWithNoArgz");
            fail("shant be here");
        }
        catch (Exception e)
        {
            TestUtil.assertContainsIgnoreCase(e.getMessage(), "methodWithNoArgz", "not found");
        }
    }

    @Test
    public void testGetClassNameFromByteCode()
    {
        Class<?> c = TestReflectionUtils.class;
        String className = c.getName();
        String classAsPath = className.replace('.', '/') + ".class";
        InputStream stream = c.getClassLoader().getResourceAsStream(classAsPath);
        byte[] byteCode = IOUtilities.inputStreamToBytes(stream);

        try
        {
            className = ReflectionUtils.getClassNameFromByteCode(byteCode);
            assert "com.cedarsoftware.util.TestReflectionUtils".equals(className);
        }
        catch (Exception e)
        {
            fail("This should not throw an exception");
        }
    }

    @Test
    public void testGetMethodWithDifferentClassLoaders()
    {
        ClassLoader testClassLoader1 = new TestClassLoader();
        ClassLoader testClassLoader2 = new TestClassLoader();
        try
        {
            Class clazz1 = testClassLoader1.loadClass("com.cedarsoftware.util.TestClass");
            Method m1 = ReflectionUtils.getMethod(clazz1,"getPrice", (Class<?>[])null);

            Class clazz2 = testClassLoader2.loadClass("com.cedarsoftware.util.TestClass");
            Method m2 = ReflectionUtils.getMethod(clazz2,"getPrice", (Class<?>[])null);

            // Should get different Method instances since this class was loaded via two different ClassLoaders.
            assert m1 != m2;
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetMethod2WithDifferentClassLoaders()
    {
        ClassLoader testClassLoader1 = new TestClassLoader();
        ClassLoader testClassLoader2 = new TestClassLoader();
        try
        {
            Class<?> clazz1 = testClassLoader1.loadClass("com.cedarsoftware.util.TestClass");
            Object foo = clazz1.getDeclaredConstructor().newInstance();
            Method m1 = ReflectionUtils.getMethod(foo, "getPrice", 0);

            Class<?> clazz2 = testClassLoader2.loadClass("com.cedarsoftware.util.TestClass");
            Object bar = clazz2.getDeclaredConstructor().newInstance();
            Method m2 = ReflectionUtils.getMethod(bar,"getPrice", 0);

            // Should get different Method instances since this class was loaded via two different ClassLoaders.
            assert m1 != m2;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetMethod3WithDifferentClassLoaders()
    {
        ClassLoader testClassLoader1 = new TestClassLoader();
        ClassLoader testClassLoader2 = new TestClassLoader();
        try
        {
            Class clazz1 = testClassLoader1.loadClass("com.cedarsoftware.util.TestClass");
            Method m1 = ReflectionUtils.getNonOverloadedMethod(clazz1, "getPrice");

            Class clazz2 = testClassLoader2.loadClass("com.cedarsoftware.util.TestClass");
            Method m2 = ReflectionUtils.getNonOverloadedMethod(clazz2,"getPrice");

            // Should get different Method instances since this class was loaded via two different ClassLoaders.
            assert m1 != m2;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public String methodWithNoArgs()
    {
        return "0";
    }

    public String methodWith0Args()
    {
        return "0";
    }
    public String methodWith0Args(int justKidding)
    {
        return "0";
    }

    public String methodWithOneArg(int x)
    {
        return "1";
    }

    public String methodWithTwoArgs(int x, String y)
    {
        return "2";
    }

    public String pitaMethod()
    {
        throw new IllegalStateException("this always blows up");
    }

    protected void notAllowed()
    {
    }

    private class Parent {
        private String foo;
    }

    private class Child extends Parent {
        private String foo;
    }

    public static class TestClassLoader extends URLClassLoader
    {
        public TestClassLoader()
        {
            super(getClasspathURLs());
        }

        public Class<?> loadClass(String name) throws ClassNotFoundException
        {
            if (name.contains("TestClass"))
            {
                return super.findClass(name);
            }

            return super.loadClass(name);
        }

        private static URL[] getClasspathURLs()
        {
            // If this were Java 8 or earlier, we could have done:
//            URL[] urls = ((URLClassLoader)getSystemClassLoader()).getURLs();
            try
            {
                URL url = TestReflectionUtils.class.getClassLoader().getResource("test.txt");
                String path = url.getPath();
                path = path.substring(0,path.length() - 8);

                List<URL> urls = new ArrayList<>();
                urls.add(new URL("file:" + path));

                URL[] urlz = urls.toArray(new URL[1]);
                return urlz;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }
        }
    }


    @Retention(RetentionPolicy.RUNTIME)
    private @interface TestAnnotation {}

    @TestAnnotation
    private static class AnnotatedTestClass {
        @Override
        public String toString()
        {
            return super.toString();
        }
    }

    private static class TestClass {
        private int field1;
        public int field2;
    }

    @Test
    void testGetClassAnnotation() {
        assertNotNull(ReflectionUtils.getClassAnnotation(AnnotatedTestClass.class, TestAnnotation.class));
        assertNull(ReflectionUtils.getClassAnnotation(TestClass.class, TestAnnotation.class));
    }

    @Test
    void testGetMethodAnnotation() throws NoSuchMethodException {
        Method method = AnnotatedTestClass.class.getDeclaredMethod("toString");
        assertNull(ReflectionUtils.getMethodAnnotation(method, TestAnnotation.class));
    }

    @Test
    void testGetMethod() throws NoSuchMethodException {
        Method method = ReflectionUtils.getMethod(TestClass.class, "toString");
        assertNotNull(method);
        assertEquals("toString", method.getName());

        assertNull(ReflectionUtils.getMethod(TestClass.class, "nonExistentMethod"));
    }

    @Test
    void testGetDeepDeclaredFields() {
        Collection<Field> fields = ReflectionUtils.getDeepDeclaredFields(TestClass.class);
        assertEquals(2, fields.size()); // field1 and field2
    }

    @Test
    void testGetDeepDeclaredFieldMap() {
        Map<String, Field> fieldMap = ReflectionUtils.getDeepDeclaredFieldMap(TestClass.class);
        assertEquals(2, fieldMap.size());
        assertTrue(fieldMap.containsKey("field1"));
        assertTrue(fieldMap.containsKey("field2"));
    }

    @Test
    void testCall() throws NoSuchMethodException {
        TestClass testInstance = new TestClass();
        Method method = TestClass.class.getMethod("toString");
        String result = (String) ReflectionUtils.call(testInstance, method);
        assertEquals(testInstance.toString(), result);
    }

    @Test
    void testCallWithArgs() throws NoSuchMethodException {
        TestClass testInstance = new TestClass();
        String methodName = "equals";
        Object[] args = new Object[]{testInstance};
        Boolean result = (Boolean) ReflectionUtils.call(testInstance, methodName, args);
        assertTrue(result);
    }

    @Test
    void testGetNonOverloadedMethod() {
        Method method = ReflectionUtils.getNonOverloadedMethod(TestClass.class, "toString");
        assertNotNull(method);
        assertEquals("toString", method.getName());
    }
}

