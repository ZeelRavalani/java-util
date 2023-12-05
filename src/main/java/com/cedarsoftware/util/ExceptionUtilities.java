package com.cedarsoftware.util;

/**
 * Useful Exception Utilities
 * @author Keneth Partlow
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
public final class ExceptionUtilities
{
    private ExceptionUtilities() {
        super();
    }


    /**
     * Safely Ignore a Throwable or rethrow if it is a Throwable that should
     * not be ignored.
     * @param t Throwable to possibly ignore (ThreadDeath and OutOfMemory are not ignored).
     */
    public static void safelyIgnoreException(Throwable t)
    {
        if (t instanceof OutOfMemoryError)
        {
            throw (OutOfMemoryError) t;
        }
    }

    /**
     * @return Throwable representing the actual cause (most nested exception).
     */
    public static Throwable getDeepestException(Throwable e)
    {
        while (e.getCause() != null)
        {
            e = e.getCause();
        }

        return e;
    }

}
