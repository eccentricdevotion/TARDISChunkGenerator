package me.eccentric_nz.tardischunkgenerator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.StringUtils;

import java.io.*;

/**
 * This class provides various  methods for retrieving Exception stacktrace as a String in full or shortened version.
 * Shortened version of the stacktrace will contain concise information focusing on specific package or subpackage while
 * removing long parts of irrelevant stacktrace. This could be very useful for logging in web-based architecture where
 * stacktrace may contain long parts of server provided classes trace that could be eliminated with the methods of this
 * class while retaining important parts of the stacktrace relating to user's packages. Also the same utility (starting
 * from version 1.5.0.3) allows to filter and shorten stacktrace as a string the same way as the stacktrace extracted
 * from exception. So, essentially stacktraces could be filtered "on the fly" at run time or later on from any text
 * source such as log.</p>
 *
 * @author Michael Gantman
 */
public class TextUtils {

    private static final Logger logger = (Logger) LogManager.getRootLogger();
    private static final String STANDARD_STAKTRACE_PREFIX = "at ";
    private static final String SKIPPING_LINES_STRING = "\t...";
    private static final String CAUSE_STAKTRACE_PREFIX = "Caused by:";
    private static final String SUPPRESED_STAKTRACE_PREFIX = "Suppressed:";
    private static final String RELEVANT_PACKAGE_SYSTEM_EVIRONMENT_VARIABLE = "MGNT_RELEVANT_PACKAGE";
    private static final String RELEVANT_PACKAGE_SYSTEM_PROPERTY = "mgnt.relevant.package";
    /*
     * Strings defined bellow are for the use of methods getStacktrace() of this class
     */
    private static String RELEVANT_PACKAGE = null;

    static {
        initRelevantPackageFromSystemProperty();
    }

    private static void initRelevantPackageFromSystemProperty() {
        String relevantPackage = System.getProperty(RELEVANT_PACKAGE_SYSTEM_PROPERTY);
        if (StringUtils.isBlank(relevantPackage)) {
            relevantPackage = System.getenv(RELEVANT_PACKAGE_SYSTEM_EVIRONMENT_VARIABLE);
        }
        if (StringUtils.isNotBlank(relevantPackage)) {
            setRelevantPackage(relevantPackage);
        }
    }

    /**
     * <p>
     * This method retrieves a stacktrace from {@link Throwable} as a String in full or shortened format. Shortened
     * format skips the lines in the stacktrace that do not start with a configurable package prefix and replaces them
     * with "..." line. The stacktrace is viewed as consisting possibly of several parts. If stacktrace contains {@code
     * "caused by"} or {@code "Suppressed"} section, each such section for the purposes of this utility is called
     * "Singular stacktrace". For example the stacktrace bellow contains 2 singular stacktraces: First is 4 top lines
     * and the second starting from the line {@code "Caused by: ..."} and to the end.<br>
     * <br>
     * </p>
     * java.lang.Exception: Bad error<br> &emsp; at com.plain.analytics.v2.utils.test.UtilsTester.TestGetStackTrace(UtilsTester.java:80)<br>
     * &emsp; at com.plain.analytics.v2.utils.test.UtilsTester.run(UtilsTester.java:30)<br> &emsp; at
     * com.plain.analytics.v2.utils.test.UtilsTester.main(UtilsTester.java:25)<br> Caused by:
     * java.lang.NumberFormatException: For input string: "Hello"<br> &emsp; at java.lang.NumberFormatException.forInputString(Unknown
     * Source)<br> &emsp; at java.lang.Integer.parseInt(Unknown Source)<br> &emsp; at java.lang.Integer.parseInt(Unknown
     * Source)<br> &emsp; at com.plain.analytics.v2.utils.test2.UtilsTesterHelper.parseInt(UtilsTesterHelper.java:8)<br>
     * &emsp; at com.plain.analytics.v2.utils.test.UtilsTester$Invoker.runParser(UtilsTester.java:97)<br> &emsp; at
     * com.plain.analytics.v2.utils.test2.UtilsTesterHelper.innerInvokeParser(UtilsTesterHelper.java:17)<br> &emsp; at
     * com.plain.analytics.v2.utils.test2.UtilsTesterHelper.invokeParser(UtilsTesterHelper.java:12)<br> &emsp; at
     * com.plain.analytics.v2.utils.test.UtilsTester.TestGetStackTrace(UtilsTester.java:76)<br> &emsp; ... 2 more<br>
     * <br>
     * <p>
     * The way this method shortens the stacktrace is as follows. Each "singular" stacktraces are analyzed and shortened
     * separately. For each singular stacktrace the error message is always printed. Then all the lines that follow are
     * printed even if they do not start with prefix specified by
     * <b>relevantPackage</b>. Once the first line with the prefix is found this line and all immediately following
     * lines that start with the relevant package prefix are printed as well. The first line that does not start with
     * the prefix after a section of the lines that did is also printed. But all the following lines that do not start
     * with the prefix are skipped and replaced with a single line "...". If at some point within the stacktrace a line
     * that starts with the prefix is encountered again this line and all the following line that start with the prefix
     * + one following line that does not start with the prefix are printed in. And so on. Here is an example: Assume
     * that exception above was passed as a parameter to this method and parameter <b>relevantPackage</b> is set to
     * {@code "com.plain.analytics.v2.utils.test."} which means that the lines starting with that prefix are the
     * important or "relevant" lines. (Also the parameter <b>cutTBS</b> set to true which means that stacktrace should
     * be shortened at all. In this case the result of this method should be as follows:<br>
     * <br>
     * </p>
     * java.lang.Exception: Bad error<br> &emsp; at com.plain.analytics.v2.utils.test.UtilsTester.TestGetStackTrace(UtilsTester.java:80)<br>
     * &emsp; at com.plain.analytics.v2.utils.test.UtilsTester.run(UtilsTester.java:30)<br> &emsp; at
     * com.plain.analytics.v2.utils.test.UtilsTester.main(UtilsTester.java:25)<br> Caused by:
     * java.lang.NumberFormatException: For input string: "Hello"<br> &emsp; at java.lang.NumberFormatException.forInputString(Unknown
     * Source)<br> &emsp; at java.lang.Integer.parseInt(Unknown Source)<br> &emsp; at java.lang.Integer.parseInt(Unknown
     * Source)<br> &emsp; at com.plain.analytics.v2.utils.test2.UtilsTesterHelper.parseInt(UtilsTesterHelper.java:8)<br>
     * &emsp; at com.plain.analytics.v2.utils.test.UtilsTester$Invoker.runParser(UtilsTester.java:97)<br> &emsp; at
     * com.plain.analytics.v2.utils.test2.UtilsTesterHelper.innerInvokeParser(UtilsTesterHelper.java:17)<br> &emsp;
     * ...<br> &emsp; at com.plain.analytics.v2.utils.test.UtilsTester.TestGetStackTrace(UtilsTester.java:76)<br> &emsp;
     * ... 2 more<br>
     * <br>
     * <p>
     * Note that the first singular stacktrace is printed in full because all the lines start with the required prefix.
     * The second singular stacktrace prints the first 7 lines because at first all the lines are printed until the
     * first line with relevant prefix is found, and then all the lines with the prefix (one in our case) are printed +
     * plus one following line without the prefix. And then the second line without the prefix (3d from the bottom) is
     * skipped and replaced with line "...". But then again we encounter a line with the prefix which is printed and
     * finally the last line is printed because it is the first line without prefix following the one with the prefix.
     * In this particular example only one line was skipped over which is not very much, but for web-based environments
     * for the long stacktraces that contain long traces of server related classes this method could be very effective
     * in removing irrelevant lines and leaving only application related lines making log files more concise and
     * clear.<br>
     * <br>
     * </p>
     * <b>Important Note:</b> Parameter <b>relevantPackage</b> may be left null. In this case the value of relevant
     * package prefix will be taken from
     * <b>RelevantPackage</b> property (See the methods {@link #setRelevantPackage(String)} and {@link
     * #getRelevantPackage()}). Using method {@link #setRelevantPackage(String)} to set the value will preset the value
     * of relevant package prefix for all calls for which parameter
     * <b>relevantPackage</b> is null. In fact there is a convinience method {@link #getStacktrace(Throwable, boolean)}
     * that invokes this method with parameter <b>relevantPackage</b> set to null and relies on the globally set
     * property through method {@link #setRelevantPackage(String)}. However if the global property was not set and
     * parameter <b>relevantPackage</b> was left null then the method will return stacktrace in full as if the
     * parameter
     * <b>cutTBS</b> was set to false<br>
     *
     * @param e               {@link Throwable} from which stacktrace should be retrieved
     * @param cutTBS          boolean that specifies if stacktrace should be shortened. The stacktrace should be
     *                        shortened if this flag is set to {@code true}. Note that if this parameter set to {@code
     *                        false} the stacktrace will be printed in full and parameter <b>relevantPackage</b> becomes
     *                        irrelevant.
     * @param relevantPackage {@link String} that contains the prefix specifying which lines are relevant. It is
     *                        recommended to be in the following format "packag_name1.[package_name2.[...]]." In the
     *                        example above it should be "com.plain.analytics.v2.utils.test.".
     * @return String with stacktrace value
     */
    public static String getStacktrace(Throwable e, boolean cutTBS, String relevantPackage) {

        // retrieve full stacktrace as byte array
        ByteArrayOutputStream stacktraceContent = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(stacktraceContent));

        return extractStackTrace(cutTBS, relevantPackage, stacktraceContent);
    }

    /**
     * This method retrieves a stacktrace from {@link Throwable} as a String in full or shortened format. This is
     * convenience method that invokes method {@link #getStacktrace(Throwable, boolean, String)} with last parameter as
     * {@code null}. It relies on relevant package prefix to have been set by method {@link
     * #setRelevantPackage(String)}. There are several ways to pre-invoke method {@link
     * #setRelevantPackage(String)}:<br>
     *     <ul>
     *     <li>Set system environment variable <b>"MGNT_RELEVANT_PACKAGE"</b> with relevant package value (for the purposes of our example
     *     it would be "com.plain.")</li>
     *     <li>Run your code with System property <b>"mgnt.relevant.package"</b> set to relevant package value It could be done with
     *     -D: <b>"-Dmgnt.relevant.package=com.plain."</b> Note that System property value would take precedence over environment variable
     *     if both are set</li>
     *     <li>In case when Spring framework is used and system property and environment variable described above are not used then it is
     * recommended to add the following bean into your Spring configuration xml file. This will ensure an invocation of method
     * {@link #setRelevantPackage(String)} which will appropriately initialize the package prefix and enable the use of this method
     *
     * <p>
     *     &lt;bean class="org.springframework.beans.factory.config.MethodInvokingFactoryBean"&gt;<br>
     *     &nbsp;&lt;property name="targetClass" value="com.mgnt.utils.TextUtils"&#47;&gt;<br>
     *     &nbsp;&lt;property name="targetMethod" value="setRelevantPackage"&#47;&gt;<br>
     *     &nbsp;&lt;property name="arguments" value="com.plain."&#47;&gt;<br>
     *     &lt;&#47;bean&gt;
     * </p></li>
     * </ul>
     *
     * @param e      {@link Throwable} from which stacktrace should be retrieved
     * @param cutTBS boolean flag that specifies if stacktrace should be shortened or not. It is shortened if the flag
     *               value is {@code true}
     * @return String that contains the stacktrace
     * @see #getStacktrace(Throwable, boolean, String)
     */
    public static String getStacktrace(Throwable e, boolean cutTBS) {
        return getStacktrace(e, cutTBS, null);
    }

    /**
     * This method retrieves a stacktrace from {@link Throwable} as a String in shortened format. This is convenience
     * method that invokes method {@link #getStacktrace(Throwable, boolean, String)} with second parameter set to {@code
     * 'true'} and last parameter as {@code null}. It relies on relevant package prefix to have been set by method
     * {@link #setRelevantPackage(String)}. There are several ways to pre-invoke method {@link
     * #setRelevantPackage(String)}:<br>
     *     <ul>
     *     <li>Set system environment variable <b>"MGNT_RELEVANT_PACKAGE"</b> with relevant package value (for the purposes of our example
     *     it would be "com.plain.")</li>
     *     <li>Run your code with System property <b>"mgnt.relevant.package"</b> set to relevant package value It could be done with
     *     -D: <b>"-Dmgnt.relevant.package=com.plain."</b> Note that System property value would take precedence over environment variable
     *     if both are set</li>
     *     <li>In case when Spring framework is used and system property and environment variable described above are not used then it is
     * recommended to add the following bean into your Spring configuration xml file. This will ensure an invocation of method
     * {@link #setRelevantPackage(String)} which will appropriately initialize the package prefix and enable the use of this method
     *
     * <p>
     *     &lt;bean class="org.springframework.beans.factory.config.MethodInvokingFactoryBean"&gt;<br>
     *     &nbsp;&lt;property name="targetClass" value="com.mgnt.utils.TextUtils"&#47;&gt;<br>
     *     &nbsp;&lt;property name="targetMethod" value="setRelevantPackage"&#47;&gt;<br>
     *     &nbsp;&lt;property name="arguments" value="com.plain."&#47;&gt;<br>
     *     &lt;&#47;bean&gt;
     * </p></li>
     * </ul>
     *
     * @param e {@link Throwable} from which stacktrace should be retrieved
     * @return String that contains the stacktrace
     * @see #getStacktrace(Throwable, boolean, String)
     */
    public static String getStacktrace(Throwable e) {
        return getStacktrace(e, true, null);
    }

    /**
     * This method retrieves a stacktrace from {@link Throwable} as a String in shortened format. This is convenience
     * method that invokes method {@link #getStacktrace(Throwable, boolean, String)} with second parameter set to {@code
     * 'true'}.
     *
     * @param e               {@link Throwable} from which stacktrace should be retrieved
     * @param relevantPackage {@link String} that contains the prefix specifying which lines are relevant. It is
     *                        recommended to be in the following format "packag_name1.[package_name2.[...]]."
     * @return String that contains the stacktrace
     * @see #getStacktrace(Throwable, boolean, String)
     */
    public static String getStacktrace(Throwable e, String relevantPackage) {
        return getStacktrace(e, true, relevantPackage);
    }

    /**
     * This method retrieves a stacktrace  in shortened format from source stactrace provided as  {@link CharSequence}.
     * Since this method receives stacktrace as a {@link CharSequence}, it is assumed that it is always desirable to get
     * shortened format since the full stacktrace is already available as a {@link CharSequence}. To shorten the
     * original stacktrace this method processes the stacktrace exactly as described in method {@link
     * #getStacktrace(Throwable, boolean, String)}.
     *
     * @param stacktrace      {@link CharSequence} that holds full stacktrace text
     * @param relevantPackage {@link String} that contains the prefix specifying which lines are relevant. It is
     *                        recommended to be in the following format "packag_name1.[package_name2.[...]]." (Again for
     *                        full explanation on how this parameter (and the entire method) works see the method {@link
     *                        #getStacktrace(Throwable, boolean, String)}.
     * @return Stacktrace string in shortened format
     * @see #getStacktrace(Throwable, boolean, String)
     * @since 1.5.0.3
     */
    public static String getStacktrace(CharSequence stacktrace, String relevantPackage) {
        return extractStackTrace(true, relevantPackage, convertToByteArray(stacktrace));
    }

    /**
     * This method retrieves a stacktrace  in shortened format from source stactrace provided as  {@link CharSequence}.
     * This is convenience method that works the same way as method {@link #getStacktrace(CharSequence, String)} with
     * second parameter set to {@code null}. It relies on relevant package prefix to have been set by method {@link
     * #setRelevantPackage(String)}. There are several options to pre-set this value. For detailed explanation of these
     * options see method {@link #getStacktrace(Throwable, boolean)}. Since this method receives stacktrace as a {@link
     * CharSequence}, it is assumed that it is always desirable to get shortened format since the full stacktrace is
     * already available as a {@link CharSequence}
     *
     * @param stacktrace {@link CharSequence} that holds full stacktrace text
     * @return Stacktrace string in shortened format
     * @see #getStacktrace(Throwable, boolean)
     * @since 1.5.0.3
     */
    public static String getStacktrace(CharSequence stacktrace) {
        return extractStackTrace(true, null, convertToByteArray(stacktrace));
    }

    /**
     * This method simply takes a {@link CharSequence} parameter and converts it to {@link ByteArrayOutputStream}. If
     * the parameter is null then blank {@link ByteArrayOutputStream} is returned
     *
     * @param stacktrace {@link CharSequence} that supposed to contain the stacktrace text
     * @return {@link ByteArrayOutputStream} that contains the contents of <b>stacktrace</b> parameter converted to
     * bytes or blank {@link ByteArrayOutputStream} if <b>stacktrace</b> parameter is {@code null} or blank
     */
    private static ByteArrayOutputStream convertToByteArray(CharSequence stacktrace) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (stacktrace != null) {
            byte[] content = stacktrace.toString().getBytes();
            baos.write(content, 0, content.length);
        }
        return baos;
    }

    /**
     * This method receives the stacktrace content as {@link ByteArrayOutputStream} and processes it exactly as
     * described in method {@link #getStacktrace(Throwable, boolean, String)}. Except that it receives the stacktrace
     * content as byte array so it is agnostic of the fact whether it came from actual exception in real time of from a
     * log file or any other source. This method allows to work with stacktraces extracted on the fly at runtime or
     * taken from some static sources (such as log files)
     *
     * @param cutTBS            boolean that specifies if stacktrace should be shortened. The stacktrace should be
     *                          shortened if this flag is set to {@code true}. Note that if this parameter set to {@code
     *                          false} the stacktrace will be printed in full and parameter <b>relevantPackage</b>
     *                          becomes irrelevant.
     * @param relevantPackage   {@link String} that contains the prefix specifying which lines are relevant. It is
     *                          recommended to be in the following format "packag_name1.[package_name2.[...]]."
     * @param stacktraceContent {@link ByteArrayOutputStream} that contains the stacktrace content
     * @return
     */
    private static String extractStackTrace(boolean cutTBS, String relevantPackage, ByteArrayOutputStream stacktraceContent) {
        StringBuilder result = new StringBuilder("\n");

        // Determine the value of relevant package prefix
        String relPack = (relevantPackage != null && !relevantPackage.isEmpty()) ? relevantPackage : RELEVANT_PACKAGE;
        /*
         * If the relevant package prefix was not set neither locally nor globally revert to retrieving full stacktrace even if shortening was
         * requested
         */
        if (relPack == null || "".equals(relPack)) {
            if (cutTBS) {
                cutTBS = false;
                logger.warn("Relevant package was not set for the method. Stacktrace can not be shortened. Returning full stacktrace");
            }
        }
        if (cutTBS) {
            if (stacktraceContent.size() > 0) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(stacktraceContent.toByteArray())))) {
                    /*
                     * First line from stacktrace is an actual error message and is always printed. If it happens to be null (which should never
                     * occur) it won't cause any problems as appending null to StringBuilder (within method traverseSingularStacktrace()) will be a
                     * no-op and subsequent readLine will still return null and will be detected in the method traverseSingularStacktrace()
                     */
                    String line = reader.readLine();
                    do {
                        /*
                         * Process singular stacktraces until all are processed.
                         */
                        line = traverseSingularStacktrace(result, relPack, reader, line);
                    } while (line != null);
                } catch (IOException ioe) {
                    /*
                     * In the very unlikely event of any error just fall back on printing the full stacktrace
                     */
                    error("Error occurred while reading and shortening stacktrace of an exception. Printing the original stacktrace", ioe);
                    result.delete(0, result.length()).append(new String(stacktraceContent.toByteArray()));
                }
            }
        } else {
            /*
             * This is the branch that prints full stacktrace
             */
            result.append(new String(stacktraceContent.toByteArray()));
        }
        return result.toString();
    }

    /**
     * This method traverses through Singular stacktrace and skips irrelevant lines from it replacing them by line "..."
     * The resulting shortened stacktrace is appended into {@link StringBuilder} The stacktrace is viewed as consisting
     * possibly of several parts. If stacktrace contains {@code "caused by"} or {@code "Suppressed"} section, each such
     * section for the purposes of this utility is called "Singular stacktrace". For more detailed explanation see
     * method {@link #getStacktrace(Throwable, boolean, String)}
     *
     * @param result  {@link StringBuilder} to which the resultant stacktrace will be appended
     * @param relPack {@link String} that contains relevant package prefix
     * @param reader  {@link BufferedReader} that contains the source from where the stacktrace may be read line by
     *                line. Current position in the reader is assumed to be at the beginning of the second line of the
     *                current singular stacktrace, following the line with the name of the exception and error message
     * @param line    {@link String} that contains the first line of the current singular stacktrace i.e. the line with
     *                the name of the exception and error message
     * @return The first string of the next singular stacktrace or null if current singular stacktrace is the last one
     * in the stacktrace
     * @throws IOException if any error occurs.
     * @see #getStacktrace(Throwable, boolean, String)
     */
    private static String traverseSingularStacktrace(StringBuilder result, String relPack, BufferedReader reader, String line) throws IOException {
        result.append(line).append("\n");

        // Flag that holds the status for the current line if it should be printed or not
        boolean toBePrinted = true;

        // Flag that holds information on previous line whether or not it starts with relevant prefix package
        boolean relevantPackageReached = false;

        // Flag that specifies if the current line starts with relevant prefix or not
        boolean isCurLineRelevantPack = false;

        // Flag that specifies that skipping line should be printed as next line
        boolean skipLineToBePrinted = false;

        // Main cycle reading lines until reaching the end of stacktrace
        while ((line = reader.readLine()) != null) {
            String trimmedLine = line.trim();
            if (trimmedLine.startsWith(STANDARD_STAKTRACE_PREFIX)) {
                /*
                 * This "if" branch deals with lines that are standard satacktrace lines atarting with "at "
                 */

                //Check if the current line starts with thge prefix (after the "at " part)
                isCurLineRelevantPack = trimmedLine.substring(STANDARD_STAKTRACE_PREFIX.length()).startsWith(relPack);
                if (!relevantPackageReached && isCurLineRelevantPack) {
                    /*
                     * If the current line starts with the prefix but previous line did not we change the printing status. This case deals with the
                     * first found line with the prefix and in this case it actually "changes" the flag "toBePrinted" from "true" to "true", but also
                     * it deals with the line with the prefix found after the first section of lines with the prefix was treated and was followed by
                     * some lines without prefix and then again the line with the prefix was found. That is why this "if" branch has to be before the
                     * actual printing. In this case flag "toBePrinted" is changed from "false" to "true". Also if previous line was the first line
                     * without prefix and we were supposed to print a skip line here we cancel that by setting flag "skipLineToBePrinted" back to
                     * false
                     */
                    relevantPackageReached = true;
                    toBePrinted = true;
                    skipLineToBePrinted = false;
                }

                // Add (or "print" the line into result if it is considered to be relevant
                if (toBePrinted) {
                    result.append(line).append("\n");
                } else if (skipLineToBePrinted) {
                    result.append(SKIPPING_LINES_STRING).append("\n");
                    skipLineToBePrinted = false;
                }

                /*
                 * Check if the previous line was with the prefix but current one is not. If this is the case, change the value of the "toBePrinted"
                 * flag to false switch the value of the flag skipLineToBePrinted to true to indicate that next line that should be printed into the
                 * result is the skip line ("...")to indicate that some lines are skipped. Note that the current line already was added to the result
                 * which is by design as one "irrelevant" line is printed after a section of "relevant" lines. See documentation for method
                 * getStacktrace(Throwable e, boolean cutTBS, String relevantPackage) for details. Also note that here we don't actually print the
                 * skip line but just set the flag "skipLineToBePrinted" because we don't know if the next line in the stacktrace may be actually a
                 * relevant line and then the skip line won't be needed to be printed.
                 */
                if (relevantPackageReached && !isCurLineRelevantPack) {
                    relevantPackageReached = false;
                    toBePrinted = false;
                    skipLineToBePrinted = true;
                }
            } else {
                /*
                 * This "else" branch deals with lines in the stacktrace that either start next singular stacktrace or are the last line in current
                 * singular stacktrace and it is of the form "... X more" where X is a number.
                 */
                if (trimmedLine.startsWith(CAUSE_STAKTRACE_PREFIX) || trimmedLine.startsWith(SUPPRESED_STAKTRACE_PREFIX)) {
                    /*
                     * If this is the first line of next singular stacktrace we break out of the current cycle and return this line for the next
                     * iteration which will invoke this method again
                     */
                    break;
                    /*
                     * If it is last line in current singular stacktrace that starts with "..." then we print it if needed based on the value of the
                     * flag "toBePrinted" or in the case if we needed to add our line "..." instead we just print the original line as it has also the
                     * number of skipped lines
                     */
                } else if (toBePrinted || skipLineToBePrinted) {
                    result.append(line).append("\n");

                    //Just in case
                    skipLineToBePrinted = false;
                }
            }
        }
        return line;
    }

    /**
     * This a getter method for global value of relevant package prefix property
     *
     * @return String that holds the prefix value.
     */
    public static String getRelevantPackage() {
        return RELEVANT_PACKAGE;
    }

    /**
     * This is a setter method for relevant package prefix property for method {@link #getStacktrace(Throwable,
     * boolean)} Once the value has been set the convenience method {@link #getStacktrace(Throwable, boolean)} could be
     * used instead of method {@link #getStacktrace(Throwable, boolean, String)}
     *
     * @param relevantPackage {@link String} that contains the prefix specifying which lines are relevant. It is
     *                        recommended to be in the following format "packag_name1.[package_name2.[...]]."
     * @see #getStacktrace(Throwable, boolean, String)
     */
    public static void setRelevantPackage(String relevantPackage) {
        RELEVANT_PACKAGE = relevantPackage;
    }

    private static void error(String message, Throwable t) {
        if (RELEVANT_PACKAGE != null && !RELEVANT_PACKAGE.isEmpty()) {
            logger.error(message + getStacktrace(t));
        } else {
            logger.error(message, t);
        }
    }
}
